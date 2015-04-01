/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.utilities;

import com.terramenta.time.DateInterval;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.render.DrawContext;
import java.time.Instant;
import java.util.Date;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class RenderUtilities {

    public static boolean determineTemporalVisibility(DrawContext dc, AVList avlist) {
        if (dc != null && avlist != null && avlist.hasKey("DISPLAY_DATE") && dc.hasKey("DISPLAY_DATEINTERVAL")) {
            Date displayDate = (Date) avlist.getValue("DISPLAY_DATE");
            DateInterval displayDateInterval = (DateInterval) dc.getValue("DISPLAY_DATEINTERVAL");

            if (displayDate != null && displayDateInterval != null) {
                //does this displayDate exist within the displayDateInterval?
                long displayDateMillis = displayDate.getTime();
                return (displayDateMillis >= displayDateInterval.getStartMillis() && displayDateMillis <= displayDateInterval.getEndMillis());
            }
        }

        //if we have no date restrictions then its always visible
        return true;
    }
    
    public abstract static class DisplayDateChecker {

        private Instant currentDisplayInstant;

        public void doCheck(DrawContext dc) {
            if (dc == null) {
                throw new IllegalArgumentException("Null DrawContext");
            }

            Instant newDate = getDisplayDate(dc);
            Instant oldDate = currentDisplayInstant;
            if (newDate == null || newDate.equals(oldDate)) {
                return;
            }
            currentDisplayInstant = newDate;
            onDateChange(oldDate, newDate);
        }

        public Instant getDisplayDate(DrawContext dc) {
            if (dc == null) {
                throw new IllegalArgumentException("Null DrawContext");
            }

            Date displayDate = (Date) dc.getValue("DISPLAY_DATE");
            return (displayDate == null) ? null : displayDate.toInstant();
        }

        public abstract void onDateChange(Instant oldDate, Instant newDate);
    }
}
