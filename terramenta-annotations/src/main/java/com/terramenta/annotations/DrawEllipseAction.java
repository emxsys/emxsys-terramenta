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
import gov.nasa.worldwind.render.SurfaceEllipse;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Tools",
        id = "com.terramenta.annotations.DrawEllipseAction")
@ActionRegistration(iconBase = "com/terramenta/annotations/images/draw-ellipse.png",
        displayName = "#CTL_DrawEllipseAction")
@ActionReference(path = "Toolbars/Annotations", position = 3)
@RibbonActionReference(path = "Menu/Insert/Annotations",
        position = 3,
        priority = "top",
        description = "#CTL_DrawEllipseAction_Hint",
        tooltipTitle = "#CTL_DrawEllipseAction_TooltipTitle",
        tooltipBody = "#CTL_DrawEllipseAction_TooltipBody",
        tooltipIcon = "com/terramenta/annotations/images/draw-ellipse32.png",
        tooltipFooter = "#CTL_DrawEllipseAction_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_DrawEllipseAction=Ellipse",
    "CTL_DrawEllipseAction_Hint=Draw an ellipse.",
    "CTL_DrawEllipseAction_TooltipTitle=Draw Ellipse",
    "CTL_DrawEllipseAction_TooltipBody=Draws an elliptical annotation on surface of the globe.",
    "CTL_DrawEllipseAction_TooltipFooter=Press F1 for more help."
})
public final class DrawEllipseAction extends TopComponentContextAction {

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

    private DrawEllipseAction() {
        super(GlobeTopComponent.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!GlobeTopComponent.hasOpenInstance()) {
            return;
        }

        //define the shape
        final SurfaceEllipse shape = new SurfaceEllipse();
        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Ellipse");
        shape.setValue(AVKey.DISPLAY_ICON, "com/terramenta/annotations/images/draw-ellipse.png");
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
