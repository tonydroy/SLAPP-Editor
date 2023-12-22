package slapp.editor.tests;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.truth_table.ParseDocForTTable;
import slapp.editor.truth_table.TableHeadItem;

import java.util.List;

public class ParseTableDocTest {


    private Document document;
    private TextFlow textFlow = new TextFlow();


    public void parseTableDocTest(Stage primaryStage) {



        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea rta = drta.getEditor();
        Button button = new Button("dump");

        BorderPane pane = new BorderPane();

        pane.setCenter(rta);
        rta.setPrefHeight(100);
        pane.setTop(drta.getEditToolbar());


        GridPane grid = new GridPane();
        grid.setHgap(5.0);

        HBox bottom = new HBox(grid);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10));

        pane.setBottom(bottom);

        pane.setLeft(button);
        button.setOnAction(e -> {
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            document = rta.getDocument();
            ParseDocForTTable parser = new ParseDocForTTable(new char[]{(char) 0x223c}, new char[]{(char) 0x2227, (char) 0x2228, (char) 0x2192, (char) 0x2194}, document);

            List<TableHeadItem> headItems = parser.generateHeadItems(document);

            for (int i = 0; i < headItems.size(); i++) {
                TableHeadItem item = headItems.get(i);
                grid.getColumnConstraints().add(item.getColumnConstraints());
                System.out.println(item.getExpression().toString());
                grid.add(item.getExpression(), i, 0);
            }
        });

        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(355);

        primaryStage.show();

    }



}
