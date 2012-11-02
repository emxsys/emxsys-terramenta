/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.utilities;

import gov.nasa.worldwind.awt.AbstractViewInputHandler;
import gov.nasa.worldwind.awt.BasicViewInputHandler.VertTransMouseWheelActionListener;
import gov.nasa.worldwind.awt.ViewInputAttributes;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewInputHandler;
import java.awt.event.MouseWheelEvent;

public class ZoomToCursorViewInputHandler extends OrbitViewInputHandler {

    public ZoomToCursorViewInputHandler() {
        ViewInputAttributes.ActionAttributes actionAttrs = this.getAttributes().getActionMap(
                ViewInputAttributes.DEVICE_MOUSE_WHEEL).getActionAttributes(
                ViewInputAttributes.VIEW_VERTICAL_TRANSLATE);
        actionAttrs.setMouseActionListener(new ZoomActionHandler());
    }

    protected class ZoomActionHandler extends VertTransMouseWheelActionListener {

        @Override
        public boolean inputActionPerformed(AbstractViewInputHandler inputHandler, MouseWheelEvent mouseWheelEvent, ViewInputAttributes.ActionAttributes viewAction) {
            double zoomInput = mouseWheelEvent.getWheelRotation();
            Position position = computeSelectedPosition();

            // Zoom toward the cursor if we're zooming in. Move straight out when zooming out.
            if (zoomInput < 0 && position != null) {
                return this.zoomToPosition(position, zoomInput, viewAction);
            } else {
                return super.inputActionPerformed(inputHandler, mouseWheelEvent, viewAction);
            }
        }

        protected boolean zoomToPosition(Position position, double zoomInput, ViewInputAttributes.ActionAttributes viewAction) {
            BasicOrbitView orbitView = (BasicOrbitView) getView();
            Position centerPosition = orbitView.getCenterPosition();

            LatLon delta = position.subtract(centerPosition);

            //Rectangle viewport = orbitView.getViewport();
            //double viewportWidth = viewport.getWidth();
            //double viewportHeight = viewport.getHeight();

            // Compute a scale factor based on how far the mouse cursor is from the viewport center.
            //double dist = getMousePoint().distanceSq(viewport.getCenterX(), viewport.getCenterY());
            //double scale = dist / (viewportWidth * viewportWidth + viewportHeight * viewportHeight);
            double scale = getScaleValueRotate(viewAction);

            Angle latitudeChange = delta.getLatitude().multiply(scale);
            Angle longitudeChange = delta.getLongitude().multiply(scale);

            // Apply horizontal translation, if necessary.
            if (!latitudeChange.equals(Angle.ZERO) || !longitudeChange.equals(Angle.ZERO)) {
                Position newPosition = orbitView.getCenterPosition().add(new Position(latitudeChange, longitudeChange, 0.0));
                setCenterPosition(orbitView, uiAnimControl, newPosition, viewAction);
            }

            onVerticalTranslate(zoomInput * scale, viewAction);

            return true;
        }
    }
}
