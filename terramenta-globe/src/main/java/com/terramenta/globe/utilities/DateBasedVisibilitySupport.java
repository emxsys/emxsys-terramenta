/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.utilities;

import com.terramenta.time.DateInterval;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.render.DrawContext;
import java.util.Date;

/**
 *
 * @author chris.heidt
 */
public class DateBasedVisibilitySupport {

    public static boolean determineVisibility(DrawContext dc, AVList avlist) {
        if (dc != null && avlist != null && avlist.hasKey("DISPLAY_DATE") && dc.hasKey("DISPLAY_DATEINTERVAL")) {
            Date displayDate = (Date) avlist.getValue("DISPLAY_DATE");
            DateInterval displayDateInterval = (DateInterval) dc.getValue("DISPLAY_DATEINTERVAL");

            if (displayDate != null && displayDateInterval != null) {
                //does this displayDate exist within the displayDateInterval?
                long displayDateMillis = displayDate.getTime();
                return (displayDateMillis >= displayDateInterval.getStartMillis() && displayDateMillis <= displayDateInterval.getEndMillis()) ? true : false;
            }
        }

        //if we have no date restrictions then its always visible
        return true;
    }
}
