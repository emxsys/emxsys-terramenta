/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License 1.0 (the "License"). You may not use this file except
 * in compliance with the License. You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0. See the License for the specific
 * language governing permissions and limitations under the License. 
 */
package com.terramenta.ribbon;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicBandControlPanelUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;

/**
 * This class draws the bands (groups) of command buttons within a task pane.
 *
 * @author Bruce Schubert <bruce@emxsys.com>
 * @version $Id: Office2013BandControlPanelUI.java 100 2012-02-29 14:41:51Z bdschubert $
 */
public class Office2013BandControlPanelUI extends BasicBandControlPanelUI {

    public static ComponentUI createUI(JComponent c) {
        return new Office2013BandControlPanelUI();
    }

    /**
     * Overrides the default of 4.
     * @return 0 -> no gap
     */
    @Override
    public int getLayoutGap() {
        return 0;
    }

    /**
     * Installs default parameters on the associated control panel of a ribbon band.
     */
    @Override
    protected void installDefaults() {
        Color bg = this.controlPanel.getBackground();
        if (bg == null || bg instanceof UIResource) {
            this.controlPanel.setBackground(FlamingoUtilities.getColor(Color.lightGray,
                    "ControlPanel.background", "Panel.background"));
        }
        // BDS - Office 2013: no border around the title band
        this.controlPanel.setBorder(new BorderUIResource.EmptyBorderUIResource(1, 2, 1, 2));
    }

    /**
     * Override to paint the band background with a gradient.
     *
     * @param graphics
     * @param toFill
     */
//    @Override
//    protected void paintBandBackground(Graphics graphics, Rectangle toFill) {
//        //super.paintBandBackground(graphics, toFill);
//
//        Graphics2D g2d = (Graphics2D) graphics.create();
//        Color backgroundColor = controlPanel.getBackground();
//        Paint paint = new GradientPaint(0, 0, backgroundColor,
//                0, toFill.height, FlamingoUtilities.getLighterColor(backgroundColor, 0.75));
//        g2d.setPaint(paint);
//        g2d.fillRect(toFill.x, toFill.y, toFill.width, toFill.height);
//        g2d.dispose();
//
//    }

}
