/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.annotations;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 * 
 * @author heidtmare
 */
@ActionID(category = "Tools",
id = "com.terramenta.annotations.DrawPolygonAction")
@ActionRegistration(iconBase = "images/polygon.png",
displayName = "#CTL_DrawPolygonAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools/Annotations", position = 6),
    @ActionReference(path = "Toolbars/Annotations", position = 6)
})
@Messages("CTL_DrawPolygonAction=Polygon")
public final class DrawPolygonAction implements ActionListener {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        SurfacePolygon shape = new SurfacePolygon();
        ShapeAttributes attr = new BasicShapeAttributes();
        attr.setInteriorMaterial(new Material(Color.yellow));
        attr.setInteriorOpacity(0.2);
        attr.setOutlineMaterial(new Material(Color.yellow));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);
        shape.setAttributes(attr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Polygon");
        shape.setValue(AVKey.DISPLAY_ICON, "images/polygon.png");
        shape.setEnableBatchPicking(false);

        AnnotationController builder = new AnnotationController(wwm.getWorldWindow(), shape);
        builder.setFreeHand(true);
        builder.setArmed(true);
    }
}
