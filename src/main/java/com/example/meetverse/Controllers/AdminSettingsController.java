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
import java.util.List;
import java.util.Optional;

public class AdminSettingsController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label profileInitialLabel;
    
    private int adminId;

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            FXMLLoader loader = Navigation.load("/com/example/meetverse/AdminDashboard.fxml");
            Parent root = loader.getRoot();
            
            AdminDashboardController controller = loader.getController();
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText());
            
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
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText(), "Admin");
            
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
            controller.setUserInfo(nameLabel.getText(), emailLabel.getText(), "Admin");
            
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword(ActionEvent event) {
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
    private void handleEditParticipants(ActionEvent event) {
        showManageUsersDialog();
    }
    
    private void showManageUsersDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Manage Users");
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("User Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Add User Button
        Button addUserBtn = new Button("+ Add New User");
        addUserBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20; -fx-font-weight: bold;");
        addUserBtn.setOnAction(e -> showAddUserDialog(dialog));
        
        Label usersLabel = new Label("All Users:");
        usersLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Users list
        ScrollPane scrollPane = new ScrollPane();
        VBox usersList = new VBox(8);
        usersList.setPadding(new Insets(10));
        
        loadUsersIntoList(usersList, dialog);
        
        scrollPane.setContent(usersList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);
        
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #666; -fx-text-fill: white; -fx-padding: 8 20;");
        closeButton.setOnAction(e -> dialog.close());
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);
        
        vbox.getChildren().addAll(titleLabel, addUserBtn, usersLabel, scrollPane, buttonBox);
        
        Scene scene = new Scene(vbox, 550, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void loadUsersIntoList(VBox usersList, Stage parentDialog) {
        usersList.getChildren().clear();
        List<DatabaseManager.User> allUsers = DatabaseManager.getAllUsers();
        
        for (DatabaseManager.User user : allUsers) {
            HBox userRow = new HBox(15);
            userRow.setPadding(new Insets(10));
            userRow.setAlignment(Pos.CENTER_LEFT);
            userRow.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
            
            VBox userInfo = new VBox(3);
            Label nameLabel = new Label(user.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            Label emailLabel = new Label(user.getEmail());
            emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            
            Label roleLabel = new Label("Role: " + user.getRole());
            roleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            
            userInfo.getChildren().addAll(nameLabel, emailLabel, roleLabel);
            HBox.setHgrow(userInfo, javafx.scene.layout.Priority.ALWAYS);
            
            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5 15;");
            
            // Prevent deleting admin
            if ("Admin".equals(user.getRole())) {
                deleteBtn.setDisable(true);
                deleteBtn.setText("Admin");
                deleteBtn.setStyle("-fx-background-color: #999; -fx-text-fill: white; -fx-padding: 5 15;");
            } else {
                deleteBtn.setOnAction(e -> {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirm Delete");
                    confirmAlert.setHeaderText("Delete User");
                    confirmAlert.setContentText("Are you sure you want to delete user: " + user.getName() + "?\n\nThe user will be removed but their meetings will be kept.");
                    
                    Optional<ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        boolean success = DatabaseManager.deleteUser(user.getId());
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                            loadUsersIntoList(usersList, parentDialog);
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
                        }
                    }
                });
            }
            
            userRow.getChildren().addAll(userInfo, deleteBtn);
            usersList.getChildren().add(userRow);
        }
    }
    
    private void showAddUserDialog(Stage parentDialog) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New User");
        
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Add New User");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter full name");
        
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        
        Label roleLabel = new Label("Role:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("User", "Admin");
        roleComboBox.setValue("User");
        
        Button addButton = new Button("Add User");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 20; -fx-font-weight: bold;");
        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String role = roleComboBox.getValue();
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all fields.");
                return;
            }
            
            if (DatabaseManager.emailExists(email)) {
                showAlert(Alert.AlertType.WARNING, "Email Exists", "This email is already registered.");
                return;
            }
            
            boolean success = DatabaseManager.addUser(name, email, password, role);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
                dialog.close();
                // Reload users list in parent dialog
                parentDialog.close();
                showManageUsersDialog();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user. Please try again.");
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8 20;");
        cancelButton.setOnAction(e -> dialog.close());
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addButton, cancelButton);
        
        vbox.getChildren().addAll(titleLabel, nameLabel, nameField, emailLabel, emailField, 
                                   passwordLabel, passwordField, roleLabel, roleComboBox, buttonBox);
        
        Scene scene = new Scene(vbox, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    private void handleEditAdmin(ActionEvent event) {
        if (adminId <= 0) {
            showAlert(Alert.AlertType.WARNING, "Error", "Admin information not available.");
            return;
        }
        
        // Get all non-admin users
        List<DatabaseManager.User> allUsers = DatabaseManager.getAllUsers();
        List<DatabaseManager.User> nonAdminUsers = allUsers.stream()
            .filter(user -> !"Admin".equals(user.getRole()))
            .toList();
        
        if (nonAdminUsers.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Users", "There are no users available to promote to admin.");
            return;
        }
        
        showChangeAdminDialog(nonAdminUsers);
    }
    
    private void showChangeAdminDialog(List<DatabaseManager.User> users) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Change Admin");
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Transfer Admin Role");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label warningLabel = new Label("⚠️ Warning: You will be demoted to User role and the selected user will become the new Admin.");
        warningLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #d32f2f; -fx-wrap-text: true;");
        warningLabel.setWrapText(true);
        
        Label instructionLabel = new Label("Select a user to promote to Admin:");
        instructionLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        ScrollPane scrollPane = new ScrollPane();
        VBox userListBox = new VBox(8);
        userListBox.setPadding(new Insets(10));
        
        final DatabaseManager.User[] selectedUser = {null};
        
        for (DatabaseManager.User user : users) {
            Button userBtn = new Button(user.getName() + " (" + user.getEmail() + ")");
            userBtn.setMaxWidth(Double.MAX_VALUE);
            userBtn.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-alignment: center-left;");
            userBtn.setOnAction(e -> {
                selectedUser[0] = user;
                // Highlight selected
                userListBox.getChildren().forEach(node -> {
                    if (node instanceof Button) {
                        ((Button) node).setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-alignment: center-left;");
                    }
                });
                userBtn.setStyle("-fx-background-color: #c8e6c9; -fx-padding: 10; -fx-alignment: center-left;");
            });
            userListBox.getChildren().add(userBtn);
        }
        
        scrollPane.setContent(userListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(250);
        
        Button confirmButton = new Button("Confirm Transfer");
        confirmButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        confirmButton.setOnAction(e -> {
            if (selectedUser[0] == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to promote to Admin.");
                return;
            }
            
            // Confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Admin Transfer");
            confirmAlert.setHeaderText("Are you sure?");
            confirmAlert.setContentText("You are about to transfer admin role to " + selectedUser[0].getName() + 
                                       ".\n\nYou will become a regular User and will need to log in again.\n\nThis action cannot be undone. Continue?");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = DatabaseManager.transferAdminRole(adminId, selectedUser[0].getId());
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                            "Admin role has been transferred to " + selectedUser[0].getName() + 
                            ".\n\nYou will now be redirected to the login page.");
                    dialog.close();
                    
                    // Redirect to login
                    redirectToLogin(e);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to transfer admin role. Please try again.");
                }
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10 20;");
        cancelButton.setOnAction(e -> dialog.close());
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        
        vbox.getChildren().addAll(titleLabel, warningLabel, instructionLabel, scrollPane, buttonBox);
        
        Scene scene = new Scene(vbox, 500, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void redirectToLogin(ActionEvent event) {
        try {
            Parent root = Navigation.load("/com/example/meetverse/Login.fxml").getRoot();
            Navigation.setRoot(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserInfo(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        
        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
        
        // Get adminId from email
        DatabaseManager.User user = DatabaseManager.getUserByEmail(email);
        if (user != null) {
            this.adminId = user.getId();
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
