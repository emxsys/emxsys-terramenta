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
package com.terramenta.fx.utilities;

import java.io.IOException;
import java.net.URL;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author Chris.Heidt
 */
public class FXUtilities {

    public static void run(Runnable run) {
        if (Platform.isFxApplicationThread()) {
            run.run();
        } else {
            Platform.runLater(run);
        }
    }

    public static Scene createScene(URL fxml, Object controller, String stylesheet) throws IOException {
        if (fxml == null) {
            return null;
        }

        FXMLLoader loader = new FXMLLoader(fxml);
        if (controller != null) {
            loader.setController(controller);
        }

        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet);
        }

        return scene;
    }
}
