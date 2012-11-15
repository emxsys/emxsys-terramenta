/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.drag;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.DragSelectEvent;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import java.awt.Point;

/**
 *
 * @author Chris.Heidt
 */
public class DragController implements SelectListener, Disposable {

    private final WorldWindow wwd;
    private boolean dragging = false;
    private Point dragRefCursorPoint;
    private Vec4 dragRefObjectPoint;
    private double dragRefAltitude;
    private boolean armed = false;

    public DragController(WorldWindow wwd) {
        if (wwd == null) {
            throw new IllegalArgumentException("nullValue.WorldWindow");
        }
        this.wwd = wwd;
    }

    @Override
    public void dispose() {
        setArmed(false);
    }

    /**
     *
     * @return
     */
    public boolean isArmed() {
        return armed;
    }

    /**
     *
     * @param armed
     */
    public void setArmed(boolean armed) {
        if (this.armed == armed) {
            return;
        }
        this.armed = armed;
        if (armed) {
            this.wwd.addSelectListener(this);
        } else {
            this.wwd.removeSelectListener(this);
        }
    }

    public boolean isDragging() {
        return this.dragging;
    }

    @Override
    public void selected(SelectEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("nullValue.EventIsNull");
        }

        if (event.getEventAction().equals(SelectEvent.DRAG_END)) {
            this.dragging = false;
            event.consume();
        } else if (event.getEventAction().equals(SelectEvent.DRAG)) {
            DragSelectEvent dragEvent = (DragSelectEvent) event;
            Object topObject = dragEvent.getTopObject();
            if (topObject == null) {
                return;
            }

            if (!(topObject instanceof Draggable)) {
                return;
            }

            Draggable dragMe = (Draggable) topObject;

            if (!dragMe.isDraggable()) {
                return;
            }

            View view = wwd.getView();
            Globe globe = wwd.getModel().getGlobe();

            // Compute dragged object ref-point in model coordinates.
            // Use the Icon and Annotation logic of elevation as offset above ground when below max elevation.
            if (dragMe.getPosition() == null) {
                return;
            }


            if (!this.isDragging()) {
                Vec4 refPoint = globe.computePointFromPosition(dragMe.getPosition());

                // Save initial reference points for object and cursor in screen coordinates
                // Note: y is inverted for the object point.
                this.dragRefObjectPoint = view.project(refPoint);
                // Save cursor position
                this.dragRefCursorPoint = dragEvent.getPreviousPickPoint();
                // Save start altitude
                this.dragRefAltitude = globe.computePositionFromPoint(refPoint).getElevation();
            }

            // Compute screen-coord delta since drag started.
            int dx = dragEvent.getPickPoint().x - this.dragRefCursorPoint.x;
            int dy = dragEvent.getPickPoint().y - this.dragRefCursorPoint.y;

            // Find intersection of screen coord (refObjectPoint + delta) with globe.
            double x = this.dragRefObjectPoint.x + dx;
            double y = event.getMouseEvent().getComponent().getSize().height - this.dragRefObjectPoint.y + dy - 1;
            Line ray = view.computeRayFromScreenPoint(x, y);

            Position pickPos = null;
            // Use intersection with sphere at reference altitude.
            Intersection inters[] = globe.intersect(ray, this.dragRefAltitude);
            if (inters != null) {
                pickPos = globe.computePositionFromPoint(inters[0].getIntersectionPoint());
            }

            if (pickPos != null) {
                // Intersection with globe. Move reference point to the intersection point,
                // but maintain current altitude.
                Position p = new Position(pickPos, dragMe.getPosition().getElevation());
                dragMe.setPosition(p);
            }

            this.dragging = true;
            event.consume();
        }
    }
}
