/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License 1.0 (the "License"). You may not use this file except
 * in compliance with the License. You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0. See the License for the specific
 * language governing permissions and limitations under the License. 
 */
package com.terramenta.ribbon.spi;

import com.terramenta.ribbon.api.RibbonPreferences;
import org.openide.util.Lookup;

/**
 * RibbonPreferencesProvider is a service provider interface used by the application for injecting
 * its own ribbon configuration into the lookup for discovery a RibbonComponentProvider.
 *
 * @author Bruce Schubert
 * @version $Id$
 */
public abstract class RibbonPreferencesProvider {

    /**
     * Gets the preferences and settings used to configure the ribbon bar.
     *
     * @return the RibbonPreferences instance used to configure the ribbon bar.
     */
    public abstract RibbonPreferences getPreferences();

    /**
     * Service provider interface for the ribbon preferences.
     *
     * @return the provider found on the the global lookup; if not found, the default Terramenta
     * preferences provider is returned.
     */
    public static RibbonPreferencesProvider getDefault() {
        RibbonPreferencesProvider provider = Lookup.getDefault().lookup(RibbonPreferencesProvider.class);
        if (provider == null) {
            provider = new DefaultRibbonPreferencesProvider();
        }
        return provider;
    }

    /**
     * Creates the default preferences for the Terramenta application.
     */
    private final static class DefaultRibbonPreferencesProvider extends RibbonPreferencesProvider {

        private RibbonPreferences preferences;

        @Override
        public RibbonPreferences getPreferences() {
            if (preferences == null) {
                //preferences = new BasicRibbonPreferences();       // Test
                //preferences = new Office2013RibbonPreferences();  // Test
                preferences = new DefaultRibbonPreferences(); 
            }
            return preferences;
        }

    }

}
