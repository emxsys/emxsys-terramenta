/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.interfaces;

/**
 *
 * @author heidtmare
 */
public interface BooleanState {

    public interface Provider {

        public boolean getBooleanState();

        public void setBooleanState(boolean state);
    }
}
