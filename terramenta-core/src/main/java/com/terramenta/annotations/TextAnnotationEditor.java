package com.terramenta.annotations;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
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
 * @author R. Wathelet, April 2012
 */
public class TextAnnotationEditor extends AVListImpl {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private AnnotationLayer layer;
    private GlobeAnnotation text;
    public static final String TEXT_ANNOTATION_LAYER = "Text Annotations";
    private boolean armed = false;
    //
    private final MouseAdapter ma = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
                editText();
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

    private void editText() {
        final Position curPos = wwm.getWorldWindow().getCurrentPosition();
        if (curPos != null) {
            String result = (String) JOptionPane.showInputDialog(new Frame(), "", "Add text",
                    JOptionPane.PLAIN_MESSAGE, null, null, "some text");

            // get the screen location
//        Vec4 vecPoint = wwm.getWorldWindow().getView().project(wwm.getWorldWindow().getModel().getGlobe().computePointFromPosition(curPos));
//        String result = (String) showJOptionPaneAt(new Point((int) vecPoint.x, (int) vecPoint.y));

            if ((result != null) && !result.isEmpty()) {
                text.setText(result);
                text.moveTo(curPos);
                layer.addAnnotation(text);
            }

            wwm.getWorldWindow().redraw();
        }        
    }

    // trying to popup the dialog at the mouse click. Needs more work .... TODO
    private Object showJOptionPaneAt(Point location) {
        JOptionPane optionPane = new JOptionPane("",
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, null, "some text");
        optionPane.setWantsInput(true);
        optionPane.setInitialSelectionValue("some text");
        JDialog dialog = optionPane.createDialog(null, "Add text");
        dialog.setLocation(location);
        dialog.setVisible(true);
        return optionPane.getInputValue();
    }

    public AnnotationLayer getLayer() {
        return layer;
    }

    public GlobeAnnotation getTextAnnotation() {
        return text;
    }

    public void setTextAnnotation(GlobeAnnotation text) {
        this.text = text;
    }

    private GlobeAnnotation getDefaultAnnotation() {
        GlobeAnnotation theText = new GlobeAnnotation("", Position.ZERO);

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
