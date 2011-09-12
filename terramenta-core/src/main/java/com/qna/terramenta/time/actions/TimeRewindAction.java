/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.time.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;

@ActionID(category = "Other", id = "com.qna.terramenta.time.actions.TimeRewindAction")
@ActionRegistration(iconBase = "images/controlRewind.png", displayName = "#CTL_TimeRewindAction")
@ActionReferences({
    @ActionReference(path = "Menu/Time", position = 2),
    @ActionReference(path = "Toolbars/Time", position = 2)
})
@Messages("CTL_TimeRewindAction=Rewind")
public final class TimeRewindAction extends AbstractAction {

    private final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private final Listener listener = new Listener();

    public TimeRewindAction() {
        setEnabled(!tac.isPlaying());
        tac.addPropertyChangeListener(WeakListeners.propertyChange(listener, tac));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tac.play(-1);
    }

    private class Listener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(TimeActionController.PLAY) && (evt.getNewValue().equals(-1))) {
                TimeRewindAction.this.setEnabled(false);
            } else {
                TimeRewindAction.this.setEnabled(true);
            }
        }
    }
}
