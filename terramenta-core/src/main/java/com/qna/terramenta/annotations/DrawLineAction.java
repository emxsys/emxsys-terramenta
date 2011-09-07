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
import gov.nasa.worldwind.render.SurfacePolyline;
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
id = "com.qna.terramenta.annotations.DrawLineAction")
@ActionRegistration(iconBase = "images/line.png",
displayName = "#CTL_DrawLineAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/Annotations", position = 1)
})
@Messages("CTL_DrawLineAction=Line")
public final class DrawLineAction implements ActionListener {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        SurfacePolyline shape = new SurfacePolyline();
        ShapeAttributes attr = new BasicShapeAttributes();
        attr.setInteriorMaterial(new Material(Color.yellow));
        attr.setInteriorOpacity(0.2);
        attr.setOutlineMaterial(new Material(Color.yellow));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);
        shape.setAttributes(attr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Line");
        shape.setValue(AVKey.DISPLAY_ICON, "images/line.png");
        shape.setEnableBatchPicking(false);

        AnnotationController builder = new AnnotationController(wwm.getWorldWindow(), shape);
        builder.setArmed(true);
    }
}
