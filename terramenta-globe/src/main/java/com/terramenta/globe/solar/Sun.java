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
package com.terramenta.globe.solar;

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
@ServiceProvider(service = Sun.class)
public class Sun extends Observable {

    public static final double AU = 149597870700d;//meters
    public static final double ALTITUDE = AU - Earth.WGS84_EQUATORIAL_RADIUS;//meters
    private static final Logger logger = LoggerFactory.getLogger(Sun.class);
    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private final Observer dateProviderObserver = (Observable o, Object arg) -> {
        Instant date;
        if (arg instanceof Date) {
            date = ((Date) arg).toInstant();
        } else {
            date = dateProvider.getDate().toInstant();
        }

        Sun.this.update(date);
    };
    private Position position;
    private LatLon subsolarPosition;

    public Sun() {
        //set current position
        update(dateProvider.getDate().toInstant());

        //listen for date changes
        dateProvider.addObserver(dateProviderObserver);
    }

    public Position getPosition() {
        return position;
    }

    public LatLon getSubsolarPosition() {
        return subsolarPosition;
    }

    /**
     *
     * @param datetime
     */
    private void update(Instant datetime) {
        if (datetime == null) {
            return;
        }

        double jd = DateConverter.toJD(Date.from(datetime));
        subsolarPosition = calculateSubsolarPosition(jd);
        position = new Position(subsolarPosition, ALTITUDE);
        logger.debug("The Sun's Position at {} is {}", datetime, position);

        this.setChanged();
        this.notifyObservers(position);
    }

    /**
     * Calculates the subsolar latitude/longitude coordinates of sun at a given time. The subsolar
     * point on the earth is where the sun is perceived to be directly overhead (in zenith), that is
     * where the sun's rays are hitting the planet exactly perpendicular to its surface.
     *
     * Original c++ source found here: http://www.psa.es/sdg/archive/SunPos.cpp. Algorithm changed
     * to use alternative greenwich mean sidereal time calculation.
     *
     * @param julianDate The date/time used to compute the sun's position.
     * @return The latitude and longitude of the subsolar point. [radians]
     */
    private static LatLon calculateSubsolarPosition(double julianDate) {
        // Main variables
        double elapsedJulianDays, eclipticLongitude,
                eclipticObliquity, rightAscension,
                declination, longitude;
        
        // Calculate difference in days between the current Julian Day
        // and JD 2451545.0, which is noon 1 January 2000 Universal Time
        {
            elapsedJulianDays = julianDate - 2451545.0;
        }
        // Calculate ecliptic coordinates (ecliptic longitude and obliquity of the
        // ecliptic in radians but without limiting the angle to be less than 2*Pi
        // (i.e., the result may be greater than 2*Pi)
        {
            double omega = 2.1429 - 0.0010394594 * elapsedJulianDays;
            double meanLongitude = 4.8950630 + 0.017202791698 * elapsedJulianDays; // Radians
            double meanAnomaly = 6.2400600 + 0.0172019699 * elapsedJulianDays;
            eclipticLongitude = meanLongitude
                    + 0.03341607 * Math.sin(meanAnomaly)
                    + 0.00034894 * Math.sin(2 * meanAnomaly)
                    - 0.0001134 - 0.0000203 * Math.sin(omega);
            eclipticObliquity = 0.4090928
                    - 6.2140e-9 * elapsedJulianDays
                    + 0.0000396 * Math.cos(omega);
        }
        // Calculate celestial coordinates ( right ascension and declination ) in radians
        // but without limiting the angle to be less than 2*Pi (i.e., the result may be
        // greater than 2*Pi)
        {
            double sinEclipticLongitude = Math.sin(eclipticLongitude);
            double dY = Math.cos(eclipticObliquity) * sinEclipticLongitude;
            double dX = Math.cos(eclipticLongitude);
            rightAscension = Math.atan2(dY, dX);
            if (rightAscension < 0.0) {
                rightAscension = rightAscension + Math.PI * 2.0;
            }
            declination = Math.asin(Math.sin(eclipticObliquity) * sinEclipticLongitude);
        }
        // Convert from celestial coordinates to horizontal coordinates; solar latitude and 
        // declination are identical.
        {
            // Alternative from: see http://aa.usno.navy.mil/faq/docs/GAST.php
            double greenwichMeanSiderealTime = (18.697374558 + 24.06570982441908 * elapsedJulianDays) % 24;
            longitude = rightAscension - Math.toRadians(greenwichMeanSiderealTime * 15);
        }

        while (declination > Math.PI / 2.0) {
            declination -= Math.PI;
        }
        while (declination <= -Math.PI / 2.0) {
            declination += Math.PI;
        }
        while (longitude > Math.PI) {
            longitude -= Math.PI * 2.0;
        }
        while (longitude <= -Math.PI) {
            longitude += Math.PI * 2.0;
        }

        return LatLon.fromRadians(declination, longitude);
    }
}
