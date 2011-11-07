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
import gov.nasa.worldwind.render.SurfaceEllipse;
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
id = "com.terramenta.annotations.DrawEllipseAction")
@ActionRegistration(iconBase = "images/ellipse.png",
displayName = "#CTL_DrawEllipseAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools/Annotations", position = 3),
    @ActionReference(path = "Toolbars/Annotations", position = 3)
})
@Messages("CTL_DrawEllipseAction=Ellipse")
public final class DrawEllipseAction implements ActionListener {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        SurfaceEllipse shape = new SurfaceEllipse();
        ShapeAttributes attr = new BasicShapeAttributes();
        attr.setInteriorMaterial(new Material(Color.yellow));
        attr.setInteriorOpacity(0.2);
        attr.setOutlineMaterial(new Material(Color.yellow));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);
        shape.setAttributes(attr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Ellipse");
        shape.setValue(AVKey.DISPLAY_ICON, "images/ellipse.png");
        shape.setEnableBatchPicking(false);

        AnnotationController builder = new AnnotationController(wwm.getWorldWindow(), shape);
        builder.setArmed(true);
    }
}
