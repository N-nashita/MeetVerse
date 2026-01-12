package com.example.meetverse.Controllers;

import com.example.meetverse.util.DatabaseManager;
import com.example.meetverse.util.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserSettingsController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label profileInitialLabel;
    
    private int userId;

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/UserDashboard.fxml");
            Parent root = loader.getRoot();
            
            UserDashboardController controller = loader.getController();
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText(), userId);
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            System.out.println("Error loading User Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUsers(ActionEvent event) {
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/Users.fxml");
            Parent root = loader.getRoot();
            
            UsersController controller = loader.getController();
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText(), "User");
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleHistory(ActionEvent event) {
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/History.fxml");
            Parent root = loader.getRoot();
            
            HistoryController controller = loader.getController();
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText(), "User");
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelMeeting(ActionEvent event) {
        if (userId <= 0) {
            showAlert(Alert.AlertType.WARNING, "Error", "User ID not set. Please log in again.");
            return;
        }
        
        List<DatabaseManager.Meeting> myMeetings = DatabaseManager.getMeetingsByCreator(userId);
        
        if (myMeetings.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Meetings", "You haven't created any meetings yet.");
            return;
        }
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Cancel Meeting");
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Select a meeting to cancel");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox meetingsBox = new VBox(10);
        meetingsBox.setPadding(new Insets(10));
        
        for (DatabaseManager.Meeting meeting : myMeetings) {
            VBox card = new VBox(8);
            card.setPadding(new Insets(12));
            card.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
            
            Label meetingTitle = new Label(meeting.getTitle());
            meetingTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            Label meetingDetails = new Label("📅 " + meeting.getMeetingDate() + "  🕐 " + meeting.getMeetingTime() + "  📍 " + meeting.getMeetingType());
            meetingDetails.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
            
            Label statusLabel = new Label("Status: " + meeting.getStatus());
            statusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            
            Button deleteBtn = new Button("Delete Meeting");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 6 12; -fx-cursor: hand;");
            deleteBtn.setOnAction(e -> {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Deletion");
                confirmation.setHeaderText("Delete Meeting: " + meeting.getTitle());
                confirmation.setContentText("Are you sure you want to delete this meeting? This action cannot be undone.");
                
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean success = DatabaseManager.deleteMeeting(meeting.getId());
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Meeting deleted successfully!");
                            dialog.close();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete meeting.");
                        }
                    }
                });
            });
            
            card.getChildren().addAll(meetingTitle, meetingDetails, statusLabel, deleteBtn);
            meetingsBox.getChildren().add(card);
        }
        
        scrollPane.setContent(meetingsBox);
        
        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #1b3d64; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> dialog.close());
        
        HBox buttonBox = new HBox(closeBtn);
        buttonBox.setAlignment(Pos.CENTER);
        
        vbox.getChildren().addAll(titleLabel, scrollPane, buttonBox);
        
        Scene scene = new Scene(vbox, 500, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = Navigation.load("/com/example/meetverse/Login.fxml").getRoot();
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setUserInfo(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        
        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
        
        DatabaseManager.User user = DatabaseManager.getUserByEmail(email);
        if (user != null) {
            this.userId = user.getId();
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAllUsers() {
        Stage stage = new Stage();
        stage.setTitle("All Users");

        TableView<UserData> tableView = new TableView<>();

        TableColumn<UserData, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<UserData, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<UserData, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<UserData, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);

        tableView.getColumns().addAll(idCol, nameCol, emailCol, roleCol);

        ObservableList<UserData> data = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/com/example/meetverse/Databases/meetverse.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, email, role FROM users ORDER BY id")) {

            while (rs.next()) {
                data.add(new UserData(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setItems(data);

        Scene scene = new Scene(tableView, 500, 400);
        stage.setScene(scene);
        stage.show();
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
