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

@ActionID(category = "Other", id = "com.qna.terramenta.time.actions.TimePlayAction")
@ActionRegistration(iconBase = "images/controlPlay.png", displayName = "#CTL_TimePlayAction")
@ActionReferences({
    @ActionReference(path = "Menu/Time", position = 1),
    @ActionReference(path = "Toolbars/Time", position = 4)
})
@Messages("CTL_TimePlayAction=Play")
public final class TimePlayAction extends AbstractAction {

    private final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private final Listener listener = new Listener();

    public TimePlayAction() {
        setEnabled(!tac.isPlaying());
        tac.addPropertyChangeListener(WeakListeners.propertyChange(listener, tac));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tac.play(1);
    }

    private class Listener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(TimeActionController.PLAY) && (evt.getNewValue().equals(1))) {
                TimePlayAction.this.setEnabled(false);
            } else {
                TimePlayAction.this.setEnabled(true);
            }
        }
    }
}
