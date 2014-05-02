/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.globe.actions;

import com.terramenta.globe.WorldWindManager;
import com.terramenta.ribbon.RibbonActionReference;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools", id = "com.terramenta.globe.actions.ScreenShotAction")
@ActionRegistration(iconBase = "com/terramenta/globe/images/screenshot.png", displayName = "#CTL_ScreenShotAction", popupText = "Save an image of the current globe.")
@RibbonActionReference(path = "Menu/Tools/Create",
        position = 100,
        priority = "top",
        description = "#CTL_ScreenShotAction_Hint",
        tooltipTitle = "#CTL_ScreenShotAction_TooltipTitle",
        tooltipBody = "#CTL_ScreenShotAction_TooltipBody",
        tooltipIcon = "com/terramenta/globe/images/screenshot32.png",
        //tooltipFooter = "#CTL_Default_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_ScreenShotAction=Screen Shot",
    "CTL_ScreenShotAction_Hint=Save an image of the current globe.",
    "CTL_ScreenShotAction_TooltipTitle=Create Screen Shot",
    "CTL_ScreenShotAction_TooltipBody=Creates a screen shot of the current globe in the user's home directory.",})

/**
 *
 */
public class ScreenShotAction extends gov.nasa.worldwindx.examples.util.ScreenShotAction {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    public ScreenShotAction() {
        super(wwm.getWorldWindow());
    }
}
