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

    public DateInterval(long startMillis, long endMillis) {
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }
}
