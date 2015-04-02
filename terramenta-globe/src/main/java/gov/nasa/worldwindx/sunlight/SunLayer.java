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
package gov.nasa.worldwindx.sunlight;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.terrain.Tessellator;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import org.openide.util.Lookup;

/**
 *
 * @author heidtmare
 */
public class SunLayer extends LensFlareLayer implements Observer, SunDependent {

    private final SunTessellator sunTessellator = new SunTessellator();
    private Tessellator originalTessellator;
    private Sun sun;

    /**
     *
     */
    public SunLayer() {
        setName("Sun");
        setPickEnabled(false);

        BufferedImage sunDisk = createHaloImage(64, new Color(1f, 1f, .8f), 2f);
        BufferedImage disk = createDiskImage(128, Color.WHITE);
        BufferedImage star = createStarImage(128, Color.WHITE);
        BufferedImage halo = createHaloImage(128, Color.WHITE);
        BufferedImage rainbow = createRainbowImage(128);
        BufferedImage rays = createRaysImage(128, 12, Color.WHITE);

        ArrayList flares = new ArrayList();
        flares.add(new FlareImage(rays, 4, 0, .05));
        flares.add(new FlareImage(star, 1.4, 0, .1));
        flares.add(new FlareImage(star, 2.5, 0, .04));
        flares.add(new FlareImage(sunDisk, .6, 0, .9));
        flares.add(new FlareImage(halo, 1.0, 0, .9));
        flares.add(new FlareImage(halo, 4, 0, .9));
        flares.add(new FlareImage(rainbow, 2.2, 0, .03));
        flares.add(new FlareImage(rainbow, 1.2, 0, .04));
        flares.add(new FlareImage(disk, .1, .4, .1));
        flares.add(new FlareImage(disk, .15, .6, .1));
        flares.add(new FlareImage(disk, .2, .7, .1));
        flares.add(new FlareImage(disk, .5, 1.1, .2));
        flares.add(new FlareImage(disk, .2, 1.3, .1));
        flares.add(new FlareImage(disk, .1, 1.4, .05));
        flares.add(new FlareImage(disk, .1, 1.5, .1));
        flares.add(new FlareImage(disk, .1, 1.6, .1));
        flares.add(new FlareImage(disk, .2, 1.65, .1));
        flares.add(new FlareImage(disk, .12, 1.71, .1));
        flares.add(new FlareImage(disk, 3, 2.2, .05));
        flares.add(new FlareImage(disk, .5, 2.4, .2));
        flares.add(new FlareImage(disk, .7, 2.6, .1));
        flares.add(new FlareImage(rainbow, 5, 3.0, .03));
        flares.add(new FlareImage(disk, .2, 3.5, .1));
        this.addRenderables(flares);
    }

    public Sun getSun() {
        return sun;
    }

    @Override
    public void setSun(Sun sun) {
        if (this.sun != null) {
            this.sun.deleteObserver(this);
        }
        this.sun = sun;

        if (this.sun != null) {
            this.sun.addObserver(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Position sunPosition;
        if (arg instanceof Position) {
            sunPosition = (Position) arg;
        } else {
            sunPosition = sun.getPosition();
        }

        if (sunPosition == null) {
            return;
        }

        WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
        if (wwm == null) {
            return;
        }

        Vec4 sunVector = wwm.getWorldWindow().getModel().getGlobe().computePointFromPosition(sunPosition).normalize3();
        this.setSunDirection(sunVector);
        this.sunTessellator.setLightDirection(sunVector.getNegative3());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (getSun() == null) {
            return;
        }

        WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
        if (wwm == null) {
            return;
        }

        //swap tessellators
        if (enabled) {
            originalTessellator = wwm.getWorldWindow().getModel().getGlobe().getTessellator();
            wwm.getWorldWindow().getModel().getGlobe().setTessellator(sunTessellator);
        } else if (originalTessellator != null) {
            wwm.getWorldWindow().getModel().getGlobe().setTessellator(originalTessellator);
        }
    }

}
