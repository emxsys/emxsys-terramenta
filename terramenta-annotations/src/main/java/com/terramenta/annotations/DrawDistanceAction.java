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
import gov.nasa.worldwind.render.SurfacePolyline;
import java.awt.Color;
import java.awt.event.ActionEvent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Tools", id = "com.terramenta.annotations.DrawDistanceAction")
@ActionRegistration(iconBase = "com/terramenta/annotations/images/measure-distance.png", displayName = "#CTL_DrawDistanceAction", popupText = "#CTL_DrawDistanceAction_Hint")
@ActionReference(path = "Toolbars/Annotations", position = 8)
@RibbonActionReference(path = "Menu/Insert/Annotations",
        position = 8,
        priority = "top",
        description = "#CTL_DrawDistanceAction_Hint",
        tooltipTitle = "#CTL_DrawDistanceAction_TooltipTitle",
        tooltipBody = "#CTL_DrawDistanceAction_TooltipBody",
        tooltipIcon = "com/terramenta/annotations/images/measure-distance32.png",
        tooltipFooter = "#CTL_Default_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages(
        {
            "CTL_DrawDistanceAction=Distance Measurement",
            "CTL_DrawDistanceAction_Hint=Point to point distance measurement.",
            "CTL_DrawDistanceAction_TooltipTitle=Distance Measurement Tool",
            "CTL_DrawDistanceAction_TooltipBody=Measures point to point distances on the globe."
        })

public final class DrawDistanceAction extends TopComponentContextAction {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private static final ShapeAttributes attr = new BasicShapeAttributes();
    private static final ShapeAttributes highattr = new BasicShapeAttributes();

    static {
        attr.setOutlineMaterial(new Material(Color.red));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);
        attr.setOutlineStipplePattern((short) 0xAAAA);
        attr.setOutlineStippleFactor(8);

        highattr.copy(attr);
        highattr.setOutlineOpacity(1.0);
    }

    private DrawDistanceAction() {
        super(GlobeTopComponent.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!GlobeTopComponent.hasOpenInstance()) {
            return;
        }

        final SurfacePolyline shape = new SurfacePolyline();

        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "Distance Measurement");
        shape.setValue(AVKey.DISPLAY_ICON, "com/terramenta/annotations/images/measure-distance.png");
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
        builder.setShowLabel(true);
        builder.setArmed(true);
    }
}
