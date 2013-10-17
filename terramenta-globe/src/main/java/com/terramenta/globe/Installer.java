/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe;

import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

public class Installer extends ModuleInstall {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void restored() {
        wwm.restoreSessionState();
    }

    @Override
    public boolean closing() {
        wwm.saveSessionState();
        return true;
    }
}
