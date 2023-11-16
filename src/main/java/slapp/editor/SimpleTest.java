package slapp.editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SimpleTest {
    GridPane grid = new GridPane();
    RowConstraints baseR = new RowConstraints(20);
    RowConstraints thinR = new RowConstraints(5);

    RowConstraints gapR = new RowConstraints(7);


    void testGrid(Stage primaryStage) {
        ColumnConstraints fixedCol = new ColumnConstraints();
        fixedCol.setMinWidth(10);
        ColumnConstraints growCol = new ColumnConstraints();
        growCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, growCol);
        grid.setPadding(new Insets(20));



        setContentRow(0, 1, 1, new RichTextArea(EditorMain.mainStage), new Label("P"));
        setThinShelfRow(1, 1);
        setContentRow(2, 2, 2, new Label("B"), new Label("A (g, \u2192I)"));
        setThinShelfRow(3, 2);
        setContentRow(4,3, 3, new Label("C"), new Label("A (g, \u2192I)"));
        setThinShelfRow(5, 3);
        setContentRow(6, 3, 4, new Label("A"), new Label("1 R"));
        setContentRow(7, 2, 5, new Label("C \u2192 A"), new Label("3-4 \u2192I"));
        setContentRow(8, 1, 6, new Label("B \u2192 (C \u2192 A)"), new Label("2-5 \u2192I"));


        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(100);
        primaryStage.show();
    }

    private void setContentRow(int rowIndex, int depth, int lineNum, Node contentNode, Node justificationNode) {
        ObservableList<RowConstraints> rowConstraints = grid.getRowConstraints();
        if (rowIndex >= rowConstraints.size()) rowConstraints.add(rowIndex, baseR);
        else rowConstraints.set(rowIndex, baseR);

        Label numLabel = new Label(String.valueOf(lineNum) + ". ");
        grid.add(numLabel, 0, rowIndex, 1, 1);
        grid.setHalignment(numLabel, HPos.RIGHT);

        for (int i = 1; i < depth; i++) {
            Pane spacer = new Pane();
            spacer.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1;");
            grid.add(spacer, i, rowIndex, 1, 1);
        }

        Pane contentPane = new Pane(contentNode);
//        ((Label) contentNode).setPadding(new Insets(0,0,0,3));    ////
        contentPane.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1;");
        grid.add(contentPane, depth, rowIndex, 20 - depth, 1);


        Pane justificationPane = new Pane(justificationNode);

 //       ((Label) contentNode).setPadding(new Insets(0,0,0,3));
        grid.add(justificationPane, 22, rowIndex);
    }

    private void setThinRow(int rowIndex, int depth) {
        ObservableList<RowConstraints> rowConstraints = grid.getRowConstraints();
        if (rowIndex >= rowConstraints.size()) rowConstraints.add(rowIndex, gapR);
        else rowConstraints.set(rowIndex, gapR);

        Pane noNum = new Pane();
        grid.add(noNum, 0, rowIndex, 1, 1);
        for (int i = 1; i <= depth; i++) {
            Pane spacer = new Pane();
            spacer.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1;");
            grid.add(spacer, i, rowIndex, 1, 1);
        }
    }

    private void setThinShelfRow(int rowIndex, int depth) {
        ObservableList<RowConstraints> rowConstraints = grid.getRowConstraints();
        if (rowIndex >= rowConstraints.size()) rowConstraints.add(rowIndex, thinR);
        else rowConstraints.set(rowIndex, thinR);

        Pane noNum = new Pane();
        grid.add(noNum, 0, rowIndex, 1, 1);
        for (int i = 1; i < depth; i++) {
            Pane spacer = new Pane();
            spacer.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1;");
            grid.add(spacer, i, rowIndex, 1, 1);
        }
        Pane shelf = new Pane();
        shelf.setStyle("-fx-border-color: black; -fx-border-width: 1 0 0 1;");
        grid.add(shelf, depth, rowIndex, 1, 1);
    }

}
