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

import com.terramenta.globe.utilities.CoordinateConversion;
import com.terramenta.time.DateConverter;
import com.terramenta.time.DateProvider;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import java.time.Instant;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.jastronomy.jsofa.JSOFA;
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
        return new Position(position, 0);
    }

    private void update(Instant datetime) {
        if (datetime == null) {
            return;
        }

        position = calculateMoonPosition(datetime);
        logger.debug("The Moon's Position at {} is {}", datetime, position);

        this.setChanged();
        this.notifyObservers(position);
    }

    private static Position calculateMoonPosition(Instant datetime) {

        double jd = DateConverter.toDecimalDays(DateConverter.J2000, datetime);

        double tdbTime = jd / JSOFA.DJC;

        //====================================================================
        // M_moon (2003) method
        double meanAnomalyOfMoon = JSOFA.jauFal03(tdbTime);

        // M_sun (2003) method
        double meanAnomalyOfSun = JSOFA.jauFalp03(tdbTime);

        /**
         * u_M_c Corresponds to the definition in Vallado (2014) and JSOFA (2003) methods
         */
        double meanArgumentOfLatitude = JSOFA.jauFaf03(tdbTime);

        //D_sun  (2003) method
        double meanElongationOfMoonFromSun = JSOFA.jauFad03(tdbTime);

        //====================================================================
        /**
         * Green 1988, 174, Equation page 287, Vallado (2014) Green, R.M. (1988). Spherical
         * Astronomy. New York: Cambridge University Press
         * <p>
         */
        //Ecliptic Lon
        double eclipticLonDeg = generateMoonLongitude(tdbTime)
                + 6.29 * Math.sin(meanAnomalyOfMoon)
                - 1.27 * Math.sin(meanAnomalyOfMoon - 2 * meanElongationOfMoonFromSun)
                + 0.66 * Math.sin(2 * meanElongationOfMoonFromSun)
                + 0.21 * Math.sin(2 * meanAnomalyOfMoon)
                - 0.19 * Math.sin(meanAnomalyOfSun)
                - 0.11 * Math.sin(2 * meanArgumentOfLatitude);
        if (eclipticLonDeg < 0) {
            eclipticLonDeg = 360 + eclipticLonDeg;// Flip Positive
        }
        double eclipticLonRad = Math.toRadians(eclipticLonDeg);
        double cMoonLon = Math.cos(eclipticLonRad);
        double sMoonLon = Math.sin(eclipticLonRad);

        //Ecliptic Lat
        double eclipticLatDeg = 5.13 * Math.sin(meanArgumentOfLatitude)
                + 0.28 * Math.sin(meanAnomalyOfMoon + meanArgumentOfLatitude)
                - 0.28 * Math.sin(meanArgumentOfLatitude - meanAnomalyOfMoon)
                - 0.17 * Math.sin(meanArgumentOfLatitude - 2 * meanElongationOfMoonFromSun);
        double eclipticLatRad = Math.toRadians(eclipticLatDeg);
        double cMoonLat = Math.cos(eclipticLatRad);
        double sMoonLat = Math.sin(eclipticLatRad);

        double parallax = Math.toRadians(
                0.9508
                + 0.0518 * Math.cos(meanAnomalyOfMoon)
                + 0.0095 * Math.cos(meanAnomalyOfMoon - 2 * meanElongationOfMoonFromSun)
                + 0.0078 * Math.cos(2 * meanElongationOfMoonFromSun)
                + 0.0028 * Math.cos(2 * meanAnomalyOfMoon)
        );
        double distanceInMeters = (1 / Math.sin(parallax)) * Earth.WGS84_EQUATORIAL_RADIUS;

        /**
         * Vallado (2014) Equation 3.68, Mean Obliquity of Earth
         */
        double eclipticObliquityRad = JSOFA.jauObl06(jd, 0.0);
        double cEcliptic = Math.cos(eclipticObliquityRad);
        double sEcliptic = Math.sin(eclipticObliquityRad);

        // Geocentric Equatorial Frame
        RealVector moonPosition = MatrixUtils.createRealVector(
                new double[]{
                    cMoonLat * cMoonLon,
                    cEcliptic * cMoonLat * sMoonLon - sEcliptic * sMoonLat,
                    sEcliptic * cMoonLat * sMoonLon + cEcliptic * sMoonLat});

        //coordinate conversions
        double[] teme = moonPosition.mapMultiply(distanceInMeters).toArray();
        double[] ecef = CoordinateConversion.teme2ecef(jd, teme);
        double[] lla = CoordinateConversion.ecef2lla(ecef);

        return Position.fromRadians(lla[0], lla[1], lla[2]);
    }

    private static double generateMoonLongitude(double tdbTime) {
        double longitudeOfMoon = 218.3164591
                + tdbTime * (481267.88134236
                + tdbTime * (-0.0013268
                + tdbTime * (1.0 / 538841.0
                + tdbTime * (-1.0 / 65194000.0))));
        longitudeOfMoon = longitudeOfMoon % 360.0;
        if (longitudeOfMoon < 0.0) {
            longitudeOfMoon += 360.0;
        }
        return longitudeOfMoon;
    }
}
