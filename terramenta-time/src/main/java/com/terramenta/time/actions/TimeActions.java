/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;

/**
 *
 * @author chris.heidt
 */
public class TimeActions {

    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);

    /**
     *
     * @author Chris.Heidt
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStepBackwardAction")
    @ActionRegistration(iconBase = "com/terramenta/time/images/time-step-backward.png", displayName = "#CTL_TimeStepBackwardAction", popupText = "Step backwards one frame in time.")
    @ActionReferences({
        @ActionReference(path = "Menu/Animate/Controls", position = 1)
    })
    @Messages("CTL_TimeStepBackwardAction=Step Backward")
    public static final class TimeStepBackwardAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.step(-1);
        }
    }

    /**
     *
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeRewindAction")
    @ActionRegistration(iconBase = "com/terramenta/time/images/time-play-backward.png", displayName = "#CTL_TimeRewindAction", popupText = "Play backwards through time.")
    @ActionReferences({
        @ActionReference(path = "Menu/Animate/Controls", position = 2)
    })
    @Messages("CTL_TimeRewindAction=Rewind")
    public static final class TimeRewindAction extends AbstractAction {

        private final TimeRewindAction.Listener listener = new TimeRewindAction.Listener();

        /**
         *
         */
        public TimeRewindAction() {
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
                if (evt.getPropertyName().equals(TimeActionController.PLAY) && evt.getNewValue().equals(-1)) {
                    TimeRewindAction.this.setEnabled(false);
                } else {
                    TimeRewindAction.this.setEnabled(true);
                }
            }
        }
    }

    /**
     *
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStopAction")
    @ActionRegistration(iconBase = "com/terramenta/time/images/time-stop.png", displayName = "#CTL_TimeStopAction", popupText = "Stop time animation.")
    @ActionReferences({
        @ActionReference(path = "Menu/Animate/Controls", position = 3)
    })
    @Messages("CTL_TimeStopAction=Stop")
    public static final class TimeStopAction extends AbstractAction {

        private final TimeStopAction.Listener listener = new TimeStopAction.Listener();

        /**
         *
         */
        public TimeStopAction() {
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
                if (evt.getPropertyName().equals(TimeActionController.PLAY)) {
                    TimeStopAction.this.setEnabled(true);
                } else if (evt.getPropertyName().equals(TimeActionController.STOP)) {
                    TimeStopAction.this.setEnabled(false);
                }
            }
        }
    }

    /**
     *
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimePlayAction")
    @ActionRegistration(iconBase = "com/terramenta/time/images/time-play.png", displayName = "#CTL_TimePlayAction", popupText = "Play through time.")
    @ActionReferences({
        @ActionReference(path = "Menu/Animate/Controls", position = 4)
    })
    @Messages("CTL_TimePlayAction=Play")
    public static final class TimePlayAction extends AbstractAction {

        private final TimePlayAction.Listener listener = new TimePlayAction.Listener();

        /**
         *
         */
        public TimePlayAction() {
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
                if (evt.getPropertyName().equals(TimeActionController.PLAY) && evt.getNewValue().equals(1)) {
                    TimePlayAction.this.setEnabled(false);
                } else {
                    TimePlayAction.this.setEnabled(true);
                }
            }
        }
    }

    /**
     *
     */
    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStepForwardAction")
    @ActionRegistration(iconBase = "com/terramenta/time/images/time-step-forward.png", displayName = "#CTL_TimeStepForwardAction", popupText = "Step forward one frame in time.")
    @ActionReferences({
        @ActionReference(path = "Menu/Animate/Controls", position = 5)
    })
    @Messages("CTL_TimeStepForwardAction=Step Forward")
    public static final class TimeStepForwardAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.step(1);
        }
    }
}
