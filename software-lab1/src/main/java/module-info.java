module lab1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    opens org.example.lab1 to javafx.fxml;
    exports org.example.lab1;
}