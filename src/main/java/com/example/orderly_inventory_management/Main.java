package com.example.orderly_inventory_management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static Stage stg;


    /*
    *
    *   Start
    *
    * */
    @Override
    public void start(Stage stage) throws IOException {

        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/Assets/AppIcon.png"))));
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("StudentLoginScene.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Stylesheets/Login.css")).toExternalForm());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("ORDERLY - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stg = stage;
        stage.show();
        centerStage(stage);


        /*
        *
        *   Prompts logout
        *
        * */
        stage.setOnCloseRequest(event -> {
            try {
                event.consume();
                logout(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


    /*
    *
    *   Changes scene
    *
    * */
    public void changeScene(String fxml) throws IOException{

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
        Parent root = loader.load();
        stg.getScene().setRoot(root);
        stg.sizeToScene();

    }


    /*
    *
    *   Centers stage
    *
    * */    
    public void centerStage(Stage stg) {

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        // Calculate centered position
        double centerX = bounds.getMinX() + (bounds.getWidth() - stg.getWidth()) / 2;
        double centerY = bounds.getMinY() + (bounds.getHeight() - stg.getHeight()) / 2;
        stg.setX(centerX);
        stg.setY(centerY);

    }


    /*
    *
    *   Logout
    *
    * */
    public void logout(Stage stage) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You're about to be logged out!");
        alert.setContentText("Do you want to save before exiting?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("You successfully logged out!");
            stage.close();
        }

    }

    public static void main(String[] args) {
        launch();
    }

}
