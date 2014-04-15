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
