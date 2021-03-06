/*
 * Copyright (c) 2014, Bruce Schubert. <bruce@emxsys.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the Emxsys company nor the names of its 
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.terramenta.ribbon;

import java.awt.*;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicRibbonBandUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;

/**
 * This class provides a Microsoft Windows compliant band that can paint bands without titles. Per
 * Windows UI guidelines, bands with only button are not supposed to show the title.
 * <p>
 * @author Bruce Schubert
 */
public class Office2013RibbonBandUI extends BasicRibbonBandUI {

    /**
     * Factory for creating a custom RibbonBand.
     * <p>
     * @param c ignored
     * @return A RibbonBand that doesn't paint a blank title.
     */
    public static ComponentUI createUI(JComponent c) {
        return new Office2013RibbonBandUI();
    }

    /**
     * Override establishes the ribbonBand foreground color.
     */
    @Override
    protected void installDefaults() {
        super.installDefaults();

        this.ribbonBand.setBackground(FlamingoUtilities.getColor(
                Color.lightGray, "ControlPanel.background",	"Panel.background")); 
        
        // BDS - looking for custom theme element: RibbonBand.foreground
        super.ribbonBand.setForeground(FlamingoUtilities.getColor(Color.black,
                "Panel.foreground"));

        // BDS Office 2013: Suppress the border around the band; create a separator instead.
        this.ribbonBand.setBorder(new BorderUIResource(new VerticalSeparatorBorder()));
    }

    /**
     * This override accommodates empty titles.
     */
    @Override
    protected void paintBandTitle(Graphics g, Rectangle titleRectangle, String title) {
        if (title == null || title.isEmpty()) {
            return;
        }
        super.paintBandTitle(g, titleRectangle, title);
    }

    /**
     * This override accommodates empty titles and draws a custom background.
     * <p>
     */
    @Override
    protected void paintBandTitleBackground(Graphics g, Rectangle titleRectangle, String title) {
        if (title == null || title.isEmpty()) {
            return;
        }
        // TODO: figure out the button count, if only one, don't display the title.
            
        // BDS - super.paintBandTitleBackground(g, titleRectangle, title);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver.derive(0.7f + 0.3f * this.rolloverAmount));

        // BDS - Using our customized renderSurface method instead of FlamingoUtilities.renderSurface
        renderBand(g2d, this.ribbonBand, titleRectangle,
                this.rolloverAmount > 0.0f, false, false);

        g2d.dispose();

    }

    /**
     * Copied from FlamingoUtilities.renderSurface and modified to use the EmxsysButton instead of a
     * JButton so that we can override the default LAF for the button.
     */
    public static void renderBand(Graphics g, Container c, Rectangle rect,
            boolean toSimulateRollover, boolean hasTopBorder, boolean hasBottomBorder) {

        CellRendererPane buttonRendererPane = new CellRendererPane();

        // Use JButton's colors
        JButton rendererButton = new JButton("");
        rendererButton.setBackground(FlamingoUtilities.getColor(Color.lightGray, 
                "ControlPanel.background",	"Panel.background")); 
        rendererButton.getModel().setRollover(toSimulateRollover);

        buttonRendererPane.setBounds(rect.x, rect.y, rect.width, rect.height);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.clipRect(rect.x, rect.y, rect.width, rect.height);
        buttonRendererPane.paintComponent(g2d, rendererButton, c,   // uses this.ribbonBand.getColor()
                rect.x - rect.width / 2, rect.y - rect.height / 2,
                2 * rect.width, 2 * rect.height, true);

        g2d.setColor(FlamingoUtilities.getBorderColor());
        if (hasTopBorder) {
            g2d.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
        }
        if (hasBottomBorder) {
            g2d.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width
                    - 1, rect.y + rect.height - 1);
        }
        g2d.dispose();
    }
    
	/**
	 * Right-side only border for the ribbon bands.
	 * 
	 * @author Bruce Schubert
	 */
	protected static class VerticalSeparatorBorder extends EtchedBorder {

		VerticalSeparatorBorder() {
            super();
		}


		@Override
		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
            super.paintBorder(c, g, width-1, y+1, width, height-1);
		}

	}


}
