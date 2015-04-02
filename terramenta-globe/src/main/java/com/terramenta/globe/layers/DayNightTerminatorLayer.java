/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.layers;

import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Polyline;
import java.awt.Color;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class DayNightTerminatorLayer extends Polyline {

    public DayNightTerminatorLayer() {
        this.setColor(Color.DARK_GRAY);
        this.setAntiAliasHint(ANTIALIAS_DONT_CARE);
        this.setPathType(GREAT_CIRCLE); // because this ends up being a big circle and this saves us on points needed
        this.setClosed(true);
        this.setLineWidth(2.0);
        this.setFollowTerrain(true);
    }

    @Override
    public void render(DrawContext dc) {
        GL2 gl = dc.getGL().getGL2();
        gl.glPushAttrib(GL2.GL_TEXTURE_BIT | GL2.GL_ENABLE_BIT | GL2.GL_CURRENT_BIT);

        // Added so that the colors wouldn't depend on sun shading
        gl.glDisable(GL.GL_TEXTURE_2D);

        super.render(dc);

        gl.glPopAttrib();

    }
}
