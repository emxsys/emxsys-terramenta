/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * The contents of this file are subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.ribbon;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import org.pushingpixels.flamingo.internal.ui.common.BasicCommandToggleButtonUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;

/**
 * This class draws a command button that can be toggled on/off.
 *
 * @author Bruce
 */
public class Office2007CommandToggleButtonUI extends BasicCommandToggleButtonUI {

    private static final Logger logger = Logger.getLogger(Office2007CommandToggleButtonUI.class.getName());

    public static ComponentUI createUI(JComponent c) {
        return new Office2007CommandToggleButtonUI();
    }

    /**
     * Creates a new UI delegate.
     */
    public Office2007CommandToggleButtonUI() {
    }

    @Override
    protected void updatePopupActionIcon() {
    }

    @Override
    protected boolean isPaintingSeparators() {
        return false;
    }

    /**
     * We override the base class in order to inject the RibbonButton.foreground color. This color
     * is used to paint the RibbonBand titles and the CommandButtons.
     *
     * @param isTextPaintedEnabled
     * @return RibbonButton.foreground color
     */
    @Override
    protected Color getForegroundColor(boolean isTextPaintedEnabled) {
        //return super.getForegroundColor(isTextPaintedEnabled);
        if (isTextPaintedEnabled) {
            return FlamingoUtilities.getColor(Color.black,
                    "RibbonButton.foreground", "Button.foreground");
        } else {
            return FlamingoUtilities.getColor(Color.gray,
                    "Label.disabledForeground");
        }
    }

    /**
     * Returns a JToggleButton derived class used for rendering this UI component.
     *
     * @return an EmxsysToggleButton
     */
    @Override
    protected AbstractButton createRendererButton() {
        //return super.createRendererButton();
        return new EmxsysToggleButton();
    }

    /**
     * This class creates a JToggleButton with a custom UI.
     */
    public static class EmxsysToggleButton extends JToggleButton {

        public EmxsysToggleButton() {
            setUI(new EmxsysToggleButtonUI());
            // We need to specify a border for our button if we want one to be drawn.
            setBorder(new BasicBorders.ButtonBorder(Color.yellow, Color.darkGray, Color.lightGray, Color.lightGray));
        }

        @Override
        public void updateUI() {
            // Do nothing - prevents the UI from being reset to the UIManager settings
            //super.updateUI();
        }
    }

    /**
     * Test class for messing around with a custom button UI
     */
    static class EmxsysToggleButtonUI extends MetalToggleButtonUI {

        private static final Office2007CommandButtonUI.EmxsysButtonUI buttonUI = new Office2007CommandButtonUI.EmxsysButtonUI();

        EmxsysToggleButtonUI() {
        }

        public static ComponentUI createUI(JComponent c) {
            return buttonUI;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
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
        public void paintButtonPressed(Graphics g, AbstractButton b) {
            paintText(g, b, b.getBounds(), b.getText());
            g.setColor(Color.red.brighter());
            g.fillRect(0, 0, b.getSize().width, b.getSize().height);
        }

        @Override
        protected void paintFocus(Graphics g, AbstractButton b,
                Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        }
    }
}
