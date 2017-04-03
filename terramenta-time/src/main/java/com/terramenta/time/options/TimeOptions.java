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
package com.terramenta.time.options;

import java.time.ZoneId;
import java.util.Locale;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Chris.Heidt
 */
public class TimeOptions {

    public static final String TIMEZONE = "tm.time.timezone";
    public static final String LOCALE = "tm.time.locale";
    public static final String FORMAT = "tm.time.format";

    public static final String DEFAULT_TIMEZONE = ZoneId.systemDefault().getId();
    public static final String DEFAULT_LOCALE = Locale.getDefault().toLanguageTag();
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss XXX";

    public static final Preferences getPreferences() {
        return NbPreferences.forModule(TimeOptions.class);
    }
}
