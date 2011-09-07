/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.time.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "Other", id = "com.qna.terramenta.time.actions.TimeIncrementAction")
@ActionRegistration(displayName = "#CTL_TimeIncrementAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/Time", position = 8)
})
@Messages("CTL_TimeIncrementAction=Adjust Increment")
public final class TimeIncrementAction extends AbstractAction implements Presenter.Toolbar {

    private JButton comp;
    private static final int[] stepIncrements = new int[]{1000, 10000, 60000, 600000, 3600000, 86400000};
    private static int stepIncrementsIndex = 0;
    private final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);

    public TimeIncrementAction() {
        comp = new JButton();
        comp.addActionListener(this);
        int step = stepIncrements[stepIncrementsIndex];
        updateText(step);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        stepIncrementsIndex += 1;

        // bounds checking
        if (stepIncrementsIndex > (stepIncrements.length - 1)) {
            stepIncrementsIndex = 0;
        }

        int step = stepIncrements[stepIncrementsIndex];
        tac.setStepIncrement(step);
        updateText(step);
    }

    @Override
    public Component getToolbarPresenter() {
        return this.comp;
    }

    private void updateText(int step) {
        comp.setText(step / 1000 + " s");
    }
}
