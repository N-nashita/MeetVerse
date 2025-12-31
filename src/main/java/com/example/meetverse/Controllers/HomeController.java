package com.example.meetverse.Controllers;

import java.io.IOException;

import com.example.meetverse.util.Navigation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private void handleSignUpButton(ActionEvent event) throws IOException {
        switchScene(event, "Signup.fxml");
    }

    @FXML
    private void handleLoginLink(ActionEvent event) throws IOException {
        switchScene(event, "Login.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/meetverse/" + fxmlFile));
        Parent root = loader.load();
        Navigation.setRoot(event, root);
    }
}
