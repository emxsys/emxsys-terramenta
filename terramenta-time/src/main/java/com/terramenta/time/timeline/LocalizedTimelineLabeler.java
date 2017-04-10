/**
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
package com.terramenta.time.timeline;

import com.terramenta.time.options.TimeOptions;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.prefs.Preferences;
import javafx.util.StringConverter;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class LocalizedTimelineLabeler extends StringConverter<Number> {

    private DateTimeFormatter year;
    private DateTimeFormatter yearMonth;
    private DateTimeFormatter yearMonthDay;
    private DateTimeFormatter hourMin;
    private DateTimeFormatter hourMinSecond;
    private Number previous = null;//volatile?

    public LocalizedTimelineLabeler() {
        //setup initial date formatters
        Preferences timeprefs = TimeOptions.getPreferences();
        Locale locale = Locale.forLanguageTag(timeprefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE));
        ZoneId zone = ZoneId.of(timeprefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE));
        year = DateTimeFormatter.ofPattern("yyyy G").withLocale(locale).withZone(zone);
        yearMonth = DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale).withZone(zone);
        yearMonthDay = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale).withZone(zone);
        hourMin = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale).withZone(zone);
        hourMinSecond = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(locale).withZone(zone);

        //listen for user locale/timezone preference modifications
        timeprefs.addPreferenceChangeListener(
                (evt) -> {
                    if (evt.getKey().equals(TimeOptions.LOCALE)) {
                        Locale newLocale = Locale.forLanguageTag(timeprefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE));
                        year = year.withLocale(newLocale);
                        yearMonth = yearMonth.withLocale(newLocale);
                        yearMonthDay = yearMonthDay.withLocale(newLocale);
                        hourMin = hourMin.withLocale(newLocale);
                        year = year.withLocale(newLocale);
                    } else if (evt.getKey().equals(TimeOptions.TIMEZONE)) {
                        ZoneId newZone = ZoneId.of(timeprefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE));
                        year = year.withZone(newZone);
                        yearMonth = yearMonth.withZone(newZone);
                        yearMonthDay = yearMonthDay.withZone(newZone);
                        hourMin = hourMin.withZone(newZone);
                        hourMinSecond = hourMinSecond.withZone(newZone);
                    }
                }
        );
    }

    @Override
    public String toString(Number seconds) {

        /**
         * NOTE: This only works because we are creating an offscreen tick at each end of the
         * region. The initial label for the first(offscreen) tick will be blank, subsequently its
         * label will be incorrect since it's value will be compared to the "previous" of the last
         * tick from the prior label formatting sequence.
         */
        if (previous == null) {
            previous = seconds;
            return "";
        }

        ZoneId zone = ZoneId.of(TimeOptions.getPreferences().get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE));
        ZonedDateTime datetime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds.longValue()), zone);

        String output;
        long diff = seconds.longValue() - previous.longValue();
        if (diff < ChronoUnit.HOURS.getDuration().getSeconds()) {
            output = hourMinSecond.format(datetime);
        } else if (diff < ChronoUnit.DAYS.getDuration().getSeconds()) {
            output = hourMin.format(datetime);
        } else if (diff < ChronoUnit.MONTHS.getDuration().getSeconds()) {
            output = yearMonthDay.format(datetime);
        } else if (diff < ChronoUnit.YEARS.getDuration().getSeconds()) {
            output = yearMonth.format(datetime);
        } else {
            output = year.format(datetime);
        }
        previous = seconds;
        return output;
    }

    @Override
    public Number fromString(String label) {
        throw new UnsupportedOperationException("not implemented.");
    }
}
