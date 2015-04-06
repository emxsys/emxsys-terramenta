/**
 * Copyright © 2014, Terramenta. All rights reserved. * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this work except in compliance
 * with the License.
 *
 * You can obtain a copy of the License at http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 *
 */
package com.terramenta.globe.lunar;

import com.terramenta.time.DateConverter;
import com.terramenta.time.DateProvider;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import java.time.Instant;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
@ServiceProvider(service = Moon.class)
public class Moon extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(Moon.class);
    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private final Observer dateProviderObserver = (Observable o, Object arg) -> {
        Instant date;
        if (arg instanceof Date) {
            date = ((Date) arg).toInstant();
        } else {
            date = dateProvider.getDate().toInstant();
        }

        Moon.this.update(date);
    };
    private Position position;
    private LatLon sublunarPosition;

    public Moon() {
        //set current position
        update(dateProvider.getDate().toInstant());

        //listen for date changes
        dateProvider.addObserver(dateProviderObserver);
    }

    public Position getPosition() {
        return position;
    }

    public LatLon getSublunarPosition() {
        return sublunarPosition;
    }

    private void update(Instant datetime) {
        if (datetime == null) {
            return;
        }

        position = calculateMoonPosition(datetime);
        sublunarPosition = new Position(position, 0);
        logger.info("The Moon's Position at {} is {}", datetime, position);

        this.setChanged();
        this.notifyObservers(position);
    }

    private static Position calculateMoonPosition(Instant datetime) {
        double j2000 = DateConverter.toJ2000(Date.from(datetime));
        double[] lla = MoonPosition.getMoonPositionLLA(j2000);
        return Position.fromRadians(lla[0], lla[1], lla[2]);
    }
}
