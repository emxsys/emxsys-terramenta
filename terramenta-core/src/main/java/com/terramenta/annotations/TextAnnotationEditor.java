package com.terramenta.annotations;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.render.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.openide.util.Lookup;

/**
 *
 * @author R. Wathelet, April 2012. 
 * 
 * modified Feb 2013, replaced GlobeAnnotation with DraggableAnnotation and 
 * show the dialog box at the mouse location
 */
public class TextAnnotationEditor extends AVListImpl {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private AnnotationLayer layer;
    private DraggableAnnotation text;
    public static final String TEXT_ANNOTATION_LAYER = "Text Annotations";
    private boolean armed = false;
    //
    private final MouseAdapter ma = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
                editText(mouseEvent);
                setArmed(false);
                mouseEvent.consume();
            }
        }
    };

    public TextAnnotationEditor() {
        this.text = getDefaultAnnotation();
        this.layer = (AnnotationLayer) wwm.getWorldWindow().getModel().getLayers().getLayerByName(TEXT_ANNOTATION_LAYER);
        if (layer == null) {
            layer = new AnnotationLayer();
            layer.setName(TEXT_ANNOTATION_LAYER);
            wwm.getWorldWindow().getModel().getLayers().add(layer);
        }
    }

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

    private void editText(MouseEvent mouseEvent) {
        final Position curPos = wwm.getWorldWindow().getCurrentPosition();
        if (curPos != null) {
            Integer buttonType = 0;
            JOptionPane optionPane = showJOptionPaneAt(mouseEvent.getLocationOnScreen());
            String result = ((String) optionPane.getInputValue()).trim();
            Object selectedValue = optionPane.getValue();
            if (selectedValue instanceof Integer) {
                buttonType = ((Integer) selectedValue).intValue();
            }
            if ((result != null) && !result.isEmpty() && (buttonType != JOptionPane.CANCEL_OPTION)) {
                text.setText(result);
                text.moveTo(curPos);
                layer.addAnnotation(text);
            }
            wwm.getWorldWindow().redraw();
        }
    }

    private JOptionPane showJOptionPaneAt(Point location) {
        JOptionPane optionPane = new JOptionPane("", JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, null, "");
        optionPane.setWantsInput(true);
        JDialog dialog = optionPane.createDialog(null, "Add text");
        dialog.setLocation(location);
        dialog.setVisible(true);
        return optionPane;
    }

    public AnnotationLayer getLayer() {
        return layer;
    }

    public GlobeAnnotation getTextAnnotation() {
        return text;
    }

    public void setTextAnnotation(DraggableAnnotation text) {
        this.text = text;
    }

    private DraggableAnnotation getDefaultAnnotation() {
        DraggableAnnotation theText = new DraggableAnnotation("", Position.ZERO);

        AnnotationAttributes attr = new AnnotationAttributes();
        attr.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
        attr.setBackgroundColor(new Color(255, 255, 0, 130));
        attr.setTextColor(Color.black);
        attr.setFrameShape(AVKey.SHAPE_RECTANGLE);

        theText.setAttributes(attr);
        theText.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        theText.setValue(AVKey.DISPLAY_ICON, "images/textbox.png");
        theText.setPickEnabled(true);
        return theText;
    }
}
