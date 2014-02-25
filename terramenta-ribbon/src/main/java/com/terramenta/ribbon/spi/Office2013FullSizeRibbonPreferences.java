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

import com.terramenta.ribbon.ColorUtil;
import java.awt.Color;
import java.awt.Dimension;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;


/**
 * Office 2013 Ribbon Preferences.
 *
 * @author Bruce Schubert
 */
public class Office2013FullSizeRibbonPreferences extends BasicRibbonPreferences
{

    public Office2013FullSizeRibbonPreferences()
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
        setAlwaysDisplayGroupText(false);
        setShouldDisplayTaskBar(false); // TODO: draw taskbar on glass pane.
        setUsePopupMenus(true);
        setUseTabNameForTasksBand(false);
    }
}
