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
import com.terramenta.globe.properties.RenderableProperties;
import com.terramenta.ribbon.RibbonActionReference;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceCircle;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Tools", id = "com.terramenta.annotations.DrawCircleAction")
@ActionRegistration(
        iconBase = "com/terramenta/annotations/images/draw-circle.png",
        displayName = "#CTL_DrawCircleAction")
@RibbonActionReference(path = "Ribbon/TaskPanes/Insert/Annotations",
        position = 2,
        priority = "top",
        description = "#CTL_DrawCircleAction_Hint",
        tooltipTitle = "#CTL_DrawCircleAction_TooltipTitle",
        tooltipBody = "#CTL_DrawCircleAction_TooltipBody",
        tooltipIcon = "com/terramenta/annotations/images/draw-circle32.png",
        tooltipFooter = "#CTL_DrawCircleAction_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_DrawCircleAction=Circle",
    "CTL_DrawCircleAction_Hint=Draw a circle annotation.",
    "CTL_DrawCircleAction_TooltipTitle=Draw Circle",
    "CTL_DrawCircleAction_TooltipBody=Draws a circular annotation on the surface of the globe.",
    "CTL_DrawCircleAction_TooltipFooter=Press F1 for more help."
})
public final class DrawCircleAction extends TopComponentContextAction {

    private static final ShapeAttributes attr = new BasicShapeAttributes();
    private static final ShapeAttributes highattr = new BasicShapeAttributes();

    static {
        attr.setInteriorMaterial(new Material(Color.yellow));
        attr.setInteriorOpacity(0.2);
        attr.setOutlineMaterial(new Material(Color.yellow));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);

        highattr.copy(attr);
        highattr.setInteriorOpacity(0.4);
        highattr.setOutlineOpacity(1.0);
    }

    private DrawCircleAction() {
        super(GlobeTopComponent.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!GlobeTopComponent.hasOpenInstance()) {
            return;
        }

        //define the shape
        final SurfaceCircle shape = new SurfaceCircle();
        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Circle");
        shape.setValue(AVKey.DISPLAY_ICON, "com/terramenta/annotations/images/draw-circle.png");
        shape.setEnableBatchPicking(false);
        shape.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (RenderableProperties.valueOf(evt.getPropertyName())) {
                    case SELECT:
                        //edit on select
                        AnnotationEditor.modify(shape);
                        break;
                    case VISIBLE:
                        //toggle visibility
                        shape.setVisible((boolean) evt.getNewValue());
                        break;
                }
            }
        });

        //clear edit mode of other annotations
        if (AnnotationEditor.isEditing()) {
            AnnotationEditor.commit();
        }

        //arm the builder
        final AnnotationBuilder builder = new AnnotationBuilder(shape);
        builder.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //edit on create
                if (evt.getPropertyName().equals("armed") && evt.getNewValue().equals(false)) {
                    if (!builder.isCanceled()) {
                        AnnotationEditor.modify(shape);
                    }
                }
            }
        });
        builder.setArmed(true);
    }
}
