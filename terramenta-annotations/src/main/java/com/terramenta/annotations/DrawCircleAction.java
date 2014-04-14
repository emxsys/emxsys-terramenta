/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.annotations;

import com.terramenta.actions.TopComponentContextAction;
import com.terramenta.globe.GlobeTopComponent;
import com.terramenta.globe.WorldWindManager;
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
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Tools", id = "com.terramenta.annotations.DrawCircleAction")
@ActionRegistration(iconBase = "com/terramenta/annotations/images/draw-circle.png", displayName = "#CTL_DrawCircleAction")
@ActionReference(path = "Toolbars/Annotations", position = 2)
@RibbonActionReference(path = "Menu/Insert/Annotations",
        position = 2,
        priority = "top",
        description = "#CTL_DrawCircleAction_Hint",
        tooltipTitle = "#CTL_DrawCircleAction_TooltipTitle",
        tooltipBody = "#CTL_DrawCircleAction_TooltipBody",
        tooltipIcon = "com/terramenta/annotations/images/draw-circle32.png",
        tooltipFooter = "#CTL_Default_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages(
        {
            "CTL_DrawCircleAction=Circle",
            "CTL_DrawCircleAction_Hint=Draw a circle annotation.",
            "CTL_DrawCircleAction_TooltipTitle=Draw Circle",
            "CTL_DrawCircleAction_TooltipBody=Draws a circular annotation on the surface of the globe."
        })
public final class DrawCircleAction extends TopComponentContextAction {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
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

        final SurfaceCircle shape = new SurfaceCircle();
        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Circle");
        shape.setValue(AVKey.DISPLAY_ICON, "com/terramenta/annotations/images/draw-circle.png");
        shape.setEnableBatchPicking(false);

        //edit on select
        shape.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("SELECT")) {
                    AnnotationEditor.modify(shape);
                }
            }
        });

        if (AnnotationEditor.isEditing()) {
            AnnotationEditor.commit();
        }

        AnnotationBuilder builder = new AnnotationBuilder(wwm.getWorldWindow(), shape);
        builder.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("armed") && evt.getNewValue().equals(false)) {
                    AnnotationEditor.modify(shape);
                }
            }
        });
        builder.setArmed(true);
    }
}
