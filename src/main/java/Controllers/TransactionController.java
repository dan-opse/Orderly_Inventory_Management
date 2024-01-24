package Controllers;

import com.example.orderly_inventory_management.Items;
import com.example.orderly_inventory_management.Main;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {


    /*--------------------------------------------------------------------------------*/


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
    public void switchToSignOut() throws IOException {
        m.changeScene("SignOutScene.fxml");
    }
    public void switchToTransaction() throws IOException {
        m.changeScene("TransactionScene.fxml");
    }


    /*--------------------------------------------------------------------------------*/


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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You're about to be logged out!");
        alert.setContentText("Do you want to save before exiting?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("You successfully logged out!");
            stage.close();
        }
    }


    /*--------------------------------------------------------------------------------*/


    @FXML
    private TextArea linkTab;

    @FXML
    public void loadLinks() {

        // Clear text-area
        linkTab.clear();

        // Store all selectedItems
        ObservableList<Items> selectedItems = table_items.getItems();

        // Loop over items
        for (Items selectedItem : selectedItems) {
            boolean isSelected = col_select.getCellObservableValue(selectedItem).getValue();

            // Check if selected column is selected
            if (isSelected) {
                // Create string to store link value and append to text-area
                String link = col_link.getCellData(selectedItem);
                linkTab.appendText(link+"\n");
            }
        }

    }


    /*--------------------------------------------------------------------------------*/


    /*
    *
    *   Exporting & Importing as .CSV
    *
    * */

    public void handleCSVExport() {

        // Retrieve all items in table-view
        List<Items> items = table_items.getItems();
        exportToCSV(items, "src/main/resources/CSV-Files/transaction_data.csv");

    }

    public void exportToCSV(List<Items> items, String fp) {
        try (FileWriter fw = new FileWriter(fp)) {

            // Write header
            fw.append("Component,Value,Amount,DateLastBought,Link\n");

            // Write data
            for (Items item : items) {
                fw.append(String.format("%s,%s,%s,%s,%s\n",
                        item.getComponent(), item.getValue(), item.getAmount(), item.getDateLastBought(), item.getLink()));
            }

            System.out.println("CSV exported successfully");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /*--------------------------------------------------------------------------------*/


    // Entry modifications

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
    private ObjectId originalId;
    private Items originalEntry;

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
    @FXML
    public void deleteEntry() {

        ObservableList<Items> selectedItems = table_items.getItems();
        MongoCollection<Document> targetCollection = getCollection();

        // Loop over selected items
        for (Items selectedItem : selectedItems) {
            boolean isSelected = col_select.getCellObservableValue(selectedItem).getValue();

            // Check if selected column is selected
            if (isSelected) {
                String component = col_component.getCellData(selectedItem);
                String value = col_value.getCellData(selectedItem);
                String amount = col_amount.getCellData(selectedItem);
                String dateLastBought = col_dlb.getCellData(selectedItem);
                String link = col_link.getCellData(selectedItem);

                // Create document to delete
                Document document = new Document("Component", component)
                        .append("Value", value)
                        .append("Amount", amount)
                        .append("DateLastBought", dateLastBought)
                        .append("Link", link);

                targetCollection.deleteOne(document);
                refreshTableView();
            }
        }

        // Define the filter to find the document to delete
        Bson filter = Filters.eq("_id", originalId);

        // Delete the document from the MongoDB collection
        getCollection().deleteOne(filter);
        refreshTableView();

    }
    @FXML
    private void updateEntry() {
        if (originalEntry != null && originalEntry.getId() != null) {
            originalId = originalEntry.getId();

            // Get the values from the text fields
            String nComponent = tf_component.getText();
            String nValue = cb_value.getValue();
            String nAmount = tf_amount.getText();
            String nDlb = tf_dlb.getText();
            String nLink = tf_link.getText();

            // Check if any of the fields are empty
            if (nComponent.isBlank() || nValue == null || nAmount.isBlank() || nDlb.isBlank() || nLink.isBlank()) {
                // Show a warning or error message to the user
                showWarning("Insufficient information", "Please fill in text fields.");
                return;
            }

            // Create filter: Find the entry selected by using "ObjectId" -- Unique id generated when an entry is created by MongoDB
            Bson filter = Filters.eq("_id", originalId);

            // Create a new entry
            Document updatedDocument = new Document("Component", nComponent)
                    .append("Value", nValue)
                    .append("Amount", nAmount)
                    .append("DateLastBought", nDlb)
                    .append("Link", nLink);
            Bson update = new Document("$set", updatedDocument);

            // Update the document in the MongoDB collection
            getCollection().updateOne(filter, update);
            refreshTableView();

            // Reset entry values
            originalEntry = null;
            originalId = null;

        } else {
            showWarning("Failed to update", "Retry?");
        }
    }
    @FXML
    private void resetSelection() {

        // Deselect all entries in table-view
        table_items.getSelectionModel().clearSelection();

        // Clear individual text fields
        tf_component.clear();
        cb_value.setValue(null);
        tf_amount.clear();
        tf_dlb.clear();
        tf_link.clear();

    }
    public void refreshTableView() {

        ObservableList<Items> updatedList = retrieveDataFromMongoDB();
        table_items.setItems(updatedList);

    }
    private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    /*--------------------------------------------------------------------------------*/


    // Tableview + keyword search
    @FXML
    public void searchTable(String keyword) {
        // Convert toLower
        String lowerCaseKeyword = keyword.toLowerCase();

        // Create list
        // Predicate: Student
        // 'Predicate' creates a filter, in this case you create an item filter
        // and check each value in each column using .contains()
        // The table-view is then updated
        ObservableList<Items> filteredList = retrieveDataFromMongoDB().filtered(item ->
                item.getComponent().toLowerCase().contains(lowerCaseKeyword) ||
                        item.getValue().toLowerCase().contains(lowerCaseKeyword) ||
                        item.getAmount().toLowerCase().contains(lowerCaseKeyword) ||
                        item.getDateLastBought().toLowerCase().contains(lowerCaseKeyword) ||
                        item.getLink().toLowerCase().contains(lowerCaseKeyword)
        );

        table_items.setItems(filteredList);
    }

    // Initialize table-view variables
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
    @FXML
    private TableColumn<Items, Boolean> col_select;


    /*--------------------------------------------------------------------------------*/


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Initialize 'Select' column in table-view
        col_select.setCellValueFactory(
                param -> {
                    Items entry = param.getValue();
                    SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(entry.isSelected());
                    booleanProp.addListener((observable, oldValue, newValue) -> entry.setSelected(newValue));
                    return booleanProp;
                }
        );
        col_select.setCellFactory(CheckBoxTableCell.forTableColumn(col_select));


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

                // Stores the current entry chosen as a Student object and the _id as a ObjectId, this makes sure that when you are editing
                // the text-fields on the right the update, delete, and add methods can use the ObjectId as a Bson filter
                // instead of any other values such as "DateLastBought"
                originalEntry = newSelection;
                originalId = newSelection.getId();
                System.out.println(originalId);

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

        // Initialize choice box values
        cb_value.setItems(FXCollections.observableArrayList("N/A", "RED", "GREEN", "BLUE", "WHITE", "RGB", "NPN", "PNP", "Sensor",
                "1μF", "0.12μF", "100μF 16V", "2.2μF", "10μF",
                "330pF", "1nF", "10nF", "50nF", "100nF", "Diode",
                "10Ω", "100Ω", "1KΩ", "10KΩ", "100KΩ", "1MΩ", "10MΩ",
                "1.8Ω", "18Ω", "180Ω", "1.8KΩ", "18KΩ", "180KΩ", "1.8MΩ",
                "2.2Ω", "22Ω","220Ω","2.2KΩ", "22KΩ","220KΩ", "2.2MΩ",
                "3.3Ω", "33Ω", "3.3KΩ", "33KΩ", "330KΩ", "3.3MΩ",
                "4.7Ω", "47Ω", "470Ω", "4.7KΩ", "47KΩ", "470KΩ", "4.7MΩ",
                "5.6Ω", "56Ω", "560Ω", "5.6KΩ", "56KΩ", "560KΩ", "5.6MΩ",
                "6.8Ω", "68Ω", "680Ω", "6.8KΩ", "68KΩ", "680KΩ", "6.8MΩ",
                "8.2Ω", "82Ω", "820Ω", "8.2KΩ", "82KΩ", "820KΩ", "8.2MΩ",
                ""));

        // Grab initial list & populate table-view
        ObservableList<Items> componentList = retrieveDataFromMongoDB();

        col_component.setCellValueFactory(new PropertyValueFactory<>("Component"));
        col_value.setCellValueFactory(new PropertyValueFactory<>("Value"));
        col_amount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        col_dlb.setCellValueFactory(new PropertyValueFactory<>("DateLastBought"));
        col_link.setCellValueFactory(new PropertyValueFactory<>("Link"));

        table_items.refresh();
        table_items.setItems(componentList);

        // Search
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchTable(newValue);
        });
    }


    /*--------------------------------------------------------------------------------*/


    /*
    *
    *   Retrieve data from 'transactionList' collection in ORDERLY database
    *
    * */
    private ObservableList<Items> retrieveDataFromMongoDB() {
        String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
        String databaseName = "ORDERLY";
        String collectionName = "transactionList";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            // Create new list to be filled with database data
            ObservableList<Items> componentList = FXCollections.observableArrayList();

            // Loop through the whole database and store them in 'document'
            for (Document document : collection.find()) {

                // Create variable to store and kinda append to database
                Items items = new Items(
                        document.getString("Component"),
                        document.getString("Value"),
                        document.getString("Amount"),
                        document.getString("DateLastBought"),
                        document.getString("Link")
                );
                // Before adding to database, set the 'ObjectId' to the one created by MongoDB
                items.setId(document.getObjectId("_id"));

                // Add to database
                componentList.add(items);
            }

            return componentList;
        }
    }

    /*
    *
    *   Helper method to get the MongoDB collection
    *
    * */
    private MongoCollection<Document> getCollection() {
        String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
        String databaseName = "ORDERLY";

        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection("transactionList");
    }
}