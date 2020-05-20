package gui;
// Colors: https://color.adobe.com/de/create/color-wheel/?base=1&rule=Analogous&selected=1&name=Mein%20Color-Thema&mode=rgb&rgbvalues=0.96,0.8812194026281875,0.02488888888888887,0.91,0.6561296604937552,0.021907407407407365,1,0.8197439267219209,0.025925925925925908,1,0.6233580246910362,0.025925925925925908,0.96,0.47458382222160267,0.02488888888888887&swatchOrder=0,1,2,3,4

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import screenshot.Constants;
import screenshot.Screenshot;
import screenshot.ScreenshotTask;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;


public class Controller implements Initializable {

    @FXML
    VBox root;

    @FXML
    ImageView ivFolder;

    @FXML
    ImageView ivRecord;

    @FXML
    Label lbImagesTaken;

    @FXML
    ImageView imPostview;

    @FXML
    Label lbInterval;

    @FXML
    StackPane spInterval;

    @FXML
    StackPane spNrImages;

    @FXML
    ImageView ivMove;

    @FXML
    ImageView ivExit;

    private File selectedFolder;

    private BooleanProperty recording = new SimpleBooleanProperty(false);

    private Timer timer;

    private Stage stage;

    private Image IM_RECORD;
    private Image IM_STOP;


//	@FXML
//	private TextField tfInterval;

    @FXML
    private Label lbVideoSpeed;

    private IntervalHandler intervalHandler;

    public static InnerShadow getInnerShadow() {
        InnerShadow is = new InnerShadow();
        is.setChoke(1.0);
        is.setRadius(2.0);
        is.setWidth(5.0);
        is.setHeight(5.0);
        return is;
    }

    public static DropShadow getDropShadow() {
        DropShadow ds = new DropShadow();
        return ds;
    }

    static void addHoverEffect(Node node) {
        InnerShadow is = getInnerShadow();
        node.setOnMouseEntered((me) ->
        {
            addMouseEnteredEffect(node, is);
        });

        node.setOnMouseExited((me) ->
        {
            addMouseExitEffect(node, is);
        });
    }

    public static void addMouseEnteredEffect(Node node, InnerShadow is) {
        DropShadow ds = getDropShadow();
        is.setInput(ds);
        node.setEffect(is);
    }

    public static void addMouseExitEffect(Node node, InnerShadow is) {
        is.setInput(null);
        node.setEffect(is);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
        intervalHandler = new IntervalHandler(lbInterval, spInterval);
        intervalHandler.intervalProperty().addListener((observable, oldValue, newValue) -> {
            calculateVideoSpeed(newValue.intValue());
        });
        new MoveHandler(ivMove);
    }

    public void initialize() {
        ivExit.setOnMouseClicked((me) -> {
            exitProgram();
        });

        addHoverEffect(ivExit);

        root.setBackground(new Background(new BackgroundFill(Color.web("B58304"), CornerRadii.EMPTY, Insets.EMPTY)));

        IM_RECORD = new Image(getClass().getResourceAsStream("/record2.png"));
        IM_STOP = new Image(getClass().getResourceAsStream("/stop2.png"));

        try {
            File file = new File(Screenshot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            if (file.isFile() || file.getPath().endsWith(".jar") || file.getPath().endsWith(".zip")) {
                selectedFolder = new File(file.getParent());
            } else {
                selectedFolder = file;
            }
            System.out.println(selectedFolder);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ivFolder.setOnMouseClicked((me) -> {
            getFolder();
        });

        createImageFolderTooltip();

        ivRecord.setOnMouseClicked((me) -> {
            if (recording.get()) {
                recording.set(false);
                timer.cancel();
            } else {
                timer = new Timer();
                int interval = intervalHandler.getInterval() * 1000;
                System.out.println(interval);
                timer.schedule(new ScreenshotTask(selectedFolder, this), 1000, interval);
                recording.set(true);
//			    stage.setIconified(true);
            }
        });

        addHoverEffect(ivRecord);
        addHoverEffect(spNrImages);
//		addHoverEffect(ivFolder);

        ivRecord.imageProperty().bind(Bindings.when(recording).then(IM_STOP).otherwise(IM_RECORD));
        calculateVideoSpeed(Constants.INTERVAL_DEFAULT);
    }

    private void calculateVideoSpeed(int interval) {
        int speed = (int) (60 * ((double) Constants.FPS / (double) Constants.FRAMES_PER_IMAGE) * interval);
        StringBuilder speedText = new StringBuilder();
        int hours = speed / 3600;
        int minutes = (speed % 3600) / 60;
        int seconds = speed % 60;
        boolean timeSet = false;
        if (hours >= 1) {
            speedText.append(String.format("%02dh ", hours));
            timeSet = true;
        }
        if (minutes >= 1 || timeSet) {
            speedText.append(String.format("%02dm ", minutes));
        }
        speedText.append(String.format("%02ds", seconds));

        String text = Constants.TEXT_VIDEO_SPEED
                .replace("%speed%", speedText.toString())
                .replace("%fps%", String.valueOf(Constants.FPS))
                .replace("%fpi%", String.valueOf(Constants.FRAMES_PER_IMAGE));
        lbVideoSpeed.setText(text);
    }

    private void getFolder() {
        File dialogFolder = showResourceDialog();
        if (dialogFolder != null) {
            selectedFolder = dialogFolder;
            createImageFolderTooltip();
        }
    }

    private File showResourceDialog() {
        stage = (Stage) ivFolder.getScene().getWindow();
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Open Resource Folder");
        if (selectedFolder != null) {
            fileChooser.setInitialDirectory(selectedFolder);
        }
        return fileChooser.showDialog(stage);
    }

    public void setNumberImages(int nr) {
        lbImagesTaken.setText(String.valueOf(nr));
    }

    public void previewLastImage(Image lastImage) {
        imPostview.setImage(lastImage);
    }

    /**
     * Creates a tooltip at a cursor
     */
    public void createImageFolderTooltip() {
        final Tooltip tooltip = new Tooltip("Current: " + selectedFolder.getAbsolutePath());
        InnerShadow is = getInnerShadow();
        ivFolder.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // +15 moves the tooltip 15 pixels below the mouse cursor;
                // if you don't change the y coordinate of the tooltip, you
                // will see constant screen flicker
                tooltip.show(ivFolder, event.getScreenX(), event.getScreenY() + 20);

                addMouseEnteredEffect(ivFolder, is);
            }
        });

        ivFolder.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tooltip.setAnchorX(event.getScreenX());
                tooltip.setAnchorY(event.getScreenY() + 20);
            }
        });
        ivFolder.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                addMouseExitEffect(ivFolder, is);
                tooltip.hide();
            }
        });
    }

    private void exitProgram() {
        ivFolder.getScene().getWindow().hide();
        System.exit(0);
    }

}
