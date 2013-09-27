/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.bind.DatatypeConverter;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author chris.heidt
 */
@ServiceProvider(service = DateUtilities.class)
public class DateUtilities {

    /**
     * Parse Calendar from iso string
     *
     * @param s
     * @return
     */
    public Calendar toCalendar(String s) {
        return DatatypeConverter.parseDateTime(s);
    }

    /**
     * Parse Date from iso string
     *
     * @param s
     * @return
     */
    public Date toDate(String s) {
        return toCalendar(s).getTime();
    }

    /**
     * Creates a string representation of the calendar using the local time zone.
     *
     * @param c
     * @return
     */
    public String toString(Calendar c) {
        return DatatypeConverter.printDateTime(c);
    }

    /**
     * Creates a string representation of the date using the local time zone.
     *
     * @param d
     * @return
     */
    public String toString(Date d) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(d);
        return toString(c);
    }

    /**
     * Creates a string representation of the date using the provided time zone.
     *
     * @param d
     * @param tz
     * @return
     */
    public String toString(Date d, TimeZone tz) {
        Calendar c = GregorianCalendar.getInstance(tz);
        c.setTime(d);
        return toString(c);
    }
}
