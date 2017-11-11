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

import com.terramenta.time.DatetimeProvider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    public static final String RESET = "TimeActionController.Reset";
    /**
     *
     */
    public static final String INCREMENT = "TimeActionController.StepIncrement";
    /**
     *
     */
    public static final String DURATION = "TimeActionController.LingerDuration";
    private static final DatetimeProvider datetimeProvider = Lookup.getDefault().lookup(DatetimeProvider.class);
    private static final int animationRefreshRateMs = 100;//~10 frames per second
    private final PropertyChangeSupport pcs;
    private Timer playTimer = null;
    private int stepIncrement = AnimationSpeed.SLOW.getMilliseconds();
    private Duration displayDuration = Duration.ofHours(1);
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
     * 1=forward, -1=backward, 0=no animation step, but can update time (esentially a graphic ini or
     * refresh)
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
        datetimeProvider.modifyDatetime(stepIncrement * direction, ChronoUnit.MILLIS);
        pcs.firePropertyChange(STEP, false, true);
    }

    /**
     * reset the application time to the current system time
     *
     */
    public void reset() {
        if (isPlaying()) {
            stop();
        }
        datetimeProvider.setDatetime(Instant.now());
        pcs.firePropertyChange(RESET, false, true);
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
    public Duration getDisplayDuration() {
        return this.displayDuration;
    }

    /**
     *
     * @param displayDuration
     */
    public void setDisplayDuration(Duration displayDuration) {
        if (displayDuration == null) {
            displayDuration = Duration.ZERO;
        }
        pcs.firePropertyChange(DURATION, this.displayDuration, this.displayDuration = displayDuration);
    }
}
