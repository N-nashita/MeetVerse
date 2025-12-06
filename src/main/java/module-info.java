module com.example.meetverse {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.meetverse to javafx.fxml;
    exports com.example.meetverse;
}