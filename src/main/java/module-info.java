module com.example.meetverse {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.meetverse to javafx.fxml;
    opens com.example.meetverse.Controllers to javafx.fxml;
    exports com.example.meetverse;
    exports com.example.meetverse.Controllers;
}