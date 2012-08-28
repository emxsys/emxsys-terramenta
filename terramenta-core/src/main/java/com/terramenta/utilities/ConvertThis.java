/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.utilities;

import java.util.*;

/**
 *
 * @author chris.heidt
 */
public class ConvertThis {

    /**
     * Convert anything that implements java.lang.Readable(e.g.: InputStream, File, Channel) to a String
     *
     * @param r
     * @return
     */
    public static String toString(Readable r) {
        try {
            return new Scanner(r).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    /**
     *
     * @param d
     * @return
     */
    public static Map toMap(Dictionary d) {
        Map m = new HashMap();
        if (d != null) {
            Enumeration keys = d.keys();
            while (keys.hasMoreElements()) {
                String k = (String) keys.nextElement();
                Object v = d.get(k);
                m.put(k, v);
            }
        }
        return m;
    }

    /**
     *
     * @param s
     * @return
     */
    public static Integer toInteger(String s) {
        return Integer.parseInt(s);
    }

    /**
     *
     * @param s
     * @return
     */
    public static Double toDouble(String s) {
        return Double.parseDouble(s);
    }

    /**
     *
     * @param s
     * @return
     */
    public static Boolean toBoolean(String s) {
        return Boolean.parseBoolean(s);
    }

    public static double[] toDoubleArray(int[] ints) {
        double[] doubles = new double[ints.length];
        for (int i = 0; i < ints.length; ++i) {
            doubles[i] = ints[i];
        }
        return doubles;
    }
}
