/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
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