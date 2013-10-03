/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.fx.utilities;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author Chris.Heidt
 */
public class FXUtilities {

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
