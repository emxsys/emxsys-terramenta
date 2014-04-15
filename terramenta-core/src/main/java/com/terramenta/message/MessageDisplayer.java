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

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * For displaying message boxes
 *
 * @author qbeukes.blogspot.com
 * @license Apache License 2.0
 */
public class MessageDisplayer {

    private MessageDisplayer() {
    }

    /**
     * @return The dialog displayer used to show message boxes
     */
    public static DialogDisplayer getDialogDisplayer() {
        return DialogDisplayer.getDefault();
    }

    /**
     * Show a message of the specified type
     *
     * @param message
     * @param messageType As in {@link NotifyDescription} message type constants.
     */
    public static void show(String message, MessageType messageType) {
        getDialogDisplayer().notify(new NotifyDescriptor.Message(message,
                messageType.getNotifyDescriptorType()));
    }

    /**
     * Show an exception message dialog
     *
     * @param message
     * @param exception
     */
    public static void showException(String message, Throwable exception) {
        getDialogDisplayer().notify(new NotifyDescriptor.Exception(exception, message));
    }

    /**
     * Show an information dialog
     *
     * @param message
     */
    public static void info(String message) {
        show(message, MessageType.INFO);
    }

    /**
     * Show an error dialog
     *
     * @param message
     */
    public static void error(String message) {
        show(message, MessageType.ERROR);
    }

    /**
     * Show an error dialog for an exception
     *
     * @param message
     * @param exception
     */
    public static void error(String message, Throwable exception) {
        showException(message, exception);
    }

    /**
     * Show an question dialog
     *
     * @param message
     */
    public static void question(String message) {
        show(message, MessageType.QUESTION);
    }

    /**
     * Show an warning dialog
     *
     * @param message
     */
    public static void warning(String message) {
        show(message, MessageType.WARNING);
    }

    /**
     * Show an plain dialog
     *
     * @param message
     */
    public static void plain(String message) {
        show(message, MessageType.PLAIN);
    }
}