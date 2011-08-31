/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.time.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Other", id = "com.qna.terramenta.time.actions.TimeStopAction")
@ActionRegistration(iconBase = "images/control_stop_blue.png", displayName = "#CTL_TimeStopAction")
@ActionReferences({
    @ActionReference(path = "Menu/Time", position = 5),
    @ActionReference(path = "Toolbars/Time", position = 3)
})
@Messages("CTL_TimeStopAction=Stop")
public final class TimeStopAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TimeActionController.stop();
    }
}
