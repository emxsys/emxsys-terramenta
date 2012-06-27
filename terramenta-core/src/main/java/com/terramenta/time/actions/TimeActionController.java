/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

import com.terramenta.time.DateTimeController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Timer;
import org.joda.time.Duration;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author heidtmare
 */
@ServiceProvider(service = TimeActionController.class)
public class TimeActionController {

    public static final String STOP = "TimeActionController.Stop";
    public static final String PLAY = "TimeActionController.Play";
    public static final String STEP = "TimeActionController.Step";
    public static final String INCREMENT = "TimeActionController.StepIncrement";
    private static final DateTimeController dateTimeController = DateTimeController.getInstance();
    private static final int animationRefreshRateMs = 75;//~15 frames per second
    private final PropertyChangeSupport propertyChangeSupport;
    private Timer playTimer = null;
    private int stepIncrement = 1000;
    private Duration linger = null;

    public TimeActionController() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * 1=forward, -1=backward, 0=no animation step, but can update time
     * (esentially a graphic ini or refresh)
     */
    public void play(final int direction) {
        stop();

        playTimer = new Timer(animationRefreshRateMs, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                step(direction);
            }
        });
        playTimer.setRepeats(true);
        playTimer.start();
        this.firePropertyChange(PLAY, null, direction);
    }

    public void stop() {
        if (playTimer != null) {
            playTimer.stop();
            playTimer = null;
        }
        this.firePropertyChange(STOP, false, true);
    }

    /**
     * advance one frame in the animation
     */
    public void step(int direction) {
        dateTimeController.add(DateTimeController.MILLISECOND, stepIncrement * direction);
        this.firePropertyChange(STEP, false, true);
    }

    /**
     * @return the stepIncrement
     */
    public int getStepIncrement() {
        return stepIncrement;
    }

    /**
     * @param stepIncrement the stepIncrement to set
     */
    public void setStepIncrement(int si) {
        int old = stepIncrement;
        stepIncrement = si;
        this.firePropertyChange(INCREMENT, old, si);
    }

    public boolean isPlaying() {
        return playTimer != null;
    }

    public void setLingerDuration(Duration linger) {
        this.linger = linger;
    }

    public Duration getLingerDuration() {
        return this.linger;
    }
}
