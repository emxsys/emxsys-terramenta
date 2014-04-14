package com.terramenta.annotations;

import com.terramenta.actions.TopComponentContextAction;
import com.terramenta.globe.GlobeTopComponent;
import com.terramenta.ribbon.RibbonActionReference;
import java.awt.event.ActionEvent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author R. Wathelet, April 2012
 */
@ActionID(category = "Tools", id = "com.terramenta.annotations.DrawTextAction")
@ActionRegistration(iconBase = "com/terramenta/annotations/images/textAdd.png", displayName = "#CTL_DrawTextAction", popupText = "#CTL_DrawTextAction_Hint")
@ActionReference(path = "Toolbars/Annotations", position = 7)
@RibbonActionReference(path = "Menu/Insert/Annotations",
        position = 7,
        priority = "top",
        description = "#CTL_DrawTextAction_Hint",
        tooltipTitle = "#CTL_DrawTextAction_TooltipTitle",
        tooltipBody = "#CTL_DrawTextAction_TooltipBody",
        tooltipIcon = "com/terramenta/annotations/images/textAdd32.png",
        tooltipFooter = "#CTL_Default_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages(
        {
            "CTL_DrawTextAction=Text",
            "CTL_DrawTextAction_Hint=Add a textual annotation to the globe.",
            "CTL_DrawTextAction_TooltipTitle=Add Text",
            "CTL_DrawTextAction_TooltipBody=Add a textual annotation to the surface of the globe."
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
