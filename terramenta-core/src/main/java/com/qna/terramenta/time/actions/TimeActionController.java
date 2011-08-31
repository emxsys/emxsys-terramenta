/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.time.actions;

import com.qna.terramenta.time.DateTimeController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author heidtmare
 */
public class TimeActionController {

    private static final Logger logger = Logger.getLogger(TimeActionController.class.getName());
    private static final DateTimeController dateTimeController = DateTimeController.getInstance();
    private static final int animationRefreshRateMs = 75;//~15 frames per second
    private static Timer playTimer = null;
    private static int stepIncrement = 1000;

    private TimeActionController() {
        //no construction!
    }

    /**
     * 1=forward, -1=backward, 0=no animation step, but can update time (esentially a graphic ini or refresh)
     */
    public static void play(final int direction) {
        TimeActionController.stop();

        playTimer = new Timer(animationRefreshRateMs, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                step(direction);
            }
        });
        playTimer.setRepeats(true);
        playTimer.start();
    }

    public static void stop() {
        if (playTimer != null) {
            playTimer.stop();
            playTimer = null;
        }
    }

    /**
     * advance one frame in the animation
     */
    public static void step(int direction) {
        dateTimeController.add(DateTimeController.MILLISECOND, stepIncrement * direction);
    }

    /**
     * @return the stepIncrement
     */
    public static int getStepIncrement() {
        return stepIncrement;
    }

    /**
     * @param stepIncrement the stepIncrement to set
     */
    public static void setStepIncrement(int si) {
        stepIncrement = si;
    }
}
