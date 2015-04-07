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
package com.terramenta.time;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author chris.heidt
 */
public class DateConverter {

    public static final Instant JD = ZonedDateTime.of(-4713, 01, 01, 12, 0, 0, 0, ZoneOffset.UTC).toInstant();
    public static final Instant MJD = ZonedDateTime.of(1858, 11, 17, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
    public static final Instant J2000 = ZonedDateTime.of(2000, 01, 01, 12, 0, 0, 0, ZoneOffset.UTC).toInstant();

    private static final Logger logger = LoggerFactory.getLogger(DateConverter.class);
    private static final double MILLISECONDS_PER_DAY = TimeUnit.DAYS.toMillis(1);

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

    /**
     * Decimal Days since the provided epoch. JD, MJD, J200 are available predefined epochs
     *
     * @param epoch
     * @param date
     * @return
     */
    public static double toDecimalDays(Instant epoch, Instant date) {
        double dd = (date.toEpochMilli() - epoch.toEpochMilli()) / MILLISECONDS_PER_DAY;
        logger.debug("Epoch:{}, Date:{}, Days:{}", epoch, date, dd);
        return dd;
    }

    /**
     * Calculates the Greenwich mean sidereal time (GMST) on julDate (doesn't have to be 0h). Used
     * calculations from Meesus 2nd ed.
     *
     * @param mjd Modified Julian Date
     * @return Greenwich mean sidereal time in degrees (0-360)
     */
    public static double toGMST(double mjd) {
        // calculate T
        double T = (mjd - 51544.5) / 36525.0;

        // do calculation
        double gmst = ((280.46061837 + 360.98564736629 * (mjd - 51544.5)) + 0.000387933 * T * T - T * T * T / 38710000.0) % 360.0;

        // make positive
        if (gmst < 0) {
            gmst += 360.0;
        }

        return gmst;
    }

    /**
     * Calculates the mean sidereal time (MST) on julDate (doesn't have to be 0h) for a given
     * longitiude.
     *
     * @param mjd          Modified Julian Date
     * @param longitudeDeg
     * @return mean sidereal time in degrees (0-360)
     */
    public static double toMST(double mjd, double longitudeDeg) {
        return (toGMST(mjd) + longitudeDeg) % 360.0;
    }
}
