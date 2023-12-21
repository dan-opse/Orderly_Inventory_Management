package com.example.orderly_inventory_management;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SignInSceneController implements Initializable {

    /*
     *
     *   Main buttons
     *
     * */
    @FXML
    private Button dashboardButton;
    @FXML
    private Button signInButton;
    @FXML
    private Button transactionButton;
    @FXML
    private Button notiButton;
    @FXML
    private Button settingButton;


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
    }
}
