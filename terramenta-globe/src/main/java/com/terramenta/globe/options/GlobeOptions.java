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
package com.terramenta.globe.options;

import com.terramenta.globe.utilities.CoordinateSystem;
import gov.nasa.worldwind.avlist.AVKey;

/**
 *
 * @author Chris.Heidt
 */
public class GlobeOptions {

    public static final String IS_FLAT = "options.globe.isFlat";
    public static final String FLAT_PROJECTION = "options.globe.flatProjection";
    public static final String DISPLAY_MODE = "options.globe.displayMode";
    public static final String FOCUS_ANGLE = "options.globe.focusAngle";
    public static final String STATUS_BAR = "options.globe.statusBar";
    public static final String WORLDWIND_CONFIG = "options.globe.worldwindConfig";
    public static final String QUICKTIPS = "options.globe.quickTips";
    public static final String CLASSIFICATION = "options.globe.classification";
    public static final String PROTECTION = "options.globe.protection";

    public static final boolean DEFAULT_IS_FLAT = false;
    public static final String DEFAULT_FLAT_PROJECTION = "Lat Lon";
    public static final String DEFAULT_DISPLAY_MODE = AVKey.STEREO_MODE_NONE;
    public static final int DEFAULT_FOCUS_ANGLE = 0;
    public static final String DEFAULT_STATUS_BAR = CoordinateSystem.LatLon.name();
    public static final String DEFAULT_WORLDWIND_CONFIG = "";
    public static final boolean DEFAULT_QUICKTIPS = true;
    public static final String DEFAULT_CLASSIFICATION = "UNCLASSIFIED";
    public static final String DEFAULT_PROTECTION = "";
}
