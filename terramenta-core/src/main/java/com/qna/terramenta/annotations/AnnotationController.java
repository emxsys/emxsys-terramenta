/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.annotations;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceCircle;
import gov.nasa.worldwind.render.SurfaceEllipse;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.render.SurfaceQuad;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.render.SurfaceSquare;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * 
 * @author heidtmare
 */
public class AnnotationController extends AVListImpl {

    private static final Logger logger = Logger.getAnonymousLogger();
    private final WorldWindow wwd;
    private final SurfaceShape shape;
    private boolean armed = false;
    private boolean active = false;
    private boolean freehand = false;
    private ArrayList<Position> positions = new ArrayList<Position>();
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

    /**
     * Construct a new line builder using the specified polyline and layer and drawing events from the specified world
     * window. Either or both the polyline and the layer may be null, in which case the necessary object is created.
     *
     * @param wwd       the world window to draw events from.
     * @param shape 
     */
    public AnnotationController(final WorldWindow wwd, SurfaceShape shape) {
        this.wwd = wwd;
        this.shape = shape;
        RenderableLayer layer = (RenderableLayer) wwd.getModel().getLayers().getLayerByName("User Annotations");
        if (layer == null) {
            layer = new RenderableLayer();
            layer.setName("User Annotations");
            wwd.getModel().getLayers().add(layer);
        }
        layer.addRenderable(shape);
    }

    /**
     * Arms and disarms the line builder. When armed, the line builder monitors user input and builds the polyline in
     * response to the actions mentioned in the overview above. When disarmed, the line builder ignores all user input.
     *
     * @param armed true to arm the line builder, false to disarm it.
     */
    public void setArmed(boolean armed) {
        this.armed = armed;
        if (armed) {
            this.wwd.getInputHandler().addMouseListener(ma);
            this.wwd.getInputHandler().addMouseMotionListener(mma);
            this.wwd.addPositionListener(pl);
            ((Component) wwd).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            this.wwd.getInputHandler().removeMouseListener(ma);
            this.wwd.getInputHandler().removeMouseMotionListener(mma);
            this.wwd.removePositionListener(pl);
            ((Component) wwd).setCursor(Cursor.getDefaultCursor());
        }
    }

    private void addPosition() {
        Position curPos = this.wwd.getCurrentPosition();
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

        this.wwd.redraw();
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
            double globeRadius = this.wwd.getModel().getGlobe().getRadiusAt(quadShape.getCenter());
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
            double globeRadius = this.wwd.getModel().getGlobe().getRadiusAt(ellipseShape.getCenter());
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
}
