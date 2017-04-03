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

import java.time.Instant;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class TimelineItem {

    private final Instant date;
    private final String title;
    private final String description;

    public TimelineItem(Instant date, String title, String description) {
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public Instant getDateTime() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
