package Controllers;

import com.example.orderly_inventory_management.DatabaseConnection;
import com.example.orderly_inventory_management.Student;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.w3c.dom.events.MouseEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddStudentController implements Initializable {

    @FXML
    private TextField fName_Field;
    @FXML
    private TextField lName_Field;
    @FXML
    private TextField component_Field;
    @FXML
    private TextField date_Field;

    String query = null;
    Connection connection = null;
    ResultSet resultSet = null;
    PreparedStatement preparedStatement;
    Student student = null;
    private boolean update;
    int studentId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void save(MouseEvent event) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getUserInfoConnection();

        String fName = fName_Field.getText();
        String lName = lName_Field.getText();
        String component = component_Field.getText();
        String dateSignedOut = date_Field.getText();

        if (fName.isEmpty() || lName.isEmpty() || component.isEmpty() || dateSignedOut.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Fill All DATA");
            alert.showAndWait();

        } else {
            getQuery();
            insert();
        }
    }

    private void getQuery() {
        if (update == false) {

            query = "INSERT INTO `student`( `name`, `birth`, `adress`, `email`) VALUES (?,?,?,?)";

        }else{
            query = "UPDATE `student` SET "
                    + "`name`=?,"
                    + "`birth`=?,"
                    + "`adress`=?,"
                    + "`email`= ? WHERE id = '"+studentId+"'";
        }
    }

    private void insert() {
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, fName_Field.getText());
            preparedStatement.setString(2, lName_Field.getText());
            preparedStatement.setString(3, component_Field.getText());
            preparedStatement.setString(4, date_Field.getText());
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(AddStudentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
