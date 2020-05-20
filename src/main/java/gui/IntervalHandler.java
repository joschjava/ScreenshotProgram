package gui;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import screenshot.Constants;

import java.awt.*;

public class IntervalHandler {

    private IntegerProperty interval = new SimpleIntegerProperty();
    private double startMouseY = -1;
    private IntervalChanger intervalChanger;
    private double yScreenSize;
    private boolean mouseDown;


    IntervalHandler(Label lbInterval, StackPane spInterval) {
        System.out.println("Intervalhandler created");
        interval.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String output = String.valueOf(interval.get()) + "s";
                lbInterval.setText(output);
            }
        });
        interval.set(Constants.INTERVAL_DEFAULT);
        yScreenSize = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        intervalChanger = new IntervalChanger();

        spInterval.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Scene scene = spInterval.getScene();
                scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, intervalChanger);
                scene.setCursor(Cursor.V_RESIZE);
                saveCurrentMousePosition(event);
                mouseDown = true;
            }
        });

        spInterval.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Scene scene = spInterval.getScene();
                scene.removeEventFilter(MouseEvent.MOUSE_DRAGGED, intervalChanger);
                scene.setCursor(Cursor.DEFAULT);
                mouseDown = false;
            }
        });
        InnerShadow is = Controller.getInnerShadow();
        spInterval.setOnMouseEntered((me) -> {
            Scene scene = spInterval.getScene();
            scene.setCursor(Cursor.V_RESIZE);

            Controller.addMouseEnteredEffect(spInterval, is);
        });
        spInterval.setOnMouseExited((me) -> {
            Scene scene = spInterval.getScene();
            if (!mouseDown) {
                scene.setCursor(Cursor.DEFAULT);
            }
            Controller.addMouseExitEffect(spInterval, is);
        });
    }

    private void saveCurrentMousePosition(MouseEvent event) {
        startMouseY = event.getScreenY();
    }

    public int getInterval() {
        return this.interval.get();
    }

    public void setInterval(int interval) {
        this.interval.set(interval);
    }

    public IntegerProperty intervalProperty() {
        return interval;
    }

    class IntervalChanger implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            double curMouseX = event.getScreenX();
            double curMouseY = event.getScreenY();

            if (curMouseY >= yScreenSize - 1) {
                moveCursor((int) curMouseX, 0);
                saveCurrentMousePosition(event);
            } else if (curMouseY <= 0) {
                moveCursor((int) curMouseX, (int) yScreenSize);
                saveCurrentMousePosition(event);
            } else if ((curMouseY - startMouseY) < -10) {
                interval.set(interval.get() + 1);
                saveCurrentMousePosition(event);
            } else if ((curMouseY - startMouseY) > 10 && interval.get() > 1) {
                interval.set(interval.get() - 1);
                saveCurrentMousePosition(event);
            }

        }

        /**
         * Move the mouse to the specific screen position
         *
         * @param screenX
         * @param screenY
         */
        public void moveCursor(int screenX, int screenY) {
            Platform.runLater(() -> {
                try {
                    Robot robot = new Robot();
                    robot.mouseMove(screenX, screenY);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            });
        }
    }


}
