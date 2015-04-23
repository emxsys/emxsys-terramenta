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
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
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
    private static final double eps_mach = 2.22E-17; // machine precision (double?)
    private static final double eps = 1.0e3 * eps_mach;   // Convergence criterion
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
        double[] ecef = teme2ecef(jd, teme);
        double[] lla = ecef2lla(ecef);

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

    /**
     *
     * @param jd
     * @param teme
     * @return
     */
    private static double[] teme2ecef(double jd, double[] teme) {
        double angle = JSOFA.jauGmst82(jd, 0.0);

        //Rz
        final double C = Math.cos(angle);
        final double S = Math.sin(angle);
        double[][] U = new double[3][3];
        U[0][0] = +C;
        U[0][1] = +S;
        U[0][2] = 0.0;
        U[1][0] = -S;
        U[1][1] = +C;
        U[1][2] = 0.0;
        U[2][0] = 0.0;
        U[2][1] = 0.0;
        U[2][2] = 1.0;

        RealMatrix sideralTimeMatrix = MatrixUtils.createRealMatrix(U);
        return sideralTimeMatrix.operate(teme);
    }

    /**
     *
     * @param ecef
     * @return
     */
    private static double[] ecef2lla(double[] ecef) {
        //  lat, lon,  alt;
        double[] LLA = new double[3];

        // Check validity of input data
        if (norm(ecef) == 0.0) {
            System.out.println(" invalid input in Geodetic constructor");
            LLA[0] = 0.0;
            LLA[1] = 0.0;
            LLA[2] = -Earth.WGS84_EQUATORIAL_RADIUS;
            return LLA;
        }

        // Vermeille (2004) Estimate of longitude 
        double p = Math.sqrt(
                Math.pow(ecef[0], 2) + Math.pow(ecef[1], 2));

        double longitude = calculateLongitudeFromECEF(ecef);

        // Constants for Borkowski's Method
        double c = (Math.pow(Earth.WGS84_EQUATORIAL_RADIUS, 2)
                - Math.pow(Earth.WGS84_POLAR_RADIUS, 2))
                / Math.sqrt(Math.pow(Earth.WGS84_EQUATORIAL_RADIUS * p, 2)
                        + Math.pow(Earth.WGS84_POLAR_RADIUS
                                * ecef[2], 2));

        double omega = Math.atan2(Earth.WGS84_POLAR_RADIUS
                * ecef[2], Earth.WGS84_EQUATORIAL_RADIUS * p);

        // Initial Guess
        double beta_0 = Math.atan2(
                Earth.WGS84_EQUATORIAL_RADIUS * ecef[2],
                Earth.WGS84_POLAR_RADIUS * p);

        double beta_1 = 0;
        double delta = 10;

        // Iteration
        while (Math.abs(delta) > eps) {
            double fBeta = 2 * Math.sin(beta_0 - omega)
                    - c * Math.sin(2 * beta_0);
            double fBetaPrime = 2 * (Math.cos(beta_0 - omega)
                    - c * Math.cos(2 * beta_0));

            delta = fBeta / fBetaPrime;

            beta_1 = beta_0 - fBeta / fBetaPrime;

            beta_0 = beta_1;

        }

        // Determine Latitute
        double latitude = Math.atan2(
                Earth.WGS84_EQUATORIAL_RADIUS * Math.tan(beta_1),
                Earth.WGS84_POLAR_RADIUS);

        // Determine Altitude
        double altitude
                = (p - Earth.WGS84_EQUATORIAL_RADIUS * Math.cos(beta_1))
                * Math.cos(latitude)
                + (ecef[2] - Earth.WGS84_POLAR_RADIUS
                * Math.sin(beta_1))
                * Math.sin(latitude);

        LLA[0] = latitude;
        LLA[1] = longitude;
        LLA[2] = altitude;

        return LLA; //h
    }

    /**
     * Vermeille, H. (2004) "Computing geodetic coordinates from geocentric coordinate", Journal of
     * Geodesy, 78, pp 94-95: Estimate of longitude
     * <p>
     * @param ecef [in meters]
     * <p>
     * @return
     */
    private static double calculateLongitudeFromECEF(double[] ecef) {
        double p = Math.sqrt(Math.pow(ecef[0], 2) + Math.pow(ecef[1], 2));
        if (ecef[1] >= 0) {
            return (Math.PI / 2.0) - 2.0 * Math.atan2(ecef[0], p + ecef[1]);
        } else {
            return -(Math.PI / 2.0) + 2.0 * Math.atan2(ecef[0], p - ecef[1]);
        }
    }

    /**
     *
     * @param a
     * @return
     */
    private static double norm(double[] a) {
        return Math.sqrt(dot(a, a));
    }

    /**
     * dot product
     *
     * @param a
     * @param b
     * @return a dot b
     */
    private static double dot(double[] a, double[] b) {
        double c = 0.0;
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {// row
                c += a[i] * b[i];
            }
        } else {
            throw new ArithmeticException("Unequal Array Sizes");
        }
        return c;
    }
}
