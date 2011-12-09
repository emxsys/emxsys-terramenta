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
@ActionID(category = "Tools", id = "com.terramenta.annotations.DrawDistanceAction")
@ActionRegistration(iconBase = "images/measurements.png", displayName = "#CTL_DrawDistanceAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools/Annotations", position = 7),
    @ActionReference(path = "Toolbars/Annotations", position = 7)
})
@Messages("CTL_DrawDistanceAction=Distance Measurement")
public final class DrawDistanceAction implements ActionListener {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        SurfacePolyline shape = new SurfacePolyline();
        ShapeAttributes attr = new BasicShapeAttributes();
        attr.setInteriorMaterial(new Material(Color.red));
        attr.setInteriorOpacity(0.2);
        attr.setOutlineMaterial(new Material(Color.red));
        attr.setOutlineOpacity(0.6);
        attr.setOutlineWidth(2);
        attr.setOutlineStipplePattern((short) 0xAAAA);
        attr.setOutlineStippleFactor(8);
        shape.setAttributes(attr);
        shape.setValue(AVKey.DISPLAY_NAME, "Distance Measurement");
        shape.setValue(AVKey.DISPLAY_ICON, "images/measurements.png");
        shape.setEnableBatchPicking(false);

        AnnotationController builder = new AnnotationController(wwm.getWorldWindow(), shape);
        builder.setShowLabel(true);
        builder.setArmed(true);
    }
}
