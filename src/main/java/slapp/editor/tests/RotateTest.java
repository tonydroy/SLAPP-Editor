package slapp.editor.tests;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RotateTest extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        Label rect = new Label("This is some text");
        rect.setFont(new Font(48));
//        Rectangle rect = new Rectangle(300, 300, Color.RED);
        HBox.setMargin(rect, new Insets(100));




        HBox root = new HBox(rect);
        root.setStyle("-fx-background-color: white");

        Pane pane = new Pane(root);
        pane.setStyle("-fx-background-color: white");

        Pane pane2 = new Pane(pane);
        pane2.setStyle("-fx-background-color: white");

        Scene scene = new Scene(pane2);
        stage.setScene(scene);
        stage.setTitle("Rotate Transition");
        stage.show();

        // Set up a rotate transition the rectangle
        RotateTransition rt = new RotateTransition(Duration.seconds(5), rect);
        rt.setFromAngle(0.0);
        rt.setToAngle(360.0);
//        rt.setCycleCount(1);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.setAutoReverse(false);
        rt.setAxis(Rotate.X_AXIS);

        RotateTransition rt2 = new RotateTransition(Duration.seconds(5), root);
        rt2.setFromAngle(0.0);
        rt2.setToAngle(360.0);
//        rt2.setCycleCount(1);
        rt2.setCycleCount(RotateTransition.INDEFINITE);
        rt2.setAutoReverse(false);
        rt2.setAxis(Rotate.Y_AXIS);

        RotateTransition rt3 = new RotateTransition(Duration.seconds(5), pane);
        rt3.setFromAngle(0.0);
        rt3.setToAngle(360.0);
//        rt3.setCycleCount(1);
        rt3.setCycleCount(RotateTransition.INDEFINITE);
        rt3.setAutoReverse(false);
        rt3.setAxis(Rotate.Z_AXIS);

        ScaleTransition sc = new ScaleTransition(Duration.seconds(5), pane2);
        sc.setFromX(0.0);
        sc.setToX(1.0);
        sc.setFromY(0.0);
        sc.setToY(1.0);
        sc.setFromZ(0.0);
        sc.setToZ(1.0);
//        sc.setCycleCount(1);
        sc.setCycleCount(RotateTransition.INDEFINITE);
        sc.setAutoReverse(false);




        ParallelTransition p = new ParallelTransition(rt, rt2, rt3, sc);


        p.play();
    }
}