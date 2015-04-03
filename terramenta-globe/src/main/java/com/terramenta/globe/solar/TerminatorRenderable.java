/**
 * Copyright © 2014, Terramenta. All rights reserved. * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this work except in compliance
 * with the License.
 *
 * You can obtain a copy of the License at http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 *
 */
package com.terramenta.globe.solar;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceCircle;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class TerminatorRenderable extends SurfaceCircle {

    public TerminatorRenderable() {
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setInteriorMaterial(Material.DARK_GRAY);
        attrs.setInteriorOpacity(0.6);
        attrs.setOutlineMaterial(Material.DARK_GRAY);
        attrs.setOutlineOpacity(1d);
        attrs.setEnableLighting(false);
        attrs.setEnableAntialiasing(false);
        attrs.setOutlineWidth(2d);
        setAttributes(attrs);

        setValue(AVKey.DISPLAY_NAME, "Day/Night Terminator");
        setValue(AVKey.DISPLAY_ICON, "com/terramenta/globe/images/sun-terminator.png");
        setRadius(Earth.WGS84_EQUATORIAL_RADIUS * Angle.POS90.getRadians());
    }

}
