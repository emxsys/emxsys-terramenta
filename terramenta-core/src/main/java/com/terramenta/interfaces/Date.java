/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.interfaces;

/**
 *
 * @author heidtmare
 */
public interface Date {

    public interface Provider {

        public java.util.Date getDate();

        public void setDate(java.util.Date date);
    }
}
