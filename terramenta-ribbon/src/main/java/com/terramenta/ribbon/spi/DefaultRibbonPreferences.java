/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License 1.0 (the "License"). You may not use this file except
 * in compliance with the License. You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0. See the License for the specific
 * language governing permissions and limitations under the License. 
 */
package com.terramenta.ribbon.spi;

import com.terramenta.ribbon.api.RibbonPreferences;
import java.awt.Dimension;

/**
 * Default Terramenta ribbon prefernces providing a shorter ribbon without button labels.
 * 
 * @author Bruce Schubert
 */
public class DefaultRibbonPreferences implements RibbonPreferences {

    @Override
    public Object[] getLafClassDefaults() {
        return new Object[]{
            // UI Classes
            "RibbonUI", "com.terramenta.ribbon.FileRibbonUI",
            "RibbonApplicationMenuButtonUI", "com.terramenta.ribbon.FileRibbonApplicationMenuButtonUI",
        };
    }

    @Override
    public Dimension getPreferredBandSize() {
        return new Dimension(40, 60); // Full size supports buttons with two lines of text
    }

    @Override
    public boolean getUsePopupMenus() {
        return true;
    }

    @Override
    public boolean getUseTabNameForTasksBand() {
        return false;
    }

    @Override
    public boolean getAlwaysDisplayButtonText() {
        return false;
    }

}
