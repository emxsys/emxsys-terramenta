/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

import com.terramenta.time.DateProvider;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.openide.util.Lookup;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;

/**
 *
 * @author Chris.Heidt
 */
public class DatetimeBand extends JFlowRibbonBand implements Observer, PropertyChangeListener {

    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private final DateChooser chooser;

    public DatetimeBand() {
        super("Datetime", null);

        setPreferredSize(new Dimension(180, 60));

        chooser = new DateChooser();
        chooser.setDate(dateProvider.getDate());
        chooser.getDateEditor().addPropertyChangeListener("date", this);

        dateProvider.addObserver(this);

        addFlowComponent(chooser);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Date newDate = (Date) evt.getNewValue();
        if (newDate == null) {
            //revert to old value, no blank date allowed
            chooser.setDate((Date) evt.getOldValue());
        } else {
            dateProvider.setDate(newDate);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        chooser.setDate(dateProvider.getDate());
    }
}
