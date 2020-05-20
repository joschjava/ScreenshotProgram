package screenshot;

import gui.Gui;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class Screenshot extends Application {

    /**
     * @param args
     * @throws AWTException
     * @throws HeadlessException
     * @throws IOException
     * @throws InterruptedException
     */

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Gui gui = new Gui();
        gui.startApplication(primaryStage);
    }

}