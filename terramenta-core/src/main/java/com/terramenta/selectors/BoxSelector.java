/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.selectors;

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
        this.getShape().setValue(AVKey.DISPLAY_ICON, "images/square.png");
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
