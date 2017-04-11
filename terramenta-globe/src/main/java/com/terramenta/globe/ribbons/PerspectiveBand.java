/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.globe.ribbons;

import com.terramenta.globe.options.GlobeOptions;
import com.terramenta.ribbon.api.ResizableIcons;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Arrays;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

/**
 *
 * @author Chris.Heidt
 */
public class PerspectiveBand extends JRibbonBand {

    private static final Image IMAGE_ECI = ImageUtilities.loadImage("com/terramenta/globe/ribbons/frame-eci32.png");
    private static final Image IMAGE_ECEF = ImageUtilities.loadImage("com/terramenta/globe/ribbons/frame-ecef32.png");
    private static final Image IMAGE_2D = ImageUtilities.loadImage("com/terramenta/globe/ribbons/dimensions-2d32.png");
    private static final Image IMAGE_3D = ImageUtilities.loadImage("com/terramenta/globe/ribbons/dimensions-3d32.png");
    private static final Image IMAGE_FULLSCREEN = ImageUtilities.loadImage("com/terramenta/globe/images/toggle-globe32.png");

    private static final ResizableIcon ICON_ECI = ResizableIcons.fromResource("com/terramenta/globe/ribbons/frame-eci.png");
    private static final ResizableIcon ICON_ECEF = ResizableIcons.fromResource("com/terramenta/globe/ribbons/frame-ecef.png");
    private static final ResizableIcon ICON_2D = ResizableIcons.fromResource("com/terramenta/globe/ribbons/dimensions-2d.png");
    private static final ResizableIcon ICON_3D = ResizableIcons.fromResource("com/terramenta/globe/ribbons/dimensions-3d.png");
    private static final ResizableIcon ICON_FULLSCREEN = ResizableIcons.fromResource("com/terramenta/globe/images/toggle-globe.png");

    private Container originalContentPane = null;

    public PerspectiveBand() {
        super("Perspective", null);

        setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(
                new CoreRibbonResizePolicies.None(getControlPanel()),
                new CoreRibbonResizePolicies.Mirror(getControlPanel()),
                new CoreRibbonResizePolicies.High2Mid(getControlPanel()),
                new CoreRibbonResizePolicies.Mid2Low(getControlPanel()),
                new CoreRibbonResizePolicies.High2Low(getControlPanel())));

        setPreferredSize(new Dimension(40, 60));

        Preferences prefs = GlobeOptions.getPreferences();

        /**
         * Coordinate Frame button
         */
        JCommandButton frameBtn = new JCommandButton(ICON_ECI);
        updateFrame(frameBtn, prefs.getBoolean("options.globe.isECI", false));
        frameBtn.addActionListener(e -> {
            boolean isECI = e.getActionCommand().equals("ECI");
            prefs.putBoolean("options.globe.isECI", isECI);
            updateFrame(frameBtn, isECI);
        });
        addCommandButton(frameBtn, RibbonElementPriority.MEDIUM);

        /**
         * Dimension button
         */
        JCommandButton dimensionsBtn = new JCommandButton(ICON_2D);
        updateDimensions(dimensionsBtn, prefs.getBoolean("options.globe.isFlat", false));
        dimensionsBtn.addActionListener(e -> {
            boolean is2D = e.getActionCommand().equals("2D");
            prefs.putBoolean("options.globe.isFlat", is2D);
            updateDimensions(dimensionsBtn, is2D);
        });
        addCommandButton(dimensionsBtn, RibbonElementPriority.MEDIUM);

        /**
         * Fullscreen Globe button
         */
        JCommandButton fullscreenBtn = new JCommandButton(ICON_FULLSCREEN);
        RichTooltip fullscreenBtnTooltip = new RichTooltip("Toggle Full Globe", "Toggles the Globe between Maximized and Minimizes views.");
        fullscreenBtnTooltip.setMainImage(IMAGE_FULLSCREEN);
        fullscreenBtn.setActionRichTooltip(fullscreenBtnTooltip);
        fullscreenBtn.addActionListener(e -> {
            toggleFullscreen();
        });
        addCommandButton(fullscreenBtn, RibbonElementPriority.MEDIUM);
    }

    private static void updateFrame(JCommandButton frameBtn, boolean isEci) {
        RichTooltip frameBtnTooltip = new RichTooltip("Coordinate Frame", "Currently in " + (isEci ? "ECI" : "ECEF"));
        frameBtnTooltip.setMainImage(isEci ? IMAGE_ECI : IMAGE_ECEF);
        frameBtn.setActionRichTooltip(frameBtnTooltip);
        frameBtn.setIcon(isEci ? ICON_ECI : ICON_ECEF);
        frameBtn.getActionModel().setActionCommand(isEci ? "ECEF" : "ECI");
    }

    private static void updateDimensions(JCommandButton dimensionsBtn, boolean is2D) {
        RichTooltip dimensionsBtnTooltip = new RichTooltip("Display Dimensions", "Currently in " + (is2D ? "2D" : "3D"));
        dimensionsBtnTooltip.setMainImage(is2D ? IMAGE_2D : IMAGE_3D);
        dimensionsBtn.setActionRichTooltip(dimensionsBtnTooltip);
        dimensionsBtn.setIcon(is2D ? ICON_2D : ICON_3D);
        dimensionsBtn.getActionModel().setActionCommand(is2D ? "3D" : "2D");
    }

    private void toggleFullscreen() {
        if (null == originalContentPane) {
            TopComponent globeTopComponent = WindowManager.getDefault().findTopComponent("GlobeTopComponent");
            if (globeTopComponent == null || !globeTopComponent.isOpened()) {
                return;
            }

            final JFrame mainWnd = (JFrame) WindowManager.getDefault().getMainWindow();
            originalContentPane = mainWnd.getContentPane();

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(globeTopComponent, BorderLayout.CENTER);
            mainWnd.setContentPane(panel);
            mainWnd.invalidate();
            mainWnd.revalidate();
            mainWnd.repaint();
        } else {
            JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
            frame.setContentPane(originalContentPane);
            originalContentPane = null;
        }

        final TopComponent tc = TopComponent.getRegistry().getActivated();
        if (tc == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            tc.requestFocusInWindow();
        });
    }
}
