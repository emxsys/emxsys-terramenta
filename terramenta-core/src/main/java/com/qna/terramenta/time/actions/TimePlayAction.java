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

@ActionID(category = "Other", id = "com.qna.terramenta.time.actions.TimePlayAction")
@ActionRegistration(iconBase = "images/control_play_blue.png", displayName = "#CTL_TimePlayAction")
@ActionReferences({
    @ActionReference(path = "Menu/Time", position = 1),
    @ActionReference(path = "Toolbars/Time", position = 4)
})
@Messages("CTL_TimePlayAction=Play")
public final class TimePlayAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TimeActionController.play(1);
    }
}
