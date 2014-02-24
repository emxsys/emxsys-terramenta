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
import gov.nasa.worldwind.render.SurfacePolyline;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Tools",
        id = "com.terramenta.annotations.DrawFreehandAction")
@ActionRegistration(iconBase = "images/pencil.png",
        displayName = "#CTL_DrawFreehandAction")
@ActionReference(path = "Toolbars/Annotations", position = 0)
@RibbonActionReference(path = "Menu/Insert/Annotations",
        position = 0,
        priority = "top",
        description = "#CTL_DrawFreehandAction_Hint",
        tooltipTitle = "#CTL_DrawFreehandAction_TooltipTitle",
        tooltipBody = "#CTL_DrawFreehandAction_TooltipBody",
        tooltipIcon = "images/pencil32.png",
        tooltipFooter = "#CTL_Default_TooltipFooter",
        tooltipFooterIcon = "images/help.png")
@Messages(
        {
            "CTL_DrawFreehandAction=Free Hand",
            "CTL_DrawFreehandAction_Hint=Draw a free hand shape.",
            "CTL_DrawFreehandAction_TooltipTitle=Draw Free Hand",
            "CTL_DrawFreehandAction_TooltipBody=Draws a free hand shape annotation on the surface of the globe."
        })

public final class DrawFreehandAction implements ActionListener {

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
        final SurfacePolyline shape = new SurfacePolyline();
        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Freehand");
        shape.setValue(AVKey.DISPLAY_ICON, "images/pencil.png");
        shape.setEnableBatchPicking(false);
//        shape.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (evt.getPropertyName().equals("SELECT")) {
//                    AnnotationEditor.enableEdit(shape);
//                }
//            }
//        });

        AnnotationBuilder builder = new AnnotationBuilder(wwm.getWorldWindow(), shape);
        builder.setFreeHand(true);
        builder.setArmed(true);
    }
}
