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
import javax.swing.AbstractButton;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalButtonUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicRibbonBandUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;


/**
 * This class provides a Windows compliant band that can paint bands without titles. Per Windows UI
 * guidelines, bands with only button are not supposed to show the title.
 *
 * @author Bruce Schubert
 */
public class Office2007RibbonBandUI extends BasicRibbonBandUI
{

    /**
     * Factory for creating a custom RibbonBand.
     *
     * @param c ignored
     * @return A RibbonBand that doesn't paint a blank title.
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new Office2007RibbonBandUI();
    }


    /**
     * Override establishes the ribbonBand foreground color.
     */
    @Override
    protected void installDefaults()
    {
        super.installDefaults();

        // BDS - looking for custom theme element: RibbonBand.foreground
        super.ribbonBand.setForeground(FlamingoUtilities.getColor(
            Color.black, "RibbonBand.foreground", "Panel.foreground"));
    }


    /**
     * This override accommodates empty titles.
     *
     * @param g
     * @param titleRectangle
     * @param title
     */
    @Override
    protected void paintBandTitle(Graphics g, Rectangle titleRectangle, String title)
    {
        if (title == null || title.isEmpty())
        {
            return;
        }
        super.paintBandTitle(g, titleRectangle, title);
    }


    /**
     * This override accommodates empty titles and draws a custom background.
     *
     */
    @Override
    protected void paintBandTitleBackground(Graphics g, Rectangle titleRectangle, String title)
    {
        if (title == null || title.isEmpty())
        {
            return;
        }
        // BDS - super.paintBandTitleBackground(g, titleRectangle, title);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver.derive(0.7f + 0.3f * this.rolloverAmount));

        // BDS - Using our customized renderSurface method instead of FlamingoUtilities.renderSurface
        renderBand(g2d, this.ribbonBand, titleRectangle,
            this.rolloverAmount > 0.0f, true, false);

        g2d.dispose();

    }


    /**
     * Copied from FlamingoUtilities.renderSurface and modified to use the EmxsysButton instead of a
     * JButton so that we can override the default LAF for the button.
     */
    public static void renderBand(Graphics g, Container c, Rectangle rect,
        boolean toSimulateRollover, boolean hasTopBorder, boolean hasBottomBorder)
    {
        CellRendererPane buttonRendererPane = new CellRendererPane();

        //JButton rendererButton = new JButton("");
        JButton rendererButton = new EmxsysBandButton();

        rendererButton.getModel().setRollover(toSimulateRollover);

        buttonRendererPane.setBounds(rect.x, rect.y, rect.width, rect.height);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.clipRect(rect.x, rect.y, rect.width, rect.height);
        buttonRendererPane.paintComponent(g2d, rendererButton, c,
            rect.x - rect.width / 2, rect.y - rect.height / 2,
            2 * rect.width, 2 * rect.height, true);

        g2d.setColor(FlamingoUtilities.getBorderColor());
        if (hasTopBorder)
        {
            g2d.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
        }
        if (hasBottomBorder)
        {
            g2d.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width
                - 1, rect.y + rect.height - 1);
        }
        g2d.dispose();
    }


    /**
     * This class creates a JButton with a custom UI.
     */
    public static class EmxsysBandButton extends JButton
    {

        public EmxsysBandButton()
        {
            setUI(new EmxsysBandButtonUI());
        }


        @Override
        public void updateUI()
        {
            // Do nothing - prevents the UI from being reset to the UIManager settings
            //super.updateUI();
        }
    }


    /**
     * Test class for messing around with a custom button UI
     */
    public static class EmxsysBandButtonUI extends MetalButtonUI
    {

        private static final EmxsysBandButtonUI buttonUI = new EmxsysBandButtonUI();


        EmxsysBandButtonUI()
        {
        }


        public static ComponentUI createUI(JComponent c)
        {
            return buttonUI;
        }


        @Override
        public void paint(Graphics g, JComponent c)
        {
            // BDS 
            // TODO: load these colors from the themes
            final Color color1 = FlamingoUtilities.getColor(Color.darkGray, "RibbonBand.titleBackground");
            final Color color2 = Color.white;

            Graphics2D g2D = (Graphics2D) g;
            GradientPaint gradient1 = new GradientPaint(
                0.0F, c.getHeight() / 2, color1,
                0.0F, 0.0F, color2);
            Rectangle rec1 = new Rectangle(0, 0, c.getWidth(), c.getHeight());
            g2D.setPaint(gradient1);
            g2D.fill(rec1);

        }
    }
}
