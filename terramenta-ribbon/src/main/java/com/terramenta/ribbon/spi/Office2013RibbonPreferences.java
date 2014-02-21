/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License 1.0 (the "License"). You may not use this file except
 * in compliance with the License. You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0. See the License for the specific
 * language governing permissions and limitations under the License. 
 */
package com.terramenta.ribbon.spi;

import com.terramenta.ribbon.ColorUtil;
import java.awt.Color;
import java.awt.Dimension;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;


/**
 * Office 2013 Ribbon Preferences.
 *
 * @author Bruce Schubert
 */
public class Office2013RibbonPreferences extends BasicRibbonPreferences
{

    public Office2013RibbonPreferences()
    {
        setLafClassDefaults(
            new Object[]
            {
                // UI Classes
                "RibbonUI", "com.terramenta.ribbon.Office2013RibbonUI",
                "RibbonBandUI", "com.terramenta.ribbon.Office2013RibbonBandUI",
                "BandControlPanelUI", "com.terramenta.ribbon.Office2013BandControlPanelUI",
                "RibbonTaskToggleButtonUI", "com.terramenta.ribbon.Office2013RibbonTaskToggleButtonUI",
                "RibbonApplicationMenuButtonUI", "com.terramenta.ribbon.Office2013RibbonApplicationMenuButtonUI",
                // Colors
                "Ribbon.background", ColorUtil.darker(FlamingoUtilities.getColor(Color.darkGray, "Panel.background"), 0.20),
                "ControlPanel.background", FlamingoUtilities.getColor(Color.lightGray, "Panel.background"),
                "AppButton.background", Color.black,
                "AppButton.foreground", Color.white,
                "TaskButton.highlight", Color.cyan,
            });
        setPreferredBandSize(new Dimension(40, 96));// Full size supports buttons with two lines of text
        setAlwaysDisplayButtonText(true);
        setShouldDisplayTaskBar(false); // TODO: draw taskbar on glass pane.
        setUsePopupMenus(true);
        setUseTabNameForTasksBand(false);
    }
}
