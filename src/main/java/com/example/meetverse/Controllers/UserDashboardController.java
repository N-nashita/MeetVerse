package com.example.meetverse.Controllers;

import com.example.meetverse.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

public class UserDashboardController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private void handleHome(ActionEvent event) {
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/UserSettings.fxml");
            Parent root = loader.getRoot();
            
            UserSettingsController controller = loader.getController();
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText());
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLaunchMeeting(ActionEvent event) {
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/CreateMeeting.fxml");
            Parent root = loader.getRoot();
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            System.out.println("Error loading Create Meeting form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUserInfo(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
    }
}
