package Controllers;

import com.example.orderly_inventory_management.Main;
import com.example.orderly_inventory_management.Student;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class StudentSignOutController implements Initializable {


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


    /*--------------------------------------------------------------------------------*/


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Initialize 'Returned' column in table-view
        TableColumn<Student, Boolean> col_returned = new TableColumn<>("Returned");
        col_returned.setCellValueFactory(
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
        col_returned.setCellFactory(CheckBoxTableCell.forTableColumn(col_returned));

        table_students.getColumns().add(col_returned);

        loadStates();

        // Draggable topBar
        topBar.setOnMousePressed(mouseEvent-> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        topBar.setOnMouseDragged(mouseEvent-> {
            Main.stg.setX(mouseEvent.getScreenX()-x);
            Main.stg.setY(mouseEvent.getScreenY()-y);
        });


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

        saveStates();
    }


    /*--------------------------------------------------------------------------------*/


    // Save returned value on exit

    private static final String CONFIG_FILE = "src/main/resources/Config-Files/config.signOut";

    // Save states
    private void saveStates() {

        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            Properties properties = new Properties();

            // Loop over table-view and retrieve checkbox values AND store them into CONFIG_FILE
            for (int i = 0; i < table_students.getItems().size(); i++) {
                boolean state = table_students.getItems().get(i).isSelected();
                properties.setProperty("checkbox_" + i, String.valueOf(state));
            }

            properties.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Load states
    private void loadStates() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            Properties properties = new Properties();
            properties.load(input);

            if (properties.isEmpty()) {
                // If no saved states, set default values
                setCheckboxStates();
            } else {
                for (int i = 0; i < table_students.getItems().size(); i++) {
                    String key = "checkbox_" + i;
                    boolean state = Boolean.parseBoolean(properties.getProperty(key, "false"));
                    table_students.getItems().get(i).setSelected(state);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Set states on tableview
    private void setCheckboxStates() {

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
