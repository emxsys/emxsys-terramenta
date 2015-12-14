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
package com.terramenta.time;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public interface DatetimeProvider {

    /**
     * get the datetime
     *
     * @return
     */
    Instant getDatetime();

    /**
     * Set datetime to the provided value
     *
     * @param datetime
     */
    void setDatetime(Instant datetime);

    /**
     * Modify the current datetime value
     *
     * @param amount
     * @param unit
     */
    void modifyDatetime(long amount, ChronoUnit unit);

    void addChangeListener(DatetimeChangeListener listener);

    void removeChangeListener(DatetimeChangeListener listener);
}
