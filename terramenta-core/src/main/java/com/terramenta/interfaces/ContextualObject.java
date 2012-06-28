/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.interfaces;

/**
 *
 * @author chris.heidt
 */
public interface ContextualObject {

    public void setDisplayName(String name);

    public String getDisplayName();

    public void setDisplayIcon(String icon);

    public String getDisplayIcon();

    public void setDescription(String desc);

    public String getDescription();
}
