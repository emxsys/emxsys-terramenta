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
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalButtonUI;
import org.pushingpixels.flamingo.api.common.model.ActionButtonModel;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;
import org.pushingpixels.flamingo.internal.ui.common.BasicCommandButtonUI;
import org.pushingpixels.flamingo.internal.ui.common.CommandButtonUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicRibbonTaskToggleButtonUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonTaskToggleButton;
import org.pushingpixels.flamingo.internal.utils.ColorShiftFilter;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;


/**
 * This class draws the task pane "tabs."
 *
 * @author Bruce Schubert <bruce@emxsys.com>
 * @version $Id: EmxsysRibbonTaskToggleButtonUI.java 366 2012-12-05 20:27:56Z bdschubert $
 */
public class Office2007RibbonTaskToggleButtonUI extends BasicRibbonTaskToggleButtonUI
{

    public static ComponentUI createUI(JComponent c)
    {
        return new Office2007RibbonTaskToggleButtonUI();
    }


    /**
     * Override to use our custom Ribbon.font.
     */
    @Override
    protected void installDefaults()
    {
        super.installDefaults();
        Font f = this.commandButton.getFont();
        if (f == null || f instanceof UIResource)
        {
            this.commandButton.setFont(FlamingoUtilities.getFont(null,
                "Ribbon.font", "Button.font", "Panel.font"));
        }

        Border border = this.commandButton.getBorder();
        if (border == null || border instanceof UIResource)
        {
            Border toInstall = UIManager.getBorder("RibbonTaskToggleButton.border");
            if (toInstall == null)
            {
                toInstall = new BorderUIResource.EmptyBorderUIResource(1, 12,
                    1, 12);
            }
            this.commandButton.setBorder(toInstall);
        }

        this.commandButton.setFlat(true);
        this.commandButton.setOpaque(false);
    }


    /**
     * Override to return a toggle button with a custom UI.
     *
     * @return
     */
    @Override
    protected AbstractButton createRendererButton()
    {
        return new EmxsysToggleButton();
        //return new JToggleButton();
    }


    @Override
    protected void paintText(Graphics g)
    {
        g.setColor(FlamingoUtilities.getColor(Color.black, "Panel.foreground"));
        super.paintText(g);
    }


    /**
     * Override changes the task button outline radius from 2 to around 5
     * @param graphics
     * @param toFill 
     */
    @Override
    protected void paintButtonBackground(Graphics graphics, Rectangle toFill)
    {
        JRibbon ribbon = (JRibbon) SwingUtilities.getAncestorOfClass(
            JRibbon.class, this.commandButton);

        this.buttonRendererPane.setBounds(toFill.x, toFill.y, toFill.width,
            toFill.height);
        ButtonModel model = this.rendererButton.getModel();
        model.setEnabled(this.commandButton.isEnabled());
        model.setSelected(false);
        // System.out.println(toggleTabButton.getText() + ":"
        // + toggleTabButton.isSelected());

        // selected task toggle button should not have any background if
        // the ribbon is minimized and it is not shown in a popup
        boolean displayAsSelected = this.commandButton.getActionModel().isSelected();
        model.setRollover(displayAsSelected
            || this.commandButton.getActionModel().isRollover());
        model.setPressed(false);
        if (model.isRollover())
        {
            Graphics2D g2d = (Graphics2D) graphics.create();
            // partial translucency if it is not selected
            if (!this.commandButton.getActionModel().isSelected())
            {
                // BDS made lighter (larger number)
                g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            }
            g2d.translate(toFill.x, toFill.y);

            Color contextualGroupHueColor = ((JRibbonTaskToggleButton) this.commandButton).getContextualGroupHueColor();
            boolean isContextualTask = (contextualGroupHueColor != null);
            if (!isContextualTask)
            {
                Shape clip = g2d.getClip();
                g2d.clip(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                    toFill.width, toFill.height, 5));

                this.buttonRendererPane.paintComponent(g2d,
                    this.rendererButton, this.commandButton,
                    toFill.x - toFill.width / 2, toFill.y - toFill.height / 2,
                    2 * toFill.width, 2 * toFill.height, true);

                g2d.setColor(FlamingoUtilities.getBorderColor().darker());
                g2d.setClip(clip);
                g2d.draw(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                    toFill.width, toFill.height + 1, 5));
            }
            else
            {
                // draw to an offscreen image, colorize and draw the colorized image
                BufferedImage offscreen = FlamingoUtilities.getBlankImage(
                    toFill.width, toFill.height);
                Graphics2D offscreenGraphics = offscreen.createGraphics();
                Shape clip = g2d.getClip();
                offscreenGraphics.clip(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                    toFill.width, toFill.height, 5));

                this.buttonRendererPane.paintComponent(offscreenGraphics,
                    this.rendererButton, this.commandButton,
                    toFill.x - toFill.width / 2, toFill.y - toFill.height / 2,
                    2 * toFill.width, 2 * toFill.height, true);

                offscreenGraphics.setColor(FlamingoUtilities.getBorderColor().darker());
                offscreenGraphics.setClip(clip);
                offscreenGraphics.draw(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                    toFill.width, toFill.height + 1, 5));
                offscreenGraphics.dispose();

                ColorShiftFilter filter = new ColorShiftFilter(
                    contextualGroupHueColor,
                    RibbonContextualTaskGroup.HUE_ALPHA);
                BufferedImage colorized = filter.filter(offscreen, null);
                g2d.drawImage(colorized, 0, 0, null);
            }
            g2d.dispose();
        }
    }


    /**
     * This class creates a JButton with a custom UI.
     */
    public static class EmxsysToggleButton extends JToggleButton
    {

        public EmxsysToggleButton()
        {
            setUI(new EmxsysToggleButtonUI());
            //setUI(new BasicCommandButtonUI());
            // We need to specify a border for our button if we want one to be drawn.
            setBorder(new BasicBorders.ButtonBorder(Color.yellow, Color.darkGray, Color.lightGray, Color.lightGray));
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
    static class EmxsysToggleButtonUI extends MetalButtonUI
    {

        private static final EmxsysToggleButtonUI buttonUI = new EmxsysToggleButtonUI();


        EmxsysToggleButtonUI()
        {
        }


        public static ComponentUI createUI(JComponent c)
        {
            return buttonUI;
        }


        @Override
        public void paint(Graphics g, JComponent c)
        {
            final Color color1 = FlamingoUtilities.getColor(Color.darkGray, "ControlPanel.background");
            final Color color2 = color1.brighter();

            final Color alphaColor = color1;
            final Color color3 = new Color(
                alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), 255);
            final Color color4 = new Color(
                alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue(), 128);

            super.paint(g, c);

            // Top half
            Graphics2D g2D = (Graphics2D) g;
            GradientPaint gradient1 = new GradientPaint(
                0.0F, (float) c.getHeight() / (float) 2, color1, 0.0F, 0.0F, color2);
            Rectangle rec1 = new Rectangle(0, 0, c.getWidth(), c.getHeight() / 2);
            g2D.setPaint(gradient1);
            g2D.fill(rec1);

            // Bottom half
            GradientPaint gradient2 = new GradientPaint(
                0.0F, (float) c.getHeight() / (float) 2, color3, 0.0F, c.getHeight(), color4);
            Rectangle rec2 = new Rectangle(0, c.getHeight() / 2, c.getWidth(), c.getHeight());
            g2D.setPaint(gradient2);
            g2D.fill(rec2);

        }
    }
}
