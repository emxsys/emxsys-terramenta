/*
 Copyright © 2014, Terramenta. All rights reserved.

 This work is subject to the terms of either
 the GNU General Public License Version 3 ("GPL") or 
 the Common Development and Distribution License("CDDL") (collectively, the "License").
 You may not use this work except in compliance with the License.

 You can obtain a copy of the License at
 http://opensource.org/licenses/CDDL-1.0
 http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.time.picker;

import com.terramenta.time.options.TimeOptions;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import org.openide.util.NbPreferences;

/**
 * This picker takes care of all the preference listening required to display datetimes using the
 * correct timezone, locale, and format. Make sure to call the setLocalizedDateTime methods when
 * applying your date, otherwise you must set the correct zone externally.
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class LocalizedDateTimePicker extends DateTimePicker {

    private static final Preferences prefs = NbPreferences.forModule(TimeOptions.class);

    public LocalizedDateTimePicker() {
        super();

        setDateTime(ZonedDateTime.now(ZoneId.of(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE))));

        setDateTimeFormatter(
                DateTimeFormatter.ofPattern(
                        prefs.get(TimeOptions.FORMAT, TimeOptions.DEFAULT_FORMAT),
                        Locale.forLanguageTag(prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE))
                )
        );

        //update on preference change
        prefs.addPreferenceChangeListener((PreferenceChangeEvent evt) -> {
            Platform.runLater(() -> {
                switch (evt.getKey()) {
                    case TimeOptions.TIMEZONE:
                        ZonedDateTime withZoneSameInstant = dateTimeProperty().get().withZoneSameInstant(ZoneId.of(evt.getNewValue())); //.setLocalDateTime(LocalDateTime.of(plusSeconds.toLocalDate(), plusSeconds.toLocalTime()));
                        dateTimeProperty().set(withZoneSameInstant);
                        break;
                    case TimeOptions.LOCALE:
                    //fall through
                    case TimeOptions.FORMAT:
                        setDateTimeFormatter(
                                DateTimeFormatter.ofPattern(
                                        prefs.get(TimeOptions.FORMAT, TimeOptions.DEFAULT_FORMAT),
                                        Locale.forLanguageTag(prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE))
                                )
                        );
                        break;
                }
            });
        });
    }

    public void setLocalizedDateTime(Date date) {
        setLocalizedDateTime(date.toInstant());
    }

    public void setLocalizedDateTime(Instant insant) {
        setDateTime(ZonedDateTime.ofInstant(insant, ZoneId.of(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE))));
    }

    public void setLocalizedDateTime(LocalDateTime localDateTime) {
        setDateTime(ZonedDateTime.of(localDateTime, ZoneId.of(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE))));
    }

}
