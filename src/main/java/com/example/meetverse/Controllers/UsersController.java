package com.example.meetverse.Controllers;

import com.example.meetverse.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.io.IOException;
import java.sql.*;

public class UsersController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label profileInitialLabel;

    @FXML
    private VBox usersContainer;

    private String userRole;

    @FXML
    public void initialize() {
        loadUsers();
    }

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            String fxmlPath = userRole != null && userRole.equals("Admin") ? 
                "/com/example/meetverse/AdminDashboard.fxml" : 
                "/com/example/meetverse/UserDashboard.fxml";
            
            FXMLLoader loader = Navigation.load(fxmlPath);
            Parent root = loader.getRoot();
            
            if (userRole != null && userRole.equals("Admin")) {
                AdminDashboardController controller = loader.getController();
                controller.setUserInfo(nameLabel.getText(), emailLabel.getText());
            } else {
                UserDashboardController controller = loader.getController();
                controller.setUserInfo(nameLabel.getText(), emailLabel.getText());
            }
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUsers(ActionEvent event) {
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            String fxmlPath = userRole != null && userRole.equals("Admin") ? 
                "/com/example/meetverse/AdminSettings.fxml" : 
                "/com/example/meetverse/UserSettings.fxml";
            
            FXMLLoader loader = Navigation.load(fxmlPath);
            Parent root = loader.getRoot();
            
            if (userRole != null && userRole.equals("Admin")) {
                AdminSettingsController controller = loader.getController();
                controller.setUserInfo(nameLabel.getText(), emailLabel.getText());
            } else {
                UserSettingsController controller = loader.getController();
                controller.setUserInfo(nameLabel.getText(), emailLabel.getText());
            }
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserInfo(String name, String email, String role) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        this.userRole = role;
        
        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
    }

    private void loadUsers() {
        usersContainer.getChildren().clear();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/example/meetverse/Databases/meetverse.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, email, role FROM users ORDER BY id")) {

            while (rs.next()) {
                HBox userCard = createUserCard(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
                usersContainer.getChildren().add(userCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createUserCard(int id, String name, String email, String role) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("user-card");
        card.setPrefHeight(70);
        card.setMaxWidth(Double.MAX_VALUE);

        // Profile circle with initial
        StackPane profileCircle = new StackPane();
        Circle circle = new Circle(25);
        circle.getStyleClass().add("user-profile-circle");
        
        Label initial = new Label(name.substring(0, 1).toUpperCase());
        initial.getStyleClass().add("user-profile-initial");
        
        profileCircle.getChildren().addAll(circle, initial);

        VBox userInfo = new VBox(5);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("user-name-label");

        Label emailLabel = new Label(email);
        emailLabel.getStyleClass().add("user-email-label");

        userInfo.getChildren().addAll(nameLabel, emailLabel);

        Label roleBadge = new Label(role);
        if (role.equals("Admin")) {
            roleBadge.getStyleClass().add("user-role-badge-admin");
        } else {
            roleBadge.getStyleClass().add("user-role-badge-user");
        }

        Label idLabel = new Label("#" + id);
        idLabel.getStyleClass().add("user-id-label");
        idLabel.setMinWidth(40);

        card.getChildren().addAll(profileCircle, userInfo, roleBadge, idLabel);

        return card;
    }

    public static class UserData {
        private final Integer id;
        private final String name;
        private final String email;
        private final String role;

        public UserData(Integer id, String name, String email, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }
}
