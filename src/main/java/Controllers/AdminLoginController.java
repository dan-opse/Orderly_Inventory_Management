package Controllers;

import com.example.orderly_inventory_management.Main;
import com.mongodb.client.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminLoginController implements Initializable {


    /*--------------------------------------------------------------------------------*/


    /*
     *
     *   Draggable topBar + topBar actions
     *
     * */
    @FXML
    private AnchorPane root;

    private double x = 0;
    private double y = 0;


    /*--------------------------------------------------------------------------------*/


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Draggable topBar
        root.setOnMousePressed(mouseEvent-> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });
        root.setOnMouseDragged(mouseEvent-> {
            Main.stg.setX(mouseEvent.getScreenX()-x);
            Main.stg.setY(mouseEvent.getScreenY()-y);
        });

    }

    @FXML
    private Button quitButton;
    @FXML
    private Button minimizeButton;

    public void minimizeAction() {

        Stage stage = (Stage)minimizeButton.getScene().getWindow();
        stage.setIconified(true);

    }
    public void quitOnAction() {

        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();

    }


    /*--------------------------------------------------------------------------------*/


    /*
     *
     *   Login buttons
     *
     * */
    Main m = new Main();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginMessage;

    public void switchToStudentLogin() throws IOException {
        m.changeScene("StudentLoginScene.fxml");
    }

    /*--------------------------------------------------------------------------------*/


    /*
     *
     *   Login method
     *
     * */
    public static String username;

    public void login() throws IOException {

        // Store username and password
        username = usernameField.getText();
        String password = passwordField.getText();

        // Check for empty fields
        if (!username.isBlank() && !password.isBlank()) {

            // Check if username & password are found in database entry
            if(validateLogin()) {
                m.changeScene("DashboardScene.fxml");
            }

        } else {
            loginMessage.setText("Insufficient Information.");
        }
    }


    /*
     *
     *   Validates the login given the database
     *   executes a query and returns true if the username and password the user has entered is valid
     *
     * */
    public boolean validateLogin() {

        // Validate login against the database
        try {
            // Initialize connection variables/details
            String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
            String databaseName = "ORDERLY";
            String collectionName = "adminAccounts";

            // Try connection
            try (MongoClient mongoClient = MongoClients.create(connectionString)) {
                MongoDatabase database = mongoClient.getDatabase(databaseName);
                MongoCollection<Document> collection = database.getCollection(collectionName);

                // Create a temporary document with user entered values
                Document query = new Document("Username", usernameField.getText()).append("Password", passwordField.getText());

                // Find a document that matches the temp document above
                try (MongoCursor<Document> cursor = collection.find(query).iterator()) {

                    // If a matching document is found
                    if (cursor.hasNext()) {
                        loginMessage.setText("Welcome, " + usernameField.getText());
                        return true;
                    } else {
                        loginMessage.setText("Invalid login. Please try again.");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return false;
    }
}
