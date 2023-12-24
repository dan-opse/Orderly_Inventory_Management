package Controllers;

import com.example.orderly_inventory_management.AnimationUtils;
import com.example.orderly_inventory_management.DatabaseConnection;
import com.example.orderly_inventory_management.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginSceneController implements Initializable {
    @FXML
    private BorderPane root;
    double x = 0;
    double y = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnimationUtils.applyButtonHoverClickAnimations(loginButton);
        AnimationUtils.applyButtonHoverClickAnimations(quitButton);

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


    /*
     *
     *   TopBar Buttons
     *
     * */
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

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getUserInfoConnection();

            // Store the query to be executed to check for a matching account
            String verifyLogin = "SELECT count(1) FROM UserAccount WHERE Username = '" + usernameField.getText() + "' AND Password = '" + passwordField.getText() + "'";

            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()) {
                // If there is one unique username found
                if (queryResult.getInt(1) == 1) {
                    loginMessage.setText("Welcome, " + usernameField.getText());
                    return true;
                } else {
                    loginMessage.setText("Invalid login. Please try again.");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
