module com.example.orderly_inventory_management {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.orderly_inventory_management to javafx.fxml;
    exports com.example.orderly_inventory_management;
}