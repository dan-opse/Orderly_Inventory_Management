package Controllers;

import com.example.orderly_inventory_management.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class SettingSceneController implements Initializable {


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

    private static final String CONFIG_FILE = "src/main/resources/Config-Files/config.properties";
    @FXML
    private CheckBox lowQuantityBox;
    @FXML
    private CheckBox lateSignOutBox;
    @FXML
    private CheckBox allBox;

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


    /*--------------------------------------------------------------------------------*/


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


    }
}
