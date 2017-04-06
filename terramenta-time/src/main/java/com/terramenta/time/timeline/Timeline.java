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
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
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
import javafx.scene.shape.Rectangle;
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

            invalidateAxis();
            invalidateOverlays();
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

            invalidateAxis();
            invalidateOverlays();
        }
    };

    private final BooleanProperty armedProperty = new SimpleBooleanProperty(false);
    private final ObservableList<TimelineItem> timelineItems = FXCollections.<TimelineItem>observableArrayList();
    private final Group timelineItemMarkers = new Group();
    private final TimelineAxis topAxis;
    private final TimelineAxis bottomAxis;
    private final Rectangle displayDurationRectangle;
    private final Line displayDatetimeLine;
    private final Line cursorLine;
    private double priorX;
    private boolean axisInvalidated = false;
    private boolean overlaysInvalidated = false;
    private Orientation orientation = Orientation.HORIZONTAL;
    private double priorY;

    public Timeline() {
        this(null, null, null);
    }

    public Timeline(Instant displayDate, Duration displayDuration, Duration boundingDuration) {
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        //ordered
        this.displayDatetimeProperty.set(displayDate);
        this.timelineDurationProperty.set(boundingDuration);
        this.displayDurationProperty.set(displayDuration);

        //invalidate if any date propertys change
        this.displayDatetimeProperty.addListener((obs, ov, nv) -> invalidateAxis());

        //auto scaling number axis
        topAxis = new TimelineAxis();
        topAxis.setTickLabelRotation(90);
        topAxis.setSide(Side.BOTTOM);//effectivly inversed
        topAxis.prefWidthProperty().bind(this.widthProperty());
        topAxis.prefHeightProperty().bind(this.heightProperty());

        //auto scaling number axis
        bottomAxis = new TimelineAxis();
        bottomAxis.setSide(Side.TOP);
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

        //display data line, always centerd in region. favoring performance over accuracy for display indicators
        displayDatetimeLine = new Line();
        displayDatetimeLine.setStrokeWidth(1);
        displayDatetimeLine.setStroke(Color.GRAY);
        displayDatetimeLine.startXProperty().bind(Bindings.add(layoutXProperty(), widthProperty().divide(2)));
        displayDatetimeLine.startYProperty().bind(layoutYProperty());
        displayDatetimeLine.endXProperty().bind(displayDatetimeLine.startXProperty());
        displayDatetimeLine.endYProperty().bind(Bindings.add(layoutYProperty(), heightProperty()));

        //display duration region, centered around display datetime line, updated only on duration/bounds changes
        displayDurationRectangle = new Rectangle();
        displayDurationRectangle.setOpacity(0.3);
        displayDurationRectangle.setStrokeWidth(0);
        displayDurationRectangle.setFill(Color.GRAY);
        displayDurationRectangle.yProperty().bind(layoutYProperty());
        displayDurationRectangle.heightProperty().bind(heightProperty());

        //display data line
        cursorLine = new Line();
        cursorLine.setStrokeWidth(1);
        cursorLine.setStroke(Color.LIGHTGRAY);
        cursorLine.startYProperty().bind(layoutYProperty());
        cursorLine.endYProperty().bind(Bindings.add(layoutYProperty(), heightProperty()));

        //set all kids back to front
        this.getChildren().addAll(displayDurationRectangle, displayDatetimeLine, topAxis, bottomAxis, timelineItemMarkers, cursorLine);

        //listen for data changes
        timelineItems.addListener((ListChangeListener.Change<? extends TimelineItem> c) -> {

            //just rebuilding all the markers for now.
            //TODO: review add/remove lists from Change parameter and create/remove markers accordingly
            List<Line> markers = timelineItems.stream().map(ti -> {
                double position = topAxis.getDisplayPosition(ti.getDateTime().getEpochSecond());
                Line marker = new Line();
                if (Orientation.HORIZONTAL.equals(orientation)) {
                    marker.setStartX(position);
                    marker.setEndX(position);
                    marker.setStartY(topAxis.getLayoutY());
                    marker.setEndY(topAxis.getLayoutY() + topAxis.getHeight());
                } else {
                    marker.setStartX(topAxis.getLayoutX());
                    marker.setEndX(topAxis.getLayoutX() + topAxis.getWidth());
                    marker.setStartY(position);
                    marker.setEndY(position);
                }
                marker.setUserData(ti);
                marker.setStroke(Color.MAGENTA);
                marker.setStrokeWidth(1);
                return marker;
            }).collect(Collectors.toList());

            timelineItemMarkers.getChildren().setAll(markers);
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
            priorY = e.getY();
            //set drag styling
            if (Orientation.HORIZONTAL.equals(orientation)) {
                this.setCursor(Cursor.H_RESIZE);
            } else {
                this.setCursor(Cursor.V_RESIZE);
            }
        });
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double offset;
            if (Orientation.HORIZONTAL.equals(orientation)) {
                double snapedX = snap(e.getX());
                cursorLine.setStartX(snapedX);
                cursorLine.setEndX(snapedX);

                offset = e.getX() - priorX;
                priorX = e.getX();
            } else {
                double snapedY = snap(e.getY());
                cursorLine.setStartY(snapedY);
                cursorLine.setEndY(snapedY);

                offset = e.getY() - priorY;
                priorY = e.getY();
            }

            if (e.isControlDown()) {
                adjustDisplayDuration(offset);
            } else {
                adjustDisplayDatetime(-offset);
            }
        });
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            priorX = Double.NaN;
            priorY = Double.NaN;
            //reset styling
            this.setCursor(Cursor.DEFAULT);
        });

        //cursor line events
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            cursorLine.setVisible(true);
        });
        this.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {

            if (Orientation.HORIZONTAL.equals(orientation)) {
                double snapedX = snap(e.getX());
                cursorLine.setStartX(snapedX);
                cursorLine.setEndX(snapedX);
            } else {
                double snapedY = snap(e.getY());
                cursorLine.setStartY(snapedY);
                cursorLine.setEndY(snapedY);
            }
        });
        this.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            cursorLine.setVisible(false);
        });

        //update on bounds changes
        this.layoutBoundsProperty().addListener((obs, ov, nv) -> {
            invalidateAxis();
            invalidateOverlays();
        });

        //update only invalidated items
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (axisInvalidated) {
                    axisInvalidated = false;
                    updateAxis();
                }

                if (overlaysInvalidated) {
                    overlaysInvalidated = false;
                    updateOverlays();
                }
            }
        };

        armedProperty.addListener((obs, ov, nv) -> {
            if (nv) {
                timer.start();
            } else {
                timer.stop();
            }
        });
    }

    public BooleanProperty armedProperty() {
        return armedProperty;
    }

    public boolean getArmed() {
        return armedProperty.get();
    }

    public void setArmed(boolean armed) {
        armedProperty.set(armed);
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        if (Orientation.HORIZONTAL.equals(orientation)) {
            topAxis.setSide(Side.BOTTOM);
            topAxis.setTickLabelRotation(90);

            bottomAxis.setSide(Side.TOP);

            displayDatetimeLine.startXProperty().bind(Bindings.add(layoutXProperty(), widthProperty().divide(2)));
            displayDatetimeLine.startYProperty().bind(layoutYProperty());
            displayDatetimeLine.endXProperty().bind(displayDatetimeLine.startXProperty());
            displayDatetimeLine.endYProperty().bind(Bindings.add(layoutYProperty(), heightProperty()));

            cursorLine.startXProperty().unbind();
            cursorLine.endXProperty().unbind();
            cursorLine.startYProperty().bind(layoutYProperty());
            cursorLine.endYProperty().bind(Bindings.add(layoutYProperty(), heightProperty()));

            displayDurationRectangle.xProperty().unbind();
            displayDurationRectangle.widthProperty().unbind();
            displayDurationRectangle.yProperty().bind(layoutYProperty());
            displayDurationRectangle.heightProperty().bind(heightProperty());
        } else {
            topAxis.setSide(Side.RIGHT);
            topAxis.setTickLabelRotation(0);

            bottomAxis.setSide(Side.LEFT);

            displayDatetimeLine.startXProperty().bind(layoutXProperty());
            displayDatetimeLine.startYProperty().bind(Bindings.add(layoutYProperty(), heightProperty().divide(2)));
            displayDatetimeLine.endXProperty().bind(Bindings.add(layoutXProperty(), widthProperty()));
            displayDatetimeLine.endYProperty().bind(displayDatetimeLine.startYProperty());

            cursorLine.startXProperty().bind(layoutXProperty());
            cursorLine.endXProperty().bind(Bindings.add(layoutXProperty(), widthProperty()));
            cursorLine.startYProperty().unbind();
            cursorLine.endYProperty().unbind();

            displayDurationRectangle.xProperty().bind(layoutXProperty());
            displayDurationRectangle.widthProperty().bind(widthProperty());
            displayDurationRectangle.yProperty().unbind();
            displayDurationRectangle.heightProperty().unbind();

        }
    }

    public void invalidateAxis() {
        axisInvalidated = true;
    }

    public void invalidateOverlays() {
        overlaysInvalidated = true;
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

    private void updateAxis() {
        Instant displayDatetime = displayDatetimeProperty.get();//required
        if (displayDatetime == null) {
            return;
        }

        long displayDatetimeInSeconds = displayDatetime.getEpochSecond();
        long displayDurationInSeconds = displayDurationProperty.get().getSeconds();
        long timelineDurationInSeconds = timelineDurationProperty.get().getSeconds();
        updateAxisBounds(displayDatetimeInSeconds, timelineDurationInSeconds);
        updateTimelineItemMarkers(displayDatetimeInSeconds, displayDurationInSeconds, timelineDurationInSeconds);
    }

    private void updateAxisBounds(long displayDatetimeInSeconds, long timelineDurationInSeconds) {
        //set axis bounds
        double halfTimelineDurationInSeconds = timelineDurationInSeconds / 2;
        topAxis.setLowerBound(displayDatetimeInSeconds - halfTimelineDurationInSeconds);
        topAxis.setUpperBound(displayDatetimeInSeconds + halfTimelineDurationInSeconds);
    }

    private void updateOverlays() {
        Instant displayDatetime = displayDatetimeProperty.get();//required
        if (displayDatetime == null) {
            return;
        }

        long displayDatetimeInSeconds = displayDatetime.getEpochSecond();
        long displayDurationInSeconds = displayDurationProperty.get().getSeconds();

        //draw display offset region
        double halfDisplayDurationInSeconds = displayDurationInSeconds / 2;
        double pastPixel = topAxis.getDisplayPosition(displayDatetimeInSeconds - halfDisplayDurationInSeconds);
        double futurePixel = topAxis.getDisplayPosition(displayDatetimeInSeconds + halfDisplayDurationInSeconds);
        if (Orientation.HORIZONTAL.equals(orientation)) {
            displayDurationRectangle.setX(Math.floor(pastPixel));
            displayDurationRectangle.setWidth(Math.ceil(futurePixel - pastPixel));
        } else {
            displayDurationRectangle.setY(Math.floor(futurePixel));
            displayDurationRectangle.setHeight(Math.ceil(pastPixel - futurePixel));
        }
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
                double position = topAxis.getDisplayPosition(sec);
                if (Orientation.HORIZONTAL.equals(orientation)) {
                    marker.setStartX(position);
                    marker.setEndX(position);
                    marker.setStartY(topAxis.getLayoutY());
                    marker.setEndY(topAxis.getLayoutY() + topAxis.getHeight());
                } else {
                    marker.setStartX(topAxis.getLayoutX());
                    marker.setEndX(topAxis.getLayoutX() + topAxis.getWidth());
                    marker.setStartY(position);
                    marker.setEndY(position);
                }
                if (sec >= lowerDisplayBoundInSeconds && sec <= upperDisplayBoundInSeconds) {
                    marker.setFill(Color.CYAN);
                } else {
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
