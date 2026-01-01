package com.example.meetverse.Controllers;

import com.example.meetverse.util.DatabaseManager;
import com.example.meetverse.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (email.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        DatabaseManager.User user = DatabaseManager.loginUser(email, password, role);
        
        if (user != null) {
            if (user.getRole().equals("Admin")) {
                navigateToDashboard(event, "/com/example/meetverse/AdminDashboard.fxml", user.getName(), user.getEmail());
            } else {
                navigateToDashboard(event, "/com/example/meetverse/UserDashboard.fxml", user.getName(), user.getEmail());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email, password, or role.");
        }
    }

    private void navigateToDashboard(ActionEvent event, String fxmlPath, String name, String email) throws IOException {
        FXMLLoader loader = Navigation.load(fxmlPath);
        
        if (fxmlPath.contains("Admin")) {
            AdminDashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserInfo(name, email);
            }
        } else {
            UserDashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserInfo(name, email);
            }
        }
        Parent root = loader.getRoot();
        Navigation.setRoot(event, root);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
