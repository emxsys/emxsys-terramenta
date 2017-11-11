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
package com.terramenta.time.actions;

import com.terramenta.ribbon.RibbonActionReference;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;

/**
 *
 * @author chris.heidt
 */
@Messages({
    "LBL_RibbonTask_Time=Time",
    "LBL_RibbonBand_Controls=Controls"
})
public class TimeActions {

    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);

    /**
     * STEP BACKWARDS
     *
     * @author Chris.Heidt
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStepBackwardAction")
    @ActionRegistration(
            iconBase = "com/terramenta/time/images/time-step-backward.png",
            displayName = "#CTL_TimeStepBackwardAction",
            popupText = "Step backwards one frame in time.")
    @RibbonActionReference(
            path = "Ribbon/TaskPanes/Time/Controls",
            position = 1)
    @Messages("CTL_TimeStepBackwardAction=Step Backward")
    public static final class TimeStepBackwardAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.step(-1);
        }
    }

    /**
     * REWIND
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeRewindAction")
    @ActionRegistration(
            displayName = "#CTL_TimeRewindAction",
            popupText = "Play backwards through time.",
            lazy = false)
    @RibbonActionReference(
            path = "Ribbon/TaskPanes/Time/Controls",
            position = 2)
    @Messages("CTL_TimeRewindAction=Rewind")
    public static final class TimeRewindAction extends AbstractAction {

        private static final String ICON_BASE = "com/terramenta/time/images/time-play-backward.png";
        private final TimeRewindAction.Listener listener = new TimeRewindAction.Listener();

        public TimeRewindAction() {
            // Non-lazy initialization requires use to put some properties into the action
            putValue(Action.NAME, Bundle.CTL_TimeRewindAction());
            putValue("iconBase", ICON_BASE);

            setEnabled(!tac.isPlaying());
            tac.addPropertyChangeListener(WeakListeners.propertyChange(listener, tac));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.play(-1);
        }

        private class Listener implements PropertyChangeListener {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case TimeActionController.PLAY:
                        TimeRewindAction.this.setEnabled(!evt.getNewValue().equals(-1));
                        break;
                    case TimeActionController.STEP:
                    case TimeActionController.INCREMENT:
                        break;
                    default:
                        TimeRewindAction.this.setEnabled(true);
                }
            }
        }
    }

    /**
     * STOP
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStopAction")
    @ActionRegistration(displayName = "#CTL_TimeStopAction", popupText = "Stop time animation.", lazy = false)
    @RibbonActionReference(path = "Ribbon/TaskPanes/Time/Controls", position = 3)
    @Messages("CTL_TimeStopAction=Stop")
    public static final class TimeStopAction extends AbstractAction {

        private static final String ICON_BASE = "com/terramenta/time/images/time-stop.png";
        private final TimeStopAction.Listener listener = new TimeStopAction.Listener();

        public TimeStopAction() {
            // Non-lazy initialization requires use to put some properties into the action
            putValue(Action.NAME, Bundle.CTL_TimeStopAction());
            putValue("iconBase", ICON_BASE);
            setEnabled(tac.isPlaying());
            tac.addPropertyChangeListener(WeakListeners.propertyChange(listener, tac));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.stop();
        }

        private class Listener implements PropertyChangeListener {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case TimeActionController.PLAY:
                        TimeStopAction.this.setEnabled(true);
                        break;
                    case TimeActionController.STOP:
                        TimeStopAction.this.setEnabled(false);
                        break;
                }
            }
        }
    }

    /**
     * PLAY
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimePlayAction")
    @ActionRegistration(displayName = "#CTL_TimePlayAction", popupText = "Play through time.", lazy = false)
    @RibbonActionReference(path = "Ribbon/TaskPanes/Time/Controls", position = 4)
    @Messages("CTL_TimePlayAction=Play")
    public static final class TimePlayAction extends AbstractAction {

        private static final String ICON_BASE = "com/terramenta/time/images/time-play.png";
        private final TimePlayAction.Listener listener = new TimePlayAction.Listener();

        public TimePlayAction() {
            // Non-lazy initialization requires use to put some properties into the action
            putValue(Action.NAME, Bundle.CTL_TimePlayAction());
            putValue("iconBase", ICON_BASE);

            setEnabled(!tac.isPlaying());
            tac.addPropertyChangeListener(WeakListeners.propertyChange(listener, tac));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.play(1);
        }

        private class Listener implements PropertyChangeListener {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case TimeActionController.PLAY:
                        TimePlayAction.this.setEnabled(!evt.getNewValue().equals(1));
                        break;
                    case TimeActionController.STEP:
                    case TimeActionController.INCREMENT:
                        break;
                    default:
                        TimePlayAction.this.setEnabled(true);
                }
            }
        }
    }

    /**
     * STEP FORWARD
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStepForwardAction")
    @ActionRegistration(iconBase = "com/terramenta/time/images/time-step-forward.png", displayName = "#CTL_TimeStepForwardAction", popupText = "Step forward one frame in time.")
    @RibbonActionReference(path = "Ribbon/TaskPanes/Time/Controls", position = 5)
    @Messages("CTL_TimeStepForwardAction=Step Forward")
    public static final class TimeStepForwardAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.step(1);
        }
    }

    /**
     * RESET
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeResetAction")
    @ActionRegistration(iconBase = "com/terramenta/time/images/time-reset.png", displayName = "#CTL_TimeResetAction", popupText = "Reset the application time.")
    @RibbonActionReference(path = "Ribbon/TaskPanes/Time/Controls", position = 7, separatorBefore = 6)
    @Messages("CTL_TimeResetAction=Reset")
    public static final class TimeResetAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.reset();
        }
    }
}
