/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import org.joda.time.Duration;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "Other", id = "com.terramenta.time.actions.TimeLingerAction")
@ActionRegistration(displayName = "#CTL_TimeLingerAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/Time", position = 8)
})
@Messages({
    "CTL_TimeLingerAction=Adjust Linger Time",
    "HINT_TimeLingerAction=Linger Time"
})
public final class TimeLingerAction extends AbstractAction implements Presenter.Toolbar {

    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private JComboBox comp = null;

    private enum LingerDuration {

        NONE("None", new Duration(1)),//dont linger
        SECOND10("10 seconds", new Duration(10000)),
        MINUTE1("1 minute", new Duration(60000)),
        MINUTE10("10 minutes", new Duration(600000)),
        MINUTE30("30 minutes", new Duration(1800000)),
        HOUR1("1 hour", new Duration(3600000)),
        ALWAYS("Always", null);//always visible
        private String label;
        private Duration duration = null;

        LingerDuration(String label, Duration duration) {
            this.label = label;
            this.duration = duration;
        }

        public Duration getDuration() {
            return duration;
        }

        @Override
        public String toString() {
            return label;
        }
    };

    @Override
    public Component getToolbarPresenter() {
        if (comp == null) {
            comp = new JComboBox(LingerDuration.values());
            comp.setToolTipText(Bundle.HINT_TimeLingerAction());
            comp.addActionListener(this);
        }
        return this.comp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LingerDuration selected = (LingerDuration) comp.getSelectedItem();
        tac.setLingerDuration(selected.getDuration());
    }
}
