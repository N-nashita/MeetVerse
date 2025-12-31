package com.example.meetverse.Controllers;

import com.example.meetverse.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

public class UserSettingsController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label profileInitialLabel;

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/UserDashboard.fxml");
            Parent root = loader.getRoot();
            
            UserDashboardController controller = loader.getController();
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText());
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            System.out.println("Error loading User Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
    }

    @FXML
    private void handleSeeParticipants(ActionEvent event) {
    }

    @FXML
    private void handleSeeAdmin(ActionEvent event) {
        
    }

    public void setUserInfo(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        
        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
    }
}
