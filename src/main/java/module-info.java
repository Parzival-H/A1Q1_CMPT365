module com.parzival.a1q1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.parzival.a1q1 to javafx.fxml;
    exports com.parzival.a1q1;
}