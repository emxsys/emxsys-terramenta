/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time;

/**
 *
 * @author chris.heidt
 */
public class DateInterval {

    private final long startMillis;
    private final long endMillis;

    /**
     *
     * @param startMillis
     * @param endMillis
     */
    public DateInterval(long startMillis, long endMillis) {
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }

    /**
     *
     * @return
     */
    public long getStartMillis() {
        return startMillis;
    }

    /**
     *
     * @return
     */
    public long getEndMillis() {
        return endMillis;
    }
}
