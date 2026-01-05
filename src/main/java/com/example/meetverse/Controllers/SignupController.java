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

public class SignupController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignup(ActionEvent event) {
        System.out.println("Signup button clicked!");
        try {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            System.out.println("Name: " + name + ", Email: " + email);

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                System.out.println("Validation failed: empty fields");
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
                return;
            }

            if (password.length() < 6) {
                System.out.println("Validation failed: password too short");
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters long.");
                return;
            }

            if (DatabaseManager.emailExists(email)) {
                System.out.println("Email already exists");
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "This email is already registered.");
                return;
            }

            // First user becomes admin, others become regular users
            boolean isFirstUser = DatabaseManager.getUserCount() == 0;
            String assignedRole = isFirstUser ? "Admin" : "User";

            System.out.println("Registering user with role: " + assignedRole);

            if (DatabaseManager.registerUser(name, email, password)) {
                System.out.println("User registered successfully!");
                String message = isFirstUser ? 
                    "Account created successfully! You are registered as an Admin." : 
                    "Account created successfully!";
                showAlert(Alert.AlertType.INFORMATION, "Success", message);
                
                // Get the newly created user to get their ID
                DatabaseManager.User user = DatabaseManager.getUserByEmail(email);
                
                System.out.println("Navigating to dashboard...");
                if (assignedRole.equals("Admin")) {
                    navigateToDashboard(event, "/com/example/meetverse/AdminDashboard.fxml", user);
                } else {
                    navigateToDashboard(event, "/com/example/meetverse/UserDashboard.fxml", user);
                }
            } else {
                System.out.println("Registration failed in database");
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Could not create account. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void navigateToDashboard(ActionEvent event, String fxmlPath, DatabaseManager.User user) throws IOException {
        FXMLLoader loader = Navigation.load(fxmlPath);
        
        if (fxmlPath.contains("Admin")) {
            AdminDashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserInfo(user.getName(), user.getEmail());
            }
        } else {
            UserDashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserInfo(user.getName(), user.getEmail(), user.getId());
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
