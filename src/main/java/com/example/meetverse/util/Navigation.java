package com.example.meetverse.util;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigation {
    private Navigation() {}
    public static FXMLLoader load(String resource) throws IOException{
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(resource));
        loader.load();
        return loader;
    }

    public static void setRoot(ActionEvent e,Parent root){
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = stage.getScene();
        if (scene == null){
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }
}
