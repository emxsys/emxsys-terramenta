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

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwindx.examples.util.SectorSelector;
import java.awt.Color;

/**
 *
 * @author heidtmare
 */
public class BoxSelector extends SectorSelector {

    /**
     *
     * @param worldWindow
     */
    public BoxSelector(WorldWindow worldWindow) {
        super(worldWindow);
        this.setInteriorColor(new Color(1f, 1f, 1f, 0f));
        this.setBorderColor(new Color(1f, 1f, 0f, 0.5f));
        this.setBorderWidth(2);
        this.getShape().setValue(AVKey.DISPLAY_NAME, "Bounding Box");
        this.getShape().setHighlightAttributes(getShape().getAttributes());
    }

    /**
     *
     */
    public void save() {
        this.setCursor(null);
        this.getWwd().removeRenderingListener(this);
        this.getWwd().removeSelectListener(this);
        this.getWwd().getInputHandler().removeMouseListener(this);
        this.getWwd().getInputHandler().removeMouseMotionListener(this);
    }
}
