/**
 * Copyright © 2014, Terramenta. All rights reserved. * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this work except in compliance
 * with the License.
 *
 * You can obtain a copy of the License at http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 *
 */
package com.terramenta.globe.lunar;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class MoonLayer extends RenderableLayer implements Observer, MoonDependent {

    private final MoonPlacemark moonRenderable = new MoonPlacemark();
    private final SublunarPlacemark sublunarPlacemark = new SublunarPlacemark();
    private Moon moon;

    public MoonLayer() {
        setName("Moon");
        setPickEnabled(false);

        sublunarPlacemark.setVisible(true);
        addRenderable(sublunarPlacemark);

        moonRenderable.setVisible(false);
        addRenderable(moonRenderable);
    }

    @Override
    public Moon getMoon() {
        return this.moon;
    }

    @Override
    public void setMoon(Moon moon) {
        if (this.moon != null) {
            this.moon.deleteObserver(this);
        }
        this.moon = moon;

        if (this.moon != null) {
            this.moon.addObserver(this);
        }

        //trigger update for initial positioning
        update(null, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        Position moonPosition;
        if (arg instanceof Position) {
            moonPosition = (Position) arg;
        } else {
            moonPosition = moon.getPosition();
        }

        if (moonPosition == null) {
            return;
        }

        moonRenderable.setPosition(moonPosition);
        sublunarPlacemark.setPosition(moonPosition);
    }
}
