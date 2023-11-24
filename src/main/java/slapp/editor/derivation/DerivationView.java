package slapp.editor.derivation;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;

public class DerivationView implements ExerciseView<DecoratedRTA, SplitPane> {

    MainWindowView mainView;
    private String exerciseName = new String("");

    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private String contentPrompt = new String("");
    private boolean isLeftmostScopeLine = true;



    private Node exerciseControlNode = new VBox();
    private SplitPane exerciseContent = new SplitPane();
    private GridPane grid = new GridPane();
    private List<ViewLine> viewLines = new ArrayList<>();

    private Button insertButton;
    private Button deleteButton;




    public DerivationView(MainWindowView mainView) {
        this.mainView = mainView;
        Pane blankPane = new Pane();
        blankPane.setMinWidth(0);
        blankPane.setMaxWidth(1000);
        blankPane.setStyle("-fx-background-color: white;");
        exerciseContent.getItems().addAll(grid, blankPane);
        exerciseContent.setOrientation(Orientation.HORIZONTAL);

        ColumnConstraints fixedCol = new ColumnConstraints();
        fixedCol.setMinWidth(10);
        ColumnConstraints numCol = new ColumnConstraints();
        numCol.setMinWidth(20);
        ColumnConstraints justCol = new ColumnConstraints();
        justCol.setMinWidth(100);
        ColumnConstraints growCol = new ColumnConstraints();
        growCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(numCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, fixedCol, growCol, fixedCol, justCol);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white;");
        grid.setMinWidth(350);




 //       grid.setGridLinesVisible(true);

        //temp
        insertButton = new Button("Insert");
        deleteButton = new Button("Delete");

        VBox controlBox = new VBox(40, insertButton, deleteButton);
        controlBox.setAlignment(Pos.CENTER);
        exerciseControlNode = controlBox;
        //


        //create other members of view
    }

    //temp
    public Button getInsertButton() {return insertButton;}
    public Button getDeleteButton() {return deleteButton;}
    //

    public void initializeViewDetails() {
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.setPrefHeight(statementPrefHeight);
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPrefHeight(70.0);
        commentRTA.setPromptText("Comment:");
    }

    public void setGridFromViewLines() {
        grid.getChildren().clear();
        ObservableList<RowConstraints> gridRowConstraints = grid.getRowConstraints();
        int lineNumber = 1;
        for (int index = 0; index < viewLines.size(); index++) {
            ViewLine viewLine = viewLines.get(index);
            LineType lineType = viewLine.getLineType();

            RowConstraints constraint = new RowConstraints();
            if (lineType == LineType.CONTENT_LINE) constraint.setPrefHeight(21);
            if (lineType == LineType.GAP_LINE) constraint.setPrefHeight(7);
            if (lineType == LineType.SHELF_LINE) constraint.setPrefHeight(5);

            if (index >= gridRowConstraints.size()) gridRowConstraints.add(index, constraint);
            else gridRowConstraints.set(index, constraint);

            Label lineNumberLabel = viewLine.getLineNumberLabel();
            if (lineNumberLabel != null) {
                lineNumberLabel.setText(Integer.toString(lineNumber++));
                HBox numBox = new HBox(lineNumberLabel, new Label(". "));
                numBox.setAlignment(Pos.BASELINE_RIGHT);
                grid.add(numBox, 0, index, 1, 1);
            }

            int depth = viewLine.getDepth();
            HBox contentBox = new HBox();
            if (viewLine.getLineContentDRTA() != null) {
                DecoratedRTA drta = viewLine.getLineContentDRTA();
                drta.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
                RichTextArea rta = drta.getEditor();
  //              rta.setMaxHeight(25);
                rta.setPrefHeight(20);
                rta.setPrefWidth(100);
                rta.getStylesheets().add("slappDerivation.css");

 //               rta.setStyle("-fx-border-color: green; -fx-border-width: 1 1 1 1");

                contentBox.getChildren().add(rta);


                contentBox.setHgrow(rta, Priority.ALWAYS);
            }
            if (depth > 1) {
                Pane spacer1 = new Pane();
                if (isLeftmostScopeLine) spacer1.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1;");
                else spacer1.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 0;");
                grid.add(spacer1, 1, index, 1, 1);

                for (int i = 2; i < depth; i++) {
                    Pane spacer = new Pane();
                    spacer.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1;");
                    grid.add(spacer, i, index, 1, 1);
                }
            }
            if (lineType == LineType.CONTENT_LINE) {
                if (isLeftmostScopeLine || depth > 1)
                    contentBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1;");
                else contentBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 0;");
                grid.add(contentBox, depth, index, 21 - depth, 1);




            } else {
                Pane endSpacer = new Pane();
                if (lineType == LineType.GAP_LINE) endSpacer.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 0;");
                if (lineType == LineType.SHELF_LINE && (depth > 1 || isLeftmostScopeLine))  endSpacer.setStyle("-fx-border-color: black; -fx-border-width: 1 0 0 1;");
                grid.add(endSpacer, depth, index, 1, 1);
            }

            if (viewLine.getJustificationFlow() != null) grid.add(viewLine.getJustificationFlow(), 22, index);
        }
        /*
        Scene tempScene = new Scene(grid);
        Stage tempStage = new Stage();
        tempStage.setScene(tempScene);
        tempStage.initModality(Modality.NONE);
        tempStage.show();

         */
    }

    public GridPane getGrid() { return grid; }

    public boolean isLeftmostScopeLine() {
        return isLeftmostScopeLine;
    }

    public void setLeftmostScopeLine(boolean leftmostScopeLine) {
        isLeftmostScopeLine = leftmostScopeLine;
    }

    public List<ViewLine> getViewLines() { return viewLines; }
    public void setViewLines(List<ViewLine> viewLines) {this.viewLines = viewLines; }
    @Override
    public String getExerciseName() { return exerciseName; }
    @Override
    public void setExerciseName(String name) { this.exerciseName = name; }
    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment; }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public double getCommentHeight() {
        return 50;
     //   return exerciseComment.getEditor().getHeight();
    }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) { this.exerciseStatement = exerciseStatement; }
    @Override
    public Node getExerciseStatementNode() { return exerciseStatement.getEditor(); }
    @Override
    public double getStatementHeight() {
        return 80;
        //return exerciseStatement.getEditor().getHeight();
        }
    @Override
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
    }
    @Override
    public SplitPane getExerciseContent() { return exerciseContent; }
    @Override
    public void setExerciseContent(SplitPane exerciseContent) { this.exerciseContent = exerciseContent; }
    @Override
    public Node getExerciseContentNode() { return exerciseContent; }
    @Override
    public void setContentPrompt(String prompt) { contentPrompt = prompt; }
    @Override
    public DoubleProperty getContentHeightProperty() { return grid.prefHeightProperty(); }
    @Override
    public double getContentFixedHeight() { return 0.0; }
    @Override
    public Node getExerciseControl() { return exerciseControlNode; }

}
