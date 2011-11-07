/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.utilities;

import java.awt.Color;

/**
 *
 * @author heidtmare
 */
public enum Classification {

    NONE("None", null, null),
    UNCLASSIFIED("UNCLASSIFIED", Color.white, new Color(0f, 1f, 0f, 0.6f)),
    RESTRICTED("RESTRICTED", Color.black, new Color(1f, 1f, 1f, 0.6f)),
    CONFIDENTIAL("CONFIDENTIAL", Color.white, new Color(0f, 0f, 1f, 0.6f)),
    SECRET("SECRET", Color.white, new Color(1f, 0f, 0f, 0.6f)),
    TOPSECRET("TOP SECRET", Color.black, new Color(1f, 1f, 0f, 0.6f));
    private final String text;
    private final Color textColor;
    private final Color backColor;

    Classification(String text, Color textColor, Color backColor) {
        this.text = text;
        this.textColor = textColor;
        this.backColor = backColor;
    }

    public String getText() {
        return text;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getBackgroundColor() {
        return backColor;
    }
}
