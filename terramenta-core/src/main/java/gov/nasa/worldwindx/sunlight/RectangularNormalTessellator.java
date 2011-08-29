package gov.nasa.worldwindx.sunlight;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.terrain.*;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import java.awt.*;
import java.nio.DoubleBuffer;

public class RectangularNormalTessellator extends RectangularTessellator {

    private Vec4 lightDirection;
    private Material material = new Material(Color.WHITE);
    private Color lightColor = Color.WHITE;
    private Color ambientColor = new Color(.1f, .1f, .1f);

    public Vec4 getLightDirection() {
        return this.lightDirection;
    }

    public void setLightDirection(Vec4 direction) {
        this.lightDirection = direction;
    }

    public Color getLightColor() {
        return this.lightColor;
    }

    public void setLightColor(Color color) {
        if (color == null) {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.lightColor = color;
    }

    public Color getAmbientColor() {
        return this.ambientColor;
    }

    public void setAmbientColor(Color color) {
        if (color == null) {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.ambientColor = color;
    }

    @Override
    protected long render(DrawContext dc, RectTile rt, int numTextureUnits) {

        GL gl = dc.getGL();

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);

        if (!dc.isPickingMode() && this.lightDirection != null) {
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

        gl.glVertexPointer(3, GL.GL_FLOAT, 0, rt.getRi().getVertices().rewind());
        for (int i = 0; i < numTextureUnits; i++) {
            gl.glClientActiveTexture(GL.GL_TEXTURE0 + i);
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            Object texCoords = dc.getValue(AVKey.TEXTURE_COORDINATES);
            if (texCoords != null && texCoords instanceof DoubleBuffer) {
                gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, ((DoubleBuffer) texCoords).rewind());
            } else {
                gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, rt.getRi().getTexCoords().rewind());
            }
        }

        gl.glDrawElements(javax.media.opengl.GL.GL_TRIANGLE_STRIP, rt.getRi().getIndices().limit(),
                javax.media.opengl.GL.GL_UNSIGNED_INT, rt.getRi().getIndices().rewind());

        if (!dc.isPickingMode() && this.lightDirection != null) {
            gl.glDisable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHT0);
            gl.glDisable(GL.GL_LIGHTING);
            gl.glPopAttrib();
        }

        return rt.getRi().getIndices().limit() - 2; // return number of triangles rendered
    }
}
