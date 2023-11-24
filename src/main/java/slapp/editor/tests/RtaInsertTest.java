package slapp.editor.tests;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slapp.editor.decorated_rta.DecoratedRTA;

public class RtaInsertTest {
    Document document;

    public void rtaInsertTest(Stage primaryStage) {

        DecoratedRTA drta = new DecoratedRTA();


        ToolBar editToolbar = drta.getEditToolbar();
        ToolBar fontsToolbar = drta.getFontsToolbar();
        ToolBar paragraphToolbar = drta.getParagraphToolbar();

        VBox topBox = new VBox(10, editToolbar, fontsToolbar, paragraphToolbar);

        RichTextArea rta = drta.getEditor();

 //       RichTextArea rta = new RichTextArea(primaryStage);
    //    rta.setDocument(new Document("this is\na test"));

        Button button = new Button("dump");



        BorderPane pane = new BorderPane();
        //      pane.setTop(topBox);
        pane.setCenter(rta);
        rta.setStyle("-fx-border-color: green; -fx-border-width: 2;");

        rta.setPrefHeight(100);
        pane.setTop(drta.getEditToolbar());
        pane.setBottom(new Label());

        pane.setLeft(button);

        button.setOnAction(e -> {
            rta.getActionFactory().saveNow().execute(new ActionEvent());

            document = rta.getDocument();
            /*
            RichTextArea rta2 = new RichTextArea(primaryStage);
            rta2.setDocument(document);
            rta2.setPrefHeight(100);
            rta2.setStyle("-fx-border-color: blue; -fx-border-width: 2");
            pane.setBottom(rta2);

             */
            System.out.println(rta.getDocument().toString());
        });





        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(355);

        primaryStage.show();

    }

}


/* to put filter on rta -- seems not to comsume tab, but does for other keys??

rta.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
        if (e.getCode() == KeyCode.TAB || e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
        System.out.println("key caught");
        e.consume();
        }
        });

 */

