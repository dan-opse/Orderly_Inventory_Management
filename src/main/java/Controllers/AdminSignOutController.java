package Controllers;

import com.example.orderly_inventory_management.Main;
import com.example.orderly_inventory_management.Student;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.net.URL;

import java.util.Objects;
import java.util.ResourceBundle;


public class AdminSignOutController implements Initializable {


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


    /*
     *
     *   Entry modifications
     *
     * */
    @FXML
    private TextField tf_name;
    @FXML
    private TextField tf_item;
    @FXML
    private TextField tf_amount;
    @FXML
    private ComboBox<String> cb_returned;
    @FXML
    private TextField tf_date;
    private ObjectId originalId;
    private Student originalEntry;
    @FXML
    public void addEntry() {

        String nName = tf_name.getText();
        String nItem = tf_item.getText();
        String nAmount = tf_amount.getText();
        String nReturned = cb_returned.getValue();
        String nDate = tf_date.getText();

        // Check if any of the fields are empty
        if (nName.isBlank() || nItem == null || nAmount.isBlank() || nReturned == null || nDate.isBlank()) {

            showWarning("Error", "Insufficient information.", "Please add the sufficient information.");
            return;

        }

        // Create a new document with the input data
        Document document = new Document("Name", nName)
                .append("Item", nItem)
                .append("Amount", nAmount)
                .append("Returned", nReturned)
                .append("Date", nDate);

        // Insert the document into the MongoDB collection
        getCollection("signOutList").insertOne(document);
        refreshTableView();

    }
    @FXML
    public void deleteEntry() {

        ObservableList<Student> selectedStudents = table_students.getItems();
        MongoCollection<Document> targetCollection = getCollection("signOutList");

        // Loop over selected items
        for (Student selectedStudent : selectedStudents) {
            boolean isSelected = col_select.getCellObservableValue(selectedStudent).getValue();

            if (isSelected) {
                String name = col_name.getCellData(selectedStudent);
                String item = col_item.getCellData(selectedStudent);
                String amount = String.valueOf(col_amount.getCellData(selectedStudent));
                String returned = String.valueOf(col_returned.getCellData(selectedStudent));
                String date = col_date.getCellData(selectedStudent);

                Document document = new Document("Name", name).append("Item", item).append("Amount", amount).append("Returned", returned).append("Date", date);

                targetCollection.deleteOne(document);
                refreshTableView();
            }
        }

        // Define the filter to find the document to delete
        Bson filter = Filters.eq("_id", originalId);

        // Delete the document from the MongoDB collection
        getCollection("signOutList").deleteOne(filter);
        refreshTableView();

    }
    @FXML
    private void updateEntry() {

        if (originalEntry != null && originalEntry.getId() != null) {
            originalId = originalEntry.getId();

            // Get the values from the text fields
            String nName = tf_name.getText();
            String nItem = tf_item.getText();
            String nAmount = tf_amount.getText();
            String nReturned = cb_returned.getValue();
            String nDate = tf_date.getText();

            // Check if any of the fields are empty
            if (nName.isBlank() || nItem.isBlank() || nAmount.isBlank() || nReturned == null || nDate.isBlank()) {
                showWarning("Error", "Insufficient information", "Please fill in text fields.");
                return;
            }

            // Define the filter to find the document to update
            Bson filter = Filters.eq("_id", originalId);

            // Create a new document with the updated values
            Document updatedDocument = new Document("Name", nName)
                    .append("Item", nItem)
                    .append("Amount", nAmount)
                    .append("Returned", nReturned)
                    .append("Date", nDate);
            Bson update = new Document("$set", updatedDocument);

            // Update the document in the MongoDB collection
            getCollection("signOutList").updateOne(filter, update);

            // If 'Returned' value updated as 'Yes' prompt user to remove entry
            if (Objects.equals(cb_returned.getValue(), "Yes")) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Returned has been set to true.");
                alert.setContentText("Would you like to remove this entry?");

                if (alert.showAndWait().get() == ButtonType.OK) {
                    alert.close();
                    deleteEntry();
                }

            }

            refreshTableView();

            // Reset entry values
            originalEntry = null;
            originalId = null;

        } else {
            showWarning("Error", "Failed to update", "Retry?");
        }

    }
    @FXML
    private void resetSelection() {

        // Deselect all entries in table-view
        table_students.getSelectionModel().clearSelection();

        // Clear individual text fields
        tf_name.clear();
        tf_item.clear();
        tf_amount.clear();
        cb_returned.setValue(null);
        tf_date.clear();

    }
    private void refreshTableView() {

        ObservableList<Student> updatedList = retrieveDataFromMongoDB();
        table_students.setItems(updatedList);

    }
    private void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    /*--------------------------------------------------------------------------------*/


    /*
     *
     *   Tableview + keyword search
     *
     * */
    @FXML
    public void searchTable(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();

        ObservableList<Student> filteredList = retrieveDataFromMongoDB().filtered(student ->
                student.getName().toLowerCase().contains(lowerCaseKeyword) ||
                        student.getItem().toLowerCase().contains(lowerCaseKeyword) ||
                        student.getAmount().toLowerCase().contains(lowerCaseKeyword) ||
                        student.getReturned().toLowerCase().contains(lowerCaseKeyword) ||
                        student.getDate().toLowerCase().contains(lowerCaseKeyword)
        );

        table_students.setItems(filteredList);
    }
    @FXML
    private TextField keywordTextField;
    @FXML
    private TableView<Student> table_students;
    @FXML
    private TableColumn<Student, String> col_name;
    @FXML
    private TableColumn<Student, String> col_item;
    @FXML
    private TableColumn<Student, Integer> col_amount;
    @FXML
    private TableColumn<Student, Boolean> col_returned;
    @FXML
    private TableColumn<Student, String> col_date;
    @FXML
    private TableColumn<Student, Boolean> col_select;


    /*--------------------------------------------------------------------------------*/


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Initialize 'Select' column in table-view
        col_select.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Student, Boolean>, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Student, Boolean> param) {
                        Student entry = param.getValue();
                        SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(entry.isSelected());
                        booleanProp.addListener((observable, oldValue, newValue) -> entry.setSelected(newValue));
                        return booleanProp;
                    }
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
        table_students.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                originalEntry = newSelection;
                originalId = newSelection.getId();
                System.out.println(originalId);
                // Update the text fields with the properties of the selected item
                tf_name.setText(newSelection.getName());
                tf_item.setText(newSelection.getItem());
                tf_amount.setText(newSelection.getAmount());
                cb_returned.setValue(newSelection.getReturned());
                tf_date.setText(newSelection.getDate());
            } else {
                // Clear the text fields if no item is selected
                tf_name.clear();
                tf_item.clear();
                tf_amount.clear();
                cb_returned.setValue(null);
                tf_date.clear();
            }
        });

        // Initialize 'Returned' value in table-view
        cb_returned.setItems(FXCollections.observableArrayList("No", "Yes"));

        // Grab initial list of students & populate table-view
        ObservableList<Student> signOutList = retrieveDataFromMongoDB();

        col_name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        col_item.setCellValueFactory(new PropertyValueFactory<>("Item"));
        col_amount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        col_returned.setCellValueFactory(new PropertyValueFactory<>("Returned"));
        col_date.setCellValueFactory(new PropertyValueFactory<>("Date"));

        table_students.setItems(signOutList);

        // Search
        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchTable(newValue);
        });

    }


    /*--------------------------------------------------------------------------------*/


    // Retrieve data from 'componentList' collection in ORDERLY database
    private ObservableList<Student> retrieveDataFromMongoDB() {

        String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
        String databaseName = "ORDERLY";
        String collectionName = "signOutList";

        try (var mongoClient = MongoClients.create(connectionString)) {
            var database = mongoClient.getDatabase(databaseName);
            var collection = database.getCollection(collectionName);

            var cursor = collection.find().iterator();

            ObservableList<Student> signOutList = FXCollections.observableArrayList();

            while (cursor.hasNext()) {
                var document = cursor.next();

                var student = new Student(
                        document.getString("Name"),
                        document.getString("Item"),
                        document.getString("Amount"),
                        document.getString("Returned"),
                        document.getString("Date")
                        // ... add more fields based on document structure
                );
                student.setId(document.getObjectId("_id"));
                signOutList.add(student);
            }

            return signOutList;
        }

    }

    // Helper method to get the MongoDB collection
    private MongoCollection<Document> getCollection(String collectionName) {

        String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
        String databaseName = "ORDERLY";

        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(collectionName);

    }
}
