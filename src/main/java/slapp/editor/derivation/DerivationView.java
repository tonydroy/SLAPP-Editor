/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

package slapp.editor.derivation;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;

/**
 * View for the derivation exercise
 */
public class DerivationView implements ExerciseView<DecoratedRTA> {
    MainWindowView mainView;
    private RichTextAreaSkin.KeyMapValue keyboardSelector;
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 0;
    private double commentPrefHeight = 0;
    private double splitPanePrefWidth = 0;
    private Spinner<Double> statementHeightSpinner;
    private Spinner<Double> statementWidthSpinner;
    private Spinner<Double> commentHeightSpinner;
    private Spinner<Double> commentWidthSpinner;
    private Spinner<Double> splitPaneHeightSpinner;
    private Spinner<Double> splitPaneWidthSpinner;
    private Node currentSpinnerNode;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private boolean isLeftmostScopeLine = true;
    private Node exerciseControlNode = new VBox();
    private SplitPane contentSplitPane = new SplitPane();
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
    private double contentRowHeight = 19.0;
    private double shelfRowHeight = 5.0;
    private double gapRowHeight = 7.0;
    private DoubleProperty windowHeightProperty;


    /**
     * Construct the derivation view
     * @param mainView the main view
     */
    public DerivationView(MainWindowView mainView) {
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

        insertLineButton.setPrefWidth(100);
        insertLineButton.setTooltip(new Tooltip("Insert blank line above current one"));
        deleteLineButton.setPrefWidth(100);
        deleteLineButton.setTooltip(new Tooltip("Delete current line"));
        indentButton.setPrefWidth(100);
        indentButton.setTooltip(new Tooltip("Increase depth of current line"));
        outdentButton.setPrefWidth(100);
        outdentButton.setTooltip(new Tooltip("Decrease depth of current line"));
        addShelfButton.setPrefWidth(100);
        addShelfButton.setTooltip(new Tooltip("Add shelf beneath current line"));
        addGapButton.setPrefWidth(100);
        addGapButton.setTooltip(new Tooltip("Add gap beneath current line"));
        insertSubButton.setPrefWidth(100);
        insertSubButton.setTooltip(new Tooltip("Insert subderivation above current line"));
        insertSubsButton.setPrefWidth(100);
        insertSubsButton.setTooltip(new Tooltip("Insert subderivation pair above current line"));
        undoButton.setPrefWidth(100);
        undoButton.setTooltip(new Tooltip("Undo last button action"));
        redoButton.setPrefWidth(100);
        redoButton.setTooltip(new Tooltip("Redo button action"));

        VBox controlBox = new VBox(20, undoButton, redoButton, insertLineButton, deleteLineButton, insertSubButton, insertSubsButton, indentButton, outdentButton, addShelfButton, addGapButton);
        controlBox.setAlignment(Pos.BASELINE_RIGHT);
        controlBox.setMargin(insertLineButton, new Insets(0,0,20, 0));
        controlBox.setPadding(new Insets(40,20,0,20));
        controlBox.setMinWidth(150); controlBox.setMaxWidth(150);
        exerciseControlNode = controlBox;
    }

    /**
     * Initialize statement, comment and split pane
     */
    void initializeViewDetails() {

        //statement
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        double statementInitialHeight = Math.round(statementPrefHeight / mainView.getScalePageHeight() * 100.0 );
        statementHeightSpinner = new Spinner<>(0.0, 999.0, statementInitialHeight, 1.0);
        statementHeightSpinner.setPrefWidth(65);
        statementHeightSpinner.setDisable(false);
        statementHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        statementHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = statementHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = statementHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        statementRTA.maxWidthProperty().bind(mainView.scalePageWidthProperty());
        statementWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        statementWidthSpinner.setPrefWidth(65);
        statementWidthSpinner.setDisable(true);
        statementWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        statementRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != statementRTA) {
                currentSpinnerNode = statementRTA;
                mainView.updateSizeSpinners(statementHeightSpinner, statementWidthSpinner);
            }
        });

        //comment
        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPromptText("Comment:");

        double commentInitialHeight = Math.round(commentPrefHeight / mainView.getScalePageHeight() * 100.0 );
        commentHeightSpinner = new Spinner<>(0.0, 999.0, commentInitialHeight, 1.0);
        commentHeightSpinner.setPrefWidth(65);
        commentHeightSpinner.setDisable(false);
        commentHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        commentHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = commentHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = commentHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        commentRTA.maxWidthProperty().bind(mainView.scalePageWidthProperty());
        commentRTA.minWidthProperty().bind(mainView.scalePageWidthProperty());

        commentWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        commentWidthSpinner.setPrefWidth(65);
        commentWidthSpinner.setDisable(true);
        commentWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        commentRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != commentRTA) {
                currentSpinnerNode = commentRTA;
                mainView.updateSizeSpinners(commentHeightSpinner, commentWidthSpinner);
            }
        });

        //split pane
        double splitPaneInitialWidth = Math.round(splitPanePrefWidth / mainView.getScalePageWidth() * 20.0) * 5.0;
        splitPaneWidthSpinner = new Spinner<>(70.0, 999.0, splitPaneInitialWidth, 5.0);
        splitPaneWidthSpinner.setPrefWidth(65);
        splitPaneWidthSpinner.setDisable(false);
        splitPaneWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));
        contentSplitPane.prefWidthProperty().bind(Bindings.multiply(mainView.scalePageWidthProperty(), DoubleProperty.doubleProperty(splitPaneWidthSpinner.getValueFactory().valueProperty()).divide(100.0)));

        splitPaneWidthSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = splitPaneWidthSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = splitPaneWidthSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        contentSplitPane.setMinHeight(200);
        splitPaneHeightSpinner = new Spinner<>(0.0,999.0, 0,1.0);
        splitPaneHeightSpinner.setPrefWidth(65);
        splitPaneHeightSpinner.setDisable(true);
        splitPaneHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));

        contentSplitPane.heightProperty().addListener((ob, ov, nv) -> {
            splitPaneHeightSpinner.getValueFactory().setValue((double) Math.round(contentSplitPane.getHeight() / mainView.getScalePageHeight() * 100));
        });

        contentSplitPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
//            if (currentSpinnerNode != contentSplitPane) {
                currentSpinnerNode = contentSplitPane;
                splitPaneHeightSpinner.getValueFactory().setValue((double) Math.round(contentSplitPane.getHeight()/mainView.getScalePageHeight() * 100.0));
                mainView.updateSizeSpinners(splitPaneHeightSpinner, splitPaneWidthSpinner);
 //           }
        });

        //page size listeners
        mainView.scalePageHeightProperty().addListener((ob, ov, nv) -> {

            statementRTA.prefHeightProperty().unbind();
            statementHeightSpinner.getValueFactory().setValue((double) Math.round(statementHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            commentRTA.prefHeightProperty().unbind();
            commentHeightSpinner.getValueFactory().setValue((double) Math.round(commentHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            splitPaneHeightSpinner.getValueFactory().setValue((double) Math.round(contentSplitPane.getHeight() / mainView.getScalePageHeight() * 100.0));
        });

        mainView.scalePageWidthProperty().addListener((ob, ov, nv) -> {
            contentSplitPane.prefWidthProperty().unbind();
            splitPaneWidthSpinner.getValueFactory().setValue((double) Math.round(splitPaneWidthSpinner.getValue() * ov.doubleValue() / nv.doubleValue() / 5.0) * 5.0);
            contentSplitPane.prefWidthProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(splitPaneWidthSpinner.getValueFactory().valueProperty()).divide(100.0))));
        });

    }

    void setGridFromViewLines() {

        grid.getChildren().clear();
        grid.getRowConstraints().clear();
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
                if (isLeftmostScopeLine) spacer1.setStyle("-fx-border-color: white white white black; -fx-border-width: 0 0 0 1");
                else spacer1.setStyle("-fx-border-color: white white white white; -fx-border-width: 0 0 0 0;");
                grid.add(spacer1, 1, index, 1, 1);

                for (int i = 2; i < depth; i++) {
                    Pane spacer = new Pane();
                    spacer.setStyle("-fx-border-color: white white white black; -fx-border-width: 0 0 0 1;");
                    grid.add(spacer, i, index, 1, 1);
                }
            }
            if (LineType.isContentLine(lineType)) {


                if (isLeftmostScopeLine || depth > 1)
                    contentBox.setStyle("-fx-border-color: white, white, white, black; -fx-border-width: 0 0 0 1;");
                else contentBox.setStyle("-fx-border-color: white white white white; -fx-border-width: 0 0 0 0;");

                grid.add(contentBox, depth, index, 21 - depth, 1);

            } else {
                Pane endSpacer = new Pane();
                if (LineType.isGapLine(lineType)) endSpacer.setStyle("-fx-border-color: white white white white; -fx-border-width: 0 0 0 0;");
                if (LineType.isShelfLine(lineType) && (depth > 1 || isLeftmostScopeLine))  endSpacer.setStyle("-fx-border-color: black white white black; -fx-border-width: 1 0 0 1;");
                grid.add(endSpacer, depth, index, 1, 1);
            }

            if (viewLine.getJustificationFlow() != null) grid.add(viewLine.getJustificationFlow(), 22, index);
        }
    }

    void setJustificationFlowOnGrid(int index) {
        ViewLine line = viewLines.get(index);
        if (line.getJustificationFlow() != null) grid.add(line.getJustificationFlow(), 22, index);

    }

    GridPane getGrid() { return grid; }
    void setLeftmostScopeLine(boolean leftmostScopeLine) { isLeftmostScopeLine = leftmostScopeLine;  }
    void setKeyboardSelector(RichTextAreaSkin.KeyMapValue keyboardSelector) {this.keyboardSelector = keyboardSelector;}
    List<ViewLine> getViewLines() { return viewLines; }
    void setViewLines(List<ViewLine> viewLines) {this.viewLines = viewLines; }
    SplitPane getContentSplitPane() { return contentSplitPane; }

    void setCommentPrefHeight(double commentPrefHeight) {   this.commentPrefHeight = commentPrefHeight;  }
    double getCommentPrefHeight() { return exerciseComment.getEditor().getPrefHeight(); }
    double getSplitPanePrefWidth() {return contentSplitPane.getPrefWidth(); }
    void setSplitPanePrefWidth(double splitPanePrefWidth) { this.splitPanePrefWidth = splitPanePrefWidth; }

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


    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment; }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) { this.exerciseStatement = exerciseStatement; }
    @Override
    public Node getExerciseStatementNode() { return exerciseStatement.getEditor(); }
    @Override
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
    }
    @Override
    public Node getExerciseContentNode() { return new VBox(contentSplitPane); }

    @Override
    public Node getExerciseControl() { return exerciseControlNode; }
    @Override
    public Node getRightControl() { return null; }


}
