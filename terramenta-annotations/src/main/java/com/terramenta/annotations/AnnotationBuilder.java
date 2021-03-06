/*
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
package com.terramenta.annotations;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.UnitsFormat;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import org.openide.util.Lookup;

/**
 *
 * @author heidtmare
 */
public class AnnotationBuilder extends AVListImpl {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private static final AnnotationAttributes labelAttributes = new AnnotationAttributes();
    private static final UnitsFormat unitsFormat = new UnitsFormat();
    private RenderableLayer layer;
    private final SurfaceShape shape;
    private GlobeAnnotation label = null;
    private boolean armed = false;
    private boolean active = false;
    private boolean freehand = false;
    private boolean showLabel = false;
    private ArrayList<Position> positions = new ArrayList<>();

    static {
        labelAttributes.setFrameShape(AVKey.SHAPE_NONE);
        labelAttributes.setInsets(new Insets(0, 0, 0, 0));
        labelAttributes.setDrawOffset(new Point(0, 10));
        labelAttributes.setTextAlign(AVKey.CENTER);
        labelAttributes.setEffect(AVKey.TEXT_EFFECT_OUTLINE);
        labelAttributes.setFont(Font.decode("Arial-Bold-14"));
        labelAttributes.setTextColor(Color.BLACK);
        labelAttributes.setBackgroundColor(Color.WHITE);
        labelAttributes.setSize(new Dimension(220, 0));
    }
    private final KeyAdapter ka = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (armed && !active && keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                cancel();
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
                // Don't update the polyline here because the wwd current cursor position will not
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
    private boolean canceled;

    /**
     * Construct a new line builder using the specified polyline and layer and drawing events from the specified world
     * window. Either or both the polyline and the layer may be null, in which case the necessary object is created.
     *
     * @param shape
     */
    public AnnotationBuilder(SurfaceShape shape) {
        this.shape = shape;
        this.layer = (RenderableLayer) wwm.getLayers().getLayerByName("User Annotations");
        if (layer == null) {
            layer = new RenderableLayer();
            layer.setName("User Annotations");
            wwm.getLayers().add(layer);
        }
        layer.addRenderable(shape);

        //USECASE: signals layer manager that the layer has modified children
        //         and the count in the display name should be updated
        layer.firePropertyChange("Renderables", null, layer.getRenderables());
    }

    public AnnotationBuilder(SurfaceShape shape, RenderableLayer layer) {
        this.shape = shape;
        this.layer = layer;
        layer.addRenderable(shape);
    }

    public RenderableLayer getLayer() {
        return this.layer;
    }

    /**
     * Arms and disarms the line builder. When armed, the line builder monitors user input and builds the polyline in
     * response to the actions mentioned in the overview above. When disarmed, the line builder ignores all user input.
     *
     * @param armed true to arm the line builder, false to disarm it.
     */
    public void setArmed(boolean armed) {
        firePropertyChange("armed", this.armed, this.armed = armed);
        if (armed) {
            // Force keyboard focus to globe
            ((Component)wwm.getWorldWindow()).requestFocusInWindow();

            //add listeners
            wwm.getWorldWindow().getInputHandler().addKeyListener(ka);
            wwm.getWorldWindow().getInputHandler().addMouseListener(ma);
            wwm.getWorldWindow().getInputHandler().addMouseMotionListener(mma);
            wwm.getWorldWindow().addPositionListener(pl);

            //set cursor
            ((Component)wwm.getWorldWindow()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            //remove listeners
            wwm.getWorldWindow().getInputHandler().removeKeyListener(ka);
            wwm.getWorldWindow().getInputHandler().removeMouseListener(ma);
            wwm.getWorldWindow().getInputHandler().removeMouseMotionListener(mma);
            wwm.getWorldWindow().removePositionListener(pl);

            //reset cursor
            ((Component)wwm.getWorldWindow()).setCursor(Cursor.getDefaultCursor());
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
        }

        if (isShowLabel()) {
            updateLabel(curPos);
        }

        wwm.getWorldWindow().redraw();
    }

    /**
     *
     * @param shape
     * @param newPosition
     */
    protected void updateShape(SurfaceShape shape, Position newPosition) {
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
        }
    }

    protected void updateLabel(Position pos) {
        String text = getDisplayString(pos);
        if (label == null) {
            label = new GlobeAnnotation(text, pos, labelAttributes);
            layer.addRenderable(label);
        } else {
            label.setPosition(pos);
            label.setText(text);
        }
        label.getAttributes().setVisible(pos != null);
    }

    protected String getDisplayString(Position pos) {
        String displayString = null;
        if (pos != null) {
            if (shape instanceof SurfacePolyline) {
                displayString = formatLineMeasurements(pos);
            }
        }
        return displayString;
    }

    protected String formatLineMeasurements(Position pos) {
        StringBuilder sb = new StringBuilder();
        double length = new LengthMeasurer(positions).getLength(wwm.getWorldWindow().getModel().getGlobe());
        sb.append(AnnotationBuilder.unitsFormat.lengthNL("Length: ", length));
        //sb.append(AnnotationBuilder.unitsFormat.lengthNL("Length", this.shape.getLength(wwd.getModel().getGlobe())));
        if (positions.size() > 1) {
            Angle greatCircleAzimuth = LatLon.greatCircleAzimuth(this.positions.get(0), this.positions.get(1));
            sb.append(AnnotationBuilder.unitsFormat.angleNL("Orientation: ", greatCircleAzimuth));
        }
        sb.append(AnnotationBuilder.unitsFormat.angleNL("Latitude: ", pos.getLatitude()));
        sb.append(AnnotationBuilder.unitsFormat.angleNL("Longitude: ", pos.getLongitude()));
        return sb.toString();
    }

    /*
     * protected Vec4 computeAnnotationPosition(Position pos) { Vec4 surfacePoint =
     * this.wwd.getSceneController().getTerrain().getSurfacePoint(pos.getLatitude(), pos.getLongitude()); if (surfacePoint == null) { Globe globe =
     * this.wwd.getModel().getGlobe(); surfacePoint = globe.computePointFromPosition(pos.getLatitude(), pos.getLongitude(),
     * globe.getElevation(pos.getLatitude(), pos.getLongitude())); } return this.wwd.getView().project(surfacePoint);
     * }
     */
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
    public boolean isShowLabel() {
        return showLabel;
    }

    /**
     *
     * @param measurement
     */
    public void setShowLabel(boolean measurement) {
        this.showLabel = measurement;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        this.canceled = true;
        setArmed(false);
    }
}
