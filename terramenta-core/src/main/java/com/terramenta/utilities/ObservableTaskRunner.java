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
package com.terramenta.utilities;

import java.util.Observable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;

/**
 *
 * @author chris.heidt
 */
public class ObservableTaskRunner extends Observable implements Cancellable {

    private static final Logger LOGGER = Logger.getLogger(ObservableTaskRunner.class.getName());
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Runnable runnable;
    private ProgressHandle handle;
    private Future future;

    public ObservableTaskRunner(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     *
     * @return
     */
    public ProgressHandle getProgressHandle() {
        return this.handle;
    }

    /**
     * @see org.netbeans.api.progress.ProgressHandle
     * @param handle
     */
    public void setProgressHandle(ProgressHandle handle) {
        this.handle = handle;
    }

    /**
     *
     */
    public void start() {
        LOGGER.log(Level.FINE, "Starting Task {0}", runnable);

        if (handle != null) {
            handle.start();
        }

        //nested runnable to finish the handle
        future = executor.submit(() -> {
            runnable.run();
            
            if (future.isCancelled()) {
                return;
            }
            
            if (handle != null) {
                handle.finish();
            }
            
            LOGGER.log(Level.FINE, "Completed Task {0}", runnable);
            setChanged();
            notifyObservers(runnable);
        });
    }

    /**
     *
     * @return
     */
    @Override
    public boolean cancel() {
        LOGGER.log(Level.INFO, "Canceling Task {0}", runnable);
        if (future != null) {
            future.cancel(true);
        }

        if (handle != null) {
            handle.finish();
        }

        setChanged();
        notifyObservers(runnable);
        return true;
    }

    /**
     *
     */
    public void join() {
        if (future != null) {
            try {
                future.get();// blocks, which is the point.
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
