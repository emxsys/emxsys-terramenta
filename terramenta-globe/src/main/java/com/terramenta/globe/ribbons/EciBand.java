/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import org.pushingpixels.flamingo.api.common.CommandToggleButtonGroup;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
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
    private static final ActionListener eciListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            prefs.putBoolean("options.globe.isECI", e.getActionCommand().equals("ECI"));
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
        JCommandToggleButton eciBtn = new JCommandToggleButton("ECI", ResizableIcons.fromResource("com/terramenta/globe/images/globeECI.png"));
        eciBtn.getActionModel().setSelected(isEci);
        eciBtn.getActionModel().setActionCommand("ECI");
        eciBtn.addActionListener(eciListener);

        JCommandToggleButton ecefBtn = new JCommandToggleButton("ECEF", ResizableIcons.fromResource("com/terramenta/globe/images/globeECEF.png"));
        ecefBtn.getActionModel().setSelected(!isEci);
        ecefBtn.getActionModel().setActionCommand("ECEF");
        ecefBtn.addActionListener(eciListener);

        CommandToggleButtonGroup perspectiveGroup = new CommandToggleButtonGroup();
        perspectiveGroup.add(eciBtn);
        perspectiveGroup.add(ecefBtn);

        //layout
//        startGroup();
        addCommandButton(eciBtn, RibbonElementPriority.MEDIUM);
        addCommandButton(ecefBtn, RibbonElementPriority.MEDIUM);
    }
}
