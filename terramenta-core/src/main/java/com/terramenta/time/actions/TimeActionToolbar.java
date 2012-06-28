/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

import com.terramenta.time.DateTimeChangeEvent;
import com.terramenta.time.DateTimeController;
import com.terramenta.time.DateTimeEventListener;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;

/**
 *
 * @author chris.heidt
 */
public class TimeActionToolbar {

    private static final DateTimeController dateTimeController = DateTimeController.getInstance();
    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private static final int maxHeight = 24;
    private static final int minHeight = 16;
    private static final int prefHeight = 20;

    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStepBackwardAction")
    @ActionRegistration(iconBase = "images/controlStepBackward.png", displayName = "#CTL_TimeStepBackwardAction")
    @ActionReferences({
        @ActionReference(path = "Menu/Time", position = 4),
        @ActionReference(path = "Toolbars/Time", position = 1)
    })
    @Messages("CTL_TimeStepBackwardAction=Step Backward")
    public static final class TimeStepBackwardAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.step(-1);
        }
    }

    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeRewindAction")
    @ActionRegistration(iconBase = "images/controlRewind.png", displayName = "#CTL_TimeRewindAction")
    @ActionReferences({
        @ActionReference(path = "Menu/Time", position = 2),
        @ActionReference(path = "Toolbars/Time", position = 2)
    })
    @Messages("CTL_TimeRewindAction=Rewind")
    public static final class TimeRewindAction extends AbstractAction {

        private final TimeRewindAction.Listener listener = new TimeRewindAction.Listener();

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
                if (evt.getPropertyName().equals(TimeActionController.PLAY) && (evt.getNewValue().equals(-1))) {
                    TimeRewindAction.this.setEnabled(false);
                } else {
                    TimeRewindAction.this.setEnabled(true);
                }
            }
        }
    }

    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStopAction")
    @ActionRegistration(iconBase = "images/controlStop.png", displayName = "#CTL_TimeStopAction")
    @ActionReferences({
        @ActionReference(path = "Menu/Time", position = 5),
        @ActionReference(path = "Toolbars/Time", position = 3)
    })
    @Messages("CTL_TimeStopAction=Stop")
    public static final class TimeStopAction extends AbstractAction {

        private final Listener listener = new Listener();

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

    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimePlayAction")
    @ActionRegistration(iconBase = "images/controlPlay.png", displayName = "#CTL_TimePlayAction")
    @ActionReferences({
        @ActionReference(path = "Menu/Time", position = 1),
        @ActionReference(path = "Toolbars/Time", position = 4)
    })
    @Messages("CTL_TimePlayAction=Play")
    public static final class TimePlayAction extends AbstractAction {

        private final TimePlayAction.Listener listener = new TimePlayAction.Listener();

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
                if (evt.getPropertyName().equals(TimeActionController.PLAY) && (evt.getNewValue().equals(1))) {
                    TimePlayAction.this.setEnabled(false);
                } else {
                    TimePlayAction.this.setEnabled(true);
                }
            }
        }
    }

    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStepForwardAction")
    @ActionRegistration(iconBase = "images/controlStepForward.png", displayName = "#CTL_TimeStepForwardAction")
    @ActionReferences({
        @ActionReference(path = "Menu/Time", position = 3),
        @ActionReference(path = "Toolbars/Time", position = 5)
    })
    @Messages("CTL_TimeStepForwardAction=Step Forward")
    public static final class TimeStepForwardAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            tac.step(1);
        }
    }

    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeIncrementAction")
    @ActionRegistration(displayName = "#CTL_TimeIncrementAction")
    @ActionReferences({
        @ActionReference(path = "Toolbars/Time", position = 6)
    })
    @Messages({
        "CTL_TimeIncrementAction=Adjust Increment",
        "HINT_TimeIncrementAction=Time Per Frame"
    })
    public static final class TimeIncrementAction extends AbstractAction implements Presenter.Toolbar {

        private JComboBox comp = null;

        @Override
        public Component getToolbarPresenter() {
            if (comp == null) {
                comp = new JComboBox(TimeIncrement.values());
                comp.setMaximumSize(new Dimension(75, maxHeight));
                comp.setPreferredSize(new Dimension(75, prefHeight));
                comp.setMinimumSize(new Dimension(75, minHeight));
                comp.setToolTipText(Bundle.HINT_TimeIncrementAction());
                comp.addActionListener(this);
            }
            return this.comp;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TimeIncrement selected = (TimeIncrement) comp.getSelectedItem();
            tac.setStepIncrement(selected.getIncrement());
        }
    }

    @ActionID(category = "Other", id = "com.terramenta.time.actions.Spacer")
    @ActionRegistration(displayName = "#CTL_Spacer")
    @ActionReferences({
        @ActionReference(path = "Toolbars/Time", position = 7)
    })
    @Messages("CTL_Spacer=")
    public static final class NavigationSpacer extends AbstractAction implements Presenter.Toolbar {

        @Override
        public void actionPerformed(ActionEvent e) {
            //...
        }

        @Override
        public Component getToolbarPresenter() {
            Dimension dim = new Dimension(2, 0);
            return new Box.Filler(dim, dim, dim);
        }
    }

    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeDisplayAction")
    @ActionRegistration(displayName = "#CTL_TimeDisplayAction")
    @ActionReferences({
        @ActionReference(path = "Toolbars/Time", position = 8)
    })
    @Messages("CTL_TimeDisplayAction=Current Time")
    public static final class TimeDisplayAction extends AbstractAction implements Presenter.Toolbar {

        private JTextField field = new JTextField();// {
//
//        @Override
//        public void setBorder(Border border) {
//            // GO AWAY!
//        }
//    };

        public TimeDisplayAction() {
            field.setMaximumSize(new Dimension(150, maxHeight));
            field.setPreferredSize(new Dimension(150, prefHeight));
            field.setMinimumSize(new Dimension(150, minHeight));
//        field.setEnabled(false);
            DateTime datetime = dateTimeController.getDateTime();
            String dateText = datetime.plusMillis(-datetime.getMillisOfSecond()).toString();//Remove millis for display
            field.setText(dateText);
            field.addActionListener(this);
//        field.addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (!field.isEnabled()) {
//                    field.setEnabled(true);
//                }
//            }
//        });

            dateTimeController.addDateTimeEventListener(new DateTimeEventListener() {

                @Override
                public void changeEventOccurred(DateTimeChangeEvent evt) {
                    DateTime datetime = evt.getDateTime();
                    String dateText = datetime.plusMillis(-datetime.getMillisOfSecond()).withZone(DateTimeZone.UTC).toString();//Remove millis for display
                    field.setText(dateText);
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(field)) {
                tac.stop();
                parseDateTextField(field.getText().trim());
                //field.setEnabled(false);
            }
        }

        @Override
        public Component getToolbarPresenter() {
            return field;
        }

        /**
         *
         */
        private void parseDateTextField(String text) {
            DateTime dt = DateTimeController.parseIsoString(text);
            if (dt == null) {
                dateTimeController.setDateTime(dateTimeController.getDateTime());
            } else {
                dateTimeController.setDateTime(dt);
            }
        }
    }

    @ActionID(category = "Other", id = "com.terramenta.time.actions.TimeLingerAction")
    @ActionRegistration(displayName = "#CTL_TimeLingerAction")
    @ActionReferences({
        @ActionReference(path = "Toolbars/Time", position = 9)
    })
    @Messages({
        "CTL_TimeLingerAction=Adjust Linger Time",
        "HINT_TimeLingerAction=Linger Time"
    })
    public static final class TimeLingerAction extends AbstractAction implements Presenter.Toolbar {

        private JSlider comp = null;

        @Override
        public Component getToolbarPresenter() {
            if (comp == null) {
                comp = new JSlider(JSlider.HORIZONTAL, 1, 50, 10);
                comp.setMaximumSize(new Dimension(50, maxHeight));
                comp.setPreferredSize(new Dimension(40, prefHeight));
                comp.setMinimumSize(new Dimension(30, minHeight));
                comp.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        //if (!source.getValueIsAdjusting()) {
                        Duration dur = null;
                        int multiplier = (int) source.getValue();
                        if (multiplier < 50) {
                            int linger = tac.getStepIncrement() * multiplier;
                            dur = new Duration(linger);
                        }
                        tac.setLingerDuration(dur);
                        tac.step(0);//force refresh
                        //}
                    }
                });
            }
            return comp;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //...
        }
    }

    private enum TimeIncrement {

        SECOND1("1 second", 1000),
        SECOND10("10 seconds", 10000),
        MINUTE1("1 minute", 60000),
        MINUTE10("10 minutes", 600000),
        MINUTE30("30 minutes", 1800000),
        HOUR1("1 hour", 3600000);
        private String label;
        private int increment;

        TimeIncrement(String label, int increment) {
            this.label = label;
            this.increment = increment;
        }

        public int getIncrement() {
            return increment;
        }

        @Override
        public String toString() {
            return label;
        }
    };
}
