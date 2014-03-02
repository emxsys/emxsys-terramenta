/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.annotations;

import com.terramenta.globe.WorldWindManager;
import com.terramenta.ribbon.RibbonActionReference;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceQuad;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Tools",
        id = "com.terramenta.annotations.DrawRectangleAction")
@ActionRegistration(iconBase = "com/terramenta/annotations/images/rectangle.png",
        displayName = "#CTL_DrawRectangleAction")
@ActionReference(path = "Toolbars/Annotations", position = 5)
@RibbonActionReference(path = "Menu/Insert/Annotations",
        position = 5,
        priority = "top",
        description = "#CTL_DrawRectangleAction_Hint",
        tooltipTitle = "#CTL_DrawRectangleAction_TooltipTitle",
        tooltipBody = "#CTL_DrawRectangleAction_TooltipBody",
        tooltipIcon = "com/terramenta/annotations/images/rectangle32.png",
        tooltipFooter = "#CTL_Default_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages(
        {
            "CTL_DrawRectangleAction=Rectangle",
            "CTL_DrawRectangleAction_Hint=Draw a rectangle annotation.",
            "CTL_DrawRectangleAction_TooltipTitle=Draw Rectangle",
            "CTL_DrawRectangleAction_TooltipBody=Draws a rectanglular annotation on the surface of the globe."
        })
public final class DrawRectangleAction implements ActionListener {

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

    @Override
    public void actionPerformed(ActionEvent e) {
        final SurfaceQuad shape = new SurfaceQuad();
        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Rectangle");
        shape.setValue(AVKey.DISPLAY_ICON, "com/terramenta/annotations/images/rectangle.png");
        shape.setEnableBatchPicking(false);
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
