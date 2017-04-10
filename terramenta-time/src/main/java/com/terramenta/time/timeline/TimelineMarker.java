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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.prefs.Preferences;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Popup;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class TimelineMarker extends Line {

    private static final Preferences timeprefs = TimeOptions.getPreferences();
    private static DateTimeFormatter formatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.LONG)
            .withLocale(Locale.forLanguageTag(timeprefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE)))
            .withZone(ZoneId.of(timeprefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE)));

    static {
        timeprefs.addPreferenceChangeListener((evt) -> {
            if (evt.getKey().equals(TimeOptions.LOCALE)) {
                formatter = formatter.withLocale(Locale.forLanguageTag(timeprefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE)));
            } else if (evt.getKey().equals(TimeOptions.TIMEZONE)) {
                formatter = formatter.withZone(ZoneId.of(timeprefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE)));
            }
        });
    }

    private final Instant date;
    private final String title;
    private final String description;

    public TimelineMarker(Instant date, String title, String description) {
        this.date = date;
        this.title = title;
        this.description = description;

        //line defaults
        setStroke(Color.MAGENTA);
        setStrokeWidth(2);

        //tooltip content
        Pane content = new VBox();
        content.getStyleClass().add("tooltip");
        content.setPrefWidth(300);
        Label dateLabel = new Label(formatter.format(date));
        Label titleLabel = new Label(title);
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        content.getChildren().addAll(dateLabel, titleLabel, descLabel);

        Popup popup = new Popup();
        popup.getContent().add(content);
        this.setOnMouseEntered((e) -> {
            popup.show(TimelineMarker.this, e.getScreenX(), e.getScreenY());
        });
        this.setOnMouseExited((e) -> {
            popup.hide();
        });
    }

    public Instant getDateTime() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

//    public Tooltip getTooltip() {
//        return tooltip;
//    }
}
