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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import org.openide.util.NbBundle;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.internal.ui.ribbon.appmenu.BasicRibbonApplicationMenuButtonUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.appmenu.JRibbonApplicationMenuButton;
import org.pushingpixels.flamingo.internal.ui.ribbon.appmenu.JRibbonApplicationMenuPopupPanel;
import org.pushingpixels.flamingo.internal.utils.ColorShiftFilter;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;
import org.pushingpixels.flamingo.internal.utils.RenderingUtils;

/**
 * This class presents a "FILE" tab for the application menu instead of a round button with an icon.
 * This implementation conserves vertical screen real estate.
 *
 * @author Bruce Schubert <bruce@emxsys.com>
 */
public class Office2013RibbonApplicationMenuButtonUI extends BasicRibbonApplicationMenuButtonUI {

    private final Color buttonColor = FlamingoUtilities.getColor(new Color(0, 0, 0), "AppButton.background");

    public static ComponentUI createUI(JComponent c) {
        return new Office2013RibbonApplicationMenuButtonUI();
    }

    // TODO: Add custom layout manager to produce the proper sized rectangle for the button
    @Override
    protected void installComponents() {
        super.installComponents();

        // Set the name of the applciation menu button, e.g., "FILE"
        this.commandButton.setText(NbBundle.getMessage(this.getClass(), "CTL_FileTab"));
        this.commandButton.setSize(this.commandButton.getPreferredSize());
        final JRibbonApplicationMenuButton appMenuButton = (JRibbonApplicationMenuButton) this.commandButton;

        appMenuButton.setPopupCallback(new PopupPanelCallback() {

            @Override
            public JPopupPanel getPopupPanel(final JCommandButton commandButton) {
                if (appMenuButton.getParent() instanceof JRibbon) {
                    final JRibbon ribbon = (JRibbon) appMenuButton.getParent();
                    RibbonApplicationMenu ribbonMenu = ribbon.getApplicationMenu();
                    final JRibbonApplicationMenuPopupPanel menuPopupPanel = new JRibbonApplicationMenuPopupPanel(
                            appMenuButton, ribbonMenu);
                    menuPopupPanel.applyComponentOrientation(appMenuButton.getComponentOrientation());
                    menuPopupPanel.setCustomizer(new JPopupPanel.PopupPanelCustomizer() {

                        @Override
                        public Rectangle getScreenBounds() {
                            boolean ltr = commandButton.getComponentOrientation().isLeftToRight();

                            int pw = menuPopupPanel.getPreferredSize().width;
                            int x = ltr ? ribbon.getLocationOnScreen().x
                                    : ribbon.getLocationOnScreen().x
                                    + ribbon.getWidth() - pw;
                            int y = commandButton.getLocationOnScreen().y
                                    + commandButton.getSize().height - 1; // / 2 + 2;

                            Rectangle scrBounds = commandButton.getGraphicsConfiguration().getBounds();
                            if ((x + pw) > (scrBounds.x + scrBounds.width)) {
                                x = scrBounds.x + scrBounds.width - pw;
                            }
                            int ph = menuPopupPanel.getPreferredSize().height;
                            if ((y + ph) > (scrBounds.y + scrBounds.height)) {
                                y = scrBounds.y + scrBounds.height - ph;
                            }

                            return new Rectangle(
                                    x,
                                    y,
                                    menuPopupPanel.getPreferredSize().width,
                                    menuPopupPanel.getPreferredSize().height);
                        }
                    });
                    return menuPopupPanel;
                }
                return null;
            }
        });
    }

    @Override
    protected void installDefaults() {
        // Duplicate the look/feel of task pane buttons
        super.installDefaults();

        this.commandButton.setFont(FlamingoUtilities.getFont(null,
                "Ribbon.font", "Button.font", "Panel.font")); //NOI18N

        Border toInstall = UIManager.getBorder("RibbonTaskToggleButton.border"); //NOI18N
        if (toInstall == null) {
            toInstall = new BorderUIResource.EmptyBorderUIResource(1, 12, 1, 12);
        }
        this.commandButton.setBorder(toInstall);
        this.commandButton.setFlat(true);
        this.commandButton.setOpaque(false);

        this.rendererButton.setBackground(buttonColor);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2d = (Graphics2D) g.create();
        RenderingUtils.installDesktopHints(g2d);
        this.layoutInfo = this.layoutManager.getLayoutInfo(this.commandButton, g);
        this.paintButtonBackground(g2d, new Rectangle(0, 0, c.getWidth(), c.getHeight() + 10));
        this.paintText(g2d);
        g2d.dispose();
    }

    protected void paintText(Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        //String toPaint = this.applicationMenuButton.getText();
        String toPaint = org.openide.awt.Actions.cutAmpersand(this.applicationMenuButton.getText());

        // compute the insets
        int fullInsets = this.applicationMenuButton.getInsets().left;
        int pw = this.getPreferredSize(this.applicationMenuButton).width;
        int mw = this.getMinimumSize(this.applicationMenuButton).width;
        int w = this.applicationMenuButton.getWidth();
        int h = this.applicationMenuButton.getHeight();
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
        g.setColor(FlamingoUtilities.getColor(Color.white, "AppButton.foreground"));
        BasicGraphicsUtils.drawString(g, toPaint, -1, textRect.x, textRect.y
                + fm.getAscent());
    }

    /**
     * Paints the FILE button background. Copied from BasicRibbonTaskToggleButtonUI.
     *
     * @param graphics Graphics context.
     * @param toFill Rectangle to fill.
     */
    @Override
    protected void paintButtonBackground(Graphics graphics, Rectangle toFill) {
        JRibbon ribbon = (JRibbon) SwingUtilities.getAncestorOfClass(
                JRibbon.class, this.applicationMenuButton);

        this.buttonRendererPane.setBounds(toFill.x, toFill.y, toFill.width, toFill.height);
        ButtonModel model = this.rendererButton.getModel();
        model.setEnabled(true);
        model.setSelected(this.applicationMenuButton.getPopupModel().isSelected());
        model.setRollover(this.applicationMenuButton.getPopupModel().isRollover());
        model.setPressed(this.applicationMenuButton.getPopupModel().isPressed()
                || this.applicationMenuButton.getPopupModel().isPopupShowing());
        model.setArmed(this.applicationMenuButton.getActionModel().isArmed());

        Graphics2D g2d = (Graphics2D) graphics.create();
        g2d.translate(toFill.x, toFill.y);

        // draw to an offscreen image, colorize and draw the colorized image
        final int RADIUS = 0;
        BufferedImage offscreen = FlamingoUtilities.getBlankImage(
                toFill.width, toFill.height);
        Graphics2D offscreenGraphics = offscreen.createGraphics();
        Shape clip = g2d.getClip();
        offscreenGraphics.clip(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                toFill.width,
                toFill.height, RADIUS));
        this.buttonRendererPane.paintComponent(offscreenGraphics,
                this.rendererButton, this.commandButton,
                toFill.x - toFill.width / 2,
                toFill.y - toFill.height / 2,
                2 * toFill.width,
                2 * toFill.height, true);
        offscreenGraphics.setColor(buttonColor);
        offscreenGraphics.setClip(clip);
        offscreenGraphics.draw(FlamingoUtilities.getRibbonTaskToggleButtonOutline(
                toFill.width,
                toFill.height + 1, RADIUS));
        offscreenGraphics.dispose();

        if (model.isRollover()) {
            ColorShiftFilter filter = new ColorShiftFilter(buttonColor, 0.35);
            BufferedImage colorized = filter.filter(offscreen, null);
            g2d.drawImage(colorized, 0, 0, null);
        } else {
            ColorShiftFilter filter = new ColorShiftFilter(buttonColor, 0.70);
            BufferedImage colorized = filter.filter(offscreen, null);
            g2d.drawImage(colorized, 0, 0, null);
        }
        g2d.dispose();
    }

    /**
     * Copied from BasicRibbonTaskToggleButtonUI.
     *
     * @return the FILE tab size
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        JRibbonApplicationMenuButton b = (JRibbonApplicationMenuButton) c;

        Icon icon = b.getIcon();
        String text = b.getText();

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

    /**
     * Copied from BasicRibbonTaskToggleButtonUI.
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        JRibbonApplicationMenuButton b = (JRibbonApplicationMenuButton) c;

        Icon icon = b.getIcon();
        String text = "Www"; //NOI18N

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
        r.width += 4;
        r.height += insets.top + insets.bottom;

        return r.getSize();
    }
}
