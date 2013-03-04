package com.terramenta.annotations;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.util.Lookup;

/**
 *
 * @author R. Wathelet, April 2012.
 *
 * modified Feb 2013, replaced GlobeAnnotation with DraggableAnnotation, show the dialog box at the mouse location and
 * add the text annotation in the "User Annotations" layer.
 */
public class TextAnnotationEditor {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private RenderableLayer userLayer;
    public static final String USER_ANNOTATION_LAYER = "User Annotations";
    private boolean armed = false;
    private final MouseAdapter ma = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
                DraggableAnnotation text = getDefaultAnnotation();
                text.moveTo(wwm.getWorldWindow().getCurrentPosition());
                getLayer().addRenderable(text);

                edit(text);

                setArmed(false);
                mouseEvent.consume();
            }
        }
    };

    public void setArmed(boolean armed) {
        this.armed = armed;
        if (armed) {
            wwm.getWorldWindow().getInputHandler().addMouseListener(ma);
            ((Component) wwm.getWorldWindow()).setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else {
            wwm.getWorldWindow().getInputHandler().removeMouseListener(ma);
            ((Component) wwm.getWorldWindow()).setCursor(Cursor.getDefaultCursor());
        }
    }

    public RenderableLayer getLayer() {
        if (userLayer == null) {
            userLayer = (RenderableLayer) wwm.getLayers().getLayerByName(USER_ANNOTATION_LAYER);
            if (userLayer == null) {
                userLayer = new RenderableLayer();
                userLayer.setName(USER_ANNOTATION_LAYER);
                wwm.getLayers().add(userLayer);
            }
        }
        return userLayer;
    }

    private static DraggableAnnotation getDefaultAnnotation() {
        DraggableAnnotation theText = new DraggableAnnotation("", Position.ZERO);

        AnnotationAttributes attr = new AnnotationAttributes();
        attr.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        attr.setBackgroundColor(new Color(255, 255, 0, 130));
        attr.setTextColor(Color.black);
        attr.setFrameShape(AVKey.SHAPE_RECTANGLE);

        theText.setAttributes(attr);
        theText.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        theText.setValue(AVKey.DISPLAY_ICON, "images/textAdd.png");
        theText.setPickEnabled(true);
        return theText;
    }

    public static void edit(DraggableAnnotation anno) {
        InputLine inputLine = new NotifyDescriptor.InputLine("Text", "Input");
        Object result = DialogDisplayer.getDefault().notify(inputLine);
        if (result == DialogDescriptor.OK_OPTION) {
            String text = inputLine.getInputText();
            anno.setText(text);
            anno.setValue(AVKey.DISPLAY_NAME, text);
            anno.setValue(AVKey.DESCRIPTION, text);
        }
    }
}
