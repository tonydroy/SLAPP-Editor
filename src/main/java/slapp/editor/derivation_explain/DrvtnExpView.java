package slapp.editor.derivation_explain;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.derivation.LineType;
import slapp.editor.derivation.ViewLine;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;

public class DrvtnExpView implements ExerciseView<DecoratedRTA> {
    MainWindowView mainView;
    private String exerciseName = new String("");
    private RichTextAreaSkin.KeyMapValue keyboardSelector;
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private String contentPrompt = new String("");
    private DecoratedRTA explanationDRTA = new DecoratedRTA();
    private boolean isLeftmostScopeLine = true;
    private Node exerciseControlNode = new VBox();
    private SplitPane contentSplitPane = new SplitPane();
    private GridPane grid = new GridPane();
    private VBox contentBox = new VBox();
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
    private double contentRowHeight = 19.0;
    private double shelfRowHeight = 5.0;
    private double gapRowHeight = 7.0;


    public DrvtnExpView(MainWindowView mainView) {
        this.mainView = mainView;
        Pane blankPane = new Pane();
        blankPane.setMinWidth(0);
        blankPane.setMaxWidth(1000);
        blankPane.setStyle("-fx-background-color: white;");
        contentSplitPane.getItems().addAll(grid, blankPane);
        contentSplitPane.setOrientation(Orientation.HORIZONTAL);
        contentSplitPane.setMinHeight(10.0);
        contentSplitPane.setMinWidth(10.0);


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
        grid.setMinHeight(10);

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
        controlBox.setPadding(new Insets(40,20,30,40));
        exerciseControlNode = controlBox;


    }

    public void initializeViewDetails() {
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.setPrefHeight(statementPrefHeight);
        statementRTA.setMinHeight(statementPrefHeight);
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPrefHeight(70.0);
        commentRTA.setMinHeight(70.0);
        commentRTA.setPromptText("Comment:");


        RichTextArea explanationRTA = explanationDRTA.getEditor();
        explanationRTA.getStylesheets().add("slappTextArea.css");
        explanationRTA.setPrefHeight(150.0);
        explanationRTA.setMinHeight(150.0);
        explanationRTA.setPromptText(contentPrompt);

        contentBox.getChildren().addAll(contentSplitPane, explanationDRTA.getEditor());
    }

    public void setGridFromViewLines() {
        grid.getChildren().clear();
        ObservableList<RowConstraints> gridRowConstraints = grid.getRowConstraints();
        int lineNumber = 1;
        for (int index = 0; index < viewLines.size(); index++) {
            ViewLine viewLine = viewLines.get(index);
            LineType lineType = viewLine.getLineType();

            RowConstraints constraint = new RowConstraints();
            if (LineType.isContentLine(lineType)) { constraint.setMinHeight(contentRowHeight); constraint.setMaxHeight(contentRowHeight); }
            if (LineType.isGapLine(lineType)) {constraint.setMinHeight(gapRowHeight); constraint.setMaxHeight(gapRowHeight); }
            if (LineType.isShelfLine(lineType)) {constraint.setMinHeight(shelfRowHeight); constraint.setMaxHeight(shelfRowHeight); }

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
            if (viewLine.getLineContentBoxedDRTA() != null) {
                BoxedDRTA bdrta = viewLine.getLineContentBoxedDRTA();
                DecoratedRTA drta = bdrta.getDRTA();
                drta.getKeyboardSelector().valueProperty().setValue(keyboardSelector);
                RichTextArea rta = bdrta.getRTA();
                rta.setMaxHeight(contentRowHeight);
                rta.setMinHeight(contentRowHeight);
                rta.setPrefWidth(100);
                rta.getStylesheets().add("slappDerivation.css");

                contentBox = bdrta.getBoxedRTA();
                contentBox.setHgrow(bdrta.getRTA(), Priority.ALWAYS);
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

                grid.add(contentBox, depth, index, 21 - depth, 1);

            } else {
                Pane endSpacer = new Pane();
                if (LineType.isGapLine(lineType)) endSpacer.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 0;");
                if (LineType.isShelfLine(lineType) && (depth > 1 || isLeftmostScopeLine))  endSpacer.setStyle("-fx-border-color: black; -fx-border-width: 1 0 0 1;");
                grid.add(endSpacer, depth, index, 1, 1);
            }

            if (viewLine.getJustificationFlow() != null) grid.add(viewLine.getJustificationFlow(), 22, index);
        }
        Platform.runLater(() -> {
            mainView.updateContentHeightProperty();
        });
    }

    public double getGridHeight() {
        double height = 0;
        for (ViewLine line : viewLines) {
            if (LineType.isContentLine(line.getLineType())) height = height + contentRowHeight;
            else if (LineType.isGapLine(line.getLineType())) height = height + gapRowHeight;
            else if (LineType.isShelfLine(line.getLineType())) height = height + shelfRowHeight;
        }
        return height;
    }

    public GridPane getGrid() { return grid; }
    public void setLeftmostScopeLine(boolean leftmostScopeLine) { isLeftmostScopeLine = leftmostScopeLine;  }
    public void setKeyboardSelector(RichTextAreaSkin.KeyMapValue keyboardSelector) {this.keyboardSelector = keyboardSelector;}
    public List<ViewLine> getViewLines() { return viewLines; }
    public void setViewLines(List<ViewLine> viewLines) {this.viewLines = viewLines; }
    public Button getInsertLineButton() { return insertLineButton;  }

    public Button getDeleteLineButton() { return deleteLineButton; }

    public Button getIndentButton() { return indentButton; }

    public Button getOutdentButton() { return outdentButton; }

    public Button getAddShelfButton() { return addShelfButton; }

    public Button getAddGapButton() { return addGapButton; }

    public Button getInsertSubButton() { return insertSubButton;  }

    public Button getInsertSubsButton() { return insertSubsButton; }

    public Button getUndoButton() { return undoButton;  }

    public Button getRedoButton() {  return redoButton; }

    public SplitPane getSplitPane() {return contentSplitPane; }

    public DecoratedRTA getExplanationDRTA() { return explanationDRTA;  }

    public void setExplanationDRTA(DecoratedRTA explanationDRTA) { this.explanationDRTA = explanationDRTA;  }

    public String getContentPrompt() {
        return contentPrompt;
    }

    @Override
    public String getExerciseName() { return exerciseName; }
    @Override
    public void setExerciseName(String name) { this.exerciseName = name; }
    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment; }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public double getCommentHeight() { return exerciseComment.getEditor().getHeight(); }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) { this.exerciseStatement = exerciseStatement; }
    @Override
    public Node getExerciseStatementNode() { return exerciseStatement.getEditor(); }
    @Override
    public double getStatementHeight() { return exerciseStatement.getEditor().getHeight();    }
    @Override
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
 //       exerciseStatement.getEditor().setMinHeight(height);
    }

    public VBox getExerciseContent() { return contentBox; }

    public void setExerciseContent(VBox contentBox) { this.contentBox = contentBox; }
    @Override
    public Node getExerciseContentNode() { return contentBox; }

    public void setContentPrompt(String prompt) { contentPrompt = prompt; }
    @Override
    public DoubleProperty getContentHeightProperty() { return grid.prefHeightProperty(); }
    @Override
    public DoubleProperty getContentWidthProperty() {return grid.prefWidthProperty(); }
    @Override
    public double getContentFixedHeight() { return explanationDRTA.getEditor().getHeight() - 50; }
    @Override
    public Node getExerciseControl() { return exerciseControlNode; }
    @Override
    public double getContentWidth() { return 200.0; }
    @Override
    public double getContentHeight() { return getGridHeight() + 40; }
}
