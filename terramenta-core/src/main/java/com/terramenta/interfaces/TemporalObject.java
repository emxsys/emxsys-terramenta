/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.interfaces;

import org.joda.time.DateTime;

/**
 *
 * @author chris.heidt
 */
public interface TemporalObject {

    public void setDateTime(DateTime name);

    public DateTime getDateTime();
}
