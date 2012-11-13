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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    @ActionReference(path = "Menu/Tools/Annotations", position = 8),
    @ActionReference(path = "Toolbars/Annotations", position = 8)
})
@Messages("CTL_DrawDistanceAction=Distance Measurement")
public final class DrawDistanceAction implements ActionListener {

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

    @Override
    public void actionPerformed(ActionEvent e) {
        final SurfacePolyline shape = new SurfacePolyline();

        shape.setAttributes(attr);
        shape.setHighlightAttributes(highattr);
        shape.setValue(AVKey.DISPLAY_NAME, "Distance Measurement");
        shape.setValue(AVKey.DISPLAY_ICON, "images/measurements.png");
        shape.setEnableBatchPicking(false);
        shape.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("SELECT")) {
                    AnnotationEditor.enableEdit(shape);
                }
            }
        });

        AnnotationBuilder builder = new AnnotationBuilder(wwm.getWorldWindow(), shape);
        builder.setShowLabel(true);
        builder.setArmed(true);
    }
}
