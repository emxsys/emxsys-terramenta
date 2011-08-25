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
import gov.nasa.worldwind.render.SurfaceQuad;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

/**
 * 
 * @author heidtmare
 */
@ActionID(category = "Tools",
id = "com.qna.terramenta.annotations.DrawRectangleAction")
@ActionRegistration(iconBase = "images/rectangle.png",
displayName = "#CTL_DrawRectangleAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/Annotations", position = 5)
})
@Messages("CTL_DrawRectangleAction=Rectangle")
public final class DrawRectangleAction implements ActionListener {

    private static final WorldWindManager wwm = WorldWindManager.getInstance();

    @Override
    public void actionPerformed(ActionEvent e) {
        SurfaceQuad shape = new SurfaceQuad();
        ShapeAttributes attr = new BasicShapeAttributes();
        attr.setInteriorMaterial(new Material(Color.yellow));
        attr.setInteriorOpacity(0.2);
        attr.setOutlineMaterial(new Material(Color.yellow));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);
        shape.setAttributes(attr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Rectangle");
        shape.setValue(AVKey.DISPLAY_ICON, "images/rectangle.png");
        shape.setEnableBatchPicking(false);

        AnnotationController builder = new AnnotationController(wwm.getWorldWindow(), shape);
        builder.setArmed(true);
    }
}
