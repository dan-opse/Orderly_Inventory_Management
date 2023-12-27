package Controllers;

import com.example.orderly_inventory_management.DatabaseConnection;
import com.example.orderly_inventory_management.Items;
import com.example.orderly_inventory_management.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void confirmEdit() {

    }

    @FXML
    public void confirmAdd() {
        String nComponent = tf_component.getText();
        String nValue = cb_value.getValue();
        String nAmount = tf_amount.getText();
        String nDlb = tf_dlb.getText();
        String nLink = tf_link.getText();

        // If not enough information
        if (nComponent.isBlank() || nValue == null || nAmount.isBlank() || nDlb.isBlank() || nLink.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("WARNING");
            alert.setHeaderText("Insufficient information.");
            alert.setContentText("Please fill in all spaces.");
            alert.showAndWait();
        } else {
            // Insert query
            String query = "INSERT INTO componentList (Component, Value, Amount, DateLastBought, Link) " +
                    "VALUES (?, ?, ?, ?, ?)";

            // Connect to the DB
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDB = connectNow.getUserInfoConnection();

            try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
                preparedStatement.setString(1, nComponent);
                preparedStatement.setString(2, nValue);
                preparedStatement.setString(3, nAmount);
                preparedStatement.setDate(4, java.sql.Date.valueOf(nDlb)); // Assuming nDlb is a valid date in the format "yyyy-MM-dd"
                preparedStatement.setString(5, nLink);

                int queryOutput = preparedStatement.executeUpdate();

                if (queryOutput > 0) {
                    System.out.println("Entry added successfully!");

                } else {
                    System.out.println("Failed to add entry.");
                }

                // Refresh the table view
                ObservableList<Items> updatedList = FXCollections.observableArrayList();
                ResultSet resultSet = connectDB.createStatement().executeQuery("SELECT Id, Component, Value, Amount, DateLastBought, Link FROM componentList");

                while (resultSet.next()) {
                    updatedList.add(new Items(
                            resultSet.getInt("Id"),
                            resultSet.getString("Component"),
                            resultSet.getString("Value"),
                            resultSet.getString("Amount"),
                            resultSet.getString("DateLastBought"),
                            resultSet.getString("Link")
                    ));
                }
                table_items.setItems(FXCollections.observableArrayList(updatedList));

            } catch (SQLException e) {
                Logger.getLogger(DashboardSceneController.class.getName()).log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
        }
    }

    public void confirmDelete() {

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
    private TableColumn<Items, Integer> col_id;
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

    ObservableList<Items> componentSearchModelObservableList = FXCollections.observableArrayList();

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

        cb_value.setItems(FXCollections.observableArrayList("1Ω", "10Ω", "100Ω", "1KΩ", "10KΩ", "100KΩ", "1MΩ", "RED", "GREEN", "BLUE", "WHITE","N/A"));

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getUserInfoConnection();

        // SQL Query - Executed in the backend database
        String componentViewQuery = "SELECT Id, Component, Value, Amount, DateLastBought, Link FROM componentList";

        try {

            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(componentViewQuery);

            while(queryOutput.next()) {

                Integer queryID = queryOutput.getInt("Id");
                String queryComponent = queryOutput.getString("Component");
                String queryValue = queryOutput.getString("Value");
                String queryAmount = queryOutput.getString("Amount");
                String queryDateLastBought = queryOutput.getString("DateLastBought");
                String queryLink = queryOutput.getString("Link");

                // Populate the ObservableList variable with parameters specific to the componentList found in MySQL
                componentSearchModelObservableList.add(new Items(queryID, queryComponent, queryValue, queryAmount, queryDateLastBought, queryLink));

            }

            // PropertyValueFactory corresponds to the new componentSearchModel fields
            // The table column is the one you annotate above.
            col_id.setCellValueFactory(new PropertyValueFactory<>("Id"));
            col_component.setCellValueFactory(new PropertyValueFactory<>("Component"));
            col_value.setCellValueFactory(new PropertyValueFactory<>("Value"));
            col_amount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
            col_dlb.setCellValueFactory(new PropertyValueFactory<>("DateLastBought"));
            col_link.setCellValueFactory(new PropertyValueFactory<>("Link"));

            table_items.setItems(componentSearchModelObservableList);

            // Initial filtered list
            FilteredList<Items> filteredData = new FilteredList<>(componentSearchModelObservableList, b -> true);

            keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(Items -> {

                    // If no search value then display all records or whatever records it currently holds. no changes.
                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                        return true;
                    }

                    String searchKeyword = newValue.toLowerCase();

                    // If the search keyword matches a component name (any > -1)
                    if (Items.getComponent().toLowerCase().contains(searchKeyword)) {
                        return true; // Means we found a match in ComponentName
                    } else if (Items.getValue().toLowerCase().contains(searchKeyword)) {
                        return true; // Means we found a match in Value
                    } else if (Items.getAmount().toLowerCase().contains(searchKeyword)) {
                        return true; // Means we found a match in Amount
                    } else if (Items.getDateLastBought().toLowerCase().contains(searchKeyword)) {
                        return true; // Means we found a match in Date Last Bought
                    } else if (Items.getLink().toLowerCase().contains(searchKeyword)) {
                        return true; // Means we found a match in Link
                    }
                    return false; // No match found
                });
            });

            // Create a new list using filteredData
            SortedList<Items> sortedData = new SortedList<>(filteredData);

            // Bind sorted result with Table View
            sortedData.comparatorProperty().bind(table_items.comparatorProperty());

            // Apply filtered and sorted data to the Table View
            table_items.setItems(sortedData);


        } catch (SQLException e) { // If no data can be extracted from the SQL
            Logger.getLogger(DashboardSceneController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
}
