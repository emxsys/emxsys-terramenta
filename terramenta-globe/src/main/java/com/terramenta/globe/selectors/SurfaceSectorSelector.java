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
package com.terramenta.globe.selectors;

import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceSector;
import java.awt.Color;

/**
 *
 * @author heidtmare
 */
public class SurfaceSectorSelector extends Selector<SurfaceSector> {

    private static ShapeAttributes attr;
    private static ShapeAttributes highattr;

    @Override
    protected SurfaceSector createShape() {
        //lazy load attrs
        if (attr == null) {
            attr = new BasicShapeAttributes();
            attr.setInteriorMaterial(new Material(Color.yellow));
            attr.setInteriorMaterial(new Material(new Color(1f, 1f, 1f, 0f)));
            attr.setInteriorOpacity(0.2);
            attr.setOutlineMaterial(new Material(Color.yellow));
            attr.setOutlineMaterial(new Material(new Color(1f, 1f, 0f, 0.5f)));
            attr.setOutlineOpacity(0.6);
            attr.setOutlineWidth(2);

            highattr = new BasicShapeAttributes();
            highattr.copy(attr);
            highattr.setInteriorOpacity(0.4);
            highattr.setOutlineOpacity(1.0);
        }

        SurfaceSector sector = new SurfaceSector();
        sector.setAttributes(attr);
        sector.setHighlightAttributes(highattr);
        return sector;
    }
}
