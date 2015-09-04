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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

public class DateTimePicker extends DatePicker {

    private final ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty = new SimpleObjectProperty<>(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US));
    private final ObjectProperty<LocalTime> timeProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ZonedDateTime> dateTimeProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ZoneId> zoneProperty = new SimpleObjectProperty<>();
    private boolean isUpdating = false;// Set a semiphore to prevent a recursive call into setDate by the timestamp listener

    private ChangeListener updateDateTime = (ChangeListener) (obs, ov, nv) -> {
        if (isUpdating) {
            return;
        }

        LocalDate date = getValue();
        if (date == null) {
            //this can happen when starting from an empty field and only adjusting the
            //time sliders, so we'll give the user a default date of now.
            date = LocalDate.now();
        }

        LocalTime time = getTime();
        if (time == null) {
            //i dont think this condition can happen naturally,
            //but the user could mess with the timeProperty
            time = LocalTime.MIN;
        }

        ZoneId zone = getZone();
        if (zone == null) {
            ZonedDateTime dt = getDateTime();
            if (dt != null) {
                zone = dt.getZone();
            }
        }
        if (zone == null) {
            zone = ZoneId.systemDefault();
        }

        ZonedDateTime zdt = ZonedDateTime.of(
                date,
                time,
                zone
        );
        dateTimeProperty.set(zdt);
    };

    public DateTimePicker() {
        super();

        //update dt on date change
        valueProperty().addListener(updateDateTime);

        //update dt on time change
        timeProperty.addListener(updateDateTime);

        //update dt on zone change
        zoneProperty.addListener(updateDateTime);

        //update date, time, and zone on dt change
        dateTimeProperty.addListener((obs, ov, nv) -> {
            isUpdating = true;
            if (nv == null) {
                setValue(null);
                setTime(LocalTime.MIN);//we always need a time to prevent null points from the time sliders, 00:00 is perfect.
            } else {
                setValue(nv.toLocalDate());
                setTime(nv.toLocalTime());
                setZone(nv.getZone());
            }
            isUpdating = false;
        });

        setConverter(new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate object) {
                ZonedDateTime zdt = dateTimeProperty().get();
                return zdt == null ? "" : dateTimeFormatterProperty.get().format(zdt);
            }

            @Override
            public LocalDate fromString(String string) {
                ZonedDateTime zdt = ZonedDateTime.parse(string, dateTimeFormatterProperty.get());
                if (zdt == null) {
                    //NOTE: we dont reset zone. we'll need that later to rebuild our zoneddatetime from date and time instances
                    //clear time
                    timeProperty.set(null);
                    //clear date
                    return null;
                }
                //apply change to time
                timeProperty.set(zdt.toLocalTime());
                //apply chage to date
                return zdt.toLocalDate();
            }
        });

        //initial value
        setDateTime(ZonedDateTime.now());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DateTimePickerSkin(this);
    }

    public LocalTime getTime() {
        return timeProperty.get();
    }

    void setTime(LocalTime timeValue) {
        this.timeProperty.set(timeValue);
    }

    public ObjectProperty<LocalTime> timeProperty() {
        return timeProperty;
    }

    public ZonedDateTime getDateTime() {
        return dateTimeProperty.get();
    }

    public void setDateTime(ZonedDateTime dateTimeValue) {
        dateTimeProperty.set(dateTimeValue);
    }

    public ObjectProperty<ZonedDateTime> dateTimeProperty() {
        return dateTimeProperty;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatterProperty.get();
    }

    public void setDateTimeFormatter(DateTimeFormatter dateTimeValue) {
        dateTimeFormatterProperty.set(dateTimeValue);
    }

    public ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty() {
        return dateTimeFormatterProperty;
    }

    public ObjectProperty<ZoneId> zoneProperty() {
        return zoneProperty;
    }

    public void setZone(ZoneId zone) {
        zoneProperty.set(zone);
    }

    public ZoneId getZone() {
        return zoneProperty.get();
    }
}
