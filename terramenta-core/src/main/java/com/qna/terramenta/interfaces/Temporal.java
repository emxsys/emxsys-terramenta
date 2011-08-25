/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.interfaces;

import org.joda.time.DateTime;

/**
 *
 * @author heidtmare
 */
public interface Temporal {

    /**
     * 
     * @return
     */
    public DateTime getDateTime();

    /**
     * 
     * @param date
     */
    public void setDateTime(DateTime date);
}
