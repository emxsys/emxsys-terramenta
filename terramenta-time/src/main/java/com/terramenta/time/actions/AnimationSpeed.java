/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

/**
 *
 * @author Chris.Heidt
 */
public enum AnimationSpeed {

    /**
     *
     */
    SLOW("Slow: 30 seconds", 30000),

    /**
     *
     */
    MEDIUM("Medium: 10 minutes", 600000),

    /**
     *
     */
    FAST("Fast: 30 minutes", 1800000);
    private final String label;
    private final int increment;

    AnimationSpeed(String label, int increment) {
        this.label = label;
        this.increment = increment;
    }

    /**
     *
     * @return
     */
    public int getMilliseconds() {
        return increment;
    }

    @Override
    public String toString() {
        return label;
    }
}
