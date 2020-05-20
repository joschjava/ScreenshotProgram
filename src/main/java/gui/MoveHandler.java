package gui;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MoveHandler {
    private double xOffset;
    private double yOffset;
    private Stage stage;

    MoveHandler(ImageView ivMove) {
        Controller.addHoverEffect(ivMove);

        ivMove.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (stage == null) {
                    stage = (Stage) ivMove.getScene().getWindow();
                }
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        ivMove.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

    }

}
