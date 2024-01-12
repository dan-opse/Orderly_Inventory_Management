package Controllers;

import com.example.orderly_inventory_management.Main;
import com.mongodb.client.*;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginSceneController implements Initializable {


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
    private Button loginButton;
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
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginMessage;
    @FXML
    private CheckBox rememberDevice;


    /*--------------------------------------------------------------------------------*/


    /*
    *
    *   Login method
    *
    * */
    public void login() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Main m = new Main();

        // If populated
        if (!username.isBlank() && !password.isBlank()) {

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
        try {
            String connectionString = "mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority";
            String databaseName = "ORDERLY";
            String collectionName = "userInformation";

            try (MongoClient mongoClient = MongoClients.create(connectionString)) {
                MongoDatabase database = mongoClient.getDatabase(databaseName);
                MongoCollection<Document> collection = database.getCollection(collectionName);

                Document query = new Document("Username", usernameField.getText()).append("Password", passwordField.getText());

                // Execute the query
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
            e.printStackTrace();
        }
        return false;
    }
}
