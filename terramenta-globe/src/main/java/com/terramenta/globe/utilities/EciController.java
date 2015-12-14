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
package com.terramenta.globe.utilities;

import com.terramenta.globe.options.GlobeOptions;
import com.terramenta.time.DatetimeConverter;
import java.time.Instant;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author heidtmare
 */
@ServiceProvider(service = EciController.class)
public class EciController {

    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    public static final double offsetRotdeg = -90.0; // jogl coordinate Greenwich to ECI x-axis offset
    private double rotateECIdeg = 280.46061837 + offsetRotdeg; // rotation in degrees (default j2k)
    private Instant previousDate;

    public boolean isEci() {
        return prefs.getBoolean("options.globe.isECI", false);
    }

    public double getCurrentRotationalDegree() {
        return rotateECIdeg;
    }

    public double calculateRotationalDegree(Instant datetime) {
        if (previousDate == null || !previousDate.equals(datetime)) {
            previousDate = datetime;

            double j2000 = DatetimeConverter.toDecimalDays(DatetimeConverter.J2000, datetime);
            // centuries since J2000.0
            double tt = j2000 / 36525.0;
            // now calculate the mean sidereal time at Greenwich (UT time) in degrees
            rotateECIdeg = ((280.46061837 + 360.98564736629 * j2000) + 0.000387933 * tt * tt - tt * tt * tt / 38710000.0 + offsetRotdeg) % 360.0;

//            double mjd = DateConverter.toDecimalDays(DateConverter.MJD, datetime);
//            // centuries since J2000.0
//            double tt = (mjd - 51544.5) / 36525.0;
//            // now calculate the mean sidereal time at Greenwich (UT time) in degrees
//            rotateECIdeg = ((280.46061837 + 360.98564736629 * (mjd - 51544.5)) + 0.000387933 * tt * tt - tt * tt * tt / 38710000.0 + offsetRotdeg) % 360.0;
        }

        return rotateECIdeg;
    }
}
