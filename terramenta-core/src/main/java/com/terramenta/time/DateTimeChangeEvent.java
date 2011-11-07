/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time;

import java.util.EventObject;
import org.joda.time.DateTime;

/**
 *
 * @author heidtmare
 */
public class DateTimeChangeEvent extends EventObject {

    private final DateTime datetime;

    /**
     *
     * @param source
     * @param datetime
     */
    public DateTimeChangeEvent(DateTimeController source, DateTime datetime) {
        super(source);
        this.datetime = datetime;
    }

    /**
     * 
     * @return
     */
    public DateTime getDateTime() {
        return this.datetime;
    }
}
