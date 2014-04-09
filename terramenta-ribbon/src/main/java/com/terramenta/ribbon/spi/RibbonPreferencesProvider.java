/*
 * Copyright (c) 2012, Bruce Schubert. <bruce@emxsys.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the Emxsys company nor the names of its 
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.terramenta.ribbon.spi;

import com.terramenta.ribbon.api.RibbonPreferences;
import com.terramenta.ribbon.options.RibbonOptions;
import java.net.URL;
import java.util.Collection;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 * RibbonPreferencesProvider is a service provider interface used by the
 * application for injecting its own ribbon configuration into the lookup for
 * discovery a RibbonComponentProvider.
 *
 * @author Bruce Schubert
 */
public abstract class RibbonPreferencesProvider {

    private static final Preferences prefs = NbPreferences.forModule(RibbonOptions.class);

    /**
     * A short name used to identify the preference
     *
     * @return
     */
    public abstract String getName();

    /**
     * Description of the preferences
     *
     * @return
     */
    public abstract String getDescription();

    /**
     * An image thumbnail to be used as a preview for this set of preferences
     *
     * @return
     */
    public abstract URL getPreview();

    /**
     * Gets the preferences and settings used to configure the ribbon bar.
     *
     * @return the RibbonPreferences instance used to configure the ribbon bar.
     */
    public abstract RibbonPreferences getPreferences();

    /**
     * Service provider interface for the ribbon preferences.
     *
     * @return the preferred provider found on the the global lookup; if not found then a
     *         default Terramenta preferences provider is returned.
     */
    public static RibbonPreferencesProvider getDefault() {
        Collection<? extends RibbonPreferencesProvider> providers = Lookup.getDefault().lookupAll(RibbonPreferencesProvider.class);

        //the the preferred provider
        String style = prefs.get(RibbonOptions.STYLE, RibbonOptions.DEFAULT_STYLE);
        for (RibbonPreferencesProvider provider : providers) {
            if (provider.getClass().getName().equals(style)) {
                return provider;
            }
        }

        //not there? then get ANY provider
        if (!providers.isEmpty()) {
            return providers.iterator().next();
        }

        //no providers!?! default to this one i guess...
        return new Office2013CompactRibbonPreferencesProvider();
    }
}
