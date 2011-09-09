/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.utilities;

import java.awt.Color;

/**
 *
 * @author heidtmare
 */
public class ClassificationUtilities {

    public static final int NONE = -1;
    public static final int UNCLASSIFIED = 0;
    public static final int RESTRICTED = 1;
    public static final int CONFIDENTIAL = 2;
    public static final int SECRET = 3;
    public static final int TOPSECRET = 4;
    public static final String[] labels = new String[]{
        "UNCLASSIFIED",
        "RESTRICTED",
        "CONFIDENTIAL",
        "SECRET",
        "TOP SECRET"
    };
    public static final Color[] textcolors = new Color[]{
        Color.white,
        Color.black,
        Color.black,
        Color.white,
        Color.black
    };
    public static final Color[] backcolors = new Color[]{
        new Color(0f, 1f, 0f, 0.6f),
        new Color(0f, 0f, 0f, 0.6f),
        new Color(0f, 0f, 1f, 0.6f),
        new Color(1f, 0f, 0f, 0.6f),
        new Color(1f, 1f, 0f, 0.6f)
    };
}
