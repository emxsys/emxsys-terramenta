/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JSpinnerDateEditor;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JSpinner;

/**
 *
 * @author chris.heidt
 */
public class DateChooser extends JDateChooser {

    /**
     *
     */
    public final static String DEFAULT_DATEPATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    /**
     *
     */
    public final static TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");

    /**
     *
     */
    public DateChooser() {
        this(null, DEFAULT_DATEPATTERN, DEFAULT_TIMEZONE);
    }

    /**
     *
     * @param initDate
     */
    public DateChooser(Date initDate) {
        this(initDate, DEFAULT_DATEPATTERN, DEFAULT_TIMEZONE);
    }

    /**
     *
     * @param initDate
     * @param datePattern
     */
    public DateChooser(Date initDate, String datePattern) {
        this(initDate, datePattern, DEFAULT_TIMEZONE);
    }

    /**
     *
     * @param initDate
     * @param datePattern
     * @param timeZone
     */
    public DateChooser(Date initDate, String datePattern, TimeZone timeZone) {
        super(initDate, datePattern, new JSpinnerDateEditor());

        if (timeZone != null) {
            JSpinner spinner = (JSpinner) this.getDateEditor().getUiComponent();
            JSpinner.DateEditor editor = (JSpinner.DateEditor) spinner.getEditor();
            editor.getFormat().setTimeZone(timeZone);
        }

        //adjust calendar visuals
//        JCalendar cal = this.getJCalendar();
//        cal.setTodayButtonText("Now");
//        cal.setTodayButtonVisible(true);
        //cal.setNullDateButtonText("Clear");
        //cal.setNullDateButtonVisible(true);
    }
}
