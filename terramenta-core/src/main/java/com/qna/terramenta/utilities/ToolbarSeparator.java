/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.utilities;

import java.awt.Dimension;
import javax.swing.JSeparator;

/**
 *
 * @author heidtmare
 */
public class ToolbarSeparator extends JSeparator {

    public ToolbarSeparator() {
        super(JSeparator.VERTICAL);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(getPreferredSize().width, super.getSize().height);
    }
}
