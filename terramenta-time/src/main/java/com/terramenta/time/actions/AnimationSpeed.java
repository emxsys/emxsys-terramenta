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
