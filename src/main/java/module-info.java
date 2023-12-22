module com.example.login {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires mysql.connector.j;

    opens com.example.orderly_inventory_management to javafx.fxml;
    exports com.example.orderly_inventory_management;
    exports Controllers;
    opens Controllers to javafx.fxml;
}
