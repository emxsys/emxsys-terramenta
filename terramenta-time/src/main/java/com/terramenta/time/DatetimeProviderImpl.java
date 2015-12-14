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
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
@ServiceProvider(service = DatetimeProvider.class)
public class DatetimeProviderImpl implements DatetimeProvider {

    private final CopyOnWriteArrayList<DatetimeChangeListener> listeners = new CopyOnWriteArrayList<>();
    private Instant datetime = Instant.now();

    /**
     * get the date
     *
     * @return
     */
    @Override
    public Instant getDatetime() {
        return datetime;
    }

    /**
     * Set datetime to the provided value
     *
     * @param datetime
     */
    @Override
    public void setDatetime(Instant datetime) {
        if (Objects.equals(this.datetime, datetime)) {
            return;
        }
        fire(this.datetime, this.datetime = datetime);
    }

    /**
     * Modify the current datetime value
     *
     * @param amount
     * @param unit
     */
    @Override
    public void modifyDatetime(long amount, ChronoUnit unit) {
        setDatetime(datetime.plus(amount, unit));
    }

    @Override
    public void addChangeListener(DatetimeChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeChangeListener(DatetimeChangeListener listener) {
        listeners.remove(listener);
    }

    private void fire(Instant oldDatetime, Instant newDatetime) {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onDatetimeChange(oldDatetime, newDatetime);
        }
    }
}
