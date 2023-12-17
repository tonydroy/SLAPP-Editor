package slapp.editor.tests;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.ExtractSubText;





public class ExtractFromDoc {
    Document document;
    private TextFlow textFlow = new TextFlow();


    public void rtaInsertTest(Stage primaryStage) {

        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea rta = drta.getEditor();
        Button button = new Button("dump");

        BorderPane pane = new BorderPane();

        pane.setCenter(rta);
        rta.setPrefHeight(100);
        pane.setTop(drta.getEditToolbar());

        HBox bottom = new HBox(new Label("front"), textFlow, new Label("back"));
        bottom.setAlignment(Pos.CENTER);

        pane.setBottom(bottom);

        pane.setLeft(button);
        button.setOnAction(e -> {
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            document = rta.getDocument();

            textFlow.getChildren().addAll(ExtractSubText.getTextFromDoc(2, 8, document ));



        });

        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(355);

        primaryStage.show();

    }

}

