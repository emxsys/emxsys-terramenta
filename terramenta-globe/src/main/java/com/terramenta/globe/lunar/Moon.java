/**
 * Copyright © 2014, Terramenta. All rights reserved. * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this work except in compliance
 * with the License.
 *
 * You can obtain a copy of the License at http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 *
 */
package com.terramenta.globe.lunar;

import com.terramenta.time.DateConverter;
import com.terramenta.time.DateProvider;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import java.time.Instant;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
@ServiceProvider(service = Moon.class)
public class Moon extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(Moon.class);
    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private final Observer dateProviderObserver = (Observable o, Object arg) -> {
        Instant date;
        if (arg instanceof Date) {
            date = ((Date) arg).toInstant();
        } else {
            date = dateProvider.getDate().toInstant();
        }

        Moon.this.update(date);
    };
    private Position position;
    private LatLon sublunarPosition;

    public Moon() {
        //set current position
        update(dateProvider.getDate().toInstant());

        //listen for date changes
        dateProvider.addObserver(dateProviderObserver);
    }

    public Position getPosition() {
        return position;
    }

    public LatLon getSublunarPosition() {
        return sublunarPosition;
    }

    private void update(Instant datetime) {
        if (datetime == null) {
            return;
        }

        position = calculateMoonPosition(datetime);
        sublunarPosition = new Position(position, 0);
        logger.debug("The Moon's Position at {} is {}", datetime, position);

        this.setChanged();
        this.notifyObservers(position);
    }

    private static Position calculateMoonPosition(Instant datetime) {
        //const
        double rad = Math.PI / 180;
        double lat = 0d;
        double lng = 0d;
        double lw = rad * -lng;
        double phi = rad * lat;

        double dd = DateConverter.toDecimalDays(DateConverter.J2000, datetime);//number of Julian days since 2000/01/01 at 12 UT
        double L = rad * (218.316 + 13.176396 * dd); // ecliptic longitude
        double M = rad * (134.963 + 13.064993 * dd); // mean anomaly
        double F = rad * (93.272 + 13.229350 * dd);  // mean distance

        double l = L + rad * 6.289 * Math.sin(M); // longitude
        double b = rad * 5.128 * Math.sin(F);     // latitude
        double dt = 385001 - 20905 * Math.cos(M);  // distance to the moon in km

        double ra = rightAscension(l, b);
        double dec = declination(l, b);

        double siderealTime = siderealTime(dd, lw) - ra;
        double h = altitude(siderealTime, phi, dec);
        h = h + rad * 0.017 / Math.tan(h + rad * 10.26 / (h + rad * 5.10));// altitude correction for refraction

        double azimuth = azimuth(siderealTime, phi, dec);
        
        double dtInMeters = dt * 1000;
        double dtInRadians = dtInMeters / Earth.WGS84_EQUATORIAL_RADIUS;
        double altitude = h * dtInMeters;

        logger.debug("azimuth:{}, altitude:{}, distance:{}",
                azimuth, altitude, dtInRadians
        );

        return new Position(LatLon.greatCircleEndPosition(LatLon.ZERO, azimuth, dtInRadians), altitude);
    }

    private static double rightAscension(double l, double b) {
        double rad = Math.PI / 180;
        double e = rad * 23.4397; // obliquity of the Earth
        return Math.atan2(Math.sin(l) * Math.cos(e) - Math.tan(b) * Math.sin(e), Math.cos(l));
    }

    private static double declination(double l, double b) {
        double rad = Math.PI / 180;
        double e = rad * 23.4397; // obliquity of the Earth
        return Math.asin(Math.sin(b) * Math.cos(e) + Math.cos(b) * Math.sin(e) * Math.sin(l));
    }

    private static double siderealTime(double d, double lw) {
        double rad = Math.PI / 180;
        return rad * (280.16 + 360.9856235 * d) - lw;
    }

    private static double altitude(double H, double phi, double dec) {
        return Math.asin(Math.sin(phi) * Math.sin(dec) + Math.cos(phi) * Math.cos(dec) * Math.cos(H));
    }

    private static double azimuth(double H, double phi, double dec) {
        return Math.atan2(Math.sin(H), Math.cos(H) * Math.sin(phi) - Math.tan(dec) * Math.cos(phi));
    }
}
