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
import gov.nasa.worldwind.render.SurfaceSquare;
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
id = "com.qna.terramenta.annotations.DrawSquareAction")
@ActionRegistration(iconBase = "images/square.png",
displayName = "#CTL_DrawSquareAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools/Annotations", position = 4),
    @ActionReference(path = "Toolbars/Annotations", position = 4)
})
@Messages("CTL_DrawSquareAction=Square")
public final class DrawSquareAction implements ActionListener {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        SurfaceSquare shape = new SurfaceSquare();
        ShapeAttributes attr = new BasicShapeAttributes();
        attr.setInteriorMaterial(new Material(Color.yellow));
        attr.setInteriorOpacity(0.2);
        attr.setOutlineMaterial(new Material(Color.yellow));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);
        shape.setAttributes(attr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Square");
        shape.setValue(AVKey.DISPLAY_ICON, "images/square.png");
        shape.setEnableBatchPicking(false);

        AnnotationController builder = new AnnotationController(wwm.getWorldWindow(), shape);
        builder.setArmed(true);
    }
}
