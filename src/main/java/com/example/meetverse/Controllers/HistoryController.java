package com.example.meetverse.Controllers;

import com.example.meetverse.util.DatabaseManager;
import com.example.meetverse.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HistoryController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label profileInitialLabel;
    
    @FXML
    private ScrollPane historyScrollPane;
    
    @FXML
    private VBox historyContainer;
    
    private String userRole = "User";
    
    @FXML
    private void initialize() {
        loadHistoricalMeetings();
    }
    
    private void loadHistoricalMeetings() {
        historyContainer.getChildren().clear();
        List<DatabaseManager.Meeting> meetings = DatabaseManager.getHistoricalMeetings();
        
        if (meetings.isEmpty()) {
            Label noMeetings = new Label("No meeting history yet");
            noMeetings.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
            VBox emptyBox = new VBox(noMeetings);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            historyContainer.getChildren().add(emptyBox);
        } else {
            for (DatabaseManager.Meeting meeting : meetings) {
                VBox card = createMeetingCard(meeting);
                historyContainer.getChildren().add(card);
            }
        }
    }
    
    private VBox createMeetingCard(DatabaseManager.Meeting meeting) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setCursor(javafx.scene.Cursor.HAND);
        
        // Title and Status
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(meeting.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1d3f;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        Label statusLabel = new Label("Completed");
        statusLabel.setPadding(new Insets(4, 12, 4, 12));
        statusLabel.setStyle("-fx-background-color: #D3D3D3; -fx-text-fill: #555; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        
        titleRow.getChildren().addAll(titleLabel, statusLabel);
        
        // Description
        Label descLabel = new Label(meeting.getDescription());
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(40);
        
        // Date, Time, Type
        HBox detailsRow = new HBox(15);
        detailsRow.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label("📅 " + meeting.getMeetingDate());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        
        Label timeLabel = new Label("🕐 " + meeting.getMeetingTime());
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        
        Label typeLabel = new Label("📍 " + meeting.getMeetingType());
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        
        detailsRow.getChildren().addAll(dateLabel, timeLabel, typeLabel);
        
        card.getChildren().addAll(titleRow, descLabel, detailsRow);
        
        // Click handler
        card.setOnMouseClicked(e -> showMeetingDetails(meeting));
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #1b3d64; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"));
        
        return card;
    }
    
    private void showMeetingDetails(DatabaseManager.Meeting meeting) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Meeting Details");
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Meeting Details (Historical)");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label title = new Label("Title: " + meeting.getTitle());
        title.setStyle("-fx-font-size: 14px;");
        title.setWrapText(true);
        
        Label description = new Label("Description: " + meeting.getDescription());
        description.setStyle("-fx-font-size: 14px;");
        description.setWrapText(true);
        
        Label date = new Label("Date: " + meeting.getMeetingDate());
        date.setStyle("-fx-font-size: 14px;");
        
        Label time = new Label("Time: " + meeting.getMeetingTime());
        time.setStyle("-fx-font-size: 14px;");
        
        Label type = new Label("Type: " + meeting.getMeetingType());
        type.setStyle("-fx-font-size: 14px;");
        
        Label status = new Label("Status: " + meeting.getStatus());
        status.setStyle("-fx-font-size: 14px;");
        
        // Get and display participants
        List<DatabaseManager.User> participants = DatabaseManager.getMeetingParticipants(meeting.getId());
        Label participantsLabel = new Label("Participants:");
        participantsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        
        VBox participantsList = new VBox(5);
        for (DatabaseManager.User participant : participants) {
            Label participantLabel = new Label("• " + participant.getName() + " (" + participant.getEmail() + ")");
            participantLabel.setStyle("-fx-font-size: 13px;");
            participantsList.getChildren().add(participantLabel);
        }
        
        ScrollPane participantsScroll = new ScrollPane(participantsList);
        participantsScroll.setFitToWidth(true);
        participantsScroll.setPrefViewportHeight(100);
        participantsScroll.setStyle("-fx-background-color: transparent;");
        
        vbox.getChildren().addAll(titleLabel, title, description, date, time, type, status, participantsLabel, participantsScroll);
        
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #1b3d64; -fx-text-fill: white; -fx-padding: 8 20;");
        closeButton.setOnAction(e -> dialog.close());
        
        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        vbox.getChildren().add(buttonBox);
        
        Scene scene = new Scene(vbox, 450, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    @FXML
    private void handleHome(ActionEvent event) {
        try {
            String fxmlPath = userRole.equals("Admin") ? "/com/example/meetverse/AdminDashboard.fxml" : "/com/example/meetverse/UserDashboard.fxml";
            FXMLLoader loader = Navigation.load(fxmlPath);
            Parent root = loader.getRoot();
            
            if (userRole.equals("Admin")) {
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
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/Users.fxml");
            Parent root = loader.getRoot();
            
            UsersController controller = loader.getController();
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText(), userRole);
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            String fxmlPath = userRole.equals("Admin") ? "/com/example/meetverse/AdminSettings.fxml" : "/com/example/meetverse/UserSettings.fxml";
            FXMLLoader loader = Navigation.load(fxmlPath);
            Parent root = loader.getRoot();
            
            if (userRole.equals("Admin")) {
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
        userRole = role;
        
        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
    }
}
