/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
 */
package com.qna.terramenta.utilities;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwindx.examples.util.ToolTipController;

/**
 * Controls display of tool tips on picked objects. Any shape implementing {@link AVList} can participate. Shapes
 * provide tool tip text in their AVList for either or both of hover and rollover events. The keys associated with the
 * text are specified to the constructor.
 *
 * @author tag
 * @version $Id: QuickTipController.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class QuickTipController extends ToolTipController {

    private boolean armed = true;

    /**
     * 
     * @param ww
     */
    public QuickTipController(WorldWindow ww) {
        super(ww);
    }

    /**
     * 
     * @return
     */
    public boolean isArmed() {
        return armed;
    }

    /**
     * 
     * @param armed
     */
    public void setArmed(boolean armed) {
        if (this.armed == armed) {
            return;
        }
        this.armed = armed;
        if (armed) {
            this.wwd.addSelectListener(this);
        } else {
            this.wwd.removeSelectListener(this);
        }
    }
}
