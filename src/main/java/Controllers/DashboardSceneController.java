package Controllers;

import com.example.orderly_inventory_management.DatabaseConnection;
import com.example.orderly_inventory_management.Items;
import com.example.orderly_inventory_management.Main;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardSceneController implements Initializable {

    /*
    *
    *   Switching Scenes
    *
    * */
    Main m = new Main();
    public void switchToDashboard() throws IOException {
        m.changeScene("DashboardScene.fxml");
    }
    public void switchToSetting() throws IOException {
        m.changeScene("SettingScene.fxml");
    }
    public void switchToSignIn() throws IOException {
        m.changeScene("SignOutScene.fxml");
    }
    public void switchToTransaction() throws IOException {
        m.changeScene("TransactionScene.fxml");
    }

    /*
    *
    *   Draggable topBar + topBar actions
    *
    * */
    @FXML
    private AnchorPane topBar;
    double x = 0;
    double y = 0;
    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button fullScreenButton;
    public void fullScreenAction() {
        Stage stage = (Stage)fullScreenButton.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }
    public void minimizeAction() {
        Stage stage = (Stage)minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }
    public void quitOnAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /*
    *
    *   Adding, deleting and modifying entries
    *
    * */
    @FXML
    private TextField tf_component;
    @FXML
    private ComboBox<String> cb_value;
    @FXML
    private TextField tf_amount;
    @FXML
    private TextField tf_dlb;
    @FXML
    private TextField tf_link;

    /*  Add entry on entry clicked/entered */
    @FXML
    public void addEntry() {
        String nComponent = tf_component.getText();
        String nValue = cb_value.getValue();
        String nAmount = tf_amount.getText();
        String nDlb = tf_dlb.getText();
        String nLink = tf_link.getText();

        // Check if any of the fields are empty
        if (nComponent.isBlank() || nValue == null || nAmount.isBlank() || nDlb.isBlank() || nLink.isBlank()) {
            // Show a warning or error message to the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Add Component Entry");
            alert.setHeaderText("Insufficient Information.");
            alert.setContentText("Please add the sufficient information.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                alert.close();
            }
            return;
        }

        // Create a new document with the input data
        Document document = new Document("Component", nComponent)
                .append("Value", nValue)
                .append("Amount", nAmount)
                .append("DateLastBought", nDlb)
                .append("Link", nLink);

        // Insert the document into the MongoDB collection
        getCollection().insertOne(document);

        // Refresh the table view
        refreshTableView();
    }

    /*  Delete entry on entry clicked/entered */
    @FXML
    public void deleteEntry() {
        String componentToDelete = tf_component.getText();

        // Check if the component name is provided
        if (componentToDelete.isBlank()) {
            // Show a warning or error message to the user
            return;
        }

        // Define the filter to find the document to delete
        Bson filter = Filters.eq("Component", componentToDelete);

        // Delete the document from the MongoDB collection
        getCollection().deleteOne(filter);

        // Refresh the table view
        refreshTableView();
    }

    /*  Update on confirm button    */
    @FXML
    private void updateEntry() {
        // Get the values from the text fields
        String nComponent = tf_component.getText();
        String nValue = cb_value.getValue();
        String nAmount = tf_amount.getText();
        String nDlb = tf_dlb.getText();
        String nLink = tf_link.getText();

        // Check if any of the fields are empty
        if (nComponent.isBlank() || nValue == null || nAmount.isBlank() || nDlb.isBlank() || nLink.isBlank()) {
            // Show a warning or error message to the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Add Component Entry");
            alert.setHeaderText("Insufficient Information.");
            alert.setContentText("Please add the sufficient information.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                alert.close();
            }
            return;
        }

        // Define the filter to find the document to update
        Bson filter = Filters.eq("DateLastBought", nDlb);

        // Create a new document with the updated values
        Document updatedDocument = new Document("Component", nComponent).append("Value", nValue).append("Amount", nAmount).append("DateLastBought", nDlb).append("Link", nLink);

        // Define the update operation
        Bson update = new Document("$set", updatedDocument);

        // Update the document in the MongoDB collection
        getCollection().updateOne(filter, update);

        // Refresh the table view
        refreshTableView();
    }

    // Helper method to refresh the table view
    private void refreshTableView() {
        ObservableList<Items> updatedList = retrieveDataFromMongoDB();
        table_items.setItems(updatedList);
    }

    // Search method
    @FXML
    public void searchTable(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();

        ObservableList<Items> filteredList = retrieveDataFromMongoDB().filtered(item ->
                item.getComponent().toLowerCase().contains(lowerCaseKeyword) ||
                        item.getValue().toLowerCase().contains(lowerCaseKeyword) ||
                        item.getAmount().toLowerCase().contains(lowerCaseKeyword) ||
                        item.getDateLastBought().toLowerCase().contains(lowerCaseKeyword) ||
                        item.getLink().toLowerCase().contains(lowerCaseKeyword)
        );

        table_items.setItems(filteredList);
    }

    /*
    *
    *   Tableview + keyword search
    *
    * */
    @FXML
    private TextField keywordTextField;
    @FXML
    private TableView<Items> table_items;
    @FXML
    private TableColumn<Items, String> col_component;
    @FXML
    private TableColumn<Items, String> col_value;
    @FXML
    private TableColumn<Items, String> col_amount;
    @FXML
    private TableColumn<Items, String> col_dlb;
    @FXML
    private TableColumn<Items, String> col_link;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Draggable topBar
        topBar.setOnMousePressed(mouseEvent-> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        topBar.setOnMouseDragged(mouseEvent-> {
            Main.stg.setX(mouseEvent.getScreenX()-x);
            Main.stg.setY(mouseEvent.getScreenY()-y);
        });

        // Add a listener to the selection property of the table view
        table_items.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Update the text fields with the properties of the selected item
                tf_component.setText(newSelection.getComponent());
                cb_value.setValue(newSelection.getValue());
                tf_amount.setText(newSelection.getAmount());
                tf_dlb.setText(newSelection.getDateLastBought());
                tf_link.setText(newSelection.getLink());
            } else {
                // Clear the text fields if no item is selected
                tf_component.clear();
                cb_value.setValue(null);
                tf_amount.clear();
                tf_dlb.clear();
                tf_link.clear();
            }
        });

        cb_value.setItems(FXCollections.observableArrayList("1Ω", "10Ω", "100Ω", "1KΩ", "10KΩ", "100KΩ", "1MΩ", "RED", "GREEN", "BLUE", "WHITE","N/A"));

        ObservableList<Items> componentList = retrieveDataFromMongoDB();

        col_component.setCellValueFactory(new PropertyValueFactory<>("Component"));
        col_value.setCellValueFactory(new PropertyValueFactory<>("Value"));
        col_amount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        col_dlb.setCellValueFactory(new PropertyValueFactory<>("DateLastBought"));
        col_link.setCellValueFactory(new PropertyValueFactory<>("Link"));

        table_items.setItems(componentList);

        // Search
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchTable(newValue);
        });
    }

    // Retrieve data from 'componentList' collection in ORDERLY database
    private ObservableList<Items> retrieveDataFromMongoDB() {
        String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
        String databaseName = "ORDERLY";
        String collectionName = "componentList";

        try (var mongoClient = MongoClients.create(connectionString)) {
            var database = mongoClient.getDatabase(databaseName);
            var collection = database.getCollection(collectionName);

            var cursor = collection.find().iterator();

            ObservableList<Items> componentList = FXCollections.observableArrayList();

            while (cursor.hasNext()) {
                var document = cursor.next();

                var component = new Items(
                        document.getString("Component"),
                        document.getString("Value"),
                        document.getString("Amount"),
                        document.getString("DateLastBought"),
                        document.getString("Link")
                        // ... add more fields based on your document structure
                );
                componentList.add(component);
            }

            return componentList;
        }
    }

    // Helper method to get the MongoDB collection
    private MongoCollection<Document> getCollection() {
        String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
        String databaseName = "ORDERLY";
        String collectionName = "componentList";

        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(collectionName);
    }
}
