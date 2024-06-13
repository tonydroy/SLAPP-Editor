package slapp.editor.tests;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ClipTest {

    public void testClip(Stage primaryStage) {

        Pane pane = new Pane();
        Rectangle clipRec = new Rectangle();
        clipRec.setHeight(18);
        clipRec.widthProperty().bind(pane.widthProperty());
        NumberAxis axis = createAxis();
        HBox axisBox = new HBox(axis);
        axisBox.setPadding(new Insets(0,0,0,5));
        axisBox.setClip(clipRec);
        pane.getChildren().add(axisBox);

        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Clip Test");
        primaryStage.setWidth(500);
        primaryStage.setHeight(500);
        primaryStage.show();
    }

    NumberAxis createAxis() {
        NumberAxis axis = new NumberAxis(0,150,5);
        axis.setPrefWidth(3000);
        axis.setMinWidth(Control.USE_PREF_SIZE);
        axis.setMaxWidth(Control.USE_PREF_SIZE);
        axis.tickLabelFontProperty().set(Font.font(8));
        axis.setTickLength(6);
        return axis;
    }

}
