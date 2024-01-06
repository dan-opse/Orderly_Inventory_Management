package Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoadingSceneController implements Initializable {

    @FXML
    private AnchorPane root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        splash();
    }

    public void splash() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoadingScene.fxml")));
                            Stage stage = new Stage();
                            Scene scene = new Scene(root);
                            stage.setTitle("Loading...");
                            stage.setScene(scene);
                            stage.show();

                            root.getScene().getWindow().hide();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }.start();
    }
}
