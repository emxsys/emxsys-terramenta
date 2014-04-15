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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

/**
 *
 * @author Chris.Heidt
 */
public class EciBand extends JRibbonBand {

    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    private static final ResizableIcon[] icons = new ResizableIcon[]{
        ResizableIcons.fromResource("com/terramenta/globe/images/perspective-eci.png"),
        ResizableIcons.fromResource("com/terramenta/globe/images/perspective-ecef.png")
    };

    private final JCommandButton perspectiveButton = new JCommandButton(icons[0]);
    private final ActionListener eciListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean isEci = e.getActionCommand().equals("ECI");
            prefs.putBoolean("options.globe.isECI", isEci);

            perspectiveButton.getActionModel().setActionCommand(isEci ? "ECEF" : "ECI");
            perspectiveButton.setIcon(isEci ? icons[0] : icons[1]);
        }
    };

    public EciBand() {
        super("Perspective", null);

        setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(
                new CoreRibbonResizePolicies.None(getControlPanel()),
                new CoreRibbonResizePolicies.Mirror(getControlPanel()),
                new CoreRibbonResizePolicies.High2Mid(getControlPanel()),
                new CoreRibbonResizePolicies.Mid2Low(getControlPanel()),
                new CoreRibbonResizePolicies.High2Low(getControlPanel())));

        setPreferredSize(new Dimension(40, 60));

        boolean isEci = prefs.getBoolean("options.globe.isECI", false);
        perspectiveButton.setIcon(isEci ? icons[0] : icons[1]);
        perspectiveButton.getActionModel().setActionCommand(isEci ? "ECEF" : "ECI");
        perspectiveButton.addActionListener(eciListener);
        addCommandButton(perspectiveButton, RibbonElementPriority.MEDIUM);
    }
}
