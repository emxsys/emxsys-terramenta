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
package com.terramenta.time;

import java.time.Instant;

/**
 *
 * @author chris.heidt
 */
public class DatetimeInterval {

    private final Instant startDatetime;
    private final Instant endDatetime;

    /**
     *
     * @param startDatetime
     * @param endDatetime
     */
    public DatetimeInterval(Instant startDatetime, Instant endDatetime) {
        if (startDatetime == null || endDatetime == null) {
            throw new IllegalArgumentException("Datetimes must not be null!");
        }

        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
    }

    /**
     *
     * @return
     */
    public Instant getStartDatetime() {
        return startDatetime;
    }

    /**
     *
     * @return
     */
    public Instant getEndDatetime() {
        return endDatetime;
    }

    public boolean contains(Instant datetime) {
        if (datetime == null) {
            return false;
        }

        return startDatetime.isBefore(datetime) && endDatetime.isAfter(datetime);
    }
}
