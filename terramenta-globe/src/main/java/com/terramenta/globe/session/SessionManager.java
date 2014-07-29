// Copyright © 2014, Terramenta. All rights reserved.
//
//This work is subject to the terms of either
//the GNU General Public License Version 3 ("GPL") or 
//the Common Development and Distribution License("CDDL") (collectively, the "License").
//You may not use this work except in compliance with the License.
//
//You can obtain a copy of the License at
//http://opensource.org/licenses/CDDL-1.0
//http://opensource.org/licenses/GPL-3.0
package com.terramenta.globe.session;

import com.terramenta.globe.WorldWindManager;
import org.openide.modules.OnStart;
import org.openide.modules.OnStop;
import org.openide.util.Lookup;
import org.openide.windows.OnShowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris.Heidt
 */
public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static WorldWindManager wwm;

    @OnStart
    public static class OnStartRunnable implements Runnable {

        @Override
        public void run() {
            logger.info("OnStart");
        }

    }

    @OnShowing
    public static class OnShowingRunnable implements Runnable {

        @Override
        public void run() {
            logger.info("OnShowing");

            wwm = Lookup.getDefault().lookup(WorldWindManager.class);
            if (wwm != null) {
                wwm.restoreSessionState();
            }
        }

    }

    @OnStop
    public static class OnStopRunnable implements Runnable {

        @Override
        public void run() {
            logger.info("OnStop");

            if (wwm != null) {
                wwm.saveSessionState();
            }
        }
    }
}
