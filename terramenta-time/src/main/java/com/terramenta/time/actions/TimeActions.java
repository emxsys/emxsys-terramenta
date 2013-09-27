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
    @ActionRegistration(iconBase = "images/controlStepBackward.png", displayName = "#CTL_TimeStepBackwardAction", popupText = "Step backwards one frame in time.")
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
    @ActionRegistration(iconBase = "images/controlRewind.png", displayName = "#CTL_TimeRewindAction", popupText = "Play backwards through time.")
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
    @ActionRegistration(iconBase = "images/controlStop.png", displayName = "#CTL_TimeStopAction", popupText = "Stop time animation.")
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
    @ActionRegistration(iconBase = "images/controlPlay.png", displayName = "#CTL_TimePlayAction", popupText = "Play through time.")
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
    @ActionRegistration(iconBase = "images/controlStepForward.png", displayName = "#CTL_TimeStepForwardAction", popupText = "Step forward one frame in time.")
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
    /**
     *
     */
//    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeLingerAction")
//    @ActionRegistration(displayName = "#CTL_TimeLingerAction", lazy = false)
//    @ActionReferences({
//        @ActionReference(path = "Toolbars/Time", position = 9)
//    })
//    @Messages({
//        "CTL_TimeLingerAction=Adjust Linger Time",
//        "HINT_TimeLingerAction=Linger Time"
//    })
//    public static final class TimeLingerAction extends AbstractAction implements Presenter.Toolbar {
//
//        private JSlider comp = null;
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            //...
//        }
//
//        /**
//         *
//         * @return
//         */
//        @Override
//        public Component getToolbarPresenter() {
//            if (comp == null) {
//                comp = new JSlider(JSlider.HORIZONTAL, 1, 50, 50);
////                comp.setMaximumSize(new Dimension(50, maxHeight));
////                comp.setPreferredSize(new Dimension(50, prefHeight));
////                comp.setMinimumSize(new Dimension(50, minHeight));
//                comp.setToolTipText(Bundle.HINT_TimeLingerAction());
//                comp.addChangeListener(new ChangeListener() {
//                    @Override
//                    public void stateChanged(ChangeEvent e) {
//                        JSlider source = (JSlider) e.getSource();
//                        int linger = 0;
//                        int multiplier = (int) source.getValue();
//                        if (multiplier < 50) {
//                            linger = tac.getStepIncrement() * multiplier;
//                        }
//                        tac.setLingerDuration(linger);
//                        tac.step(0);//force refresh
//                    }
//                });
//            }
//            return comp;
//        }
//    }
}
