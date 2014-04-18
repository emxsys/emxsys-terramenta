/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.annotations;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
 *
 * @author heidtmare
 * modifications
 */
public class TextAnnotationEditor {

    public static final String USER_ANNOTATION_LAYER = "User Annotations";
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private static boolean editing = false;
    private RenderableLayer userLayer;
    private boolean armed = false;
    private final KeyAdapter ka = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (armed && keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                setArmed(false);
            }
        }
    };
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
            // Force keyboard focus to globe
            wwm.getWorldWindow().requestFocusInWindow();

            //add listeners
            wwm.getWorldWindow().getInputHandler().addKeyListener(ka);
            wwm.getWorldWindow().getInputHandler().addMouseListener(ma);

            //change cursor
            wwm.getWorldWindow().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else {
            //remove listeners
            wwm.getWorldWindow().getInputHandler().removeKeyListener(ka);
            wwm.getWorldWindow().getInputHandler().removeMouseListener(ma);

            //reset cursor
            wwm.getWorldWindow().setCursor(Cursor.getDefaultCursor());
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
        theText.setValue(AVKey.DISPLAY_ICON, "com/terramenta/annotations/images/draw-text.png");
        theText.setPickEnabled(true);
        return theText;
    }

    public static void edit(DraggableAnnotation anno) {
        if (editing) {
            return;
        }

        editing = true;
        InputLine inputLine = new NotifyDescriptor.InputLine("Content:", "Text Annotation");
        inputLine.setInputText(anno.getText());
        Object result = DialogDisplayer.getDefault().notify(inputLine);
        if (result == DialogDescriptor.OK_OPTION) {
            String text = inputLine.getInputText();
            anno.setText(text);
            anno.setValue(AVKey.DISPLAY_NAME, text);
            anno.setValue(AVKey.DESCRIPTION, text);
        }
        editing = false;
    }
}
