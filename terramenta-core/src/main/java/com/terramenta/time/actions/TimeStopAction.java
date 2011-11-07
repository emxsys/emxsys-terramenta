/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package com.terramenta.time.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;

@ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStopAction")
@ActionRegistration(iconBase = "images/controlStop.png", displayName = "#CTL_TimeStopAction")
@ActionReferences({
    @ActionReference(path = "Menu/Time", position = 5),
    @ActionReference(path = "Toolbars/Time", position = 3)
})
@Messages("CTL_TimeStopAction=Stop")
public final class TimeStopAction extends AbstractAction {

    private final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private final Listener listener = new Listener();

    public TimeStopAction() {
        setEnabled(tac.isPlaying());
        tac.addPropertyChangeListener(WeakListeners.propertyChange(listener, tac));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tac.stop();
    }

    private class Listener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(TimeActionController.PLAY)) {
                TimeStopAction.this.setEnabled(true);
            } else {
                TimeStopAction.this.setEnabled(false);
            }
        }
    }
}
