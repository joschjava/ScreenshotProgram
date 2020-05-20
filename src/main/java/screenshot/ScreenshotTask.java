package screenshot;

import gui.Controller;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class ScreenshotTask extends TimerTask {

    static Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd-H_m_s_S");
    private static int counter = 0;
    private static BufferedImage lastImage;
    private File folder;
    private Controller controller;


    public ScreenshotTask(File folder, Controller controller) {
        this.folder = folder;
        this.controller = controller;
    }

    public static Image getLastImage() {
        return SwingFXUtils.toFXImage(lastImage, null);
    }

    @Override
    public void run() {
        try {
            BufferedImage image = new Robot().createScreenCapture(screen);
            String date = simpleDateFormat.format(new Date());
            System.out.println("Screenshot taken");
            String path = folder.getAbsolutePath() + "\\screenshot-" + date + ".jpg";
            ImageIO.write(image, "jpg", new File(path));
            lastImage = image;
            Platform.runLater(() -> {
                        controller.setNumberImages(++counter);
                        controller.previewLastImage(getLastImage());
                    }
            );

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }
}