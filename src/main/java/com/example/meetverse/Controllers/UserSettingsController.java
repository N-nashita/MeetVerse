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
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText());
            
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
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = Navigation.load("/com/example/meetverse/Login.fxml").getRoot();
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditMeetingDetails(ActionEvent event) {
        if (userId <= 0) {
            showAlert(Alert.AlertType.WARNING, "Error", "User information not available.");
            return;
        }
        
        // Get meetings created by this user
        List<DatabaseManager.Meeting> userMeetings = DatabaseManager.getMeetingsByCreator(userId);
        
        if (userMeetings.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Meetings", "You haven't created any meetings yet.");
            return;
        }
        
        // Show meeting selection dialog
        showMeetingSelectionDialog(userMeetings);
    }
    
    private void showMeetingSelectionDialog(List<DatabaseManager.Meeting> meetings) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Select Meeting to Edit");
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        
        Label label = new Label("Select a meeting to edit:");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        vbox.getChildren().add(label);
        
        ScrollPane scrollPane = new ScrollPane();
        VBox meetingsBox = new VBox(8);
        meetingsBox.setPadding(new Insets(10));
        
        for (DatabaseManager.Meeting meeting : meetings) {
            Button meetingBtn = new Button(meeting.getTitle() + " - " + meeting.getMeetingDate() + " (" + meeting.getStatus() + ")");
            meetingBtn.setMaxWidth(Double.MAX_VALUE);
            meetingBtn.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-alignment: center-left;");
            meetingBtn.setOnAction(e -> {
                dialog.close();
                showEditMeetingDialog(meeting);
            });
            meetingsBox.getChildren().add(meetingBtn);
        }
        
        scrollPane.setContent(meetingsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);
        vbox.getChildren().add(scrollPane);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 20;");
        cancelButton.setOnAction(e -> dialog.close());
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(cancelButton);
        vbox.getChildren().add(buttonBox);
        
        Scene scene = new Scene(vbox, 450, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void showEditMeetingDialog(DatabaseManager.Meeting meeting) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Meeting Details");
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        
        Label titleLbl = new Label("Edit Meeting: " + meeting.getTitle());
        titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Title field
        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField(meeting.getTitle());
        
        // Description field
        Label descLabel = new Label("Description:");
        TextArea descField = new TextArea(meeting.getDescription());
        descField.setPrefRowCount(3);
        
        // Date picker
        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();
        try {
            datePicker.setValue(LocalDate.parse(meeting.getMeetingDate()));
        } catch (Exception e) {
            datePicker.setValue(LocalDate.now());
        }
        
        // Time field
        Label timeLabel = new Label("Time:");
        HBox timeBox = new HBox(10);
        String[] timeParts = meeting.getMeetingTime().split(" ");
        TextField timeField = new TextField(timeParts.length > 0 ? timeParts[0] : "");
        timeField.setPromptText("HH:MM");
        ComboBox<String> amPmComboBox = new ComboBox<>();
        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.setValue(timeParts.length > 1 ? timeParts[1] : "AM");
        timeBox.getChildren().addAll(timeField, amPmComboBox);
        
        // Meeting type
        Label typeLabel = new Label("Meeting Type:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Online", "Offline");
        typeComboBox.setValue(meeting.getMeetingType());
        
        // Participants selection
        Label participantsLabel = new Label("Participants:");
        List<DatabaseManager.User> selectedParticipants = new ArrayList<>(DatabaseManager.getMeetingParticipants(meeting.getId()));
        Button chooseParticipantsBtn = new Button("Choose Participants (" + selectedParticipants.size() + " selected)");
        chooseParticipantsBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8 15; -fx-cursor: hand;");
        chooseParticipantsBtn.setOnAction(e -> {
            showParticipantsDialog(selectedParticipants, chooseParticipantsBtn);
        });
        
        // Buttons
        Button doneButton = new Button("Done");
        doneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20;");
        doneButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String description = descField.getText().trim();
            String date = datePicker.getValue().toString();
            String time = timeField.getText().trim() + " " + amPmComboBox.getValue();
            String type = typeComboBox.getValue();
            
            if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty() || type == null || selectedParticipants.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all fields and select at least one participant.");
                return;
            }
            
            List<Integer> participantIds = new ArrayList<>();
            for (DatabaseManager.User participant : selectedParticipants) {
                participantIds.add(participant.getId());
            }
            
            boolean success = DatabaseManager.updateMeetingDetails(meeting.getId(), title, description, date, time, type, participantIds);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Meeting details updated successfully!");
                dialog.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update meeting details. Please try again.");
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 20;");
        cancelButton.setOnAction(e -> dialog.close());
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(doneButton, cancelButton);
        
        vbox.getChildren().addAll(titleLbl, titleLabel, titleField, descLabel, descField, 
                                   dateLabel, datePicker, timeLabel, timeBox, 
                                   typeLabel, typeComboBox, participantsLabel, chooseParticipantsBtn, buttonBox);
        
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
        
        Scene scene = new Scene(scrollPane, 500, 600);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void showParticipantsDialog(List<DatabaseManager.User> selectedParticipants, Button updateButton) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Select Participants");
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");
        
        Label label = new Label("Select meeting participants:");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        vbox.getChildren().add(label);
        
        List<DatabaseManager.User> allUsers = DatabaseManager.getAllUsers();
        List<CheckBox> checkBoxes = new ArrayList<>();
        
        ScrollPane scrollPane = new ScrollPane();
        VBox userListBox = new VBox(8);
        userListBox.setPadding(new Insets(10));
        userListBox.setStyle("-fx-background-color: white;");
        
        if (allUsers.isEmpty()) {
            Label noUsersLabel = new Label("No users available in the system.");
            noUsersLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #999;");
            userListBox.getChildren().add(noUsersLabel);
        } else {
            for (DatabaseManager.User user : allUsers) {
                CheckBox checkBox = new CheckBox(user.getName() + " (" + user.getEmail() + ")");
                checkBox.setStyle("-fx-font-size: 13px;");
                checkBox.setUserData(user);
                if (selectedParticipants.stream().anyMatch(u -> u.getId() == user.getId())) {
                    checkBox.setSelected(true);
                }
                checkBoxes.add(checkBox);
                userListBox.getChildren().add(checkBox);
            }
        }
        
        scrollPane.setContent(userListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
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
            updateButton.setText("Choose Participants (" + selectedParticipants.size() + " selected)");
            dialog.close();
        });
        
        cancelButton.setOnAction(e -> dialog.close());
        
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20;");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 20;");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        vbox.getChildren().add(buttonBox);
        
        Scene scene = new Scene(vbox, 450, 450);
        dialog.setScene(scene);
        dialog.show();
    }

    public void setUserInfo(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        
        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
        
        // Get userId from email
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
