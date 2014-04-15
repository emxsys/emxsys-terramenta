/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * The contents of this file are subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.ribbon.spi;

import com.terramenta.ribbon.api.RibbonPreferences;
import java.net.URL;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Chris.Heidt
 */
@ServiceProvider(service = RibbonPreferencesProvider.class)
public class Office2007RibbonPreferencesProvider extends RibbonPreferencesProvider {

    private final String name = "Office 2007";
    private final String description = "A ribbon style modeled after the Office 2007 look and feel.";
    private URL preview;
    private RibbonPreferences preferences;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public URL getPreview() {
        if (preview == null) {
            preview = getClass().getResource("/com/terramenta/ribbon/images/preview-office2007.png");
        }
        return preview;
    }

    @Override
    public RibbonPreferences getPreferences() {
        if (preferences == null) {
            preferences = new Office2007RibbonPreferences();
        }
        return preferences;
    }

}
