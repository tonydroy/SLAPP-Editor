package slapp.editor.truth_table;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import slapp.editor.EditorAlerts;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class TruthTableView implements ExerciseView<DecoratedRTA> {
    private MainWindowView mainView;
    private String exerciseName = new String();
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private DecoratedRTA explainDRTA = new DecoratedRTA();
    private GridPane basicFormulasPane;
    private List<BoxedDRTA> basicFormulasBoxedDRTAList;
    private VBox controlBox;
    private Spinner rowsSpinner;
    Button setupTableButton;
    private GridPane tableGrid;
    private VBox centerBox;
    private double contentFixedHeight = -50;
    private List<TableHeadItem> tableHeadItemsList;
    private TextField[][]  tableFields; //list of text field columns
    private BoxedDRTA[] rowCommentsArray;
    private ToggleButton[] highlightButtons;
    private int tableRows = 0;
    private VBox[] sizers;
    private Pane endPane;


    TruthTableView(MainWindowView mainView) {
        this.mainView = mainView;

        basicFormulasPane = new GridPane();
        basicFormulasPane.setVgap(10);
        basicFormulasBoxedDRTAList = new ArrayList<>();

        Label basicFormulasLabel = new Label("Basic Sentences:");
        Button addBasicFormulaButton = new Button("+");
        Button removeBasicFormulaButton = new Button("-");
        addBasicFormulaButton.setFont(new Font(16));
        addBasicFormulaButton.setPadding(new Insets(0,5,0,5));
        removeBasicFormulaButton.setFont(new Font(16));
        removeBasicFormulaButton.setPadding(new Insets(1,8,1,8));

        HBox controlButtonBox = new HBox(20, addBasicFormulaButton, removeBasicFormulaButton);
        VBox upperControlBox = new VBox(10, basicFormulasLabel, controlButtonBox);

        basicFormulasBoxedDRTAList.add(newFormulaDRTAField());
        updateBasicFormulasPaneFromList();

        addBasicFormulaButton.setOnAction(e -> {
            basicFormulasBoxedDRTAList.add(newFormulaDRTAField());
            updateBasicFormulasPaneFromList();
        });
        removeBasicFormulaButton.setOnAction(e -> {
           int index = basicFormulasBoxedDRTAList.size();
           index--;
           if (index > 0) {
               basicFormulasBoxedDRTAList.remove(index);
               updateBasicFormulasPaneFromList();
           } else {
               EditorAlerts.showSimpleAlert("Cannot Remove", "A truth table must include at least one basic sentence.");
           }
        });

        Label rowNumLabel = new Label("Rows ");
        rowsSpinner = new Spinner<>(0,256,0);
        rowsSpinner.setPrefWidth(65);
        //rowsSpinner.setEditable(true);  //maybe setup: would need to verify input, update on setup button (rather than 'enter').
        HBox spinnerBox = new HBox(5, rowsSpinner, rowNumLabel);
        spinnerBox.setAlignment(Pos.CENTER_LEFT);

        setupTableButton = new Button("Setup");
        setupTableButton.setPrefWidth(100);

        controlBox = new VBox(20, upperControlBox, basicFormulasPane, spinnerBox, setupTableButton);
        controlBox.setPadding(new Insets(100, 20, 20, 30));


        tableGrid = new GridPane();
        tableGrid.setPadding(new Insets(20,0,20,0));
        centerBox = new VBox(10, tableGrid);
        tableGrid.setStyle("-fx-border-color: gainsboro");
    }

    public void updateTableGridFromTableItems() {
        tableGrid.getChildren().clear();
        List<ColumnConstraints> gridColConstraints =  tableGrid.getColumnConstraints();
        gridColConstraints.clear();

        for (int i = 0; i < tableHeadItemsList.size(); i++) {

            TableHeadItem headItem = tableHeadItemsList.get(i);
            gridColConstraints.add(headItem.getColumnConstraints());

            if (!headItem.isBlankColumn()) {
                TextFlow headFlow = headItem.getExpression();
                headFlow.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0;");
                tableGrid.add(headFlow, i, 0);
                for (int j = 0; j < tableRows; j++) {
                    tableGrid.add(tableFields[i][j], i, j + 2);
                }
                tableGrid.add(highlightButtons[i], i, tableRows + 3);
                if (highlightButtons[i].isSelected()) {
                    highlightButtons[i].setStyle("-fx-border-radius: 10; -fx-border-color: tomato; -fx-background-color: lavenderblush;");
                    for (int j = 0; j < tableRows; j++) {
                        tableFields[i][j].setStyle("-fx-background-radius: 2; -fx-background-color: pink");
                    }
                }

            } else if (headItem.isDividerColumn()) {
                TextFlow headFlow = headItem.getExpression();
                headFlow.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 1");
                tableGrid.add(headFlow, i, 0);

                for (int j = 0; j < tableRows + 3; j++) {
                    Pane dividerPane = new Pane();
                    dividerPane.setStyle("-fx-border-color: black; -fx-border-width: 0 0 0 1");
                    tableGrid.add(dividerPane, i, j + 1);
                }
            } else if (headItem.isBlankColumn()) {
                TextFlow headFlow = headItem.getExpression();
                headFlow.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0");
                tableGrid.add(headFlow, i, 0);
            }
        }


        gridColConstraints.add(new ColumnConstraints(100));
        endPane = new Pane();
        endPane.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0");
        tableGrid.add(endPane, tableHeadItemsList.size(), 0);
        for (int j = 0; j < tableRows; j++) {
            tableGrid.add(rowCommentsArray[j].getBoxedRTA(), tableHeadItemsList.size(), j + 2);
        }
        /*
        for (int j = 0; j < tableRows; j++) {
            endPane = new Pane();
            endPane.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0");
            tableGrid.add(endPane, tableHeadItemsList.size(), 0);
            tableGrid.add(rowCommentsArray[j].getBoxedRTA(), tableHeadItemsList.size(), j + 2);
        }

         */

        sizers = new VBox[tableRows + 3];

        for (int i = 0; i < tableRows + 3; i++) {
            sizers[i] = new VBox();
            tableGrid.add(sizers[i], tableHeadItemsList.size() + 1, i);
        }

        //setup blank separator row gaps
        List<RowConstraints> tableRowConstraints = tableGrid.getRowConstraints();
        tableRowConstraints.clear();
        tableRowConstraints.add(new RowConstraints());
        tableRowConstraints.add(new RowConstraints(5));
        for (int i = 0; i < tableRows; i++) tableRowConstraints.add(new RowConstraints());
        tableRowConstraints.add(new RowConstraints(5));
        tableRowConstraints.add(new RowConstraints());
    }

    public void updateBasicFormulasPaneFromList() {
        basicFormulasPane.getChildren().clear();
        for (int i = 0; i < basicFormulasBoxedDRTAList.size(); i++) {
            basicFormulasPane.add(basicFormulasBoxedDRTAList.get(i).getBoxedRTA(),0, i);
        }
    }

    TextField newSingleCharTextField(int column, int row) {
        TextField singleCharField = new TextField();

    //    singleCharField.setPrefWidth(25);

        singleCharField.setPadding(new Insets(0));
        singleCharField.setPrefWidth(15);

        singleCharField.setAlignment(Pos.CENTER);
        singleCharField.setStyle("-fx-background-radius: 2");
        singleCharField.setPadding(new Insets(3));

        UnaryOperator<TextFormatter.Change> textFilter = c -> {
            if (c.getText().matches("[0-9a-zA-Z]")) {
                c.setRange(0, singleCharField.getText().length());
                return c;
            } else if (c.getText().isEmpty()) {
                return c;
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(textFilter);
        singleCharField.setTextFormatter(formatter);

        singleCharField.addEventFilter(KeyEvent.KEY_TYPED, e -> {
           if (row < tableRows - 1) {
                tableFields[column][row + 1].requestFocus();
           } else {
                tableFields[column][0].requestFocus();
           }

        });

        singleCharField.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.UP && row > 0) {
                tableFields[column][row - 1].requestFocus();
                e.consume();
            } else if (code == KeyCode.DOWN && row < tableRows - 1) {
                tableFields[column][row + 1].requestFocus();
                e.consume();
            } else if (code == KeyCode.RIGHT) {
                tableFields[tableColToRight(column)][row].requestFocus();
                e.consume();
            }  else if (code == KeyCode.LEFT) {
                tableFields[tableColToLeft(column)][row].requestFocus();
                e.consume();
            }

        });

        return singleCharField;
    }

    private int tableColToRight(int column) {
        int rightCol = column;
        for (int i = column + 1; i < tableHeadItemsList.size(); i++) {
            TableHeadItem item = tableHeadItemsList.get(i);
            if (!item.isBlankColumn()) {
                rightCol = i;
                break;
            }
        }
        return rightCol;
    }

    private int tableColToLeft(int column) {
        int leftCol = column;
        for (int i = column - 1; i >= 0; i--) {
            TableHeadItem item = tableHeadItemsList.get(i);
            if (!item.isBlankColumn()) {
                leftCol = i;
                break;
            }
        }
        return leftCol;
    }


    ToggleButton newHighlightButton(int index) {
        ToggleButton button = new ToggleButton();
        button.setPadding(new Insets(0));
        button.setPrefWidth(20);

        button.setStyle("-fx-border-radius: 10; -fx-border-color: lightblue; -fx-background-color: ghostwhite");

        button.setOnAction(e -> {
            if (button.isSelected()) {
                button.setStyle("-fx-border-radius: 10; -fx-border-color: tomato; -fx-background-color: lavenderblush;");
                for (int j = 0; j < tableRows; j++) {
                    tableFields[index][j].setStyle("-fx-background-radius: 2; -fx-background-color: pink");
                }

            } else {
                button.setStyle("-fx-border-radius: 10; -fx-border-color: lightblue; -fx-background-color: ghostwhite;");
                for (int j = 0; j < tableRows; j++) {
                    tableFields[index][j].setStyle("-fx-background-radius: 2; -fxBackground-color: white");
                }
            }

        });

        return button;
    }

    public BoxedDRTA newFormulaDRTAField() {
        BoxedDRTA bdrta = new BoxedDRTA();
        bdrta.getDRTA().getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea rta = bdrta.getRTA();
        rta.setMaxHeight(27);
        rta.setMinHeight(27);
        rta.setContentAreaWidth(200);
        rta.setPrefWidth(100);
        rta.getStylesheets().add("RichTextField.css");
        rta.setPromptText("Formula");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(bdrta.getDRTA(), ControlType.FIELD);
            }
        });
        return bdrta;
    }

    public BoxedDRTA newCommentBoxedDRTA() {
        BoxedDRTA bdrta = new BoxedDRTA();
        RichTextArea rta = bdrta.getRTA();
        rta.setMaxHeight(27);
        rta.setMinHeight(27);
        rta.setContentAreaWidth(200);
        rta.setPrefWidth(200);
        rta.getStylesheets().add("RichTextField.css");
 //       rta.setPromptText("Comment");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(bdrta.getDRTA(), ControlType.FIELD);
            }
        });
        return bdrta;
    }

    void initializeViewDetails() {
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
    }



    public VBox[] getSizers() {  return sizers; }

    public GridPane getTableGrid() { return tableGrid; }

    public void setTableRows(int tableRows) { this.tableRows = tableRows;  }

    public void setTableFields(TextField[][] tableFields) {  this.tableFields = tableFields; }

    public List<TableHeadItem> getTableHeadItemsList() { return tableHeadItemsList;  }

    public void setTableHeadItemsList(List<TableHeadItem> tableHeadItemsList) {   this.tableHeadItemsList = tableHeadItemsList;  }

    public void setRowCommentsArray(BoxedDRTA[] rowComments) {  this.rowCommentsArray = rowComments;   }

    public void setHighlightButtons(ToggleButton[] highlightButtons) {   this.highlightButtons = highlightButtons; }

    public BoxedDRTA[] getRowCommentsArray() { return rowCommentsArray; }

    public ToggleButton[] getHighlightButtons() { return highlightButtons; }

    public TextField[][] getTableFields() { return tableFields; }

    public Button getSetupTableButton() { return setupTableButton; }

    public List<BoxedDRTA> getBasicFormulasBoxedDRTAList() {return basicFormulasBoxedDRTAList; }

    public Spinner getRowsSpinner() { return rowsSpinner;  }

    public void setContentFixedHeight(double contentFixedHeight) { this.contentFixedHeight = contentFixedHeight; }

    public VBox getCenterBox() {return centerBox; }

    public DecoratedRTA getExplainDRTA() {return explainDRTA; }

    @Override
    public String getExerciseName() { return exerciseName;  }

    @Override
    public void setExerciseName(String name) { exerciseName = name; }

    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment; }

    @Override
    public void setExerciseComment(DecoratedRTA comment) { exerciseComment = comment;  }

    @Override
    public double getCommentHeight() { return exerciseComment.getEditor().getHeight(); }

    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement;  }

    @Override
    public void setExerciseStatement(DecoratedRTA statement) { exerciseStatement = statement; }

    @Override
    public Node getExerciseStatementNode() { return exerciseStatement.getEditor();  }

    @Override
    public double getStatementHeight() { return exerciseStatement.getEditor().getHeight();  }

    @Override
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
    }

    @Override
    public Node getExerciseContentNode() { return centerBox;  }

    @Override
    public DoubleProperty getContentHeightProperty() { return tableGrid.prefHeightProperty(); }
    @Override
    public DoubleProperty getContentWidthProperty() {return tableGrid.prefWidthProperty(); }

    @Override
    public double getContentFixedHeight() { return contentFixedHeight; }

    @Override
    public Node getExerciseControl() { return controlBox; }
    @Override
    public double getContentWidth() { return endPane.getBoundsInParent().getMaxX(); }
    @Override
    public double getContentHeight() {
        double height = 0;
        for (ToggleButton button : highlightButtons) {
            if (button != null) {
                height = button.getBoundsInParent().getMaxY();
                break;
            }
        }
        return height;

    }
}
