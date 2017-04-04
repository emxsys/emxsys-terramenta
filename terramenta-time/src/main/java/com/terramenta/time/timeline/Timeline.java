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
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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

    private final ObjectProperty<Instant> displayDatetimeProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Duration> displayDurationProperty = new SimpleObjectProperty<Duration>() {
        @Override
        public void set(Duration duration) {
            if (duration == null || duration.compareTo(Duration.ZERO) < 0) {
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
            if (duration == null || duration.getSeconds() < Timeline.this.getWidth()) {
                //restrict 1 second per pixel bounds
                duration = Duration.ofSeconds((long) Timeline.this.getWidth());
            }

            super.set(duration);

            //if bounds are less than display, set display to bounds
            Duration dd = displayDurationProperty.get();
            if (dd != null && duration != null && duration.compareTo(dd) < 0) {
                displayDurationProperty.set(duration);
            }
        }
    };

    private final ObservableList<TimelineItem> timelineItems = FXCollections.<TimelineItem>observableArrayList();
    private final ObservableList<TimelineItem> sortedTimelineItems = new SortedList<>(timelineItems, (TimelineItem a, TimelineItem b) -> {
        return a.getDateTime().compareTo(b.getDateTime());
    });

    private final Group timelineItemMarkers = new Group();
    private final TimelineAxis topAxis;
    private final TimelineAxis bottomAxis;
    private final Polygon displayDurationRegion;
    private final Line displayDatetimeLine;
    private final Line cursorLine;
    private double priorX;

    public Timeline() {
        this(Instant.now(), Duration.ofHours(1), Duration.ofHours(12));
    }

    public Timeline(Instant displayDate, Duration displayDuration, Duration boundingDuration) {
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        this.timelineDurationProperty.set(boundingDuration);
        this.displayDurationProperty.set(displayDuration);
        this.displayDatetimeProperty.set(displayDate);

        //update if any date propertys change
        this.displayDatetimeProperty.addListener((obs, ov, nv) -> update());
        this.displayDurationProperty.addListener((obs, ov, duration) -> update());
        this.timelineDurationProperty.addListener((obs, ov, duration) -> update());

        //auto scaling number axis
        topAxis = new TimelineAxis();
        topAxis.setTickLabelRotation(90);//vertical labels
        topAxis.setTickLabelFormatter(new StringConverter<Number>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm:ss");

            @Override
            public String toString(Number seconds) {
                ZoneId zone = ZoneId.of(TimeOptions.getPreferences().get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE));
                return formatter.format(ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds.longValue()), zone));
            }

            @Override
            public Number fromString(String label) {
                return formatter.parse(label, Instant::from).getEpochSecond();
            }
        });
        topAxis.setSide(Side.BOTTOM);//effectivly inversed
        topAxis.prefWidthProperty().bind(this.widthProperty());
        topAxis.prefHeightProperty().bind(this.heightProperty());

        //auto scaling number axis
        bottomAxis = new TimelineAxis();
        bottomAxis.setSide(Side.TOP);//effectivly inversed
        //cant use setTickLabelsVisible since it hides the major ticks too
        //so instead to have to rotate empty text
        //bottomAxis.setTickLabelsVisible(false); //::sad face:: 
        bottomAxis.setTickLabelFormatter(new StringConverter<Number>() {

            @Override
            public String toString(Number seconds) {
                return "";
            }

            @Override
            public Number fromString(String label) {
                return null;
            }
        });
        //bind values from top axis
        bottomAxis.tickLabelRotationProperty().bind(topAxis.tickLabelRotationProperty());
        bottomAxis.lowerBoundProperty().bind(topAxis.lowerBoundProperty());
        bottomAxis.upperBoundProperty().bind(topAxis.upperBoundProperty());
        bottomAxis.prefWidthProperty().bind(topAxis.widthProperty());
        bottomAxis.prefHeightProperty().bind(topAxis.heightProperty());

        //display data line
        displayDatetimeLine = new Line();
        displayDatetimeLine.setStrokeWidth(1);
        displayDatetimeLine.setStroke(Color.GRAY);
        displayDatetimeLine.startYProperty().bind(layoutYProperty());
        displayDatetimeLine.endYProperty().bind(Bindings.add(layoutYProperty(), heightProperty()));

        //display duration region
        displayDurationRegion = new Polygon();
        displayDurationRegion.setOpacity(0.3);
        displayDurationRegion.setStrokeWidth(0);
        displayDurationRegion.setFill(Color.GRAY);

        //display data line
        cursorLine = new Line();
        cursorLine.setStrokeWidth(1);
        cursorLine.setStroke(Color.LIGHTGRAY);
        cursorLine.startYProperty().bind(layoutYProperty());
        cursorLine.endYProperty().bind(Bindings.add(layoutYProperty(), heightProperty()));

        //set all kids back to front
        this.getChildren().addAll(displayDatetimeLine, bottomAxis, topAxis, displayDurationRegion, cursorLine, timelineItemMarkers);

        //listen for data changes
        sortedTimelineItems.addListener((Observable obs) -> {
            timelineItemMarkers.getChildren().setAll(
                    sortedTimelineItems.stream()
                            .map(ti -> {
                                double x = topAxis.getDisplayPosition(ti.getDateTime().getEpochSecond());
                                Line marker = new Line();
                                marker.setStartX(x);
                                marker.setEndX(x);
                                marker.setStartY(topAxis.getLayoutY());
                                marker.setEndY(topAxis.getLayoutY() + topAxis.getHeight());
                                marker.setUserData(ti);
                                marker.setStroke(Color.MAGENTA);
                                marker.setStrokeWidth(1);
                                return marker;
                            })
                            .collect(Collectors.toList())
            );
        });

        //scroll handler for adjusting durations 
        this.addEventHandler(ScrollEvent.SCROLL, e -> {
            if (e.isControlDown()) {
                adjustDisplayDuration(e.getDeltaY());
            } else {
                adjustTimelineDuration(e.getDeltaY());
            }
        });

        //mouse handlers for adjusting durations
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            priorX = e.getX();
            //set drag styling
            this.setCursor(Cursor.H_RESIZE);
        });
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double snapedX = snap(e.getX());
            cursorLine.setStartX(snapedX);
            cursorLine.setEndX(snapedX);

            double offsetX = e.getX() - priorX;
            priorX = e.getX();

            if (e.isControlDown()) {
                adjustDisplayDuration(offsetX);
            } else {
                adjustDisplayDatetime(-offsetX);
            }
        });
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            priorX = Double.NaN;
            //reset styling
            this.setCursor(Cursor.DEFAULT);
        });

        this.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            double snapedX = snap(e.getX());
            cursorLine.setStartX(snapedX);
            cursorLine.setEndX(snapedX);
        });
    }

    /**
     * refresh values during layout changes
     */
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        update();
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

//*******************
//* PRIVATE METHODS *
//*******************
    //adjust bounding duration
    private void adjustTimelineDuration(double offset) {
        double zeroPixels = topAxis.getDisplayPosition(0d);
        double deltaPixels = zeroPixels + offset;
        long deltaSeconds = topAxis.getValueForDisplay(deltaPixels).longValue();
        Duration newDuration = getTimelineDuration().plusSeconds(deltaSeconds);
        setTimelineDuration(newDuration);
    }

    //adjust selected duration
    private void adjustDisplayDatetime(double offset) {
        double zeroPixels = topAxis.getDisplayPosition(0d);
        double deltaPixels = zeroPixels + offset;
        long deltaSeconds = topAxis.getValueForDisplay(deltaPixels).longValue();
        Instant newDatetime = getDisplayDatetime().plusSeconds(deltaSeconds);
        setDisplayDatetime(newDatetime);
    }

    //adjust selected duration
    private void adjustDisplayDuration(double offset) {
        double zeroPixels = topAxis.getDisplayPosition(0d);
        double deltaPixels = zeroPixels + offset;
        long deltaSeconds = topAxis.getValueForDisplay(deltaPixels).longValue();
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
        updateTimelineItemMarkers(displayDatetimeInSeconds, displayDurationInSeconds, timelineDurationInSeconds);

        displayDurationRegion.setVisible(true);
        displayDatetimeLine.setVisible(true);
    }

    private void updateAxis(long displayDateInSeconds, long timelineDurationInSeconds) {
        //set axis bounds
        double halfTimelineDurationInSeconds = timelineDurationInSeconds / 2;
        topAxis.setLowerBound(displayDateInSeconds - halfTimelineDurationInSeconds);
        topAxis.setUpperBound(displayDateInSeconds + halfTimelineDurationInSeconds);
    }

    private void updateDisplayDatetimeLine(long displayDatetimeInSeconds) {
        //draw display date line
        double nowScreenX = snap(topAxis.getDisplayPosition(displayDatetimeInSeconds));
        displayDatetimeLine.setStartX(nowScreenX);
        displayDatetimeLine.setEndX(nowScreenX);
    }

    private void updateDisplayDurationRegion(long displayDatetimeInSeconds, long displayDurationInSeconds) {
        //draw display offset region
        double halfDisplayDurationInSeconds = displayDurationInSeconds / 2;
        double minScreenX = topAxis.getDisplayPosition(displayDatetimeInSeconds - halfDisplayDurationInSeconds);
        double maxScreenX = topAxis.getDisplayPosition(displayDatetimeInSeconds + halfDisplayDurationInSeconds);
        displayDurationRegion.getPoints().setAll(minScreenX, 0d, maxScreenX, 0d, maxScreenX, topAxis.getHeight(), minScreenX, topAxis.getHeight());
    }

    private void updateTimelineItemMarkers(long displayDateInSeconds, long displayDurationInSeconds, long timelineDurationInSeconds) {
        if (timelineItemMarkers.getChildren().isEmpty()) {
            return;
        }

        double halfDisplayDurationInSeconds = displayDurationInSeconds / 2;
        double lowerDisplayBoundInSeconds = displayDateInSeconds - halfDisplayDurationInSeconds;
        double upperDisplayBoundInSeconds = displayDateInSeconds + halfDisplayDurationInSeconds;

        double halfTimelineDurationInSeconds = timelineDurationInSeconds / 2;
        double lowerTimelineBoundInSeconds = displayDateInSeconds - halfTimelineDurationInSeconds;
        double upperTimelineBoundInSeconds = displayDateInSeconds + halfTimelineDurationInSeconds;

        //update position of markers
        timelineItemMarkers.getChildren().forEach(tim -> {
            Line marker = (Line) tim;
            marker.setVisible(false);
            long sec = ((TimelineItem) marker.getUserData()).getDateTime().getEpochSecond();
            if (sec >= lowerTimelineBoundInSeconds && sec <= upperTimelineBoundInSeconds) {
                double x = topAxis.getDisplayPosition(sec);
                marker.setStartX(x);
                marker.setEndX(x);
                marker.setStartY(topAxis.getLayoutY());
                marker.setEndY(topAxis.getLayoutY() + topAxis.getHeight());
                if (sec >= lowerDisplayBoundInSeconds && sec <= upperDisplayBoundInSeconds) {
                    marker.setFill(Color.CYAN);
                }else{
                    marker.setFill(Color.MAGENTA);
                }
                marker.setVisible(true);
            }
        });

    }

    public static double snap(double val) {
        return ((int) val) + .5;
    }

}
