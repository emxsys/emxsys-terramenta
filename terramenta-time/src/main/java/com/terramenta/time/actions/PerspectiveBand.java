/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

//import com.terramenta.globe.options.GlobeOptions;
import com.terramenta.ribbon.api.ResizableIcons;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
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
public class PerspectiveBand extends JRibbonBand {

    //private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private static final ActionListener speedListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            tac.setStepIncrement(AnimationSpeed.valueOf(e.getActionCommand()).getMilliseconds());
        }
    };
    private static final ActionListener eciListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //prefs.putBoolean("options.globe.isECI", e.getActionCommand().equals("ECI"));
        }
    };

    public PerspectiveBand() {
        super("Perspective", ResizableIcons.fromResource("images/controlSpeedSlow.png"));

        setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(
                new CoreRibbonResizePolicies.None(getControlPanel()),
                new CoreRibbonResizePolicies.Mirror(getControlPanel()),
                new CoreRibbonResizePolicies.High2Mid(getControlPanel()),
                new CoreRibbonResizePolicies.Mid2Low(getControlPanel()),
                new CoreRibbonResizePolicies.High2Low(getControlPanel())));


        setPreferredSize(new Dimension(40, 60));

        JCommandToggleButton slowButton = new JCommandToggleButton("Slow", ResizableIcons.fromResource("images/controlSpeedSlow.png"));
        slowButton.getActionModel().setSelected(true);
        slowButton.getActionModel().setActionCommand("SLOW");
        slowButton.addActionListener(speedListener);

        JCommandToggleButton mediumButton = new JCommandToggleButton("Medium", ResizableIcons.fromResource("images/controlSpeedMedium.png"));
        mediumButton.getActionModel().setActionCommand("MEDIUM");
        mediumButton.addActionListener(speedListener);

        JCommandToggleButton fastButton = new JCommandToggleButton("Fast", ResizableIcons.fromResource("images/controlSpeedFast.png"));
        fastButton.getActionModel().setActionCommand("FAST");
        fastButton.addActionListener(speedListener);

        CommandToggleButtonGroup speedGroup = new CommandToggleButtonGroup();
        speedGroup.add(slowButton);
        speedGroup.add(mediumButton);
        speedGroup.add(fastButton);

        //boolean isEci = prefs.getBoolean("options.globe.isECI", false);
//        JCommandToggleButton eciBtn = new JCommandToggleButton("ECI", ResizableIcons.fromResource("images/tick.png"));
//        eciBtn.getActionModel().setSelected(isEci);
//        eciBtn.getActionModel().setActionCommand("ECI");
//        eciBtn.addActionListener(eciListener);
//
//        JCommandToggleButton ecefBtn = new JCommandToggleButton("ECEF", ResizableIcons.fromResource("images/tick.png"));
//        ecefBtn.getActionModel().setSelected(!isEci);
//        ecefBtn.getActionModel().setActionCommand("ECEF");
//        ecefBtn.addActionListener(eciListener);

//        CommandToggleButtonGroup perspectiveGroup = new CommandToggleButtonGroup();
//        perspectiveGroup.add(eciBtn);
//        perspectiveGroup.add(ecefBtn);

        //layout
        addCommandButton(slowButton, RibbonElementPriority.MEDIUM);
        addCommandButton(mediumButton, RibbonElementPriority.MEDIUM);
        addCommandButton(fastButton, RibbonElementPriority.MEDIUM);
//        startGroup();
//        addCommandButton(eciBtn, RibbonElementPriority.MEDIUM);
//        addCommandButton(ecefBtn, RibbonElementPriority.MEDIUM);
    }
}
