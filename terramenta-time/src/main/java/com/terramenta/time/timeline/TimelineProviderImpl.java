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

import com.terramenta.time.DatetimeProvider;
import com.terramenta.time.actions.TimeActionController;
import java.time.Duration;
import javafx.geometry.Orientation;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
@ServiceProvider(service = TimelineProvider.class)
public class TimelineProviderImpl implements TimelineProvider {

    private static final DatetimeProvider dtp = Lookup.getDefault().lookup(DatetimeProvider.class);
    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private Timeline timeline = null;

    @Override
    public Timeline getTimeline() {
        if (timeline == null) {
            initTimeline();
        }
        return timeline;
    }

    private void initTimeline() {
        Duration displayDuration = tac.getDisplayDuration();
        Duration timelineDuration = displayDuration.isZero() ? Duration.ofDays(1) : displayDuration.multipliedBy(3);
        timeline = new Timeline(dtp.getDatetime(), displayDuration, timelineDuration);
        timeline.setOrientation(Orientation.VERTICAL);

        //listen for date changes
        dtp.addChangeListener((oldDatetime, newDatetime) -> {
            timeline.setDisplayDatetime(newDatetime);
        });

        //update Timeline to reflect changes in display duration
        tac.addPropertyChangeListener(pce -> {
            if (pce.getPropertyName().equals(TimeActionController.DURATION)) {
                timeline.setDisplayDuration((Duration) pce.getNewValue());
            }
        });

        //update DatetimeProvider to reflect changes in display date
        timeline.displayDateProperty().addListener((obs, oldDatetime, newDatetime) -> {
            dtp.setDatetime(newDatetime);
        });

        //update TimeActionController to reflect changes in display duration
        timeline.displayDurationProperty().addListener((obs, oldDuration, newDuration) -> {
            tac.setDisplayDuration(newDuration);
        });
    }
}
