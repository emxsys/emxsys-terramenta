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
    SLOW(30000, "Slow: 30 seconds", "com/terramenta/time/images/speed-slow.png"),
    /**
     *
     */
    MEDIUM(600000, "Medium: 10 minutes", "com/terramenta/time/images/speed-medium.png"),
    /**
     *
     */
    FAST(1800000, "Fast: 30 minutes", "com/terramenta/time/images/speed-fast.png");

    private final int increment;
    private final String description;
    private final String iconbase;

    AnimationSpeed(int increment, String description, String iconbase) {
        this.increment = increment;
        this.description = description;
        this.iconbase = iconbase;
    }

    public int getMilliseconds() {
        return increment;
    }

    public String getDescription() {
        return description;
    }

    public String getIconbase() {
        return iconbase;
    }

    @Override
    public String toString() {
        return description;
    }
}
