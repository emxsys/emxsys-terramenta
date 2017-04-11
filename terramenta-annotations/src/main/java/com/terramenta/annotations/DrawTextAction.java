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
package com.terramenta.annotations;

import com.terramenta.actions.TopComponentContextAction;
import com.terramenta.globe.GlobeTopComponent;
import com.terramenta.ribbon.RibbonActionReference;
import java.awt.event.ActionEvent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author R. Wathelet, April 2012
 */
@ActionID(category = "Tools", id = "com.terramenta.annotations.DrawTextAction")
@ActionRegistration(iconBase = "com/terramenta/annotations/images/draw-text.png", displayName = "#CTL_DrawTextAction", popupText = "#CTL_DrawTextAction_Hint")
@RibbonActionReference(path = "Ribbon/TaskPanes/Insert/Annotations",
        position = 7,
        priority = "top",
        description = "#CTL_DrawTextAction_Hint",
        tooltipTitle = "#CTL_DrawTextAction_TooltipTitle",
        tooltipBody = "#CTL_DrawTextAction_TooltipBody",
        tooltipIcon = "com/terramenta/annotations/images/draw-text32.png",
        tooltipFooter = "#CTL_DrawTextAction_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_DrawTextAction=Text",
    "CTL_DrawTextAction_Hint=Add a textual annotation to the globe.",
    "CTL_DrawTextAction_TooltipTitle=Add Text",
    "CTL_DrawTextAction_TooltipBody=Add a textual annotation to the surface of the globe.",
    "CTL_DrawTextAction_TooltipFooter=Press F1 for more help."
})
public class DrawTextAction extends TopComponentContextAction {

    private DrawTextAction() {
        super(GlobeTopComponent.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!GlobeTopComponent.hasOpenInstance()) {
            return;
        }

        new TextAnnotationEditor().setArmed(true);
    }
}
