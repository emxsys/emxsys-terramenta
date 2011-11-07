/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package com.terramenta.time.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Other", id = "com.terramenta.time.actions.TimeStepBackwardAction")
@ActionRegistration(iconBase = "images/controlStepBackward.png", displayName = "#CTL_TimeStepBackwardAction")
@ActionReferences({
    @ActionReference(path = "Menu/Time", position = 4),
    @ActionReference(path = "Toolbars/Time", position = 1)
})
@Messages("CTL_TimeStepBackwardAction=Step Backward")
public final class TimeStepBackwardAction implements ActionListener {

    private final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        tac.step(-1);
    }
}
