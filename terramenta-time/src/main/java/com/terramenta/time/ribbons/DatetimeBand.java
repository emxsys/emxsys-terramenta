/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.ribbons;

import com.terramenta.time.DateProvider;
import com.terramenta.time.datepicker.DatePicker;
import com.terramenta.time.options.TimeOptions;
import java.awt.Dimension;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;

/**
 *
 * @author Chris.Heidt
 */
public class DatetimeBand extends JFlowRibbonBand implements Observer {

    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private static final Preferences prefs = NbPreferences.forModule(TimeOptions.class);
    private DatePicker picker;

    /**
     *
     */
    public DatetimeBand() {
        super("Datetime", null);

        final JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(220, 48));
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                jfxPanel.setScene(createScene());
            }
        });

        setPreferredSize(new Dimension(220, 60));
        addFlowComponent(jfxPanel);

        dateProvider.addObserver(this);
    }

    private Scene createScene() {
        picker = new DatePicker(
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                TimeZone.getTimeZone(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE)),
                Locale.forLanguageTag(prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE))
        );
        picker.setDate(dateProvider.getDate());

        picker.timestampProperty().addListener(new ChangeListener<Long>() {
            @Override
            public void changed(ObservableValue<? extends Long> arg0, Long oldValue, Long newValue) {
                if (newValue == null) {
                    //revert to old value, no blank date allowed
                    picker.setDate(new Date(oldValue));
                } else {
                    dateProvider.setDate(new Date(newValue));
                }
            }
        });
        return new Scene(picker);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (picker == null) {
            return;
        }

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                picker.setDate(dateProvider.getDate());
            }
        });
    }
}
