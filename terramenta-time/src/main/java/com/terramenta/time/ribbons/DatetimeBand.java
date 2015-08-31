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
package com.terramenta.time.ribbons;

import com.terramenta.time.DateProvider;
import com.terramenta.time.picker.LocalizedDateTimePicker;
import java.awt.Dimension;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.openide.util.Lookup;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;

/**
 *
 * @author Chris.Heidt
 */
public class DatetimeBand extends JFlowRibbonBand implements Observer {

    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private LocalizedDateTimePicker dateTimePicker;
    private boolean isUpdating = false;// Set a semiphore to prevent a recursive call into setDate by the timestamp listener

    /**
     *
     */
    public DatetimeBand() {
        super("Datetime", null);

        final JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(230, 24));
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            dateTimePicker = new LocalizedDateTimePicker();
            dateTimePicker.dateTimeProperty().addListener((obs, ov, nv) -> {
                if (!isUpdating) {
                    dateProvider.setDate(Date.from(nv.toInstant()));
                }
            });
            Scene scene = new Scene(dateTimePicker);
            jfxPanel.setScene(scene);
        });

        setPreferredSize(new Dimension(240, 60));
        addFlowComponent(jfxPanel);

        //update on date change
        dateProvider.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (dateTimePicker == null) {
            return;
        }

        Date date;
        if (arg instanceof Date) {
            date = (Date) arg;
        } else {
            date = dateProvider.getDate();
        }

        Platform.runLater(() -> {
            isUpdating = true;
            dateTimePicker.setLocalizedDateTime(date);
            isUpdating = false;
        });
    }
}
