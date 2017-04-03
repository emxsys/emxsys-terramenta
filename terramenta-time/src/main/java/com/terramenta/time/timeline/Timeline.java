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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.util.StringConverter;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class Timeline extends Region {

    private static final int TICK_COUNT = 20;

    private final ObjectProperty<Instant> displayDatetimeProperty = new SimpleObjectProperty<>(null);

    private final ObjectProperty<Duration> displayDurationProperty = new SimpleObjectProperty<Duration>() {
        @Override
        public void set(Duration duration) {
            if (duration == null) {
                //default to zero
                duration = Duration.ZERO;
            } else if (duration.compareTo(Duration.ZERO) < 0) {
                //restrict to positive bounds
                duration = Duration.ZERO;
            } else if (duration.compareTo(timelineDurationProperty.get()) > 0) {
                //restrict to timeline duration
                duration = timelineDurationProperty.get();
            }

            super.set(duration);
        }
    };

    private final ObjectProperty<Duration> timelineDurationProperty = new SimpleObjectProperty<Duration>() {
        @Override
        public void set(Duration duration) {
            if (duration == null) {
                //default to zero
                duration = Duration.ZERO;
            } else if (duration.compareTo(Duration.ZERO) < 0) {
                //restrict to positive bounds
                duration = Duration.ZERO;
            }

            super.set(duration);

            //if bounds are less than display, set display to bounds
            Duration dd = displayDurationProperty.get();
            if (dd != null && duration != null && duration.compareTo(dd) < 0) {
                displayDurationProperty.set(duration);
            }
        }
    };
    ;

    private final ObservableList<TimelineItem> timelineItems = FXCollections.<TimelineItem>observableArrayList();
    private final ObservableList<TimelineItem> sortedTimelineItems = new SortedList<>(timelineItems, (TimelineItem a, TimelineItem b) -> {
        return a.getDateTime().compareTo(b.getDateTime());
    });

    private final Group timelineItemMarkers = new Group();
    private final NumberAxis axis;
    private final Polygon displayDurationRegion;
    private final Line displayDatetimeLine;

    private double priorX;
    private boolean updatingDisplayDuration;

    public Timeline() {
        this(Instant.now(), Duration.ofHours(1), Duration.ofHours(12));
    }

    public Timeline(Instant displayDate, Duration displayDuration, Duration boundingDuration) {

        this.timelineDurationProperty.set(boundingDuration);
        this.displayDurationProperty.set(displayDuration);
        this.displayDatetimeProperty.set(displayDate);

        //update if any date propertys change
        this.displayDatetimeProperty.addListener((obs, ov, nv) -> update());
        this.displayDurationProperty.addListener((obs, ov, duration) -> update());
        this.timelineDurationProperty.addListener((obs, ov, duration) -> update());

        //auto scaling number axis
        axis = new NumberAxis();
        axis.setAutoRanging(false);//!important
        axis.setSide(Side.TOP);
        axis.prefWidthProperty().bind(this.widthProperty());
        axis.prefHeightProperty().bind(this.heightProperty());
        axis.setAnimated(false);//!important
        axis.setMinorTickVisible(true);
        axis.setTickLabelRotation(90);//vertical labels
        axis.setTickLabelFormatter(new StringConverter<Number>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.US)
                    .withZone(ZoneId.systemDefault());

            @Override
            public String toString(Number seconds) {
                return formatter.format(Instant.ofEpochSecond(seconds.longValue()));
            }

            @Override
            public Number fromString(String label) {
                return formatter.parse(label, Instant::from).getEpochSecond();
            }

        });

        //display data line
        displayDatetimeLine = new Line();
        displayDatetimeLine.setStrokeWidth(2);
        displayDatetimeLine.setStroke(Color.GRAY);
        displayDatetimeLine.startYProperty().bind(axis.layoutYProperty());
        displayDatetimeLine.endYProperty().bind(Bindings.add(axis.layoutYProperty(), axis.heightProperty()));

        //display duration region
        displayDurationRegion = new Polygon();
        displayDurationRegion.setOpacity(0.4);
        displayDurationRegion.setStrokeWidth(1);
        displayDurationRegion.setStroke(Color.GRAY);
        displayDurationRegion.setFill(Color.LIGHTGRAY);

        //set all kids back to front
        this.getChildren().addAll(displayDatetimeLine, axis, displayDurationRegion, timelineItemMarkers);

        //listen for data changes
        sortedTimelineItems.addListener((Observable obs) -> {
            timelineItemMarkers.getChildren().setAll(
                    sortedTimelineItems.stream()
                            .map(ti -> {
                                double x = axis.getDisplayPosition(ti.getDateTime().getEpochSecond());
                                Line marker = new Line();
                                marker.setStartX(x);
                                marker.setEndX(x);
                                marker.setStartY(axis.getLayoutY());
                                marker.setEndY(axis.getLayoutY() + axis.getHeight());
                                marker.setUserData(ti);
                                marker.setStroke(Color.MAGENTA);
                                marker.setStrokeWidth(1);
                                return marker;
                            })
                            .collect(Collectors.toList())
            );
        });

        //scroll handler for adjusting duration 
        this.addEventHandler(ScrollEvent.SCROLL, e -> {
            if (e.isControlDown()) {
                adjustDisplayDuration(e.getDeltaY());
            } else {
                adjustTimelineDuration(e.getDeltaY());
            }
        });

        //mouse handlers for display datetime/duration adjusting
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            priorX = e.getSceneX();
            updatingDisplayDuration = e.isControlDown();
            //set drag styling
            this.setCursor(Cursor.H_RESIZE);
        });
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double offsetX = e.getSceneX() - priorX;
            priorX = e.getSceneX();

            if (updatingDisplayDuration) {
                adjustDisplayDuration(offsetX);
            } else {
                adjustDisplayDatetime(offsetX);
            }
        });
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            priorX = Double.NaN;

            //reset styling
            this.setCursor(Cursor.DEFAULT);
        });

        //listen for layout changes. Will trigger when displayed
        axis.widthProperty().addListener((Observable obs) -> update());
        axis.heightProperty().addListener((Observable obs) -> update());
    }

    public ObjectProperty<Instant> displayDateProperty() {
        return displayDatetimeProperty;
    }

    public Instant getDisplayDatetime() {
        return displayDatetimeProperty.get();
    }

    public void setDisplayDatetime(Instant selectedInstant) {
        this.displayDatetimeProperty.set(selectedInstant);
    }

    public ObjectProperty<Duration> timelineDurationProperty() {
        return timelineDurationProperty;
    }

    public Duration getTimelineDuration() {
        return timelineDurationProperty.get();
    }

    public void setTimelineDuration(Duration duration) {
        this.timelineDurationProperty.set(duration);
    }

    public ObjectProperty<Duration> displayDurationProperty() {
        return displayDurationProperty;
    }

    public Duration getDisplayDuration() {
        return displayDurationProperty.get();
    }

    public void setDisplayDuration(final Duration duration) {
        this.displayDurationProperty.set(duration);
    }

    public ObservableList<TimelineItem> getTimelineItems() {
        return timelineItems;
    }

    //adjust bounding duration
    private void adjustTimelineDuration(double offset) {
        double zeroPixels = axis.getDisplayPosition(0d);
        double deltaPixels = zeroPixels + offset;
        long deltaSeconds = axis.getValueForDisplay(deltaPixels).longValue();
        Duration newDuration = getTimelineDuration().plusSeconds(deltaSeconds);
        setTimelineDuration(newDuration);
    }

    //adjust selected duration
    private void adjustDisplayDatetime(double offset) {
        double zeroPixels = axis.getDisplayPosition(0d);
        double deltaPixels = zeroPixels + offset;
        long deltaSeconds = axis.getValueForDisplay(deltaPixels).longValue();
        Instant newDatetime = getDisplayDatetime().plusSeconds(deltaSeconds);
        setDisplayDatetime(newDatetime);
    }

    //adjust selected duration
    private void adjustDisplayDuration(double offset) {
        double zeroPixels = axis.getDisplayPosition(0d);
        double deltaPixels = zeroPixels + offset;
        long deltaSeconds = axis.getValueForDisplay(deltaPixels).longValue();
        Duration newDuration = getDisplayDuration().plusSeconds(deltaSeconds);
        setDisplayDuration(newDuration);
    }

    private void update() {
        displayDatetimeLine.setVisible(false);
        displayDurationRegion.setVisible(false);

        //required: cant do much without these values
        Instant displayDatetime = displayDatetimeProperty.get();
        if (displayDatetime == null) {
            return;
        }

        long displayDatetimeInSeconds = displayDatetime.getEpochSecond();
        long displayDurationInSeconds = displayDurationProperty.get().getSeconds();
        long timelineDurationInSeconds = timelineDurationProperty.get().getSeconds();
        updateAxis(displayDatetimeInSeconds, timelineDurationInSeconds);
        updateDisplayDatetimeLine(displayDatetimeInSeconds);
        updateDisplayDurationRegion(displayDatetimeInSeconds, displayDurationInSeconds);
        updateTimelineItemMarkers(displayDatetimeInSeconds, timelineDurationInSeconds);

        displayDurationRegion.setVisible(true);
        displayDatetimeLine.setVisible(true);
    }

    private void updateAxis(long displayDateInSeconds, long timelineDurationInSeconds) {
        //set axis bounds
        double halfTimelineDurationInSeconds = timelineDurationInSeconds / 2;
        double tickSpacingInSeconds = timelineDurationInSeconds / TICK_COUNT;
        axis.setLowerBound(displayDateInSeconds - halfTimelineDurationInSeconds);
        axis.setUpperBound(displayDateInSeconds + halfTimelineDurationInSeconds);
        axis.setTickUnit(tickSpacingInSeconds);
    }

    private void updateDisplayDatetimeLine(long displayDatetimeInSeconds) {
        //draw display date line
        double nowScreenX = axis.getDisplayPosition(displayDatetimeInSeconds);
        displayDatetimeLine.setStartX(nowScreenX);
        displayDatetimeLine.setEndX(nowScreenX);
    }

    private void updateDisplayDurationRegion(long displayDatetimeInSeconds, long displayDurationInSeconds) {
        //draw display offset region
        double halfDisplayDurationInSeconds = displayDurationInSeconds / 2;
        double minScreenX = axis.getDisplayPosition(displayDatetimeInSeconds - halfDisplayDurationInSeconds);
        double maxScreenX = axis.getDisplayPosition(displayDatetimeInSeconds + halfDisplayDurationInSeconds);
        displayDurationRegion.getPoints().setAll(minScreenX, 0d, maxScreenX, 0d, maxScreenX, axis.getHeight(), minScreenX, axis.getHeight());
    }

    private void updateTimelineItemMarkers(long displayDateInSeconds, long timelineDurationInSeconds) {
        if (timelineItemMarkers.getChildren().isEmpty()) {
            return;
        }

        double halfTimelineDurationInSeconds = timelineDurationInSeconds / 2;
        double lowerBoundInSeconds = displayDateInSeconds - halfTimelineDurationInSeconds;
        double upperBoundInSeconds = displayDateInSeconds + halfTimelineDurationInSeconds;

        //update position of markers
        timelineItemMarkers.getChildren().forEach(tim -> {
            Line marker = (Line) tim;
            marker.setVisible(false);
            long sec = ((TimelineItem) marker.getUserData()).getDateTime().getEpochSecond();
            if (sec >= lowerBoundInSeconds && sec <= upperBoundInSeconds) {
                double x = axis.getDisplayPosition(sec);
                marker.setStartX(x);
                marker.setEndX(x);
                marker.setStartY(axis.getLayoutY());
                marker.setEndY(axis.getLayoutY() + axis.getHeight());
                marker.setVisible(true);
            }
        });

    }
}
