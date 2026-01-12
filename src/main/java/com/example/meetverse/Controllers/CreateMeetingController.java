package com.example.meetverse.Controllers;

import com.example.meetverse.util.DatabaseManager;
import com.example.meetverse.util.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateMeetingController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField timeField;

    @FXML
    private ComboBox<String> amPmComboBox;

    @FXML
    private ComboBox<String> meetingTypeComboBox;

    @FXML
    private Button createMeetingButton;

    private List<DatabaseManager.User> selectedParticipants = new ArrayList<>();
    
    private int currentUserId;
    private String userName;
    private String userEmail;
    
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
    
    public void setUserInfo(String name, String email, int userId) {
        this.userName = name;
        this.userEmail = email;
        this.currentUserId = userId;
    }

    @FXML
    private void initialize() {
        createMeetingButton.setDisable(true);
        
        titleField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        descriptionField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        timeField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        amPmComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        meetingTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }
    
    private void validateForm() {
        boolean isValid = titleField.getText() != null && !titleField.getText().trim().isEmpty() &&
                         descriptionField.getText() != null && !descriptionField.getText().trim().isEmpty() &&
                         datePicker.getValue() != null &&
                         timeField.getText() != null && !timeField.getText().trim().isEmpty() &&
                         amPmComboBox.getValue() != null &&
                         meetingTypeComboBox.getValue() != null &&
                         !selectedParticipants.isEmpty();
        
        createMeetingButton.setDisable(!isValid);
    }

    @FXML
    private void handleChooseParticipants(ActionEvent event) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Select Participants");
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        
        Label label = new Label("Select meeting participants:");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        vbox.getChildren().add(label);
        
        List<DatabaseManager.User> allUsers = DatabaseManager.getAllUsers();
        List<CheckBox> checkBoxes = new ArrayList<>();
        
        ScrollPane scrollPane = new ScrollPane();
        VBox userListBox = new VBox(8);
        userListBox.setPadding(new Insets(10));
        
        for (DatabaseManager.User user : allUsers) {
            CheckBox checkBox = new CheckBox(user.getName() + " (" + user.getEmail() + ")");
            checkBox.setUserData(user);

            if (selectedParticipants.stream().anyMatch(u -> u.getId() == user.getId())) {
                checkBox.setSelected(true);
            }
            checkBoxes.add(checkBox);
            userListBox.getChildren().add(checkBox);
        }
        
        scrollPane.setContent(userListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);
        vbox.getChildren().add(scrollPane);
        
        Button confirmButton = new Button("Confirm Selection");
        Button cancelButton = new Button("Cancel");
        
        confirmButton.setOnAction(e -> {
            selectedParticipants.clear();
            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedParticipants.add((DatabaseManager.User) checkBox.getUserData());
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Participants Selected", 
                     selectedParticipants.size() + " participant(s) selected.");
            validateForm(); 
            dialog.close();
        });
        
        cancelButton.setOnAction(e -> dialog.close());
        
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20;");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 20;");
        
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        vbox.getChildren().add(buttonBox);
        
        Scene scene = new Scene(vbox, 450, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    private void handleCreateMeeting(ActionEvent event) {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String date = datePicker.getValue().toString();
        String time = timeField.getText().trim() + " " + amPmComboBox.getValue();
        String type = meetingTypeComboBox.getValue();
        
        List<Integer> participantIds = new ArrayList<>();
        for (DatabaseManager.User participant : selectedParticipants) {
            participantIds.add(participant.getId());
        }
        
        int meetingId = DatabaseManager.createMeetingRequest(title, description, date, time, type, currentUserId, participantIds);
        
        if (meetingId > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                     "Meeting request has been sent to admin for approval!");
            
            handleBack(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Failed to create meeting request. Please try again.");
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/UserDashboard.fxml");
            Parent root = loader.getRoot();
            
            UserDashboardController controller = loader.getController();
            if (controller != null && userName != null && userEmail != null) {
                controller.setUserInfo(userName, userEmail, currentUserId);
            }
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public List<DatabaseManager.User> getSelectedParticipants() {
        return selectedParticipants;
    }
}
