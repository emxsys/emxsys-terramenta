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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class TimelineAxis extends ValueAxis<Number> {

    /**
     * Possible tick spacing at the 10^1 level. must be between 1 and 10
     */
    private static final double[] dividers = new double[]{1.0, 2.5, 5.0};

    private static final int numMinorTicks = 3;

    private final Timeline animationTimeline = new Timeline();
    private final WritableValue<Double> scaleValue = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return getScale();
        }

        @Override
        public void setValue(Double value) {
            setScale(value);
        }
    };

    private List<Number> minorTicks;

    /**
     * Amount of padding to add on the each end of the axis when auto ranging.
     */
    private DoubleProperty autoRangePadding = new SimpleDoubleProperty(0.1);

    /**
     * If true, when auto-ranging, force 0 to be the min or max end of the range.
     */
    private BooleanProperty forceZeroInRange = new SimpleBooleanProperty(true);

    private static final StringConverter<Number> DEFAULT_FORMATTER = new StringConverter<Number>() {
        //private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm:ss");
        private final DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy G");
        private final DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("MMMM yyyy");
        private final DateTimeFormatter yearMonthDay = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        private final DateTimeFormatter hourMin = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        private final DateTimeFormatter hourMinSecond = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);

        private Number previous = null;

        @Override
        public String toString(Number seconds) {

            /**
             * NOTE: This only works because we are creating an offscreen tick at each end of the
             * region. The initial label for the first(offscreen) tick will be blank, subsequently
             * its label will be incorrect since it's value will be compared to the "previous" of
             * the last tick from the prior label formatting sequence.
             */
            if (previous == null) {
                previous = seconds;
                return "";
            }

            ZoneId zone = ZoneId.of(TimeOptions.getPreferences().get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE));
            ZonedDateTime datetime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds.longValue()), zone);

            String output;
            long diff = seconds.longValue() - previous.longValue();
            if (diff < ChronoUnit.HOURS.getDuration().getSeconds()) {
                output = hourMinSecond.format(datetime);
            } else if (diff < ChronoUnit.DAYS.getDuration().getSeconds()) {
                output = hourMin.format(datetime);
            } else if (diff < ChronoUnit.MONTHS.getDuration().getSeconds()) {
                output = yearMonthDay.format(datetime);
            } else if (diff < ChronoUnit.YEARS.getDuration().getSeconds()) {
                output = yearMonth.format(datetime);
            } else {
                output = year.format(datetime);
            }
            previous = seconds;
            return output;
        }

        @Override
        public Number fromString(String label) {
            throw new UnsupportedOperationException("not implemented.");
        }
    };

    public TimelineAxis() {
        setAutoRanging(false);//!important
        setAnimated(false);//!important
        setTickLength(10);
    }

    /**
     * Amount of padding to add on the each end of the axis when auto ranging.
     */
    public double getAutoRangePadding() {
        return autoRangePadding.get();
    }

    /**
     * Amount of padding to add on the each end of the axis when auto ranging.
     */
    public DoubleProperty autoRangePaddingProperty() {
        return autoRangePadding;
    }

    /**
     * Amount of padding to add on the each end of the axis when auto ranging.
     */
    public void setAutoRangePadding(double autoRangePadding) {
        this.autoRangePadding.set(autoRangePadding);
    }

    /**
     * If true, when auto-ranging, force 0 to be the min or max end of the range.
     */
    public boolean isForceZeroInRange() {
        return forceZeroInRange.get();
    }

    /**
     * If true, when auto-ranging, force 0 to be the min or max end of the range.
     */
    public BooleanProperty forceZeroInRangeProperty() {
        return forceZeroInRange;
    }

    /**
     * If true, when auto-ranging, force 0 to be the min or max end of the range.
     */
    public void setForceZeroInRange(boolean forceZeroInRange) {
        this.forceZeroInRange.set(forceZeroInRange);
    }

    @Override
    protected Range autoRange(double minValue, double maxValue, double length, double labelSize) {
//		System.out.printf( "autoRange(%f, %f, %f, %f)",
//		                   minValue, maxValue, length, labelSize );
        //By dweil: if the range is very small, display it like a flat line, the scaling doesn't work very well at these
        //values. 1e-300 was chosen arbitrarily.
        if (Math.abs(minValue - maxValue) < 1e-300) {
            //Normally this is the case for all points with the same value
            minValue = minValue - 1;
            maxValue = maxValue + 1;

        } else {
            //Add padding
            double delta = maxValue - minValue;
            double paddedMin = minValue - delta * autoRangePadding.get();
            //If we've crossed the 0 line, clamp to 0.
            //noinspection FloatingPointEquality
            if (Math.signum(paddedMin) != Math.signum(minValue)) {
                paddedMin = 0.0;
            }

            double paddedMax = maxValue + delta * autoRangePadding.get();
            //If we've crossed the 0 line, clamp to 0.
            //noinspection FloatingPointEquality
            if (Math.signum(paddedMax) != Math.signum(maxValue)) {
                paddedMax = 0.0;
            }

            minValue = paddedMin;
            maxValue = paddedMax;
        }

        //Handle forcing zero into the range
        if (forceZeroInRange.get()) {
            if (minValue < 0 && maxValue < 0) {
                maxValue = 0;
                minValue -= -minValue * autoRangePadding.get();
            } else if (minValue > 0 && maxValue > 0) {
                minValue = 0;
                maxValue += maxValue * autoRangePadding.get();
            }
        }

        Range ret = getRange(minValue, maxValue);
//		System.out.printf( " = %s%n", ret );
        return ret;
    }

    private Range getRange(double minValue, double maxValue) {
        double length = getLength();
        double delta = maxValue - minValue;
        double scale = calculateNewScale(length, minValue, maxValue);

        int maxTicks = Math.max(1, (int) (length / getLabelSize()));

        Range ret;
        ret = new Range(minValue, maxValue, calculateTickSpacing(delta, maxTicks), scale);
        return ret;
    }

    public static double calculateTickSpacing(double delta, int maxTicks) {
        if (delta == 0.0) {
            return 0.0;
        }
        if (delta <= 0.0) {
            throw new IllegalArgumentException("delta must be positive");
        }
        if (maxTicks < 1) {
            throw new IllegalArgumentException("must be at least one tick");
        }

        //The factor will be close to the log10, this just optimizes the search
        int factor = (int) Math.log10(delta);
        int divider = 0;
        double numTicks = delta / (dividers[divider] * Math.pow(10, factor));

        //We don't have enough ticks, so increase ticks until we're over the limit, then back off once.
        if (numTicks < maxTicks) {
            while (numTicks < maxTicks) {
                //Move up
                --divider;
                if (divider < 0) {
                    --factor;
                    divider = dividers.length - 1;
                }

                numTicks = delta / (dividers[divider] * Math.pow(10, factor));
            }

            //Now back off once unless we hit exactly
            //noinspection FloatingPointEquality
            if (numTicks != maxTicks) {
                ++divider;
                if (divider >= dividers.length) {
                    ++factor;
                    divider = 0;
                }
            }
        } else {
            //We have too many ticks or exactly max, so decrease until we're just under (or at) the limit.
            while (numTicks > maxTicks) {
                ++divider;
                if (divider >= dividers.length) {
                    ++factor;
                    divider = 0;
                }

                numTicks = delta / (dividers[divider] * Math.pow(10, factor));
            }
        }

//		System.out.printf( "calculateTickSpacing( %f, %d ) = %f%n",
//		                   delta, maxTicks, dividers[divider] * Math.pow( 10, factor ) );
        return dividers[divider] * Math.pow(10, factor);
    }

    @Override
    protected List<Number> calculateMinorTickMarks() {
//		System.out.println( "TimelineAxis.calculateMinorTickMarks" );
        return minorTicks;
    }

    @Override
    protected void setRange(Object range, boolean animate) {
        Range rangeVal = (Range) range;
//		System.out.format( "TimelineAxis.setRange (%s, %s)%n",
//		                   range, animate );
        if (animate) {
            animationTimeline.stop();
            ObservableList<KeyFrame> keyFrames = animationTimeline.getKeyFrames();
            keyFrames.setAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(currentLowerBound, getLowerBound()),
                            new KeyValue(scaleValue, getScale())),
                    new KeyFrame(Duration.millis(750),
                            new KeyValue(currentLowerBound, rangeVal.low),
                            new KeyValue(scaleValue, rangeVal.scale)));
            animationTimeline.play();

        } else {
            currentLowerBound.set(rangeVal.low);
            setScale(rangeVal.scale);
        }
        setLowerBound(rangeVal.low);
        setUpperBound(rangeVal.high);

        //axisTickFormatter.setRange(rangeVal.low, rangeVal.high, rangeVal.tickSpacing);
    }

    @Override
    protected Range getRange() {
        Range ret = getRange(getLowerBound(), getUpperBound());
//		System.out.println( "TimelineAxis.getRange = " + ret );
        return ret;
    }

    @Override
    protected List<Number> calculateTickValues(double length, Object range) {
        Range rangeVal = (Range) range;
//		System.out.format( "TimelineAxis.calculateTickValues (length=%f, range=%s)",
//		                   length, rangeVal );
        //Use floor so we start generating ticks before the axis starts -- this is really only relevant
        //because of the minor ticks before the first visible major tick. We'll generate a first
        //invisible major tick but the ValueAxis seems to filter it out.
        double firstTick = Math.floor(rangeVal.low / rangeVal.tickSpacing) * rangeVal.tickSpacing;
        //Generate one more tick than we expect, for "overlap" to get minor ticks on both sides of the
        //first and last major tick.
        int numTicks = (int) (rangeVal.getDelta() / rangeVal.tickSpacing) + 1;
        List<Number> ret = new ArrayList<Number>(numTicks + 1);
        minorTicks = new ArrayList<Number>((numTicks + 2) * numMinorTicks);
        double minorTickSpacing = rangeVal.tickSpacing / (numMinorTicks + 1);
        for (int i = 0; i <= numTicks; ++i) {
            double majorTick = firstTick + rangeVal.tickSpacing * i;
            ret.add(majorTick);
            for (int j = 1; j <= numMinorTicks; ++j) {
                minorTicks.add(majorTick + minorTickSpacing * j);
            }
        }
//		System.out.printf( " = %s%n", ret );
        return ret;
    }

    private double getLength() {
        if (getSide().isHorizontal()) {
            return getWidth();
        } else {
            return getHeight();
        }
    }

    private double getLabelSize() {
        Dimension2D dim = measureTickMarkLabelSize("MMM dd yyyy, HH:mm:ss", getTickLabelRotation());
        if (getSide().isHorizontal()) {
            return dim.getWidth();
        } else {
            return dim.getHeight();
        }
    }

    @Override
    protected String getTickMarkLabel(Number value) {
        StringConverter<Number> formatter = getTickLabelFormatter();
        return formatter == null ? DEFAULT_FORMATTER.toString(value) : formatter.toString(value);
    }

    private static class Range {

        public final double low;
        public final double high;
        public final double tickSpacing;
        public final double scale;

        private Range(double low, double high, double tickSpacing, double scale) {
            this.low = low;
            this.high = high;
            this.tickSpacing = tickSpacing;
            this.scale = scale;
        }

        public double getDelta() {
            return high - low;
        }

        @Override
        public String toString() {
            return "Range{"
                    + "low=" + low
                    + ", high=" + high
                    + ", tickSpacing=" + tickSpacing
                    + ", scale=" + scale
                    + '}';
        }
    }
}
