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
package com.terramenta.globe.utilities;

import gov.nasa.worldwind.globes.Earth;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.jastronomy.jsofa.JSOFA;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class CoordinateConversion {

    private static final double eps_mach = 2.22E-17; // machine precision (double?)
    private static final double eps = 1.0e3 * eps_mach;   // Convergence criterion

    /**
     *
     * @param jd
     * @param teme
     * @return
     */
    public static double[] teme2ecef(double jd, double[] teme) {
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
    public static double[] ecef2lla(double[] ecef) {
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
    public static double calculateLongitudeFromECEF(double[] ecef) {
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
    public static double norm(double[] a) {
        return Math.sqrt(dot(a, a));
    }

    /**
     * dot product
     *
     * @param a
     * @param b
     * @return a dot b
     */
    public static double dot(double[] a, double[] b) {
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
