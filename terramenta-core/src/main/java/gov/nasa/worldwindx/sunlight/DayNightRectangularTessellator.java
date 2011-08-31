package gov.nasa.worldwindx.sunlight;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.terrain.*;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import java.awt.*;

/**
 * 
 * @author heidtmare
 */
public class DayNightRectangularTessellator extends RectangularTessellator {

    private Vec4 lightDirection;
    private Material material = new Material(Color.WHITE);
    private Color lightColor = Color.WHITE;
    private Color ambientColor = new Color(.1f, .1f, .1f);

    /**
     * 
     * @return
     */
    public Vec4 getLightDirection() {
        return this.lightDirection;
    }

    /**
     * 
     * @param direction
     */
    public void setLightDirection(Vec4 direction) {
        this.lightDirection = direction;
    }

    /**
     * 
     * @return
     */
    public Color getLightColor() {
        return this.lightColor;
    }

    /**
     * 
     * @param color
     */
    public void setLightColor(Color color) {
        if (color == null) {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.lightColor = color;
    }

    /**
     * 
     * @return
     */
    public Color getAmbientColor() {
        return this.ambientColor;
    }

    /**
     * 
     * @param color
     */
    public void setAmbientColor(Color color) {
        if (color == null) {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.ambientColor = color;
    }

    /**
     * 
     * @param dc
     */
    @Override
    public void beginRendering(DrawContext dc) {
        // Tiles don't push their reference center, they set it, so push the reference center once here so it can be
        // restored later, in endRendering.
        dc.getView().pushReferenceCenter(dc, Vec4.ZERO);

        if (!dc.isPickingMode() && this.lightDirection != null) {
            GL gl = dc.getGL();
            gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT | GL.GL_LIGHTING_BIT);

            this.material.apply(gl, GL.GL_FRONT);
            gl.glDisable(GL.GL_COLOR_MATERIAL);

            float[] lightPosition = {(float) -lightDirection.x, (float) -lightDirection.y, (float) -lightDirection.z, 0.0f};
            float[] lightDiffuse = new float[4];
            float[] lightAmbient = new float[4];
            lightColor.getRGBComponents(lightDiffuse);
            ambientColor.getRGBComponents(lightAmbient);

            gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);

            gl.glDisable(GL.GL_LIGHT0);
            gl.glEnable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHTING);
        }

        dc.getGL().glPushClientAttrib(GL.GL_CLIENT_VERTEX_ARRAY_BIT);
        dc.getGL().glEnableClientState(GL.GL_VERTEX_ARRAY);
    }

    /**
     * 
     * @param dc
     */
    @Override
    public void endRendering(DrawContext dc) {
        dc.getGL().glPopClientAttrib();

        if (!dc.isPickingMode() && this.lightDirection != null) {
            GL gl = dc.getGL();
            gl.glDisable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHT0);
            gl.glDisable(GL.GL_LIGHTING);
            gl.glPopAttrib();
        }

        dc.getView().popReferenceCenter(dc);
    }
}
