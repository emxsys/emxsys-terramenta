/*
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
package com.terramenta.time.ribbons;

import com.terramenta.time.DateProvider;
import com.terramenta.time.datepicker.DatePicker;
import com.terramenta.time.options.TimeOptions;
import java.awt.Dimension;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;

/**
 *
 * @author Chris.Heidt
 */
public class DatetimeBand extends JFlowRibbonBand implements Observer {

    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private static final Preferences prefs = NbPreferences.forModule(TimeOptions.class);
    private DatePicker picker;
    private boolean processingUpdate = false;

    /**
     *
     */
    public DatetimeBand() {
        super("Datetime", null);

        final JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(220, 24));
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            jfxPanel.setScene(createScene());
        });

        setPreferredSize(new Dimension(220, 60));
        addFlowComponent(jfxPanel);

        //update on date change
        dateProvider.addObserver(this);

        //update on preference change
        prefs.addPreferenceChangeListener((PreferenceChangeEvent evt) -> {
            Platform.runLater(() -> {
                if (picker == null) {
                    return;
                }

                switch (evt.getKey()) {
                    case TimeOptions.TIMEZONE:
                        TimeZone timezone = TimeZone.getTimeZone(evt.getNewValue());
                        picker.timezoneProperty().setValue(timezone == null ? TimeZone.getDefault() : timezone);
                        break;
                    case TimeOptions.LOCALE:
                        Locale locale = Locale.forLanguageTag(evt.getNewValue());
                        picker.localeProperty().setValue(locale == null ? Locale.getDefault() : locale);
                        break;
                    case TimeOptions.FORMAT:
                        String format = evt.getNewValue();
                        picker.formatProperty().setValue(format == null ? "yyyy/MM/dd HH:mm:ss" : format);
                        break;
                }
            });
        });
    }

    private Scene createScene() {
        picker = new DatePicker(
                prefs.get(TimeOptions.FORMAT, TimeOptions.DEFAULT_FORMAT),
                TimeZone.getTimeZone(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE)),
                Locale.forLanguageTag(prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE))
        );
        picker.setDate(dateProvider.getDate());

        picker.timestampProperty().addListener((ObservableValue<? extends Long> arg0, Long oldValue, Long newValue) -> {
            if (newValue == null) {
                //revert to old value, no blank date allowed
                picker.setDate(new Date(oldValue));
            } else if (!processingUpdate) {
                dateProvider.setDate(new Date(newValue));
            }
        });
        return new Scene(picker);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (picker == null) {
            return;
        }

        Platform.runLater(() -> {
            // Set a semiphore to prevent a recursive call into setDate by the timestamp listener
            processingUpdate = true;
            picker.setDate(dateProvider.getDate());
            // Reset semiphore
            processingUpdate = false;
        });
    }
}
