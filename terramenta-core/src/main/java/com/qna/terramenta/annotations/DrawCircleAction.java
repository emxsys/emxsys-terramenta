/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.annotations;

import com.qna.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceCircle;
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
id = "com.qna.terramenta.annotations.DrawCircleAction")
@ActionRegistration(iconBase = "images/circle.png",
displayName = "#CTL_DrawCircleAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools/Annotations", position = 2),
    @ActionReference(path = "Toolbars/Annotations", position = 2)
})
@Messages("CTL_DrawCircleAction=Circle")
public final class DrawCircleAction implements ActionListener {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        SurfaceCircle shape = new SurfaceCircle();
        ShapeAttributes attr = new BasicShapeAttributes();
        attr.setInteriorMaterial(new Material(Color.yellow));
        attr.setInteriorOpacity(0.2);
        attr.setOutlineMaterial(new Material(Color.yellow));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);
        shape.setAttributes(attr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Circle");
        shape.setValue(AVKey.DISPLAY_ICON, "images/circle.png");
        shape.setEnableBatchPicking(false);

        AnnotationController builder = new AnnotationController(wwm.getWorldWindow(), shape);
        builder.setArmed(true);
    }
}
