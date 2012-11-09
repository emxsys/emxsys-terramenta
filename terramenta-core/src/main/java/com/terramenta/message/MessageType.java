/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.message;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;

public enum MessageType {

    PLAIN(NotifyDescriptor.PLAIN_MESSAGE, null),
    INFO(NotifyDescriptor.INFORMATION_MESSAGE, "information.png"),
    QUESTION(NotifyDescriptor.QUESTION_MESSAGE, "question.png"),
    ERROR(NotifyDescriptor.ERROR_MESSAGE, "exclamation.png"),
    WARNING(NotifyDescriptor.WARNING_MESSAGE, "warning.png");
    private int notifyDescriptorType;
    private Icon icon;

    private MessageType(int notifyDescriptorType, String resourceName) {
        this.notifyDescriptorType = notifyDescriptorType;
        if (resourceName == null) {
            icon = new ImageIcon();
        } else {
            icon = ImageUtilities.loadImageIcon("images/" + resourceName, false);
            if (icon == null) {
                icon = new ImageIcon();
            }
        }
    }

    public int getNotifyDescriptorType() {
        return notifyDescriptorType;
    }

    public Icon getIcon() {
        return icon;
    }
}