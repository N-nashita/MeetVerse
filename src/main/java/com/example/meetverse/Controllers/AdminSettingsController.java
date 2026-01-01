package com.example.meetverse.Controllers;

import com.example.meetverse.util.Navigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class AdminSettingsController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label profileInitialLabel;

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
    private void handleChangePassword(ActionEvent event) {
    }

    @FXML
    private void handleEditParticipants(ActionEvent event) {
    }

    @FXML
    private void handleEditAdmin(ActionEvent event) {
    }

    @FXML
    private void handleMeetingRequest(ActionEvent event) {
    }

    public void setUserInfo(String name, String email) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        
        if (name != null && !name.isEmpty()) {
            profileInitialLabel.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
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
