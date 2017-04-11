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
import gov.nasa.worldwind.render.SurfaceSquare;
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
@ActionID(category = "Tools",
        id = "com.terramenta.annotations.DrawSquareAction")
@ActionRegistration(iconBase = "com/terramenta/annotations/images/draw-square.png",
        displayName = "#CTL_DrawSquareAction")
@RibbonActionReference(path = "Ribbon/TaskPanes/Insert/Annotations",
        position = 4,
        priority = "top",
        description = "#CTL_DrawSquareAction_Hint",
        tooltipTitle = "#CTL_DrawSquareAction_TooltipTitle",
        tooltipBody = "#CTL_DrawSquareAction_TooltipBody",
        tooltipIcon = "com/terramenta/annotations/images/draw-square32.png",
        tooltipFooter = "#CTL_DrawSquareAction_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_DrawSquareAction=Square",
    "CTL_DrawSquareAction_Hint=Draw a square annotation.",
    "CTL_DrawSquareAction_TooltipTitle=Draw Square",
    "CTL_DrawSquareAction_TooltipBody=Draws a square annotation on the surface of the globe.",
    "CTL_DrawSquareAction_TooltipFooter=Press F1 for more help."
})
public final class DrawSquareAction extends TopComponentContextAction {

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

    private DrawSquareAction() {
        super(GlobeTopComponent.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!GlobeTopComponent.hasOpenInstance()) {
            return;
        }

        //define the shape
        final SurfaceSquare shape = new SurfaceSquare();
        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Square");
        shape.setValue(AVKey.DISPLAY_ICON, "com/terramenta/annotations/images/draw-square.png");
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

        // arm the builder
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
