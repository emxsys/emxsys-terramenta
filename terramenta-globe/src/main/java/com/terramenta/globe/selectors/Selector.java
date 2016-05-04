/**
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.globe.selectors;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceCircle;
import gov.nasa.worldwind.render.SurfaceEllipse;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.render.SurfaceQuad;
import gov.nasa.worldwind.render.SurfaceSector;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.render.SurfaceSquare;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import org.openide.util.Lookup;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class Selector<T extends SurfaceShape> extends AVListImpl {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private RenderableLayer layer;
    private final T shape;
    private boolean armed = false;
    private boolean active = false;
    private boolean freehand = false;
    private ArrayList<Position> positions = new ArrayList<>();

    private final KeyAdapter ka = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (armed && !active && keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                setArmed(false);
            }
        }
    };
    private final MouseAdapter ma = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
                if (armed && (mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                    if (!mouseEvent.isControlDown()) {
                        active = true;
                        addPosition();
                    }
                }
                mouseEvent.consume();
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
                active = false;
                mouseEvent.consume();
                setArmed(false);
            }
        }
    };
    private final MouseMotionAdapter mma = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            if (armed && (mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                // Don't update the shape here because the wwd current cursor position will not
                // have been updated to reflect the current mouse position. Wait to update in the
                // position listener, but consume the event so the view doesn't respond to it.
                if (active) {
                    mouseEvent.consume();
                }
            }
        }
    };
    private final PositionListener pl = new PositionListener() {
        @Override
        public void moved(PositionEvent event) {
            if (!active) {
                return;
            }
            addPosition();
        }
    };

    public Selector(T shape) {
        this.shape = shape;
        this.layer = (RenderableLayer) wwm.getLayers().getLayerByName("Selectors");
        if (layer == null) {
            layer = new RenderableLayer();
            layer.setName("Selectors");
            wwm.getLayers().add(layer);
        }
        layer.addRenderable(shape);

        //USECASE: signals layer manager that the layer has modified children
        //         and the count in the display name should be updated
        layer.firePropertyChange("Renderables", null, layer.getRenderables());
    }

    public Selector(T shape, RenderableLayer layer) {
        this.shape = shape;
        this.layer = layer;
        layer.addRenderable(shape);

        //USECASE: signals layer manager that the layer has modified children
        //         and the count in the display name should be updated
        layer.firePropertyChange("Renderables", null, layer.getRenderables());
    }

    /**
     *
     * @return
     */
    public RenderableLayer getLayer() {
        return this.layer;
    }

    /**
     *
     * @return
     */
    public T getShape() {
        return shape;
    }

    /**
     *
     * @return
     */
    public boolean isFreeHand() {
        return freehand;
    }

    /**
     *
     * @param freehand
     */
    public void setFreeHand(boolean freehand) {
        this.freehand = freehand;
    }

    /**
     *
     * @return
     */
    public boolean isArmed() {
        return armed;
    }

    /**
     * Arms and disarms the selector. When armed, the selector monitors user input and builds the
     * shape in response to the actions mentioned in the overview above. When disarmed, the selector
     * ignores all user input.
     *
     * @param armed true to arm the line builder, false to disarm it.
     */
    public void setArmed(boolean armed) {
        firePropertyChange("armed", this.armed, this.armed = armed);
        if (armed) {
            // Force keyboard focus to globe
            ((Component) wwm.getWorldWindow()).requestFocusInWindow();

            //add listeners
            wwm.getWorldWindow().getInputHandler().addKeyListener(ka);
            wwm.getWorldWindow().getInputHandler().addMouseListener(ma);
            wwm.getWorldWindow().getInputHandler().addMouseMotionListener(mma);
            wwm.getWorldWindow().addPositionListener(pl);

            //set cursor
            ((Component) wwm.getWorldWindow()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            //remove listeners
            wwm.getWorldWindow().getInputHandler().removeKeyListener(ka);
            wwm.getWorldWindow().getInputHandler().removeMouseListener(ma);
            wwm.getWorldWindow().getInputHandler().removeMouseMotionListener(mma);
            wwm.getWorldWindow().removePositionListener(pl);

            //reset cursor
            ((Component) wwm.getWorldWindow()).setCursor(Cursor.getDefaultCursor());
        }
    }

    private void addPosition() {
        Position curPos = wwm.getWorldWindow().getCurrentPosition();
        if (curPos == null) {
            return;
        }

        if (shape instanceof SurfaceQuad) {
            SurfaceQuad quadShape = (SurfaceQuad) shape;
            if (quadShape.getCenter() == LatLon.ZERO) {
                quadShape.setCenter(curPos);
            } else {
                updateShape(shape, curPos);
            }
        } else if (shape instanceof SurfaceEllipse) {
            SurfaceEllipse ellipseShape = (SurfaceEllipse) shape;
            if (ellipseShape.getCenter() == LatLon.ZERO) {
                ellipseShape.setCenter(curPos);
            } else {
                updateShape(shape, curPos);
            }
        } else if (shape instanceof SurfacePolygon) {
            SurfacePolygon polygonShape = (SurfacePolygon) shape;
            this.positions.add(curPos);
            if (isFreeHand()) {
                polygonShape.setLocations(positions);
            } else {
                ArrayList startEnd = new ArrayList();
                startEnd.add(positions.get(0));
                startEnd.add(positions.get(positions.size() - 1));
                polygonShape.setLocations(startEnd);
            }

        } else if (shape instanceof SurfacePolyline) {
            SurfacePolyline lineShape = (SurfacePolyline) shape;
            this.positions.add(curPos);
            if (isFreeHand()) {
                lineShape.setLocations(positions);
            } else {
                ArrayList startEnd = new ArrayList();
                startEnd.add(positions.get(0));
                startEnd.add(positions.get(positions.size() - 1));
                lineShape.setLocations(startEnd);
            }
        } else if (shape instanceof SurfaceSector) {
            SurfaceSector sectorShape = (SurfaceSector) shape;
            if (sectorShape.getSector() == Sector.EMPTY_SECTOR) {
                //sectors draw from the corner instead of the center
                sectorShape.setValue("STARTING_POSITION", curPos);
                sectorShape.setSector(new Sector(curPos.getLatitude(), curPos.getLatitude(), curPos.getLongitude(), curPos.getLongitude()));
            } else {
                updateShape(shape, curPos);
            }
        }

        wwm.getWorldWindow().redraw();
    }

    /**
     *
     * @param shape
     * @param newPosition
     */
    protected void updateShape(T shape, Position newPosition) {
        if (shape instanceof SurfaceQuad) {
            SurfaceQuad quadShape = (SurfaceQuad) shape;
            Angle controlAzimuth = LatLon.greatCircleAzimuth(quadShape.getCenter(), newPosition);
            Angle controlArcLength = LatLon.greatCircleDistance(quadShape.getCenter(), newPosition);
            double globeRadius = wwm.getWorldWindow().getModel().getGlobe().getRadiusAt(quadShape.getCenter());
            double arcLengthMeters = Math.abs(controlArcLength.radians) * globeRadius;
            double width = arcLengthMeters * 2;
            double height = arcLengthMeters;
            if (shape instanceof SurfaceSquare) {
                height = width;
            }
            quadShape.setSize(height, width);
            quadShape.setHeading(controlAzimuth);

        } else if (shape instanceof SurfaceEllipse) {
            SurfaceEllipse ellipseShape = (SurfaceEllipse) shape;
            Angle controlAzimuth = LatLon.greatCircleAzimuth(ellipseShape.getCenter(), newPosition);
            Angle controlArcLength = LatLon.greatCircleDistance(ellipseShape.getCenter(), newPosition);
            double globeRadius = wwm.getWorldWindow().getModel().getGlobe().getRadiusAt(ellipseShape.getCenter());
            double arcLengthMeters = Math.abs(controlArcLength.radians) * globeRadius;
            double width = arcLengthMeters;
            double height = arcLengthMeters / 2;
            if (shape instanceof SurfaceCircle) {
                height = width;
            }
            ellipseShape.setRadii(height, width);
            ellipseShape.setHeading(controlAzimuth);
        } else if (shape instanceof SurfaceSector) {
            //sectors draw from the corner instead of the center
            SurfaceSector sectorShape = (SurfaceSector) shape;
            Position startingPosition = (Position) sectorShape.getValue("STARTING_POSITION");
            sectorShape.setSector(Sector.boundingSector(startingPosition, newPosition));
        }
    }
}
