package slapp.editor.tests;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import slapp.editor.decorated_rta.BoxedDRTA;

public class TextWidthTest {

    public void testTextWidth(Stage primaryStage) {

        Text text = new Text("test a bigger line");

        TextFlow flow = new TextFlow(text);
        flow.setPrefWidth(text.getBoundsInLocal().getWidth() + 2);
        flow.setStyle("-fx-border-color: blue");


        HBox flowBox = new HBox(flow);
        flowBox.setAlignment(Pos.CENTER);

        VBox pane = new VBox(flowBox);


        pane.setPrefWidth(500);
        pane.setPrefHeight(500);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane);



        primaryStage.setScene(scene);
        primaryStage.setTitle("BoxedDRTA Test");
        primaryStage.show();
    }


}
