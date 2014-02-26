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
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalButtonUI;
import org.pushingpixels.flamingo.api.common.CommandButtonLayoutManager;
import org.pushingpixels.flamingo.api.common.CommandButtonLayoutManager.CommandButtonSeparatorOrientation;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.internal.ui.common.BasicCommandButtonUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;


/**
 * Customized button fonts for the command buttons.
 *
 * @author Bruce
 */
public class Office2007CommandButtonUI extends BasicCommandButtonUI
{
    /*
     * (non-Javadoc)
     *
     * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
     */

    public static ComponentUI createUI(JComponent c)
    {
        return new Office2007CommandButtonUI();
    }


    /**
     * We override the base class paint method in order to inject our customized RibbonButton font
     * defined in the branding's themes.xml
     *
     * @param g
     * @param c
     */
    @Override
    public void paint(Graphics g, JComponent c)
    {
        //super.paint(g, c);

        g.setFont(FlamingoUtilities.getFont(commandButton,
            "RibbonButton.font", "Ribbon.font", "Button.font", "Panel.font"));

        this.layoutInfo = this.layoutManager.getLayoutInfo(this.commandButton, g);
        commandButton.putClientProperty("icon.bounds", layoutInfo.iconRect);

        if (this.isPaintingBackground())
        {
            this.paintButtonBackground(g, new Rectangle(0, 0, commandButton.getWidth(), commandButton.getHeight()));
        }
        if (layoutInfo.iconRect != null)
        {
            this.paintButtonIcon(g, layoutInfo.iconRect);
        }
        if (layoutInfo.popupActionRect.getWidth() > 0)
        {
            paintPopupActionIcon(g, layoutInfo.popupActionRect);
        }
        FontMetrics fm = g.getFontMetrics();

        boolean isTextPaintedEnabled = commandButton.isEnabled();
        if (commandButton instanceof JCommandButton)
        {
            JCommandButton jCommandButton = (JCommandButton) commandButton;
            isTextPaintedEnabled = layoutInfo.isTextInActionArea ? jCommandButton.getActionModel().isEnabled()
                : jCommandButton.getPopupModel().isEnabled();
        }

        g.setColor(getForegroundColor(isTextPaintedEnabled));

        if (layoutInfo.textLayoutInfoList != null)
        {
            for (CommandButtonLayoutManager.TextLayoutInfo mainTextLayoutInfo : layoutInfo.textLayoutInfoList)
            {
                if (mainTextLayoutInfo.text != null)
                {
                    BasicGraphicsUtils.drawString(g, mainTextLayoutInfo.text,
                        -1, mainTextLayoutInfo.textRect.x,
                        mainTextLayoutInfo.textRect.y + fm.getAscent());
                }
            }
        }

        if (isTextPaintedEnabled)
        {
            g.setColor(FlamingoUtilities.getColor(Color.gray,
                "Label.disabledForeground"));
        }
        else
        {
            g.setColor(FlamingoUtilities.getColor(Color.gray,
                "Label.disabledForeground").brighter());
        }

        if (layoutInfo.extraTextLayoutInfoList != null)
        {
            for (CommandButtonLayoutManager.TextLayoutInfo extraTextLayoutInfo : layoutInfo.extraTextLayoutInfoList)
            {
                if (extraTextLayoutInfo.text != null)
                {
                    BasicGraphicsUtils.drawString(g, extraTextLayoutInfo.text,
                        -1, extraTextLayoutInfo.textRect.x,
                        extraTextLayoutInfo.textRect.y + fm.getAscent());
                }
            }
        }

        if (this.isPaintingSeparators() && (layoutInfo.separatorArea != null))
        {
            if (layoutInfo.separatorOrientation == CommandButtonSeparatorOrientation.HORIZONTAL)
            {
                this.paintButtonHorizontalSeparator(g, layoutInfo.separatorArea);
            }
            else
            {
                this.paintButtonVerticalSeparator(g, layoutInfo.separatorArea);
            }
        }

    }


    /**
     * We override the base class in order to inject the RibbonButton.foreground color. This color
     * is used to paint the RibbonBand titles and the CommandButtons.
     *
     * @param isTextPaintedEnabled
     * @return
     */
    @Override
    protected Color getForegroundColor(boolean isTextPaintedEnabled)
    {
        //return super.getForegroundColor(isTextPaintedEnabled);
        if (isTextPaintedEnabled)
        {
            return FlamingoUtilities.getColor(Color.black, "RibbonButton.foreground", "Button.foreground");
        }
        else
        {
            return FlamingoUtilities.getColor(Color.gray,
                "Label.disabledForeground");
        }
    }


    @Override
    protected AbstractButton createRendererButton()
    {
        //return super.createRendererButton();
        return new EmxsysButton();
    }


    /**
     * This class creates a JButton with a custom UI.
     */
    public static class EmxsysButton extends JButton
    {

        public EmxsysButton()
        {
            setUI(new EmxsysButtonUI());
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
    static class EmxsysButtonUI extends MetalButtonUI
    {

        private static final EmxsysButtonUI buttonUI = new EmxsysButtonUI();


        EmxsysButtonUI()
        {
        }


        public static ComponentUI createUI(JComponent c)
        {
            return buttonUI;
        }


        
        @Override
        public void paint(Graphics g, JComponent c)
        {
            final Color color1 = Color.orange;
            final Color color2 = Color.yellow;
            
            final Color alphaColor = Color.orange;
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


        @Override
        public void paintButtonPressed(Graphics g, AbstractButton b)
        {
            paintText(g, b, b.getBounds(), b.getText());
            g.setColor(Color.red.brighter());
            g.fillRect(0, 0, b.getSize().width, b.getSize().height);
        }



        @Override
        protected void paintFocus(Graphics g, AbstractButton b,
            Rectangle viewRect, Rectangle textRect, Rectangle iconRect)
        {
        }
    }
}