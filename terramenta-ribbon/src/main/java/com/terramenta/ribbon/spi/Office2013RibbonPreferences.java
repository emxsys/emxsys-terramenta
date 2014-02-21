/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License 1.0 (the "License"). You may not use this file except
 * in compliance with the License. You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0. See the License for the specific
 * language governing permissions and limitations under the License. 
 */
package com.terramenta.ribbon.spi;

import com.terramenta.ribbon.ColorUtil;
import com.terramenta.ribbon.api.RibbonPreferences;
import java.awt.Color;
import java.awt.Dimension;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;

/**
 * Office 2013 Ribbon Preferences.
 * 
 * @author Bruce Schubert
 * @version $Id$
 */
public class Office2013RibbonPreferences implements RibbonPreferences {

    @Override
    public Object[] getLafClassDefaults() {
        return new Object[]{
            // UI Classes
            "RibbonUI", "com.terramenta.ribbon.Office2013RibbonUI",
            "RibbonBandUI", "com.terramenta.ribbon.Office2013RibbonBandUI",
            "BandControlPanelUI", "com.terramenta.ribbon.Office2013BandControlPanelUI",
            "RibbonTaskToggleButtonUI", "com.terramenta.ribbon.Office2013RibbonTaskToggleButtonUI",
            "RibbonApplicationMenuButtonUI", "com.terramenta.ribbon.Office2013RibbonApplicationMenuButtonUI",
            // Colors
            "Ribbon.background", ColorUtil.darker(FlamingoUtilities.getColor(Color.darkGray, "Panel.background"), 0.10),
            "ControlPanel.background", FlamingoUtilities.getColor(Color.lightGray, "Panel.background"),
            "AppButton.background", Color.black,
            "AppButton.foreground", Color.white
        };
    }

    @Override
    public Dimension getPreferredBandSize() {
        return new Dimension(40, 96); // Full size supports buttons with two lines of text
    }

    @Override
    public boolean getUsePopupMenus() {
        return true;
    }

    @Override
    public boolean getUseTabNameForTasksBand() {
        return true;
    }

    @Override
    public boolean getAlwaysDisplayButtonText() {
        return true;
    }

}
