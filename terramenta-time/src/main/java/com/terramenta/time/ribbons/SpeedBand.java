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
package com.terramenta.time.ribbons;

import com.terramenta.ribbon.api.ResizableIcons;
import com.terramenta.time.actions.AnimationSpeed;
import com.terramenta.time.actions.TimeActionController;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import org.openide.util.Lookup;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

/**
 *
 * @author Chris.Heidt
 */
public class SpeedBand extends JRibbonBand {

    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private final JCommandButton speedButton = new JCommandButton((String) null);
    private final ActionListener speedListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            AnimationSpeed animationSpeed = AnimationSpeed.valueOf(e.getActionCommand());
            tac.setStepIncrement(animationSpeed.getMilliseconds());
            updateButton(animationSpeed);
        }
    };

    /**
     *
     */
    public SpeedBand() {
        super("Speed", null);

        setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(
                new CoreRibbonResizePolicies.None(getControlPanel()),
                new CoreRibbonResizePolicies.Mirror(getControlPanel()),
                new CoreRibbonResizePolicies.High2Mid(getControlPanel()),
                new CoreRibbonResizePolicies.Mid2Low(getControlPanel()),
                new CoreRibbonResizePolicies.High2Low(getControlPanel())));

        setPreferredSize(new Dimension(40, 60));

        updateButton(AnimationSpeed.SLOW);
        speedButton.addActionListener(speedListener);
        addCommandButton(speedButton, RibbonElementPriority.MEDIUM);
    }

    private void updateButton(AnimationSpeed animationSpeed) {
        speedButton.setExtraText(animationSpeed.getDescription());
        speedButton.setIcon(ResizableIcons.fromResource(animationSpeed.getIconbase()));

        //advance the action command to next speed
        int nextSpeedOrdinal = animationSpeed.ordinal() + 1;
        AnimationSpeed nextSpeed = (nextSpeedOrdinal < AnimationSpeed.values().length) ? AnimationSpeed.values()[nextSpeedOrdinal] : AnimationSpeed.values()[0];
        speedButton.getActionModel().setActionCommand(nextSpeed.name());
    }
}
