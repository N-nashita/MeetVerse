package com.example.meetverse.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
    private ComboBox<String> meetingTypeComboBox;

    @FXML
    private void handleChooseParticipants(ActionEvent event) {

    }

    @FXML
    private void handleCreateMeeting(ActionEvent event) {

    }
}
