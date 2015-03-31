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

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author heidtmare
 */
@ServiceProvider(service = EciController.class)
public class EciController {

    /**
     *
     */
    public static final double offsetRotdeg = -90.0; // jogl coordinate Greenwich to ECI x-axis offset
    private double rotateECIdeg = 280.46061837 + offsetRotdeg; // rotation in degrees (default j2k)
    private double currentMJD = 51544.5; // current modified julian date, universal time (default J2k)

    /**
     *
     * @return
     */
    public double getCurrentMJD() {
        return currentMJD;
    }

    /**
     *
     * @param currentMJD
     */
    public void setCurrentMJD(double currentMJD) {
        this.currentMJD = currentMJD;

        // centuries since J2000.0
        double tt = (currentMJD - 51544.5) / 36525.0;

        // now calculate the mean sidereal time at Greenwich (UT time) in degrees
        rotateECIdeg = ((280.46061837 + 360.98564736629 * (currentMJD - 51544.5)) + 0.000387933 * tt * tt - tt * tt * tt / 38710000.0 + offsetRotdeg) % 360.0;
    }

    /**
     *
     * @return
     */
    public double getRotateECIdeg() {
        return rotateECIdeg;
    }
}
