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

import gov.nasa.worldwind.globes.Earth;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.jastronomy.jsofa.JSOFA;

/**
 * Initializes the Moon Position Object
 *
 * See Vallado 2013 "Fundimentals of Astrondynamics and Applications" corresponds to algorithm 31
 * (Moon). Uses JSOFA, 2003 estimates for mean anomaly, mean arguments, and mean elongation.
 * <p>
 */
public class MoonPosition {

    /**
     *
     * @param j2000
     * @return lla (radians, radians, meters)
     */
    public static double[] getMoonPositionLLA(double j2000) {
        return ECEF2LLA(getMoonPositionECEF(j2000));
    }

    /**
     *
     * @param j2000: jd in J2000, ideally this should be TDB, however Kaplan 2005:43) has stated
     *               that TT (terrestial time) and TDB (Barycentric Dynamical Time) if used
     *               interchangeably here, will yield negligible results
     * <p>
     * @return ECEF (IJK) cooordinates for the MOON
     */
    public static double[] getMoonPositionECEF(double j2000) {

        double tdbTime = (j2000 - JSOFA.DJ00) / JSOFA.DJC;

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
        double eclipticLongitudeMoon = generateMoonLongitude(tdbTime)
                + 6.29 * Math.sin(meanAnomalyOfMoon)
                - 1.27 * Math.sin(meanAnomalyOfMoon - 2 * meanElongationOfMoonFromSun)
                + 0.66 * Math.sin(2 * meanElongationOfMoonFromSun)
                + 0.21 * Math.sin(2 * meanAnomalyOfMoon)
                - 0.19 * Math.sin(meanAnomalyOfSun)
                - 0.11 * Math.sin(2 * meanArgumentOfLatitude);

        double eclipticLatitudeMoon = 5.13 * Math.sin(meanArgumentOfLatitude)
                + 0.28 * Math.sin(meanAnomalyOfMoon + meanArgumentOfLatitude)
                - 0.28 * Math.sin(meanArgumentOfLatitude - meanAnomalyOfMoon)
                - 0.17 * Math.sin(meanArgumentOfLatitude - 2 * meanElongationOfMoonFromSun);

        double parallaxMoon = 0.9508
                + 0.0518 * Math.cos(meanAnomalyOfMoon)
                + 0.0095 * Math.cos(meanAnomalyOfMoon - 2 * meanElongationOfMoonFromSun)
                + 0.0078 * Math.cos(2 * meanElongationOfMoonFromSun)
                + 0.0028 * Math.cos(2 * meanAnomalyOfMoon);

        // Flip Positive
        if (eclipticLongitudeMoon < 0) {
            eclipticLongitudeMoon = 360 + eclipticLongitudeMoon;
        }

        eclipticLatitudeMoon = Math.toRadians(eclipticLatitudeMoon);
        eclipticLongitudeMoon = Math.toRadians(eclipticLongitudeMoon);
        parallaxMoon = Math.toRadians(parallaxMoon);

        //====================================================================
        /**
         * Vallado (2014) Equation 3.68, Mean Obliquity of Earth
         */
        double obliquityOfTheEcliptic = JSOFA.jauObl06(j2000, 0.0);

        // Distance to the moon, in meters
        double r_moon = (1 / Math.sin(parallaxMoon)) * Earth.WGS84_EQUATORIAL_RADIUS;

        double cMoonLat = Math.cos(eclipticLatitudeMoon);
        double sMoonLat = Math.sin(eclipticLatitudeMoon);

        double cMoonLon = Math.cos(eclipticLongitudeMoon);
        double sMoonLon = Math.sin(eclipticLongitudeMoon);

        double cEcliptic = Math.cos(obliquityOfTheEcliptic);
        double sEcliptic = Math.sin(obliquityOfTheEcliptic);

        // Geocentric Equatorial Frame
        RealVector ecef = MatrixUtils.createRealVector(
                new double[]{
                    cMoonLat * cMoonLon,
                    cEcliptic * cMoonLat * sMoonLon - sEcliptic * sMoonLat,
                    sEcliptic * cMoonLat * sMoonLon + cEcliptic * sMoonLat});

        ecef = ecef.mapMultiply(r_moon);

        return new double[]{ecef.getEntry(0), ecef.getEntry(1), ecef.getEntry(2)};
    }

    /**
     * Find the longitude of the moon Meeus (1991:132)
     * <p>
     * Meeus, J. (1991) Astronomical Algorithms. Richmond, VA., Willmann Bell Inc.
     * <p>
     * @param tdbTime <p>
     * @return in degrees
     */
    private static double generateMoonLongitude(double tdbTime) {
        return (218.32 + 481267.8813 * tdbTime) % 360.0;
    }

    public static double[] ECEF2LLA(double[] pos) {
        double[] lla = new double[3];

        // WGS84 ellipsoid constants:
        double a = Earth.WGS84_EQUATORIAL_RADIUS;
        double e = 8.1819190842622e-2;

        double b = Math.sqrt(Math.pow(a, 2.0) * (1 - Math.pow(e, 2)));
        double ep = Math.sqrt((Math.pow(a, 2.0) - Math.pow(b, 2.0)) / Math.pow(b, 2.0));
        double p = Math.sqrt(Math.pow(pos[0], 2.0) + Math.pow(pos[1], 2.0));
        double th = Math.atan2(a * pos[2], b * p);
        lla[1] = Math.atan2(pos[1], pos[0]);
        lla[0] = Math.atan2((pos[2] + Math.pow(ep, 2.0) * b * Math.pow(Math.sin(th), 3.0)), (p - Math.pow(e, 2.0) * a * Math.pow(Math.cos(th), 3.0)));
        double N = a / Math.sqrt(1 - Math.pow(e, 2.0) * Math.pow(Math.sin(lla[0]), 2.0));
        lla[2] = p / Math.cos(lla[0]) - N;

        if (lla[1] < 0) {
            lla[1] = 2.0 * Math.PI + lla[1];
        }

        // return lon in range [0,2*pi)
        lla[1] = lla[1] % (2.0 * Math.PI); // modulus

        // correct for numerical instability in altitude near exact poles:
        // (after this correction, error is about 2 millimeters, which is about
        // the same as the numerical precision of the overall function)
        if (Math.abs(pos[0]) < 1.0 & Math.abs(pos[1]) < 1.0) {
            lla[2] = Math.abs(pos[2]) - b;
        }

        // now scale longitude from [0,360] -> [-180,180]
        if (lla[1] > Math.PI) // > 180
        {
            lla[1] = lla[1] - 2.0 * Math.PI;
        }

        /*
         * // now correct for time shift // account for earth rotations lla[1] = lla[1]-(280.4606
         * +360.9856473*d)*Math.PI/180.0; // correction ?? //lla[1] = lla[1]-Math.PI/2.0; // now
         * insure [-180,180] range double div = Math.floor(lla[1]/(2*Math.PI)); lla[1] = lla[1] -
         * div*2*Math.PI; if(lla[1] > Math.PI) { lla[1] = lla[1]- 2.0*Math.PI; }
         */
        return lla;
    }
}
