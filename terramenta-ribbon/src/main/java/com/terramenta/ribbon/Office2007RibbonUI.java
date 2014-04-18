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

import com.terramenta.ribbon.api.ResizableIcons;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.internal.ui.common.BasicCommandButtonUI;

import org.pushingpixels.flamingo.internal.ui.ribbon.BasicRibbonUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonTaskToggleButton;
import org.pushingpixels.flamingo.internal.ui.ribbon.RibbonBandUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;

/**
 *
 * @author Bruce Schubert <bruce@emxsys.com>
 * @version $Id: EmxsysRibbonUI.java 100 2012-02-29 14:41:51Z bdschubert $
 */
public class Office2007RibbonUI extends BasicRibbonUI {

    JCommandButton minimizeButton;

    public static ComponentUI createUI(JComponent c) {
        return new Office2007RibbonUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
    }

    /**
     * This override injects our custom Ribbon.background, RibbonTabs.foreground and
     * RibbonGroups.background colors into the JRibbon UI components.
     */
    @Override
    protected void installComponents() {
        super.installComponents();

        // BDS: Override the default color of the task pane tabs and the unoccupied region 
        // of the menubar.  The default color was Panel.background.
        // TODO: try injecting a PanelUI delegate into the TaskToggleButtonHostPanel
        TaskToggleButtonsHostPanel tabsPanel = super.taskToggleButtonsScrollablePanel.getView();
        tabsPanel.setBackground(FlamingoUtilities.getColor(Color.lightGray,
                "Ribbon.background", "Panel.background"));
        tabsPanel.setForeground(FlamingoUtilities.getColor(Color.black,
                "RibbonTabs.foreground", "Panel.foreground"));

        // BDS: Override the color of the unoccupied area of the Ribbon Band -- make it the 
        // same color as the inactive Command Buttons (ControlPanel.background).
        // TODO: try injecting a PanelUI delegate into the BandHostPanel
        BandHostPanel groupsPanel = super.bandScrollablePanel.getView();
        groupsPanel.setBackground(FlamingoUtilities.getColor(Color.lightGray,
                "RibbonGroups.background", "ControlPanel.background", "Panel.background"));
    }

    @Override
    protected void syncRibbonState() {
        super.syncRibbonState();
        // Add a minimize button
        this.minimizeButton = new MinimizeButton(this.ribbon);
        this.minimizeButton.setDisplayState(CommandButtonDisplayState.SMALL);
        this.minimizeButton.setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_ONLY);
        this.ribbon.add(this.minimizeButton);
    }

    @Override
    protected void uninstallComponents() {
        super.uninstallComponents();
        // BDS - remove custom minimize button
        if (this.minimizeButton != null) {
            this.ribbon.remove(this.minimizeButton);
        }
    }

    /**
     * We're not using the taskbar within the ribbon area, so we return zero to shift the task
     * buttons upwards into the space normally reserved for the taskbar.
     *
     * @return zero instead of the default value of 22.
     */
    @Override
    public int getTaskbarHeight() {
        return 0;
    }

    @Override
    protected LayoutManager createLayoutManager() {
        return new FileRibbonLayout();
    }

    /**
     * Layout for the ribbon with a dynamically sized application menu button. This is a modified
     * copy of the BasicRibbonUI.RibbonLayout private class. It has been modified to accommodate a
     * dynamically sized application menu button. See the layoutContainer method for modifications.
     *
     * @author Kirill Grouchnikov
     * @author Bruce Schubert
     */
    private class FileRibbonLayout implements LayoutManager {

        /*
         * Copied.
         */
        @Override
        public void addLayoutComponent(String name, Component c) {
        }


        /*
         * Copied.
         */
        @Override
        public void removeLayoutComponent(Component c) {
        }

        /*
         * Copied.
         */
        @Override
        public Dimension preferredLayoutSize(Container c) {
            Insets ins = c.getInsets();
            int maxPrefBandHeight = 0;
            boolean isRibbonMinimized = ribbon.isMinimized();
            if (!isRibbonMinimized) {
                if (ribbon.getTaskCount() > 0) {
                    RibbonTask selectedTask = ribbon.getSelectedTask();
                    for (AbstractRibbonBand<?> ribbonBand : selectedTask.getBands()) {
                        int bandPrefHeight = ribbonBand.getPreferredSize().height;
                        Insets bandInsets = ribbonBand.getInsets();
                        maxPrefBandHeight = Math.max(maxPrefBandHeight,
                                bandPrefHeight + bandInsets.top
                                + bandInsets.bottom);
                    }
                }
            }

            int extraHeight = getTaskToggleButtonHeight();
            if (!isUsingTitlePane()) {
                extraHeight += getTaskbarHeight();
            }
            int prefHeight = maxPrefBandHeight + extraHeight + ins.top
                    + ins.bottom;
            // System.out.println("Ribbon pref = " + prefHeight);
            return new Dimension(c.getWidth(), prefHeight);
        }


        /*
         * Copied.
         */
        @Override
        public Dimension minimumLayoutSize(Container c) {
            // go over all ribbon bands and sum the width
            // of ribbon buttons (of collapsed state)
            Insets ins = c.getInsets();
            int width = 0;
            int maxMinBandHeight = 0;
            int gap = getBandGap();

            int extraHeight = getTaskToggleButtonHeight();
            if (!isUsingTitlePane()) {
                extraHeight += getTaskbarHeight();
            }

            if (ribbon.getTaskCount() > 0) {
                boolean isRibbonMinimized = ribbon.isMinimized();
                // minimum is when all the tasks are collapsed
                RibbonTask selectedTask = ribbon.getSelectedTask();
                for (AbstractRibbonBand ribbonBand : selectedTask.getBands()) {
                    int bandPrefHeight = ribbonBand.getMinimumSize().height;
                    Insets bandInsets = ribbonBand.getInsets();
                    RibbonBandUI bandUI = ribbonBand.getUI();
                    width += bandUI.getPreferredCollapsedWidth();
                    if (!isRibbonMinimized) {
                        maxMinBandHeight = Math.max(maxMinBandHeight,
                                bandPrefHeight + bandInsets.top
                                + bandInsets.bottom);
                    }
                }
                // add inter-band gaps
                width += gap * (selectedTask.getBandCount() - 1);
            } else {
                // fix for issue 44 (empty ribbon)
                width = 50;
            }
            return new Dimension(width, maxMinBandHeight + extraHeight
                    + ins.top + ins.bottom);
        }

        /**
         * Modified. Customized so that the layout adapts to the actual size of the application menu
         * button.
         */
        @Override
        public void layoutContainer(Container c) {
            // System.out.println("Ribbon real = " + c.getHeight());

            Insets ins = c.getInsets();
            int top = c.getY();
            int tabButtonGap = getTabButtonGap();

            boolean ltr = ribbon.getComponentOrientation().isLeftToRight();

            // the top row - task bar components
            int width = c.getWidth();
            int taskbarHeight = getTaskbarHeight();
            int y = ins.top;

            boolean isUsingTitlePane = isUsingTitlePane();
            // handle taskbar only if it is not marked
            if (!isUsingTitlePane) {
                taskBarPanel.removeAll();
                for (Component regComp : ribbon.getTaskbarComponents()) {
                    taskBarPanel.add(regComp);
                }
                // taskbar takes all available width
                taskBarPanel.setBounds(ins.left, ins.top, width - ins.left
                        - ins.right, taskbarHeight);
                y += taskbarHeight;
            } else {
                taskBarPanel.setBounds(0, 0, 0, 0);
            }

            int taskToggleButtonHeight = getTaskToggleButtonHeight();

            int x = ltr ? ins.left : width - ins.right;
            // the application menu button
// BDS      int appMenuButtonSize = taskbarHeight + taskToggleButtonHeight;           
            // TODO: Compute / sync with icon plust text
            int appMenuButtonWidth = applicationMenuButton.getWidth();
            int appMenuButtonHeight = taskToggleButtonHeight; //applicationMenuButton.getHeight();

            if (!isUsingTitlePane) {
                applicationMenuButton.setVisible(ribbon.getApplicationMenu() != null);
                if (ribbon.getApplicationMenu() != null) {
                    if (ltr) {
                        applicationMenuButton.setBounds(x, ins.top,
                                appMenuButtonWidth, appMenuButtonHeight);
// BDS                      applicationMenuButton.setBounds(x, ins.top,
//                                appMenuButtonSize, appMenuButtonSize);
                    } else {
                        applicationMenuButton.setBounds(x - appMenuButtonWidth,
                                ins.top, appMenuButtonWidth, appMenuButtonHeight);
// BDS                       applicationMenuButton.setBounds(x - appMenuButtonSize,
//                                ins.top, appMenuButtonSize, appMenuButtonSize);
                    }
                }
            } else {
                applicationMenuButton.setVisible(false);
            }
            x = ltr ? x + 2 : x - 2;
            if (FlamingoUtilities.getApplicationMenuButton(SwingUtilities.getWindowAncestor(ribbon)) != null) {
                x = ltr ? x + appMenuButtonWidth : x - appMenuButtonWidth;
//BDS                x = ltr ? x + appMenuButtonSize : x - appMenuButtonSize;
            }

            // the help button
            if (helpButton != null) {
                Dimension preferred = helpButton.getPreferredSize();
                if (ltr) {
                    helpButton.setBounds(width - ins.right - preferred.width,
                            y, preferred.width, preferred.height);
                } else {
                    helpButton.setBounds(ins.left, y, preferred.width,
                            preferred.height);
                }
            }
// BDS - Added minimize button
            // the minimize button - only valid for left-to-right
            if (minimizeButton != null && ltr) {
                Dimension preferred = minimizeButton.getPreferredSize();
                int buttonX = (helpButton != null)
                        ? helpButton.getX() - preferred.width
                        : width - ins.right - preferred.width;
                minimizeButton.setBounds(buttonX, y,
                        preferred.width, preferred.height - ins.bottom);
            }

            // task buttons
            if (ltr) {
                int buttonX = minimizeButton != null
                        ? minimizeButton.getX()
                        : helpButton != null ? helpButton.getX() : 0;
                int taskButtonsWidth = (buttonX > 0)
                        ? (buttonX - tabButtonGap - x)
                        : (c.getWidth() - ins.right - x);
                taskToggleButtonsScrollablePanel.setBounds(x, y,
                        taskButtonsWidth, taskToggleButtonHeight);
            } else {
                int taskButtonsWidth = (helpButton != null)
                        ? (x - tabButtonGap - helpButton.getX() - helpButton.getWidth())
                        : (x - ins.left);
                taskToggleButtonsScrollablePanel.setBounds(
                        x - taskButtonsWidth, y, taskButtonsWidth,
                        taskToggleButtonHeight);
            }

            TaskToggleButtonsHostPanel taskToggleButtonsHostPanel = taskToggleButtonsScrollablePanel.getView();
            int taskToggleButtonsHostPanelMinWidth = taskToggleButtonsHostPanel.getMinimumSize().width;
            taskToggleButtonsHostPanel.setPreferredSize(new Dimension(
                    taskToggleButtonsHostPanelMinWidth,
                    taskToggleButtonsScrollablePanel.getBounds().height));
            taskToggleButtonsScrollablePanel.doLayout();

            y += taskToggleButtonHeight;

            int extraHeight = taskToggleButtonHeight;
            if (!isUsingTitlePane) {
                extraHeight += taskbarHeight;
            }

            if (bandScrollablePanel.getParent() == ribbon) {
                if (!ribbon.isMinimized() && (ribbon.getTaskCount() > 0)) {
                    // y += ins.top;
                    Insets bandInsets = (ribbon.getSelectedTask().getBandCount() == 0) ? new Insets(0, 0, 0, 0)
                            : ribbon.getSelectedTask().getBand(0).getInsets();
                    bandScrollablePanel.setBounds(1 + ins.left, y
                            + bandInsets.top, c.getWidth() - 2 * ins.left - 2
                            * ins.right - 1, c.getHeight() - extraHeight
                            - ins.top - ins.bottom - bandInsets.top
                            - bandInsets.bottom);
                    // System.out.println("Scrollable : "
                    // + bandScrollablePanel.getBounds());
                    BandHostPanel bandHostPanel = bandScrollablePanel.getView();
                    int bandHostPanelMinWidth = bandHostPanel.getMinimumSize().width;
                    bandHostPanel.setPreferredSize(new Dimension(
                            bandHostPanelMinWidth, bandScrollablePanel.getBounds().height));
                    bandScrollablePanel.doLayout();
                    bandHostPanel.doLayout();
                } else {
                    bandScrollablePanel.setBounds(0, 0, 0, 0);
                }
            }
        }
    }

    /**
     * We override to inject our Ribbon.background color.
     *
     * @param g
     */
    @Override
    protected void paintBackground(Graphics g) {
        //super.paintBackground(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setColor(FlamingoUtilities.getColor(Color.lightGray, "Ribbon.background", "Panel.background"));
        g2d.fillRect(0, 0, this.ribbon.getWidth(), this.ribbon.getHeight());
        g2d.dispose();
    }

    @Override
    protected void paintTaskArea(Graphics g, int x, int y, int width, int height) {
        //super.paintTaskArea(g, x, y, width, height);
        if (ribbon.getTaskCount() == 0) {
            return;
        }

        JRibbonTaskToggleButton selectedTaskButton = this.taskToggleButtons.get(this.ribbon.getSelectedTask());
        Rectangle selectedTaskButtonBounds = selectedTaskButton.getBounds();
        Point converted = SwingUtilities.convertPoint(selectedTaskButton.getParent(), selectedTaskButtonBounds.getLocation(),
                this.ribbon);
        // System.out.println("Painted " + selectedTaskButtonBounds.x + "->" +
        // converted.x);
        Rectangle taskToggleButtonsViewportBounds = taskToggleButtonsScrollablePanel.getView().getParent().getBounds();
        taskToggleButtonsViewportBounds.setLocation(SwingUtilities.convertPoint(taskToggleButtonsScrollablePanel,
                taskToggleButtonsViewportBounds.getLocation(),
                this.ribbon));
        int startSelectedX = Math.max(converted.x + 1,
                (int) taskToggleButtonsViewportBounds.getMinX());
        startSelectedX = Math.min(startSelectedX,
                (int) taskToggleButtonsViewportBounds.getMaxX());
        int endSelectedX = Math.min(converted.x
                + selectedTaskButtonBounds.width - 1,
                (int) taskToggleButtonsViewportBounds.getMaxX());
        endSelectedX = Math.max(endSelectedX,
                (int) taskToggleButtonsViewportBounds.getMinX());
        Shape outerContour = FlamingoUtilities.getRibbonBorderOutline(x + 1, x
                + width - 3, startSelectedX, endSelectedX, converted.y, y, y
                + height, 2);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(FlamingoUtilities.getBorderColor());
        g2d.draw(outerContour);

        // check whether the currently selected task is a contextual task
        RibbonTask selected = this.ribbon.getSelectedTask();
        RibbonContextualTaskGroup contextualGroup = selected.getContextualGroup();
        if (contextualGroup != null) {
            // paint a small gradient directly below the task area
            Insets ins = this.ribbon.getInsets();
            int topY = ins.top + getTaskbarHeight();
            int bottomY = topY + 5;
            Color hueColor = contextualGroup.getHueColor();
            Paint paint = new GradientPaint(0, topY, FlamingoUtilities.getAlphaColor(hueColor,
                    (int) (255 * RibbonContextualTaskGroup.HUE_ALPHA)),
                    0, bottomY, FlamingoUtilities.getAlphaColor(hueColor, 0));
            g2d.setPaint(paint);
            g2d.clip(outerContour);
            g2d.fillRect(0, topY, width, bottomY - topY + 1);
        }

        g2d.dispose();
    }

    /**
     * This class creates a Minimize/Maximize with a custom UI.
     */
    public static class MinimizeButton extends JCommandButton {

        final ResizableIcon minimizeIcon = ResizableIcons.fromResource("com/terramenta/ribbon/images/minimize.png");
        final ResizableIcon maximizeIcon = ResizableIcons.fromResource("com/terramenta/ribbon/images/maximize.png");
        final JRibbon ribbon;

        PropertyChangeListener pcl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("minimized")) {
                    setIcon((boolean) (evt.getNewValue()) ? maximizeIcon : minimizeIcon);
                }
            }
        };

        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ribbon.setMinimized(!ribbon.isMinimized());
            }
        };

        public MinimizeButton(JRibbon ribbon) {
            super((String) null);
            this.ribbon = ribbon;
            this.ribbon.addPropertyChangeListener("minimized", pcl);
            setUI(new MinimizeButtonUI());
            setIcon(this.minimizeIcon);
            getActionModel().addActionListener(al);
        }

        @Override
        public void updateUI() {
            // Do nothing - prevents the UI from being reset to the UIManager settings
            //super.updateUI();
        }

        @Override
        public ResizableIcon getIcon() {
            return this.ribbon.isMinimized() ? this.maximizeIcon : this.minimizeIcon;
        }

    }

    /**
     * Test class for messing around with a custom button UI
     */
    static class MinimizeButtonUI extends BasicCommandButtonUI {

        private static final MinimizeButtonUI buttonUI = new MinimizeButtonUI();

        MinimizeButtonUI() {
        }

        public static ComponentUI createUI(JComponent c) {
            return buttonUI;
        }

        /**
         * Returns the current icon.
         */
        @Override
        protected Icon getIconToPaint() {
            return super.getIconToPaint(); //return this.commandButton.getIcon();
        }

    }

}
