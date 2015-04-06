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
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.terrain.Tessellator;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class TessellatorRenderable extends AVListImpl implements Renderable {

    private final SunTessellator sunTessellator = new SunTessellator();
    private Tessellator originalTessellator;
    private boolean visible;
    private Globe globeRef;

    public TessellatorRenderable() {
        setValue(AVKey.DISPLAY_NAME, "Solar Shading");
        setValue(AVKey.DISPLAY_ICON, "com/terramenta/globe/solar/sun-shading.png");
    }

    public SunTessellator getSunTessellator() {
        return sunTessellator;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;

        if (globeRef == null) {
            return;
        }

        //swap tessellators
        if (visible) {
            originalTessellator = globeRef.getTessellator();
            globeRef.setTessellator(sunTessellator);
        } else if (originalTessellator != null) {
            globeRef.setTessellator(originalTessellator);
        }
    }

    @Override
    public void render(DrawContext dc) {
        if (this.globeRef == null) {
            this.globeRef = dc.getGlobe();
        }
    }
}
