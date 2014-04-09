/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class Office2013RibbonPreferencesProvider extends RibbonPreferencesProvider {

    private final String name = "Office 2013";
    private final String description = "A ribbon style modeled after the Office 2013 look and feel.";
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
            preview = getClass().getResource("/com/terramenta/ribbon/images/preview-office2013.png");
        }
        return preview;
    }

    @Override
    public RibbonPreferences getPreferences() {
        if (preferences == null) {
            preferences = new Office2013RibbonPreferences();
        }
        return preferences;
    }

}
