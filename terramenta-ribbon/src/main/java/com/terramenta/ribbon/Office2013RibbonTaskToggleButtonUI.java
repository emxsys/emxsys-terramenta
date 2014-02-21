/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License 1.0 (the "License"). You may not use this file except
 * in compliance with the License. You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0. See the License for the specific
 * language governing permissions and limitations under the License. 
 */
package com.terramenta.ribbon;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import org.pushingpixels.flamingo.api.common.model.ActionButtonModel;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;
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
public class Office2013RibbonTaskToggleButtonUI extends BasicRibbonTaskToggleButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new Office2013RibbonTaskToggleButtonUI();
    }

    /**
     * Override to use our custom Ribbon.font.
     */
    @Override
    protected void installDefaults() {
        super.installDefaults();
        Font f = this.commandButton.getFont();
        if (f == null || f instanceof UIResource) {
            this.commandButton.setFont(FlamingoUtilities.getFont(null,
                    "Ribbon.font", "Button.font", "Panel.font"));
        }

        Border border = this.commandButton.getBorder();
        if (border == null || border instanceof UIResource) {
            Border toInstall = UIManager.getBorder("RibbonTaskToggleButton.border");
            if (toInstall == null) {
                toInstall = new BorderUIResource.EmptyBorderUIResource(1, 12, 1, 12);
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
    protected AbstractButton createRendererButton() {
        return new JToggleButton();
    }

    /**
     * Override to provide rollover color and to paint in UPPERCASE. Copied from base class.
     *
     * @param g
     */
    @Override
    protected void paintText(Graphics g) {

        // TODO: Get colors from UIManager
        ActionButtonModel actionModel = this.commandButton.getActionModel();
        g.setColor(actionModel.isSelected() || actionModel.isRollover() ? Color.blue.brighter() : Color.black); // BDS

        FontMetrics fm = g.getFontMetrics();
        String toPaint = this.commandButton.getText().toUpperCase();    // BDS

        // compute the insets
        int fullInsets = this.commandButton.getInsets().left;
        int pw = this.getPreferredSize(this.commandButton).width;
        int mw = this.getMinimumSize(this.commandButton).width;
        int w = this.commandButton.getWidth();
        int h = this.commandButton.getHeight();
        int insets = fullInsets - (pw - w) * (fullInsets - 2) / (pw - mw);

        // and the text rectangle
        Rectangle textRect = new Rectangle(insets,
                1 + (h - fm.getHeight()) / 2, w - 2 * insets, fm.getHeight());

        // show the first characters that fit into the available text rectangle
        while (true) {
            if (toPaint.length() == 0) {
                break;
            }
            int strWidth = fm.stringWidth(toPaint);
            if (strWidth <= textRect.width) {
                break;
            }
            toPaint = toPaint.substring(0, toPaint.length() - 1);
        }
        BasicGraphicsUtils.drawString(g, toPaint, -1, textRect.x, textRect.y
                + fm.getAscent());
    }

    /**
     * Override changes the task button outline radius to 0 and to render the selected task button
     * with same color as the tabs panel.
     *
     * @param graphics
     * @param toFill
     */
    @Override
    protected void paintButtonBackground(Graphics graphics, Rectangle toFill) {
        JRibbon ribbon = (JRibbon) SwingUtilities.getAncestorOfClass(
                JRibbon.class, this.commandButton);

        this.buttonRendererPane.setBounds(toFill.x, toFill.y, toFill.width, toFill.height);
        ButtonModel model = this.rendererButton.getModel();
        model.setEnabled(this.commandButton.isEnabled());
        model.setSelected(false);
        // System.out.println(toggleTabButton.getText() + ":"
        // + toggleTabButton.isSelected());

        // selected task toggle button should not have any background if
        // the ribbon is minimized and it is not shown in a popup
        if (ribbon.isMinimized()) {
            return;
        }
            
        
        boolean displayAsSelected = this.commandButton.getActionModel().isSelected();
        //model.setRollover(displayAsSelected);
        //in Office2013, background doesn't change on rollover, foreground does.
        //|| this.commandButton.getActionModel().isRollover());
        model.setPressed(false);
        final int radius = 0;
        if (displayAsSelected) {
            Graphics2D g2d = (Graphics2D) graphics.create();
            g2d.translate(toFill.x, toFill.y);

            Color contextualGroupHueColor = ((JRibbonTaskToggleButton) this.commandButton).getContextualGroupHueColor();
            boolean isContextualTask = (contextualGroupHueColor != null);
            if (!isContextualTask) {

                // Allow our tab button to extend below the orginal clip region (into to band)
                Shape clip = g2d.getClip();
                Rectangle tabRect = new Rectangle(toFill.x, toFill.y, toFill.width, toFill.height);
                g2d.setClip(tabRect);
                // Draw the tab
                Color backgroundColor = FlamingoUtilities.getColor(Color.lightGray,
                        "ControlPanel.background", "Panel.background");
                g2d.setColor(backgroundColor);
                g2d.fill(tabRect);
                // Restrict the height of the border to join with corners
                g2d.setClip(clip);
                // Draw the clipped border
                g2d.setColor(FlamingoUtilities.getBorderColor().darker());
                g2d.draw(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                        toFill.width-1, toFill.height, radius));

            } else {
                // draw to an offscreen image, colorize and draw the colorized image
                BufferedImage offscreen = FlamingoUtilities.getBlankImage(
                        toFill.width, toFill.height);
                Graphics2D offscreenGraphics = offscreen.createGraphics();
                Shape clip = g2d.getClip();
                offscreenGraphics.clip(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                        toFill.width, toFill.height, radius));

                this.buttonRendererPane.paintComponent(offscreenGraphics,
                        this.rendererButton, this.commandButton,
                        toFill.x - toFill.width / 2, toFill.y - toFill.height / 2,
                        2 * toFill.width, 2 * toFill.height, true);

                offscreenGraphics.setColor(FlamingoUtilities.getBorderColor().darker());
                offscreenGraphics.setClip(clip);
                offscreenGraphics.draw(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                        toFill.width, toFill.height + 1, radius));
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
     * Override to compute size for UPPERCASE. Copied from base class.
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        JRibbonTaskToggleButton b = (JRibbonTaskToggleButton) c;

        Icon icon = b.getIcon();
        String text = b.getText().toUpperCase();    // BDS - added call to toUpperCase

        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);

        Rectangle iconR = new Rectangle();
        Rectangle textR = new Rectangle();
        Rectangle viewR = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);

        SwingUtilities.layoutCompoundLabel(b, fm, text, icon,
                SwingUtilities.CENTER, b.getHorizontalAlignment(),
                SwingUtilities.CENTER, SwingUtilities.CENTER, viewR, iconR,
                textR, (text == null ? 0 : 6));

        Rectangle r = iconR.union(textR);

        Insets insets = b.getInsets();
        r.width += insets.left + insets.right;
        r.height += insets.top + insets.bottom;

        return r.getSize();
    }
}
