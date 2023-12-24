package Controllers;

import com.example.orderly_inventory_management.DatabaseConnection;
import com.example.orderly_inventory_management.Main;
import com.example.orderly_inventory_management.SignOut;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignOutSceneController implements Initializable {

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
     *   Tableview + keyword search
     *
     * */
    @FXML
    private TextField keywordTextField;
    @FXML
    private TableView<SignOut> table_students;
    @FXML
    private TableColumn<SignOut, Integer> col_id;
    @FXML
    private TableColumn<SignOut, String> col_fName;
    @FXML
    private TableColumn<SignOut, String> col_lName;
    @FXML
    private TableColumn<SignOut, String> col_component;
    @FXML
    private TableColumn<SignOut, String> col_dso;

    ObservableList<SignOut> signOutSearchModelObservableList = FXCollections.observableArrayList();

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

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getUserInfoConnection();

        // SQL Query - Executed in the backend database
        String signoutViewQuery = "SELECT Id, FirstName, LastName, Component, DateSignedOut FROM studentsignout";

        try {

            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(signoutViewQuery);

            while(queryOutput.next()) {

                Integer queryID = queryOutput.getInt("Id");
                String queryfName = queryOutput.getString("FirstName");
                String querylName = queryOutput.getString("LastName");
                String queryComponent = queryOutput.getString("Component");
                String queryDateSignedOut = queryOutput.getString("DateSignedOut");

                // Populate the ObservableList variable with parameters specific to the componentList found in MySQL
                signOutSearchModelObservableList.add(new SignOut(queryID, queryfName, querylName, queryComponent, queryDateSignedOut));

            }

            // PropertyValueFactory corresponds to the new componentSearchModel fields
            // The table column is the one you annotate above.
            col_id.setCellValueFactory(new PropertyValueFactory<>("Id"));
            col_fName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
            col_lName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
            col_component.setCellValueFactory(new PropertyValueFactory<>("Component"));
            col_dso.setCellValueFactory(new PropertyValueFactory<>("DateSignedOut"));

            table_students.setItems(signOutSearchModelObservableList);

            // Initial filtered list
            FilteredList<SignOut> filteredData = new FilteredList<>(signOutSearchModelObservableList, b -> true);

            keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(Signout -> {

                    // If no search value then display all records or whatever records it currently holds. no changes.
                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                        return true;
                    }

                    String searchKeyword = newValue.toLowerCase();

                    if (Signout.getFirstName().toLowerCase().contains(searchKeyword)) {
                        return true;
                    } else if (Signout.getLastName().toLowerCase().contains(searchKeyword)) {
                        return true;
                    } else if (Signout.getComponent().toLowerCase().contains(searchKeyword)) {
                        return true;
                    } else if (Signout.getDateSignedOut().toLowerCase().contains(searchKeyword)) {
                        return true;
                    }
                    return false;
                });
            });

            // Create a new list using filteredData
            SortedList<SignOut> sortedData = new SortedList<>(filteredData);

            // Bind sorted result with Table View
            sortedData.comparatorProperty().bind(table_students.comparatorProperty());

            // Apply filtered and sorted data to the Table View
            table_students.setItems(sortedData);

        } catch (SQLException e) { // If no data can be extracted from the SQL
            Logger.getLogger(DashboardSceneController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
}
