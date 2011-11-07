/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.interfaces;

import org.openide.util.lookup.AbstractLookup;

/**
 *
 * @author heidtmare
 */
public class Content extends AbstractLookup.Content {

    public interface Provider {

        public AbstractLookup.Content getContent();
    }
}
