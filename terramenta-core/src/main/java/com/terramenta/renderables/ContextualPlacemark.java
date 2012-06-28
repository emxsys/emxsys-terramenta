/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.renderables;

import com.terramenta.interfaces.ContextualObject;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import java.net.URL;

/**
 *
 * @author chris.heidt
 */
public class ContextualPlacemark extends PointPlacemark implements ContextualObject {

    public ContextualPlacemark(Position pstn) {
        super(pstn);
        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);//assuming no altitude for now.
        this.setEnableBatchPicking(false); //must be false to support the mutiselection menu(right-click menu) 
        //this.setLineEnabled(true); //Pointless for ground-based points, change when we get items with altitude
    }

    @Override
    public void setDisplayName(String name) {
        this.setValue(AVKey.DISPLAY_NAME, name);
    }

    @Override
    public String getDisplayName() {
        return (String) this.getValue(AVKey.DISPLAY_NAME);
    }

    @Override
    public void setDisplayIcon(String icon) {
        this.setValue(AVKey.DISPLAY_ICON, icon);

        PointPlacemarkAttributes attr = new PointPlacemarkAttributes();
        URL url = getClass().getResource("/" + icon);
        if (url != null) {
            attr.setImageAddress(url.toString());
            attr.setImageOffset(new Offset(0.5, 0.5, AVKey.FRACTION, AVKey.FRACTION));
        } else {
            attr.setUsePointAsDefaultImage(true);
        }
        this.setAttributes(attr);
    }

    @Override
    public String getDisplayIcon() {
        return (String) this.getValue(AVKey.DISPLAY_ICON);
    }

    @Override
    public void setDescription(String desc) {
        this.setValue(AVKey.DESCRIPTION, desc);
    }

    @Override
    public String getDescription() {
        return (String) this.getValue(AVKey.DESCRIPTION);
    }
}
