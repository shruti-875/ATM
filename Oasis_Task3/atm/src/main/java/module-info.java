module org.example.atm {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.example.atm to javafx.fxml;
    exports org.example.atm;
}