/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

import com.terramenta.time.DateProvider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import javax.swing.Timer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author heidtmare
 */
@ServiceProvider(service = TimeActionController.class)
public class TimeActionController {

    /**
     *
     */
    public static final String STOP = "TimeActionController.Stop";
    /**
     *
     */
    public static final String PLAY = "TimeActionController.Play";
    /**
     *
     */
    public static final String STEP = "TimeActionController.Step";
    /**
     *
     */
    public static final String INCREMENT = "TimeActionController.StepIncrement";
    /**
     *
     */
    public static final String LINGER = "TimeActionController.LingerDuration";
    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private static final int animationRefreshRateMs = 100;//~10 frames per second
    private final PropertyChangeSupport pcs;
    private Timer playTimer = null;
    private int stepIncrement = AnimationSpeed.SLOW.getMilliseconds();
    private int linger = 0;
    private int dir = 0;

    /**
     *
     */
    public TimeActionController() {
        pcs = new PropertyChangeSupport(this);
    }

    /**
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * 1=forward, -1=backward, 0=no animation step, but can update time (esentially a graphic ini or refresh)
     *
     * @param direction
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
        pcs.firePropertyChange(PLAY, null, direction);
    }

    /**
     *
     * @return
     */
    public boolean isPlaying() {
        return playTimer != null;
    }

    /**
     *
     */
    public void stop() {
        if (playTimer != null) {
            playTimer.stop();
            playTimer = null;
        }
        pcs.firePropertyChange(STOP, false, true);
    }

    /**
     * advance one frame in the animation
     *
     * @param direction
     */
    public void step(int direction) {
        this.dir = direction;//so we know the last direction we went for visual purposes.

        dateProvider.add(Calendar.MILLISECOND, stepIncrement * direction);
        pcs.firePropertyChange(STEP, false, true);
    }

    /**
     * Get the last known animation direction
     *
     * @return
     */
    public int getPreviousStepDirection() {
        return dir;
    }

    /**
     * @return the stepIncrement
     */
    public int getStepIncrement() {
        return stepIncrement;
    }

    /**
     * @param si
     */
    public void setStepIncrement(int si) {
        pcs.firePropertyChange(INCREMENT, this.stepIncrement, this.stepIncrement = si);
    }

    /**
     *
     * @return
     */
    public int getLingerDuration() {
        return this.linger;
    }

    /**
     *
     * @param linger
     */
    public void setLingerDuration(int linger) {
        pcs.firePropertyChange(LINGER, this.linger, this.linger = linger);
    }
}
