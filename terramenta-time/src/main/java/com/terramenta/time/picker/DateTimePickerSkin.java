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

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

import com.sun.javafx.scene.control.skin.DatePickerContent;
import com.sun.javafx.scene.control.skin.DatePickerSkin;
import javafx.beans.value.ChangeListener;

public class DateTimePickerSkin extends DatePickerSkin {

    private final DateTimePicker dateTimePicker;
    private DatePickerContent ret;

    private final ChangeListener updateField = (ChangeListener) (obs, ov, nv) -> {
        handleControlPropertyChanged("VALUE");
    };

    public DateTimePickerSkin(DateTimePicker dateTimePicker) {
        super(dateTimePicker);
        this.dateTimePicker = dateTimePicker;

        //this hack makes sure the editor field updates when our datetime changes(instead of just on date change)
        dateTimePicker.dateTimeProperty().addListener(updateField);
        //and this updates the field when the formatter changes
        dateTimePicker.dateTimeFormatterProperty().addListener(updateField);
    }

    @Override
    public Node getPopupContent() {
        if (ret == null) {
            ret = (DatePickerContent) super.getPopupContent();

            Slider hoursSlider = new Slider(0, 23, (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getHour() : 0));
            Label hoursLabel = new Label("Hours: " + (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getHour() : "") + " ");

            Slider minutesSlider = new Slider(0, 59, (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getMinute() : 0));
            Label minutesLabel = new Label("Minutes: " + (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getMinute() : "") + " ");

            Slider secondsSlider = new Slider(0, 59, (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getSecond() : 0));
            Label secondsLabel = new Label("Seconds: " + (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getSecond() : "") + " ");

            ret.getChildren().addAll(new HBox(hoursLabel, hoursSlider), new HBox(minutesLabel, minutesSlider), new HBox(secondsLabel, secondsSlider));

            hoursSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                int hours = newValue.intValue();
                hoursLabel.setText("Hours: " + String.format("%02d", hours) + " ");
                dateTimePicker.setTime(dateTimePicker.getTime().withHour(hours));
            });

            minutesSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                int minutes = newValue.intValue();
                minutesLabel.setText("Minutes: " + String.format("%02d", minutes) + " ");
                dateTimePicker.setTime(dateTimePicker.getTime().withMinute(minutes));
            });

            secondsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                int seconds = newValue.intValue();
                secondsLabel.setText("Seconds: " + String.format("%02d", seconds) + " ");
                dateTimePicker.setTime(dateTimePicker.getTime().withSecond(seconds));
            });

        }
        return ret;
    }

}
