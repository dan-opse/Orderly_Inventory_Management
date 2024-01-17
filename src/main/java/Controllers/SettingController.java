package Controllers;

import com.example.orderly_inventory_management.AdminAccounts;
import com.example.orderly_inventory_management.Main;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.w3c.dom.events.DocumentEvent;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class SettingController implements Initializable {


    /*--------------------------------------------------------------------------------*/


    // Switching scenes

    private final Main m = new Main();

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


    // Draggable topBar + topBar actions

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


    // Settings

    private static final String CONFIG_FILE = "src/main/resources/Config-Files/config.settings";
    @FXML
    private CheckBox lowQuantityBox;
    @FXML
    private CheckBox lateSignOutBox;
    @FXML
    private CheckBox allBox;
    @FXML
    private Button changeUsernameButton;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Label currentUsername;

    private boolean lowQuantityBoxState;
    private boolean lateSignOutBoxState;
    private boolean allBoxState;

    private void saveStates() {

        // Save the current checkbox state in a property class format to a properties file: CONFIG_FILE
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            Properties properties = new Properties();
            // Assign each checkbox a name and value in the properties value
            properties.setProperty("lowQuantityBox", String.valueOf(lowQuantityBox.isSelected()));
            properties.setProperty("lateSignOutBox", String.valueOf(lateSignOutBox.isSelected()));
            properties.setProperty("allBox", String.valueOf(allBox.isSelected()));
            // Store writes into file as 'Key=value' pair; null is the comment you want to add in each property
            properties.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadStates() {

        // Load the states from the properties file
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            Properties properties = new Properties();
            // set each checkbox to the properties it was last set to
            properties.load(input);

            lowQuantityBoxState = Boolean.parseBoolean(properties.getProperty("lowQuantityBox"));
            lateSignOutBoxState = Boolean.parseBoolean(properties.getProperty("lateSignOutBox"));
            allBoxState = Boolean.parseBoolean(properties.getProperty("allBox"));

            setCheckBoxState(lowQuantityBox, properties.getProperty("lowQuantityBox"));
            setCheckBoxState(lateSignOutBox, properties.getProperty("lateSignOutBox"));
            setCheckBoxState(allBox, properties.getProperty("allBox"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void setCheckBoxState(CheckBox checkBox, String state) {
        if (state != null) {
            // set checkbox value to state from the properties config file
            checkBox.setSelected(Boolean.parseBoolean(state));
        }
    }
    private void handleCheckboxActions() {

        if (lowQuantityBoxState) {
            System.out.println("lowQuantityBox is selected");

        }

        if (lateSignOutBoxState) {
            System.out.println("lateSignOutBox is selected");

        }

        if (allBoxState) {
            System.out.println("allBox is selected");

        }
    }

    // Initial retrieval of username for displays purpose
    private String currentUser = AdminLoginController.username;

    @FXML
    private void changeUsername() {

        // Create text dialog object
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change username");
        dialog.setHeaderText("Enter a new username:");

        // Get result of dialog
        dialog.showAndWait().ifPresent(newUsername -> {

            // Get current user's _id
            currentUser = AdminLoginController.username;

            ObservableList<AdminAccounts> adminAccounts = retrieveDataFromMongoDB();

            // Create stream called currentAdmin, allows you to work with collections/arrays more functionally
            AdminAccounts currentAdmin = adminAccounts.stream()
                    .filter(admin -> admin.getUsername().equals(currentUser))
                    .findFirst()
                    .orElse(null);

            // If currentAdmin found in stream
            if (currentAdmin != null) {

                // Get current user's objectId
                String currentUserID = currentAdmin.getId().toString();

                MongoCollection<Document> collection = getCollection("adminAccounts");

                // Create queries and $set the specified fields to given values
                Document filter = new Document("_id", new ObjectId(currentUserID));
                Document update = new Document("$set", new Document("Username", newUsername));

                // Perform update
                collection.updateOne(filter, update);

            }
        });

        // Show change confirmation
        System.out.println("Successfully changed username!");
    }

    @FXML
    private void changePassword() {

        // Create text dialog object
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change password");
        dialog.setHeaderText("Enter a new password:");

        // Get result of dialog
        dialog.showAndWait().ifPresent(newPassword -> {

            // Get current user's _id
            currentUser = AdminLoginController.username;

            ObservableList<AdminAccounts> adminAccounts = retrieveDataFromMongoDB();

            // Create stream called currentAdmin, allows you to work with collections/arrays more functionally
            AdminAccounts currentAdmin = adminAccounts.stream()
                    .filter(admin -> admin.getUsername().equals(currentUser))
                    .findFirst()
                    .orElse(null);

            // If currentAdmin found in stream
            if (currentAdmin != null) {

                // Get current user's objectId
                String currentUserID = currentAdmin.getId().toString();

                MongoCollection<Document> collection = getCollection("adminAccounts");

                // Create queries and $set the specified fields to given values
                Document filter = new Document("_id", new ObjectId(currentUserID));
                Document update = new Document("$set", new Document("Password", newPassword));

                // Perform update
                collection.updateOne(filter, update);

            }
        });

        // Show change confirmation
        System.out.println("Successfully changed password!");
    }

    /*--------------------------------------------------------------------------------*/


    @FXML
    private ComboBox<String> changeTheme;

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

        // Checkboxes
        loadStates();

        // Add event handlers to each checkbox, calls saveStates() each time a checkbox is clicked
        lowQuantityBox.setOnAction(event -> {
            lowQuantityBoxState = lowQuantityBox.isSelected();
            saveStates();
            handleCheckboxActions();
        });
        lateSignOutBox.setOnAction(event -> {
            lateSignOutBoxState = lateSignOutBox.isSelected();
            saveStates();
            handleCheckboxActions();
        });
        allBox.setOnAction(event -> {
            allBoxState = allBox.isSelected();
            saveStates();
            handleCheckboxActions();
        });

        changeTheme.setItems(FXCollections.observableArrayList("Coming soon!"));

        currentUsername.setText(currentUser);
    }

    // Retrieve data from 'adminAccounts' collection in ORDERLY database
    private ObservableList<AdminAccounts> retrieveDataFromMongoDB() {

        // Database connection variables
        String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
        String databaseName = "ORDERLY";
        String collectionName = "adminAccounts";

        // Try connection
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            var cursor = collection.find().iterator();

            ObservableList<AdminAccounts> adminAccounts = FXCollections.observableArrayList();

            while (cursor.hasNext()) {
                var document = cursor.next();

                AdminAccounts admin = new AdminAccounts(
                        document.getString("Username"),
                        document.getString("Password")
                );
                admin.setId(document.getObjectId("_id"));
                adminAccounts.add(admin);
            }

            return adminAccounts;
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
