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
package com.terramenta.globe.layers;

import javax.media.opengl.*;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.terramenta.globe.options.GlobeOptions;
import com.terramenta.utilities.Classification;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.OGLTextRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

public class ClassificationBannerLayer extends AbstractLayer {

    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    private ClassificationRenderable renderable = new ClassificationRenderable();

    public ClassificationBannerLayer() {
        setName("Classification Banner");
        setPickEnabled(false);
        setNetworkRetrievalEnabled(false);

        classificationChange(prefs.get("options.globe.classification", "UNCLASSIFIED"));
        protectionChange(prefs.get("options.globe.protection", ""));

        prefs.addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (evt.getKey().equals("options.globe.classification")) {
                    classificationChange(evt.getNewValue());
                } else if (evt.getKey().equals("options.globe.protection")) {
                    protectionChange(evt.getNewValue());
                }
            }
        });
    }

    private void classificationChange(String classification) {
        renderable.classification = Classification.valueOf(classification);
    }

    private void protectionChange(String protection) {
        if (protection.isEmpty()) {
            renderable.protection = "";
        } else {
            renderable.protection = "//" + protection;
        }
    }

    // Rendering
    @Override
    public void doRender(DrawContext dc) {
        dc.addOrderedRenderable(this.renderable);
    }

    @Override
    public void doPick(DrawContext dc, Point pickPoint) {
        // Delegate drawing to the ordered renderable list
        dc.addOrderedRenderable(this.renderable);
    }

    @Override
    public String toString() {
        return Logging.getMessage("layers.ClassificationBannerLayer.Name");
    }

    private class ClassificationRenderable implements OrderedRenderable {

        private final Font textFont = new Font("Arial", Font.BOLD, 12);
        private final float[] compArray = new float[4];
        public Classification classification = Classification.UNCLASSIFIED;
        public String protection = "";

        @Override
        public double getDistanceFromEye() {
            return 0;
        }

        @Override
        public void pick(DrawContext dc, Point pickPoint) {
            this.draw(dc);
        }

        @Override
        public void render(DrawContext dc) {
            this.draw(dc);
        }

        // Rendering
        public void draw(DrawContext dc) {
            if (classification == Classification.NONE) {
                return;
            }

            GL2 gl = dc.getGL().getGL2();
            boolean attribsPushed = false;
            boolean modelviewPushed = false;
            boolean projectionPushed = false;
            try {
                gl.glPushAttrib(GL2.GL_DEPTH_BUFFER_BIT
                        | GL2.GL_COLOR_BUFFER_BIT
                        | GL2.GL_ENABLE_BIT
                        | GL2.GL_TRANSFORM_BIT
                        | GL2.GL_VIEWPORT_BIT
                        | GL2.GL_CURRENT_BIT);
                attribsPushed = true;

                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                gl.glDisable(GL.GL_DEPTH_TEST);

                // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
                // into the GL projection matrix.
                java.awt.Rectangle viewport = dc.getView().getViewport();
                gl.glMatrixMode(GL2.GL_PROJECTION);
                gl.glPushMatrix();
                projectionPushed = true;
                gl.glLoadIdentity();

                String text = classification.getText() + protection;
                Dimension size = getTextRenderSize(dc, text);
                int offset = 1;
                if (size.width < viewport.getWidth()) {
                    double maxwh = size.width > size.height ? size.width : size.height;
                    gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh, 0.6 * maxwh);

                    gl.glMatrixMode(GL2.GL_MODELVIEW);
                    gl.glPushMatrix();
                    modelviewPushed = true;
                    gl.glLoadIdentity();

                    Dimension dimension = new Dimension((int) viewport.getWidth(), (int) size.getHeight() + offset);
                    drawFilledRectangle(dc, new Vec4(0, viewport.getHeight()), dimension, classification.getBackgroundColor());

                    Vec4 center = new Vec4(viewport.getCenterX() - (size.getWidth() / 2), viewport.getHeight() - size.getHeight() + offset);
                    drawLabel(dc, text, center, classification.getTextColor(), this.textFont);
                }
            } finally {
                if (projectionPushed) {
                    gl.glMatrixMode(GL2.GL_PROJECTION);
                    gl.glPopMatrix();
                }
                if (modelviewPushed) {
                    gl.glMatrixMode(GL2.GL_MODELVIEW);
                    gl.glPopMatrix();
                }
                if (attribsPushed) {
                    gl.glPopAttrib();
                }
            }
        }

        private Dimension getTextRenderSize(DrawContext dc, String text) {
            TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), this.textFont);
            Rectangle2D nameBound = textRenderer.getBounds(text);
            return nameBound.getBounds().getSize();
        }

        // Draw the label
        private void drawLabel(DrawContext dc, String text, Vec4 screenPoint, Color textColor, Font textFont) {
            int x = (int) screenPoint.x();
            int y = (int) screenPoint.y();
            TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), textFont);
            textRenderer.begin3DRendering();
            textRenderer.setColor(this.getBackgroundColor(textColor));
            textRenderer.draw(text, x + 1, y - 1);//back shadow
            textRenderer.setColor(textColor);
            textRenderer.draw(text, x, y);//label
            textRenderer.end3DRendering();
        }

        // Compute background color for best contrast
        private Color getBackgroundColor(Color color) {
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), compArray);
            if (compArray[2] > 0.5) {
                return new Color(0, 0, 0, 0.7f);
            } else {
                return new Color(1, 1, 1, 0.7f);
            }
        }

        private void drawFilledRectangle(DrawContext dc, Vec4 origin, Dimension dimension, Color color) {
            GL2 gl = dc.getGL().getGL2(); // GL initialization checks for GL2 compatibility.
            gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(),
                    (byte) color.getBlue(), (byte) color.getAlpha());
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(origin.x, origin.y, 0);
            gl.glVertex3d(origin.x + dimension.getWidth(), origin.y, 0);
            gl.glVertex3d(origin.x + dimension.getWidth(), origin.y + dimension.getHeight(), 0);
            gl.glVertex3d(origin.x, origin.y + dimension.getHeight(), 0);
            gl.glVertex3d(origin.x, origin.y, 0);
            gl.glEnd();
        }
    }
}
