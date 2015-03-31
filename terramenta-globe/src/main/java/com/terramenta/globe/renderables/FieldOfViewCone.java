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
package com.terramenta.globe.renderables;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Cone;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import java.awt.Color;
import java.beans.PropertyChangeEvent;

/**
 * FieldOfViewCone
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public final class FieldOfViewCone extends Cone {

    private static final ShapeAttributes defaultAttrs = new BasicShapeAttributes();

    static {
        defaultAttrs.setDrawInterior(true);
        defaultAttrs.setDrawOutline(false);
        defaultAttrs.setEnableAntialiasing(false);
        defaultAttrs.setEnableLighting(false);
        defaultAttrs.setInteriorMaterial(Material.BLACK);
        defaultAttrs.setInteriorOpacity(0.4);
    }

    private Position position;

    public FieldOfViewCone(Position position) {
        super();
        this.position = position;
        setValue(AVKey.DISPLAY_NAME, "Field of View");
        setValue(AVKey.DISPLAY_ICON, "com/terramenta/globe/images/Platform_satFieldOfView.png");
        setAttributes(defaultAttrs.copy());
        updateDimensions(position);
    }

    private void updateDimensions(Position position) {
        Position center = position;
        double baseRadius = 1;
        double halfHeight = 1;
        double altitude = position.getAltitude();
        if (altitude > 0) {
            double earthRadius = Earth.WGS84_EQUATORIAL_RADIUS;
            double lam = Math.acos(earthRadius / (earthRadius + altitude));
            baseRadius = earthRadius * Math.sin(lam);// radius of cone base
            double earthCenterToConeBase = earthRadius * Math.cos(lam);//distance from the earth center to the cone base
            double coneHeight = (earthRadius + altitude - earthCenterToConeBase);// distance from cone tip to cone base
            halfHeight = coneHeight / 2;//distance from center to tip/base 
            double centerAltitude = halfHeight - (earthRadius - earthCenterToConeBase);
            center = new Position(position.getLatitude(), position.getLongitude(), centerAltitude);
        }
        this.setCenterPosition(center);
        this.setNorthSouthRadius(baseRadius);
        this.setVerticalRadius(halfHeight);
        this.setEastWestRadius(baseRadius);
    }

    @Override
    public void setVisible(boolean visible) {
        boolean old = isVisible();
        super.setVisible(visible);
        //this bubbles up through the parent layers and causes a redraw 
        firePropertyChange(new PropertyChangeEvent(this, "Enabled", old, visible));
    }

    /**
     * This position is the vertex of the Cone
     *
     * @return
     */
    public Position getPosition() {
        return position;
    }

    /**
     *
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
        updateDimensions(position);
    }

    /**
     *
     * @return
     */
    public Color getColor() {
        return getAttributes().getInteriorMaterial().getDiffuse();
    }

    /**
     *
     * @param color
     */
    public void setColor(Color color) {
        this.getAttributes().setInteriorMaterial(new Material(color));
    }
}
