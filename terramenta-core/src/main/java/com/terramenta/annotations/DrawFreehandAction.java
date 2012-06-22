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
import gov.nasa.worldwind.render.SurfacePolyline;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
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
@ActionReferences({
    @ActionReference(path = "Menu/Tools/Annotations", position = 0),
    @ActionReference(path = "Toolbars/Annotations", position = 0)
})
@Messages("CTL_DrawFreehandAction=Free Hand")
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
        SurfacePolyline shape = new SurfacePolyline();
        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "User Annotation: Freehand");
        shape.setValue(AVKey.DISPLAY_ICON, "images/pencil.png");
        shape.setEnableBatchPicking(false);

        AnnotationController builder = new AnnotationController(wwm.getWorldWindow(), shape);
        builder.setFreeHand(true);
        builder.setArmed(true);
    }
}
