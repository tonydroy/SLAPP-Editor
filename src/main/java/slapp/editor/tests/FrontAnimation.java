package slapp.editor.tests;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FrontAnimation extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        AnchorPane treePane = new AnchorPane();
        treePane.setPrefHeight(370);

        Circle dot1 = new Circle(150,50,25, Paint.valueOf("58b6a6"));
        Circle dot2 = new Circle(300, 50, 25, Paint.valueOf("58b6a6"));
        Circle dot3 = new Circle(450, 50, 25, Paint.valueOf("58b6a6"));
        Circle dot4 = new Circle(225, 150, 25, Paint.valueOf("daf4f0"));
        Circle dot5 = new Circle(362.5, 250, 25, Paint.valueOf("b6dcdd"));
        Circle dot6 = new Circle(362.5, 350, 25, Paint.valueOf("58b6a6"));
        Circle dot7 = new Circle(450, 150, 25, Paint.valueOf("1aa780"));

        Line line1 = new Line(150, 50, 225, 150);
        Line line2 = new Line(300,50, 225, 150);
        Line line3 = new Line(225, 150, 362.5, 250);
        Line line4 = new Line(450, 50, 450, 150);
        Line line5 = new Line(362.5, 250, 362.50, 350);
        Line line6 = new Line(450, 150, 362.5, 250);

        treePane.getChildren().addAll(line1, line2, line3, line4, line5, line6, dot1, dot2, dot3, dot4, dot5, dot6, dot7);

        Pane pane1 = new Pane(treePane);
        pane1.setStyle("-fx-background-color: #AFEEEE");
        Pane pane2 = new Pane(pane1);
//        pane2.setStyle("-fx-background-color: white");
        pane2.setStyle("-fx-background-color: #AFEEEE");
        Pane pane3 = new Pane(pane2);
//        pane3.setStyle("-fx-background-color: white");
        pane3.setStyle("-fx-background-color: #AFEEEE");



        Text title0 = new Text("SLAPP");
        title0.setFont(Font.font("Noto Serif Combo", FontWeight.BOLD, FontPosture.ITALIC, 48));
        title0.setStroke(Color.BLACK);
        title0.setStrokeWidth(1.5);
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.2, Color.WHITE), new Stop(0.8, Color.BLUEVIOLET));
        title0.setFill(gradient);
        Glow g = new Glow();
        g.setLevel(1.0);
        title0.setEffect(g);

        Text title1 = new Text("Symbolic Logic APPlication");
        title1.setFont(Font.font("Noto Serif Combo", FontWeight.BOLD, 36));

        Text title2 = new Text("SLAPP editor, v1.0\u03b1");
        title2.setFont(Font.font("Noto Serif Combo",20));
        VBox titleBox = new VBox(title0, title1, title2);
        titleBox.setPadding(new Insets(10, 20, 10, 20));
        titleBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(30, pane3, titleBox);
//        root.setStyle("-fx-background-color: white");
        root.setStyle("-fx-background-color: #AFEEEE");
        root.setPrefWidth(600);
        root.setPrefHeight(600);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("SLAPP Front Animation");
        stage.show();

        RotateTransition rt = new RotateTransition(Duration.seconds(5), treePane);
        rt.setFromAngle(0.0);
        rt.setToAngle(360.0);
        rt.setCycleCount(1);
        rt.setAutoReverse(false);
        rt.setAxis(Rotate.X_AXIS);

        RotateTransition rt1 = new RotateTransition(Duration.seconds(5), pane1);
        rt1.setFromAngle(0.0);
        rt1.setToAngle(360.0);
        rt1.setCycleCount(1);
        rt1.setAutoReverse(false);
        rt1.setAxis(Rotate.Y_AXIS);

        RotateTransition rt2 = new RotateTransition(Duration.seconds(5), pane2);
        rt2.setFromAngle(0.0);
        rt2.setToAngle(360.0);
        rt2.setCycleCount(1);
        rt2.setAutoReverse(false);
        rt2.setAxis(Rotate.Z_AXIS);

        ScaleTransition sc = new ScaleTransition(Duration.seconds(5), pane3);
        sc.setFromX(0.0);
        sc.setToX(1.0);
        sc.setFromY(0.0);
        sc.setToY(1.0);
        sc.setFromZ(0.0);
        sc.setToZ(1.0);
        sc.setCycleCount(1);
        sc.setAutoReverse(false);

        FadeTransition fd = new FadeTransition(Duration.seconds(5), titleBox);
        fd.setFromValue(0);
        fd.setToValue(1.0);
        fd.setInterpolator(Interpolator.EASE_IN);

        ParallelTransition p = new ParallelTransition(rt, rt1, rt2, sc, fd);
        p.play();


    }
}