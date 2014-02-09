/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.terramenta.time.options;

import java.util.Locale;

/**
 *
 * @author Chris.Heidt
 */
public class TimeOptions {
    public static final String TIMEZONE = "tm.time.timezone";
    public static final String LOCALE = "tm.time.locale";
    public static final String FORMAT = "tm.time.format";
    
    public static final String DEFAULT_TIMEZONE = "UTC";
    public static final String DEFAULT_LOCALE = Locale.getDefault().toLanguageTag();
    public static final String DEFAULT_FORMAT = "yyyy/MM/dd HH:mm:ss";
}
