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
package com.terramenta.time.datepicker;

import java.util.Calendar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Chris.Heidt
 */
public class PopupCalendar {

    private DatePicker datePicker;

    private GridPane dayGrid;

    private Label lblMonth, lblYear;

    private Slider sldHour, sldMinute, sldSecond, sldMillisecond;

    private ChangeListener<Long> monthListener = new ChangeListener<Long>() {
        @Override
        public void changed(ObservableValue<? extends Long> arg0, Long arg1, Long arg2) {
            buildMonthLabel();
        }
    };

    private ChangeListener<Long> yearListener = new ChangeListener<Long>() {
        @Override
        public void changed(ObservableValue<? extends Long> arg0, Long arg1, Long arg2) {
            buildYearLabel();
        }
    };

    private ChangeListener<Long> dayListener = new ChangeListener<Long>() {
        @Override
        public void changed(ObservableValue<? extends Long> arg0, Long arg1, Long arg2) {
            buildDayGrid();
        }
    };

    private ChangeListener<Long> hourListener = new ChangeListener<Long>() {
        @Override
        public void changed(ObservableValue<? extends Long> arg0, Long arg1, Long arg2) {
            buildHourSlider();
        }
    };

    private ChangeListener<Long> minuteListener = new ChangeListener<Long>() {
        @Override
        public void changed(ObservableValue<? extends Long> arg0, Long arg1, Long arg2) {
            buildMinuteSlider();
        }
    };
    private ChangeListener<Long> secondListener = new ChangeListener<Long>() {
        @Override
        public void changed(ObservableValue<? extends Long> arg0, Long arg1, Long arg2) {
            buildSecondSlider();
        }
    };
    private ChangeListener<Long> millisecondListener = new ChangeListener<Long>() {
        @Override
        public void changed(ObservableValue<? extends Long> arg0, Long arg1, Long arg2) {
            buildMillisecondSlider();
        }
    };

    /**
     *
     * @param datePicker
     */
    public PopupCalendar(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    private void destroy() {
        datePicker.timestampProperty().removeListener(yearListener);
        datePicker.timestampProperty().removeListener(monthListener);
        datePicker.timestampProperty().removeListener(dayListener);
        datePicker.timestampProperty().removeListener(minuteListener);
        datePicker.timestampProperty().removeListener(secondListener);
        datePicker.timestampProperty().removeListener(millisecondListener);
    }

    /**
     *
     * @return
     */
    public Parent build() {
        // destroy previous
        destroy();

        VBox vbxMain = new VBox();
        vbxMain.getStyleClass().add("jfx-date-picker-calendar");

        GridPane dateGrid = new GridPane();

        dateGrid.getColumnConstraints().addAll(
                ColumnConstraintsBuilder.create().percentWidth(20).build(),
                ColumnConstraintsBuilder.create().percentWidth(60).build(),
                ColumnConstraintsBuilder.create().percentWidth(20).build()
        );

        dateGrid.setVgap(10);

        int rowIndex = 0;

        final int yearField = datePicker.getFieldIndex("y");
        // if we have years, show them
        if (yearField >= 0) {
            Button btnPreviousYear = new Button("<");
            btnPreviousYear.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent arg0) {
                    datePicker.incrementCalendarField(datePicker.fieldToCalendarField(yearField), -1, false);
                }
            });
            lblYear = new Label();
            datePicker.timestampProperty().addListener(yearListener);
            lblYear.getStyleClass().add("jfx-date-picker-year");
            buildYearLabel();
            Button btnNextYear = new Button(">");
            btnNextYear.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent arg0) {
                    datePicker.incrementCalendarField(datePicker.fieldToCalendarField(yearField), 1, false);
                }
            });
            GridPane.setHalignment(btnPreviousYear, HPos.CENTER);
            GridPane.setHalignment(lblYear, HPos.CENTER);
            GridPane.setHalignment(btnNextYear, HPos.CENTER);
            dateGrid.addRow(rowIndex++, btnPreviousYear, lblYear, btnNextYear);
        }

        final int monthField = datePicker.getFieldIndex("M");
        // if we have months, show them as well
        if (monthField >= 0) {
            Button btnPreviousMonth = new Button("<");
            btnPreviousMonth.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent arg0) {
                    datePicker.incrementCalendarField(datePicker.fieldToCalendarField(monthField), -1, false);
                }
            });
            lblMonth = new Label();
            datePicker.timestampProperty().addListener(monthListener);
            lblMonth.getStyleClass().add("jfx-date-picker-month");
            buildMonthLabel();
            Button btnNextMonth = new Button(">");
            btnNextMonth.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent arg0) {
                    datePicker.incrementCalendarField(datePicker.fieldToCalendarField(monthField), 1, false);
                }
            });
            GridPane.setHalignment(btnPreviousMonth, HPos.CENTER);
            GridPane.setHalignment(lblMonth, HPos.CENTER);
            GridPane.setHalignment(btnNextMonth, HPos.CENTER);
            dateGrid.addRow(rowIndex++, btnPreviousMonth, lblMonth, btnNextMonth);
        }

        if (dateGrid.getChildren().size() > 0) {
            vbxMain.getChildren().add(dateGrid);
        }

        final int dayField = datePicker.getFieldIndex("dDFE");
        if (dayField >= 0) {
            dayGrid = new GridPane();
            dayGrid.getStyleClass().add("jfx-date-picker-day");
            buildDayGrid();
            // rebuild on change
            datePicker.timestampProperty().addListener(dayListener);
            vbxMain.getChildren().add(dayGrid);
        }

        if (!datePicker.getHideTimeControls()) {
            GridPane timeGrid = new GridPane();

            timeGrid.getColumnConstraints().addAll(
                    ColumnConstraintsBuilder.create().percentWidth(35).build(),
                    ColumnConstraintsBuilder.create().percentWidth(65).build()
            );

            timeGrid.setVgap(10);

            rowIndex = 0;

            final int hourField = datePicker.getFieldIndex("HkKh");
            if (hourField >= 0) {
                sldHour = new Slider(0, 23, 0);
                buildHourSlider();
                sldHour.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        Calendar calendar = datePicker.getCalendar();
                        calendar.set(Calendar.HOUR_OF_DAY, arg2.intValue());
                        datePicker.setCalendar(calendar);
                    }
                });
                datePicker.timestampProperty().addListener(hourListener);
                timeGrid.addRow(rowIndex++, new Label("Hours:"), sldHour);
            }

            final int minuteField = datePicker.getFieldIndex("m");
            if (minuteField >= 0) {
                sldMinute = new Slider(0, 59, 0);
                buildMinuteSlider();
                sldMinute.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        Calendar calendar = datePicker.getCalendar();
                        calendar.set(Calendar.MINUTE, arg2.intValue());
                        datePicker.setCalendar(calendar);
                    }
                });
                datePicker.timestampProperty().addListener(minuteListener);
                timeGrid.addRow(rowIndex++, new Label("Minutes:"), sldMinute);
            }

            final int secondField = datePicker.getFieldIndex("s");
            if (secondField >= 0) {
                sldSecond = new Slider(0, 59, 0);
                buildSecondSlider();
                sldSecond.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        Calendar calendar = datePicker.getCalendar();
                        calendar.set(Calendar.SECOND, arg2.intValue());
                        datePicker.setCalendar(calendar);
                    }
                });
                datePicker.timestampProperty().addListener(secondListener);
                timeGrid.addRow(rowIndex++, new Label("Seconds:"), sldSecond);
            }

            final int millisecondField = datePicker.getFieldIndex("S");
            if (millisecondField >= 0) {
                sldMillisecond = new Slider(0, 999, 0);
                buildMillisecondSlider();
                sldMillisecond.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        Calendar calendar = datePicker.getCalendar();
                        calendar.set(Calendar.MILLISECOND, arg2.intValue());
                        datePicker.setCalendar(calendar);
                    }
                });
                datePicker.timestampProperty().addListener(millisecondListener);
                timeGrid.addRow(rowIndex++, new Label("Milliseconds:"), sldMillisecond);
            }

            if (timeGrid.getChildren().size() > 0) {
                vbxMain.getChildren().add(timeGrid);
            }
        }

        return vbxMain;
    }

    private void buildHourSlider() {
        Calendar calendar = datePicker.getCalendar();
        if (calendar != null) {
            sldHour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        }
    }

    private void buildMinuteSlider() {
        Calendar calendar = datePicker.getCalendar();
        if (calendar != null) {
            sldMinute.setValue(calendar.get(Calendar.MINUTE));
        }
    }

    private void buildSecondSlider() {
        Calendar calendar = datePicker.getCalendar();
        if (calendar != null) {
            sldSecond.setValue(calendar.get(Calendar.SECOND));
        }
    }

    private void buildMillisecondSlider() {
        Calendar calendar = datePicker.getCalendar();
        if (calendar != null) {
            sldMillisecond.setValue(calendar.get(Calendar.MILLISECOND));
        }
    }

    private void buildYearLabel() {
        Calendar calendar = datePicker.getCalendar();
        if (calendar != null) {
            lblYear.setText("" + calendar.get(datePicker.fieldToCalendarField(datePicker.getFieldIndex("y"))));
        }
    }

    private void buildMonthLabel() {
        Calendar calendar = datePicker.getCalendar();
        if (calendar != null) {
            lblMonth.setText(calendar.getDisplayName(datePicker.fieldToCalendarField(datePicker.getFieldIndex("M")), Calendar.LONG, datePicker.localeProperty().getValue()));
        }
    }

    private void buildDayGrid() {
        dayGrid.getChildren().clear();

        Calendar calendar = datePicker.getCalendar();
        if (calendar != null) {
            // for each day, create an entry in the grid
            // the entries for the titles may be created multiple times but this is okay
            Calendar copy = (Calendar) calendar.clone();
            for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                copy.set(Calendar.DAY_OF_MONTH, i);
                // get the column it belongs to
                int column = copy.get(Calendar.DAY_OF_WEEK);
                if (calendar.getFirstDayOfWeek() > 1) {
                    column -= (calendar.getFirstDayOfWeek() - 1);
                }
                if (column <= 0) {
                    column = 8 - column;
                }
                // so far it was 1-based, grid is 0-based
                column--;
                // 1-based again but this time it's ok because the first row is taken up by display names
                int row = copy.get(Calendar.WEEK_OF_MONTH);
                // set the displayname
                dayGrid.add(new Label(copy.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, datePicker.localeProperty().getValue())), column, 0);
                // set the actual day
                Button btnDay = new Button((copy.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + copy.get(Calendar.DAY_OF_MONTH));
                if (copy.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    btnDay.getStyleClass().add("jfx-date-picker-day-selected");
                }
                btnDay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Button btnThis = (Button) event.getSource();
                        int day = new Integer(btnThis.getText());
                        Calendar calendar = datePicker.getCalendar();
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        datePicker.setCalendar(calendar);
                    }
                });
                dayGrid.add(btnDay, column, row);
            }
        }
    }
}
