package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import screenshot.Constants;

import java.io.IOException;


public class Gui {

    public void startApplication(Stage primaryStage) {
        VBox rootLayout = null;
        try {
            rootLayout = FXMLLoader.load(getClass().getResource("../../../resources/main/mainwindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(rootLayout);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle(Constants.WINDOW_TITLE);
        primaryStage.show();
    }


}
