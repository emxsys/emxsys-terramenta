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
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

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

            Label hoursLabel = new Label("Hours: ");
            hoursLabel.setMinWidth(50);
            hoursLabel.setMaxWidth(50);
            hoursLabel.setPrefWidth(50);
            TextField hoursField = new TextField();
            hoursField.setMinWidth(20);
            hoursField.setMaxWidth(20);
            hoursField.setPrefWidth(20);
            Slider hoursSlider = new Slider(0, 23, (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getHour() : 0));
            Bindings.bindBidirectional(hoursField.textProperty(), hoursSlider.valueProperty(), new NumberStringConverter("00"));

            Label minutesLabel = new Label("Minutes: ");
            minutesLabel.setMinWidth(50);
            minutesLabel.setMaxWidth(50);
            minutesLabel.setPrefWidth(50);
            TextField minutesField = new TextField();
            minutesField.setMinWidth(20);
            minutesField.setMaxWidth(20);
            minutesField.setPrefWidth(20);
            Slider minutesSlider = new Slider(0, 59, (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getMinute() : 0));
            Bindings.bindBidirectional(minutesField.textProperty(), minutesSlider.valueProperty(), new NumberStringConverter("00"));

            Label secondsLabel = new Label("Seconds: ");
            secondsLabel.setMinWidth(50);
            secondsLabel.setMaxWidth(50);
            secondsLabel.setPrefWidth(50);
            TextField secondsField = new TextField();
            secondsField.setMinWidth(20);
            secondsField.setMaxWidth(20);
            secondsField.setPrefWidth(20);
            Slider secondsSlider = new Slider(0, 59, (dateTimePicker.getTime() != null ? dateTimePicker.getTime().getSecond() : 0));
            Bindings.bindBidirectional(secondsField.textProperty(), secondsSlider.valueProperty(), new NumberStringConverter("00"));

            ret.getChildren().addAll(
                    new HBox(hoursLabel, hoursField, hoursSlider),
                    new HBox(minutesLabel, minutesField, minutesSlider),
                    new HBox(secondsLabel, secondsField, secondsSlider));

            hoursSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                dateTimePicker.setTime(dateTimePicker.getTime().withHour(newValue.intValue()));
            });

            minutesSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                dateTimePicker.setTime(dateTimePicker.getTime().withMinute(newValue.intValue()));
            });

            secondsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                dateTimePicker.setTime(dateTimePicker.getTime().withSecond(newValue.intValue()));
            });

            dateTimePicker.timeProperty().addListener((obs, ov, nv) -> {
                if (nv == null) {
                    hoursSlider.setValue(0);
                    minutesSlider.setValue(0);
                    secondsSlider.setValue(0);
                    return;
                }
                hoursSlider.setValue(nv.getHour());
                minutesSlider.setValue(nv.getMinute());
                secondsSlider.setValue(nv.getSecond());
            });

        }
        return ret;
    }
}
