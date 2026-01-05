package com.example.meetverse.Controllers;

import com.example.meetverse.util.DatabaseManager;
import com.example.meetverse.util.Navigation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class UserDashboardController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label profileInitialLabel;
    
    @FXML
    private ScrollPane meetingsScrollPane;
    
    @FXML
    private VBox meetingsContainer;
    
    private int userId;
    
    @FXML
    private void initialize() {
        loadAllMeetings();
    }
    
    private void loadAllMeetings() {
        meetingsContainer.getChildren().clear();
        List<DatabaseManager.Meeting> meetings = DatabaseManager.getAllMeetings();
        
        if (meetings.isEmpty()) {
            Label noMeetings = new Label("No meetings yet");
            noMeetings.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
            VBox emptyBox = new VBox(noMeetings);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            meetingsContainer.getChildren().add(emptyBox);
        } else {
            for (DatabaseManager.Meeting meeting : meetings) {
                VBox card = createMeetingCard(meeting);
                meetingsContainer.getChildren().add(card);
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
        
        Label statusLabel = new Label(meeting.getStatus());
        statusLabel.setPadding(new Insets(4, 12, 4, 12));
        
        String statusStyle = switch (meeting.getStatus()) {
            case "Pending" -> "-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-background-radius: 12;";
            case "Approved" -> "-fx-background-color: #D4EDDA; -fx-text-fill: #155724; -fx-background-radius: 12;";
            case "Rejected" -> "-fx-background-color: #F8D7DA; -fx-text-fill: #721C24; -fx-background-radius: 12;";
            default -> "-fx-background-color: #E0E0E0; -fx-text-fill: #333; -fx-background-radius: 12;";
        };
        statusLabel.setStyle(statusStyle + " -fx-font-size: 11px; -fx-font-weight: bold;");
        
        titleRow.getChildren().addAll(titleLabel, statusLabel);
        
        // Description
        Label descLabel = new Label(meeting.getDescription());
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(40);
        
        // Launcher name
        Label launcherLabel = new Label("👤 Launcher: " + meeting.getCreatorName());
        launcherLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1b3d64; -fx-font-weight: bold;");
        
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
        
        // Countdown for approved meetings
        Label countdownLabel = new Label();
        countdownLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1b3d64;");
        
        if ("Approved".equals(meeting.getStatus())) {
            updateCountdown(meeting, countdownLabel);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateCountdown(meeting, countdownLabel)));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
        
        card.getChildren().addAll(titleRow, descLabel, launcherLabel, detailsRow);
        
        if ("Approved".equals(meeting.getStatus())) {
            card.getChildren().add(countdownLabel);
            
            // Show meeting code only for Online meetings and if user is a participant
            if ("Online".equals(meeting.getMeetingType()) && userId > 0 && DatabaseManager.isUserParticipant(userId, meeting.getId())) {
                String meetingCode = meeting.getMeetingLink();
                if (meetingCode == null || meetingCode.trim().isEmpty()) {
                    meetingCode = generateMeetingCode(meeting.getId());
                    DatabaseManager.updateMeetingLink(meeting.getId(), meetingCode);
                }
                
                // Display meeting code
                Label codeLabel = new Label("📋 Meeting Code: " + meetingCode);
                codeLabel.setStyle("-fx-text-fill: #1b3d64; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #E8F4F8; -fx-padding: 8 12; -fx-background-radius: 5;");
                card.getChildren().add(codeLabel);
            }
        }
        
        // Click handler
        card.setOnMouseClicked(e -> showMeetingDetails(meeting));
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #1b3d64; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"));
        
        return card;
    }
    
    private void updateCountdown(DatabaseManager.Meeting meeting, Label countdownLabel) {
        try {
            LocalDate meetingDate = LocalDate.parse(meeting.getMeetingDate());
            LocalTime meetingTime = parseTime12Hour(meeting.getMeetingTime());
            LocalDateTime meetingDateTime = LocalDateTime.of(meetingDate, meetingTime);
            LocalDateTime now = LocalDateTime.now();
            
            long totalSeconds = ChronoUnit.SECONDS.between(now, meetingDateTime);
            
            if (totalSeconds <= 0) {
                countdownLabel.setText("⏰ Meeting time has passed");
                countdownLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #999;");
                
                // Move to history
                DatabaseManager.moveToHistory(meeting.getId());
                // Reload meetings to reflect changes
                javafx.application.Platform.runLater(() -> loadAllMeetings());
            } else {
                long days = totalSeconds / 86400;
                long hours = (totalSeconds % 86400) / 3600;
                long minutes = (totalSeconds % 3600) / 60;
                long seconds = totalSeconds % 60;
                
                String countdown = String.format("⏰ Starts in: %dd %dh %dm %ds", days, hours, minutes, seconds);
                countdownLabel.setText(countdown);
            }
        } catch (Exception e) {
            countdownLabel.setText("");
        }
    }
    
    private LocalTime parseTime12Hour(String timeStr) {
        try {
            // Check if time already contains AM/PM
            if (timeStr.contains("AM") || timeStr.contains("PM")) {
                String[] parts = timeStr.trim().split(" ");
                String time = parts[0];
                String ampm = parts[1];
                
                String[] timeParts = time.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                
                // Convert to 24-hour format
                if (ampm.equals("PM") && hour != 12) {
                    hour += 12;
                } else if (ampm.equals("AM") && hour == 12) {
                    hour = 0;
                }
                
                return LocalTime.of(hour, minute);
            } else {
                // Fallback to 24-hour format
                return LocalTime.parse(timeStr);
            }
        } catch (Exception e) {
            // Default to current time if parsing fails
            return LocalTime.now();
        }
    }
    
    private String generateMeetingCode(int meetingId) {
        // Generate a simple meeting code based on meeting ID
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder code = new StringBuilder();
        int id = meetingId;
        
        for (int i = 0; i < 3; i++) {
            code.append(chars.charAt(id % 26));
            id = id / 26 + 1;
        }
        code.append("-");
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt((meetingId * (i + 1)) % 26));
        }
        code.append("-");
        for (int i = 0; i < 3; i++) {
            code.append(chars.charAt((meetingId * (i + 3)) % 26));
        }
        
        return code.toString();
    }
    
    private void showMeetingDetails(DatabaseManager.Meeting meeting) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Meeting Details");
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Meeting Details");
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
        
        vbox.getChildren().addAll(titleLabel, title, description, date, time, type, participantsLabel, participantsScroll);
        
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #1b3d64; -fx-text-fill: white; -fx-padding: 8 20;");
        closeButton.setOnAction(e -> dialog.close());
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().add(closeButton);
        vbox.getChildren().add(buttonBox);
        
        Scene scene = new Scene(vbox, 500, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    private void handleHome(ActionEvent event) {
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
            
            CreateMeetingController controller = loader.getController();
            if (controller != null) {
                controller.setUserInfo(nameLabel.getText(), emailLabel.getText(), userId);
            }
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            System.out.println("Error loading Create Meeting form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUserInfo(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        
        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
    }
    
    public void setUserInfo(String name, String email, int userId) {
        this.userId = userId;
        setUserInfo(name, email);
        // Reload meetings with userId set
        loadAllMeetings();
    }
}
