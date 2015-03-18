/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.ribbon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import org.pushingpixels.flamingo.internal.ui.common.BasicRichTooltipPanelUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class Office2007RichTooltipPanelUI extends BasicRichTooltipPanelUI {

    public static ComponentUI createUI(JComponent c) {
        return new Office2007RichTooltipPanelUI();
    }

    @Override
    protected void paintBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(FlamingoUtilities.getColor(Color.gray, "RichTooltipPanel.background", "ControlPanel.background", "Panel.background"));
        g2d.fillRect(0, 0, this.richTooltipPanel.getWidth(), this.richTooltipPanel.getHeight());
        g2d.setFont(FlamingoUtilities.getFont(this.richTooltipPanel, "Ribbon.font", "Button.font", "Panel.font"));
        g2d.dispose();
    }
}
