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
package com.terramenta.globe;

import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    private static WorldWindManager wwm;

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                wwm = Lookup.getDefault().lookup(WorldWindManager.class);
                wwm.restoreSessionState();
            }
        });
    }

    @Override
    public boolean closing() {
        if (wwm != null) {
            wwm.saveSessionState();
        }
        return true;
    }
}
