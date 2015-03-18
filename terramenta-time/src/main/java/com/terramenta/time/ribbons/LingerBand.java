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

import com.terramenta.time.actions.TimeActionController;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;

/**
 * Ribbon band for controlling the linger period for the display of timestamped renderables.
 *
 * @author Chris.Heidt
 */
@Messages({
    "LBL_TimeLingerAction=Linger Period",
    "HINT_TimeLingerAction=Adjust Linger Time"
})
public class LingerBand extends JFlowRibbonBand implements ActionListener, ChangeListener {

    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private final JSlider slider;

    /**
     *
     */
    public LingerBand() {
        super(Bundle.LBL_TimeLingerAction(), null);

        setPreferredSize(new Dimension(120, 60));

        slider = new JSlider(JSlider.HORIZONTAL, 1, 50, 50);
        slider.setMaximumSize(new Dimension(50, 32));
        slider.setPreferredSize(new Dimension(50, 32));
        slider.setMinimumSize(new Dimension(50, 32));
//        slider.setToolTipText(Bundle.HINT_TimeLingerAction());
        slider.addChangeListener(this);

        JButton never = new JButton(ImageUtilities.loadImageIcon("com/terramenta/time/images/linger-min24.png", false));
        never.setPreferredSize(new Dimension(32, 32));
        never.setActionCommand("NEVER");
        never.addActionListener(this);

        JButton always = new JButton(ImageUtilities.loadImageIcon("com/terramenta/time/images/linger-max24.png", false));
        always.setPreferredSize(new Dimension(32, 32));
        always.setActionCommand("ALWAYS");
        always.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setBackground(this.getBackground());
        panel.add(never);
        panel.add(slider);
        panel.add(always);

        this.addFlowComponent(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("NEVER")) {
            slider.setValue(1);
        } else if (e.getActionCommand().equals("ALWAYS")) {
            slider.setValue(50);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        int linger = 0;
        int multiplier = (int) source.getValue();
        if (multiplier < 50) {
            linger = tac.getStepIncrement() * multiplier;
        }
        tac.setLingerDuration(linger);
        tac.step(0);//force refresh
    }
}
