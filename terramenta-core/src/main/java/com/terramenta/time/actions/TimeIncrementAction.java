/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "Other", id = "com.terramenta.time.actions.TimeIncrementAction")
@ActionRegistration(displayName = "#CTL_TimeIncrementAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/Time", position = 6)
})
@Messages({
    "CTL_TimeIncrementAction=Adjust Increment",
    "HINT_TimeIncrementAction=Time Per Frame"
})
public final class TimeIncrementAction extends AbstractAction implements Presenter.Toolbar {

    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private JComboBox comp = null;

    private enum TimeIncrement {

        SECOND1("1 second", 1000),
        SECOND10("10 seconds", 10000),
        MINUTE1("1 minute", 60000),
        MINUTE10("10 minutes", 600000),
        MINUTE30("30 minutes", 1800000),
        HOUR1("1 hour", 3600000);
        private String label;
        private int increment;

        TimeIncrement(String label, int increment) {
            this.label = label;
            this.increment = increment;
        }

        public int getIncrement() {
            return increment;
        }

        @Override
        public String toString() {
            return label;
        }
    };

    @Override
    public Component getToolbarPresenter() {
        if (comp == null) {
            comp = new JComboBox(TimeIncrement.values());
            comp.setToolTipText(Bundle.HINT_TimeIncrementAction());
            comp.addActionListener(this);
        }
        return this.comp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TimeIncrement selected = (TimeIncrement) comp.getSelectedItem();
        tac.setStepIncrement(selected.getIncrement());
    }
}
