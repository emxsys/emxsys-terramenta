package com.terramenta.annotations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author R. Wathelet, April 2012
 */
@ActionID(category = "Tools",
id = "com.terramenta.annotations.AddTextAction")
@ActionRegistration(iconBase = "images/textbox.png",
displayName = "#CTL_AddTextAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools/Annotations", position = 7),
    @ActionReference(path = "Toolbars/Annotations", position = 7)
})
@Messages("CTL_AddTextAction=Text")
public class AddTextAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        new TextAnnotationEditor().setArmed(true);
    }
}
