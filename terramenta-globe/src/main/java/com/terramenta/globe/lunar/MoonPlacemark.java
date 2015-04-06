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

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicWWTexture;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.WWTexture;
import java.net.URL;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class MoonPlacemark extends PointPlacemark {

    public MoonPlacemark() {
        super(Position.ZERO);

        setValue(AVKey.DISPLAY_NAME, "Moon Orbital");
        setValue(AVKey.DISPLAY_ICON, "com/terramenta/globe/lunar/moon.png");//16x16

        PointPlacemarkAttributes attributes = new PointPlacemarkAttributes();
        attributes.setImageOffset(new Offset(0.5, 0.5, AVKey.FRACTION, AVKey.FRACTION));
        URL resource = getClass().getResource("moon32.png");
        if (resource != null) {
            attributes.setImageAddress(resource.toExternalForm());
        }
        setAttributes(attributes);

        setAltitudeMode(WorldWind.ABSOLUTE);
        setClipToHorizon(false);
    }

    /**
     * fixes a wwj bug in 2.0.0
     *
     * @param address
     * @return
     */
    @Override
    protected WWTexture initializeTexture(String address) {
        URL localUrl = WorldWind.getDataFileStore().requestFile(address);
        return localUrl == null ? null : new BasicWWTexture(localUrl, true);
    }
}
