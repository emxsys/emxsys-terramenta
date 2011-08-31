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

@ActionID(category = "Other", id = "com.qna.terramenta.time.actions.TimeRewindAction")
@ActionRegistration(iconBase = "images/control_back_blue.png", displayName = "#CTL_TimeRewindAction")
@ActionReferences({
    @ActionReference(path = "Menu/Time", position = 2),
    @ActionReference(path = "Toolbars/Time", position = 2)
})
@Messages("CTL_TimeRewindAction=Rewind")
public final class TimeRewindAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TimeActionController.play(-1);
    }
}
