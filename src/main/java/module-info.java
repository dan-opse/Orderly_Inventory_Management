module com.example.login {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;

    opens com.example.orderly_inventory_management to javafx.fxml;
    exports com.example.orderly_inventory_management;
    exports Controllers;
    opens Controllers to javafx.fxml;
}
