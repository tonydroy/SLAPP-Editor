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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorMain;
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

    private Button insertLineButton;
    private Button deleteLineButton;
    private Button indentButton;
    private Button outdentButton;
    private Button addShelfButton;
    private Button addGapButton;
    private Button insertSubButton;
    private Button insertSubsButton;
    private Button undoButton;
    private Button redoButton;




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
        insertLineButton = new Button("Insert Line");
        deleteLineButton = new Button("Delete Line");
        indentButton = new Button("Indent Line");
        outdentButton = new Button("Outdent Line");
        addShelfButton = new Button("Add Shelf");
        addGapButton = new Button("Add Gap");
        insertSubButton = new Button("Insert Subder");
        insertSubsButton = new Button("Insert Subders");
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");

        insertLineButton.setPrefWidth(95);
        insertLineButton.setTooltip(new Tooltip("Insert blank line above current one"));
        deleteLineButton.setPrefWidth(95);
        deleteLineButton.setTooltip(new Tooltip("Delete current line"));
        indentButton.setPrefWidth(95);
        indentButton.setTooltip(new Tooltip("Increase depth of current line"));
        outdentButton.setPrefWidth(95);
        outdentButton.setTooltip(new Tooltip("Decrease depth of current line"));
        addShelfButton.setPrefWidth(95);
        addShelfButton.setTooltip(new Tooltip("Add shelf beneath current line"));
        addGapButton.setPrefWidth(95);
        addGapButton.setTooltip(new Tooltip("Add gap beneath current line"));
        insertSubButton.setPrefWidth(95);
        insertSubButton.setTooltip(new Tooltip("Insert subderivation above current line"));
        insertSubsButton.setPrefWidth(95);
        insertSubsButton.setTooltip(new Tooltip("Insert subderivation pair above current line"));
        undoButton.setPrefWidth(95);
        undoButton.setTooltip(new Tooltip("Undo last button action"));

        redoButton.setPrefWidth(95);
        redoButton.setTooltip(new Tooltip("Redo button action"));



        VBox controlBox = new VBox(20, undoButton, redoButton, insertLineButton, deleteLineButton, indentButton, outdentButton, addShelfButton, addGapButton, insertSubButton, insertSubsButton);
        controlBox.setAlignment(Pos.BASELINE_RIGHT);
        controlBox.setMargin(insertLineButton, new Insets(0,0,20, 0));
        controlBox.setPadding(new Insets(40,10,30,80));
        exerciseControlNode = controlBox;
    }

    //temp

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

 //       RichTextArea lastRTA = new RichTextArea(EditorMain.mainStage);

        grid.getChildren().clear();
        ObservableList<RowConstraints> gridRowConstraints = grid.getRowConstraints();
        int lineNumber = 1;
        for (int index = 0; index < viewLines.size(); index++) {
            ViewLine viewLine = viewLines.get(index);
            LineType lineType = viewLine.getLineType();

            RowConstraints constraint = new RowConstraints();
            if (LineType.isContentLine(lineType)) { constraint.setMinHeight(19); constraint.setMaxHeight(19); }
            if (LineType.isGapLine(lineType)) {constraint.setMinHeight(7); constraint.setMaxHeight(7); }
            if (LineType.isShelfLine(lineType)) {constraint.setMinHeight(5); constraint.setMaxHeight(5); }

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
                rta.setMaxHeight(19);
                rta.setMinHeight(19);
       //         rta.setPrefHeight(22);
                rta.setPrefWidth(100);
                rta.getStylesheets().add("slappDerivation.css");

 //               rta.setFocusTraversable(false);

 //               rta.setStyle("-fx-border-color: green; -fx-border-width: 1 1 1 1");

                contentBox.getChildren().add(rta);

  //              lastRTA = rta;



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
            if (LineType.isContentLine(lineType)) {
                if (isLeftmostScopeLine || depth > 1)
                    contentBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1;");
                else contentBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 0;");


  //              contentBox.setFocusTraversable(false);
                grid.add(contentBox, depth, index, 21 - depth, 1);




            } else {
                Pane endSpacer = new Pane();
                if (LineType.isGapLine(lineType)) endSpacer.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 0;");
                if (LineType.isShelfLine(lineType) && (depth > 1 || isLeftmostScopeLine))  endSpacer.setStyle("-fx-border-color: black; -fx-border-width: 1 0 0 1;");
                grid.add(endSpacer, depth, index, 1, 1);
            }

            if (viewLine.getJustificationFlow() != null) grid.add(viewLine.getJustificationFlow(), 22, index);
        }

 //       lastRTA.requestFocus();
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



    public Button getInsertLineButton() {
        return insertLineButton;
    }

    public Button getDeleteLineButton() {
        return deleteLineButton;
    }

    public Button getIndentButton() {
        return indentButton;
    }

    public Button getOutdentButton() {
        return outdentButton;
    }

    public Button getAddShelfButton() {
        return addShelfButton;
    }

    public Button getAddGapButton() {
        return addGapButton;
    }

    public Button getInsertSubButton() {
        return insertSubButton;
    }

    public Button getInsertSubsButton() {
        return insertSubsButton;
    }

    public Button getUndoButton() { return undoButton;  }

    public Button getRedoButton() {  return redoButton; }

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
