/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.time.actions;

import com.qna.terramenta.time.DateTimeChangeEvent;
import com.qna.terramenta.time.DateTimeController;
import com.qna.terramenta.time.DateTimeEventListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.border.Border;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(category = "Other", id = "com.qna.terramenta.time.actions.TimeDisplayAction")
@ActionRegistration(displayName = "#CTL_TimeDisplayAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/Time", position = 6)
})
@Messages("CTL_TimeDisplayAction=Current Time")
public final class TimeDisplayAction extends AbstractAction implements Presenter.Toolbar {

    private DateTimeController dateTimeController = DateTimeController.getInstance();
    private JTextField field = new JTextField() {

        @Override
        public void setBorder(Border border) {
            // GO AWAY!
        }
    };

    public TimeDisplayAction() {
        DateTime datetime = dateTimeController.getDateTime();
        String dateText = datetime.plusMillis(-datetime.getMillisOfSecond()).toString();//Remove millis for display
        field.setText(dateText);
        field.setEnabled(false);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.addActionListener(this);
        field.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!field.isEnabled()) {
                    field.setEnabled(true);
                }
            }
        });

        dateTimeController.addDateTimeEventListener(new DateTimeEventListener() {

            @Override
            public void changeEventOccurred(DateTimeChangeEvent evt) {
                DateTime datetime = evt.getDateTime();
                String dateText = datetime.plusMillis(-datetime.getMillisOfSecond()).withZone(DateTimeZone.UTC).toString();//Remove millis for display
                field.setText(" " + dateText + " ");
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(field)) {
            TimeActionController.stop();
            parseDateTextField(field.getText().trim());
            field.setEnabled(false);
        }
    }

    @Override
    public Component getToolbarPresenter() {
        return this.field;
    }

    /**
     *
     */
    private void parseDateTextField(String text) {
        DateTime dt = DateTimeController.parseIsoString(text);
        if (dt == null) {
            dateTimeController.setDateTime(dateTimeController.getDateTime());
        } else {
            dateTimeController.setDateTime(dt);
        }
    }
}
