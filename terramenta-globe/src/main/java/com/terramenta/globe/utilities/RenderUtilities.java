/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.utilities;

import com.terramenta.time.DatetimeInterval;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.render.DrawContext;
import java.time.Instant;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class RenderUtilities {

    public static boolean determineTemporalVisibility(DrawContext dc, AVList avlist) {
        if (dc != null && avlist != null && avlist.hasKey("DISPLAY_DATETIME") && dc.hasKey("DISPLAY_DATETIME_INTERVAL")) {
            Instant displayDatetime = (Instant) avlist.getValue("DISPLAY_DATETIME");
            DatetimeInterval displayDatetimeInterval = (DatetimeInterval) dc.getValue("DISPLAY_DATETIME_INTERVAL");

            if (displayDatetime != null && displayDatetimeInterval != null) {
                //does this displayDate exist within the displayDateInterval?
                return !(displayDatetime.isBefore(displayDatetimeInterval.getStartDatetime()) || displayDatetime.isAfter(displayDatetimeInterval.getEndDatetime()));
            }
        }

        //if we have no date restrictions then its always visible
        return true;
    }

    public abstract static class DisplayDatetimeChecker {

        private Instant currentDisplayInstant;

        public void doCheck(DrawContext dc) {
            if (dc == null) {
                throw new IllegalArgumentException("Null DrawContext");
            }

            Instant newDate = getDisplayDatetime(dc);
            Instant oldDate = currentDisplayInstant;
            if (newDate == null || newDate.equals(oldDate)) {
                return;
            }
            currentDisplayInstant = newDate;
            onDisplayDatetimeChange(oldDate, newDate);
        }

        public Instant getDisplayDatetime(DrawContext dc) {
            if (dc == null) {
                throw new IllegalArgumentException("Null DrawContext");
            }

            return (Instant) dc.getValue("DISPLAY_DATETIME");
        }

        public abstract void onDisplayDatetimeChange(Instant oldDate, Instant newDate);
    }
}
