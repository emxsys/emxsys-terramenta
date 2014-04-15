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
package com.terramenta.globe.layers;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Earth.BMNGOneImage;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.Logging;

/**
 *
 * @author Chris.Heidt
 */
public class CustomBMNGOneImage extends BMNGOneImage {

    public static final String DEFAULT_IMAGE_PATH = "images/BMNG_world.topo.bathy.200405.3.2048x1024.jpg";
    protected String imagePath;

    public CustomBMNGOneImage() {
        this.setName(Logging.getMessage("layers.Earth.BlueMarbleOneImageLayer.Name"));
        this.setImagePath(DEFAULT_IMAGE_PATH);
        // Disable picking for the layer because it covers the full sphere and will override a terrain pick.
        this.setPickEnabled(false);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.clearRenderables();
        this.addRenderable(new SurfaceImage(imagePath, Sector.FULL_SPHERE));
    }

    @Override
    public String toString() {
        return Logging.getMessage("layers.Earth.BlueMarbleOneImageLayer.Name");
    }
}
