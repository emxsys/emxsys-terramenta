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
import java.awt.Dimension;
import java.util.Arrays;
import java.util.prefs.Preferences;
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

    private static final ResizableIcon ICON_ECI = ResizableIcons.fromResource("com/terramenta/globe/ribbons/frame-eci.png");
    private static final ResizableIcon ICON_ECEF = ResizableIcons.fromResource("com/terramenta/globe/ribbons/frame-ecef.png");
    private static final ResizableIcon ICON_2D = ResizableIcons.fromResource("com/terramenta/globe/ribbons/dimensions-2d.png");
    private static final ResizableIcon ICON_3D = ResizableIcons.fromResource("com/terramenta/globe/ribbons/dimensions-3d.png");

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
         * Perspective button
         */
        boolean isEci = prefs.getBoolean("options.globe.isECI", false);
        JCommandButton frameBtn = new JCommandButton(ICON_ECI);
        frameBtn.setIcon(isEci ? ICON_ECEF : ICON_ECI);
        frameBtn.setActionRichTooltip(new RichTooltip("Coordinate Frame", "Switch to " + (isEci ? "ECEF" : "ECI")));
        frameBtn.getActionModel().setActionCommand(isEci ? "ECEF" : "ECI");
        frameBtn.addActionListener(e -> {
            boolean eci = e.getActionCommand().equals("ECI");
            prefs.putBoolean("options.globe.isECI", eci);
            frameBtn.getActionModel().setActionCommand(eci ? "ECEF" : "ECI");
            frameBtn.setIcon(eci ? ICON_ECEF : ICON_ECI);
            frameBtn.setActionRichTooltip(new RichTooltip("Coordinate Frame", "Switch to " + (isEci ? "ECEF" : "ECI")));
        });
        addCommandButton(frameBtn, RibbonElementPriority.MEDIUM);

        /**
         * Dimension button
         */
        boolean isFlat = prefs.getBoolean("options.globe.isFlat", false);
        JCommandButton dimensionsBtn = new JCommandButton(ICON_2D);
        dimensionsBtn.setIcon(isFlat ? ICON_3D : ICON_2D);
        dimensionsBtn.setActionRichTooltip(new RichTooltip("Display Dimensions", "Switch to " + (isFlat ? "3D" : "2D")));
        dimensionsBtn.getActionModel().setActionCommand(isFlat ? "3D" : "2D");
        dimensionsBtn.addActionListener(e -> {
            boolean flat = e.getActionCommand().equals("2D");
            prefs.putBoolean("options.globe.isFlat", flat);
            dimensionsBtn.getActionModel().setActionCommand(flat ? "3D" : "2D");
            dimensionsBtn.setIcon(flat ? ICON_3D : ICON_2D);
            dimensionsBtn.setActionRichTooltip(new RichTooltip("Display Dimensions", "Switch to " + (flat ? "3D" : "2D")));
        });
        addCommandButton(dimensionsBtn, RibbonElementPriority.MEDIUM);
    }
}
