/*
 * Copyright (c) 2014, Bruce Schubert. <bruce@emxsys.com>
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
import java.awt.Dimension;

/**
 * BasicRibbonPreferences provides an entry point for Ribbon customizations. Customized preferences
 * can be derived from this class, which will ensure there is always a basic value for any
 * RibbonPreference property.
 *
 * @author Bruce Schubert
 */
public class BasicRibbonPreferences implements RibbonPreferences {

    private Object[] lafClassDefaults;
    private Dimension preferredBandSize;
    private boolean shouldDisplayTaskBar = true;
    private boolean usePopupMenus = true;
    private boolean useTabNameForTasksBand = true;      // true = original Terramenta style
    private boolean alwaysDisplayButtonText = false;    // true = original Terramenta style
    private boolean alwaysDisplayGroupText = true;      // true = original Terramenta style

    @Override
    public Object[] getLafClassDefaults() {
        if (lafClassDefaults == null) {
            lafClassDefaults = new Object[]{
                // UI Classes
                "RibbonApplicationMenuButtonUI", "com.terramenta.ribbon.NbRibbonApplicationMenuButtonUI",};
        }
        return lafClassDefaults;

    }

    @Override
    public Dimension getPreferredBandSize() {
        if (preferredBandSize == null) {
            preferredBandSize = new Dimension(40, 60); // Original Terramenta short ribbon without text
        }
        return preferredBandSize;
    }

    @Override
    public boolean getAlwaysDisplayButtonText() {
        return alwaysDisplayButtonText;
    }

    @Override
    public boolean getAlwaysDisplayGroupText() {
        return alwaysDisplayGroupText;
    }

    @Override
    public boolean getShouldDisplayTaskBar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getUsePopupMenus() {
        return usePopupMenus;
    }

    @Override
    public boolean getUseTabNameForTasksBand() {
        return useTabNameForTasksBand;
    }

    protected void setLafClassDefaults(Object[] lafClassDefaults) {
        this.lafClassDefaults = lafClassDefaults;
    }

    protected void setPreferredBandSize(Dimension preferredBandSize) {
        this.preferredBandSize = preferredBandSize;
    }

    protected void setAlwaysDisplayButtonText(boolean alwaysDisplayButtonText) {
        this.alwaysDisplayButtonText = alwaysDisplayButtonText;
    }

    protected void setAlwaysDisplayGroupText(boolean alwaysDisplayGroupText) {
        this.alwaysDisplayGroupText = alwaysDisplayGroupText;
    }

    protected void setShouldDisplayTaskBar(boolean shouldDisplayTaskBar) {
        this.shouldDisplayTaskBar = shouldDisplayTaskBar;
    }

    protected void setUsePopupMenus(boolean usePopupMenus) {
        this.usePopupMenus = usePopupMenus;
    }

    protected void setUseTabNameForTasksBand(boolean useTabNameForTasksBand) {
        this.useTabNameForTasksBand = useTabNameForTasksBand;
    }

}
