/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.time;

import java.util.EventListener;

/**
 *
 * @author heidtmare
 */
public interface DateTimeEventListener extends EventListener {

    /**
     * 
     * @param evt
     */
    public void changeEventOccurred(DateTimeChangeEvent evt);
}
