/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.message;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.NotificationDisplayer;

public class Notifier {

    // XML 1.1
    // [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    private static final String xml11pattern = "[^"
            + "\u0001-\uD7FF"
            + "\uE000-\uFFFD"
            + "\ud800\udc00-\udbff\udfff"
            + "]+";

    private Notifier() {
    }

    /**
     * Show message with the specified type and action listener
     */
    public static void show(String title, String message, MessageType type, ActionListener actionListener) {
        String cleanTitle = title.replaceAll(xml11pattern, "");
        String cleanMessage = message.replaceAll(xml11pattern, "");

        NotificationDisplayer.getDefault().notify(cleanTitle, type.getIcon(), cleanMessage, actionListener);
    }

    /**
     * Show message with the specified type and a default action which displays the message using {@link MessageUtil}
     * with the same message type
     */
    public static void show(String title, final String message, final MessageType type) {
        String cleanTitle = title.replaceAll(xml11pattern, "");
        String cleanMessage = message.replaceAll(xml11pattern, "");

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageDisplayer.show(message, type);
            }
        };

        NotificationDisplayer.getDefault().notify(cleanTitle, type.getIcon(), cleanMessage, actionListener);
    }

    /**
     * Show an information notification
     *
     * @param message
     */
    public static void info(String title, String message) {
        show(title, message, MessageType.INFO);
    }

    /**
     * Show an error notification
     *
     * @param message
     */
    public static void error(String title, String message) {
        show(title, message, MessageType.ERROR);
    }

    /**
     * Show an error notification for an exception
     *
     * @param message
     * @param exception
     */
    public static void error(String title, final String message, final Throwable exception) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageDisplayer.showException(message, exception);
            }
        };

        show(title, message, MessageType.ERROR, actionListener);
    }

    /**
     * Show an warning notification
     *
     * @param message
     */
    public static void warning(String title, String message) {
        show(title, message, MessageType.WARNING);
    }

    /**
     * Show an plain notification
     *
     * @param message
     */
    public static void plain(String title, String message) {
        show(title, message, MessageType.PLAIN);
    }
}
