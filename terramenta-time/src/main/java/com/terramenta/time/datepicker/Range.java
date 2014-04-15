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
package com.terramenta.time.datepicker;

/**
 *
 * @author Chris.Heidt
 * @param <T>
 */
public class Range<T extends Number> {

    private T start, end;

    /**
     *
     * @param start
     * @param end
     */
    public Range(T start, T end) {
        this.start = start;
        this.end = end;
    }

    /**
     *
     * @return
     */
    public T getStart() {
        return start;
    }

    /**
     *
     * @param start
     */
    public void setStart(T start) {
        this.start = start;
    }

    /**
     *
     * @return
     */
    public T getEnd() {
        return end;
    }

    /**
     *
     * @param end
     */
    public void setEnd(T end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "]";
    }
}
