/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.ribbons;

import com.terramenta.time.DateProvider;
import com.terramenta.time.datepicker.DatePicker;
import java.awt.Dimension;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private DatePicker picker;

    /**
     *
     */
    public DatetimeBand() {
        super("Datetime", null);

        final JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(150, 48));
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                jfxPanel.setScene(createScene());
            }
        });

        setPreferredSize(new Dimension(180, 60));
        addFlowComponent(jfxPanel);

        dateProvider.addObserver(this);
    }

    private Scene createScene() {
        picker = new DatePicker("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"));
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
