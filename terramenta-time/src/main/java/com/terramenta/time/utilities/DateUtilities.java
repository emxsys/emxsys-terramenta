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
package com.terramenta.time.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author chris.heidt
 */
public class DateUtilities {

    /**
     * Parse Calendar from iso string
     *
     * @param s
     * @return
     */
    public static Calendar toCalendar(String s) {
        return DatatypeConverter.parseDateTime(s);
    }

    /**
     * Parse Date from iso string
     *
     * @param s
     * @return
     */
    public static Date toDate(String s) {
        return toCalendar(s).getTime();
    }

    /**
     * Creates a string representation of the calendar using the local time zone.
     *
     * @param c
     * @return
     */
    public static String toString(Calendar c) {
        return DatatypeConverter.printDateTime(c);
    }

    /**
     * Creates a string representation of the date using the local time zone.
     *
     * @param d
     * @return
     */
    public static String toString(Date d) {
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
    public static String toString(Date d, TimeZone tz) {
        Calendar c = GregorianCalendar.getInstance(tz);
        c.setTime(d);
        return toString(c);
    }
}
