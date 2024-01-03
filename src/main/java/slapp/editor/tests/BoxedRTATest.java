package slapp.editor.tests;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoxedRTATest {

    BoxedDRTA boxedDRTA1;

    BoxedDRTA boxedDRTA2;

    public void testBoxedDRTA(Stage primaryStage) {


        boxedDRTA1 = new BoxedDRTA();

        RichTextArea rta1 = boxedDRTA1.getDrta().getEditor();
        rta1.setMaxHeight(27);
        rta1.setMinHeight(27);
        rta1.setPrefWidth(200);
        rta1.setContentAreaWidth(500);
        rta1.getStylesheets().add("RichTextField.css");
        rta1.setPromptText("Formula");

        boxedDRTA2 = new BoxedDRTA();

        RichTextArea rta2 = boxedDRTA2.getDrta().getEditor();
        rta2.setMaxHeight(27);
        rta2.setMinHeight(27);
        rta2.setPrefWidth(200);
        rta2.setContentAreaWidth(500);
        rta2.getStylesheets().add("RichTextField.css");
        rta2.setPromptText("Formula");





        Button button = new Button("dud");
        button.setPadding(new Insets(40));

        VBox pane = new VBox(boxedDRTA1.getBoxedRTA(), boxedDRTA2.getBoxedRTA(), button);


        pane.setPrefWidth(500);
        pane.setPrefHeight(500);
        pane.setAlignment(Pos.CENTER);


        EventHandler<KeyEvent> keyEventHandler1 = e -> {

            if (e.getCode() == KeyCode.TAB) {
                e.consume();
                button.requestFocus();
            }
        };
        pane.addEventFilter(KeyEvent.ANY, keyEventHandler1);

        /*
        EventHandler<KeyEvent> keyEventHandler2 = e -> {

            if (e.getCode() == KeyCode.TAB) {
                e.consume();
                button.requestFocus();
            }
        };

        pane.addEventFilter(KeyEvent.ANY, keyEventHandler2);


         */



        Scene scene = new Scene(pane);



        primaryStage.setScene(scene);
        primaryStage.setTitle("BoxedDRTA Test");
        primaryStage.show();
    }

}
