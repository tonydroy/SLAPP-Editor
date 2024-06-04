package slapp.editor.truth_table_explain;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
import slapp.editor.truth_table.TableHeadItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class TruthTableExpView implements ExerciseView<DecoratedRTA> {
    private MainWindowView mainView;
    private String exerciseName = new String();
    private String explainPrompt = "";
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 0;
    private double commentPrefHeight = 0;
    private double explainPrefHeight = 0;
    private Spinner<Double> statementHeightSpinner;
    private Spinner<Double> statementWidthSpinner;
    private Spinner<Double> commentHeightSpinner;
    private Spinner<Double> commentWidthSpinner;
    private Spinner<Double> explainHeightSpinner;
    private Spinner<Double> explainWidthSpinner;
    private Spinner<Double> choicesHeightSpinner;
    private Spinner<Double> choicesWidthSpinner;
    private Spinner<Double> tableGridHeightSpinner;
    private Spinner<Double> tableGridWidthSpinner;
    private Node currentSpinnerNode;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private DecoratedRTA explainDRTA = new DecoratedRTA();
    private GridPane basicFormulasPane;
    private List<BoxedDRTA> basicFormulasBoxedDRTAs;
    private VBox controlBox;
    private Spinner rowsSpinner;
    Button setupTableButton;
    private Label choiceLeadLabel = new Label();
    private CheckBox aCheckBox = new CheckBox();
    private CheckBox bCheckBox = new CheckBox();
    private GridPane tableGrid;
    private VBox resultsBox;
    private HBox choiceBox;
    private VBox centerBox;



    private double formulaBoxHeight = 22;

    private List<TableHeadItem> tableHeadItemsList;
    private TextField[][]  tableFields; //list of text field columns
    private BoxedDRTA[] rowCommentsArray;
    private ToggleButton[] highlightButtons;
    private int tableRows = 0;
    private VBox[] sizers;

    private Pane endPane;

    BoxedDRTA focusedBoxedDRTA;




    TruthTableExpView(MainWindowView mainView) {
        this.mainView = mainView;

        Font labelFont = new Font("Noto Serif Combo", 11);
        choiceLeadLabel.setFont(labelFont); aCheckBox.setFont(labelFont); bCheckBox.setFont(labelFont);


        basicFormulasPane = new GridPane();
        basicFormulasPane.setVgap(10);
        basicFormulasBoxedDRTAs = new ArrayList<>();

        Label basicFormulasLabel = new Label("Basic Sentences:");
        Button addBasicFormulaButton = new Button("+");
        Button removeBasicFormulaButton = new Button("-");
        addBasicFormulaButton.setFont(new Font(16));
        addBasicFormulaButton.setPadding(new Insets(0,5,0,5));
        removeBasicFormulaButton.setFont(new Font(16));
        removeBasicFormulaButton.setPadding(new Insets(1,8,1,8));

        HBox controlButtonBox = new HBox(20, addBasicFormulaButton, removeBasicFormulaButton);
        VBox upperControlBox = new VBox(10, basicFormulasLabel, controlButtonBox);

        basicFormulasBoxedDRTAs.add(newFormulaBoxedDRTA());
        updateBasicFormulasPaneFromList();

        basicFormulasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            int index = basicFormulasBoxedDRTAs.indexOf(focusedBoxedDRTA);
            if (index >= 0) {
                KeyCode code = e.getCode();
                if (code == KeyCode.DOWN || code == KeyCode.ENTER) {
                    if (index + 1 < basicFormulasBoxedDRTAs.size()) {
                        basicFormulasBoxedDRTAs.get(index + 1).getRTA().requestFocus();
                    }
                    e.consume();
                }
                if (code == KeyCode.UP) {
                    if (index > 0) {
                        basicFormulasBoxedDRTAs.get(index - 1).getRTA().requestFocus();
                    }
                    e.consume();
                }
            }
        });

        addBasicFormulaButton.setOnAction(e -> {
            basicFormulasBoxedDRTAs.add(newFormulaBoxedDRTA());
            updateBasicFormulasPaneFromList();
        });
        removeBasicFormulaButton.setOnAction(e -> {
           int index = basicFormulasBoxedDRTAs.size();
           index--;
           if (index > 0) {
               basicFormulasBoxedDRTAs.remove(index);
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
        controlBox.setPadding(new Insets(100, 20, 0, 30));

        choiceBox = new HBox(20, choiceLeadLabel, aCheckBox, bCheckBox);
        choiceBox.setPadding(new Insets(5,0,5,5));
        choiceBox.setStyle("-fx-border-color: lightgrey; -fx-border-width: 1 1 1 1");


        resultsBox = new VBox(5, choiceBox, explainDRTA.getEditor());
        resultsBox.setPadding(new Insets(0,0,0,0));

//        resultsBox.setStyle("-fx-border-color: lightgrey; -fx-border-width: 1 1 0 1");

        tableGrid = new GridPane();
        tableGrid.setPadding(new Insets(20,0,20,10));
        centerBox = new VBox(5, tableGrid, resultsBox);

        tableGrid.setStyle("-fx-border-color: lightgrey");

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


        ColumnConstraints commentConstraints = new ColumnConstraints();
        commentConstraints.setMinWidth(100);
        gridColConstraints.add(commentConstraints);

        endPane = new Pane();
        endPane.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0");
        tableGrid.add(endPane, tableHeadItemsList.size(), 0);
        for (int j = 0; j < tableRows; j++) {
            if (j != 0 && j % 4 == 0) {
                rowCommentsArray[j].getBoxedRTA().setAlignment(Pos.BOTTOM_LEFT);
                rowCommentsArray[j].getBoxedRTA().setPadding(new Insets(0,0,1.5,0));
            }
            tableGrid.add(rowCommentsArray[j].getBoxedRTA(), tableHeadItemsList.size(), j + 2);
        }

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
        for (int i = 0; i < tableRows; i++) {
            RowConstraints rowCon = new RowConstraints();
            if (i != 0 && i % 4 == 0) {
                rowCon.setMinHeight(30);
                rowCon.setMaxHeight(30);
                rowCon.setValignment(VPos.BOTTOM);
            }
            else {
                rowCon.setMinHeight(25);
                rowCon.setMaxHeight(25);
            }
            tableRowConstraints.add(rowCon);
//            tableRowConstraints.add(new RowConstraints(25));
        }
        tableRowConstraints.add(new RowConstraints(5));
        tableRowConstraints.add(new RowConstraints());
    }

    public void updateBasicFormulasPaneFromList() {
        basicFormulasPane.getChildren().clear();
        for (int i = 0; i < basicFormulasBoxedDRTAs.size(); i++) {
            basicFormulasPane.add(basicFormulasBoxedDRTAs.get(i).getBoxedRTA(),0, i);
        }
    }

    TextField newSingleCharTextField(int column, int row) {
        TextField singleCharField = new TextField();

        singleCharField.setPadding(new Insets(0));
        singleCharField.setMaxWidth(18);



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

    public BoxedDRTA newFormulaBoxedDRTA() {
        BoxedDRTA bdrta = new BoxedDRTA();
        DecoratedRTA drta = bdrta.getDRTA();
        drta.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea rta = bdrta.getRTA();
        rta.setMaxHeight(27);
        rta.setMinHeight(27);
        rta.setContentAreaWidth(200);
        rta.setPrefWidth(100);
        rta.getStylesheets().add("RichTextFieldWide.css");
        rta.setPromptText("Formula");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(drta, ControlType.FIELD);
                focusedBoxedDRTA = bdrta;
            }
        });
        return bdrta;
    }

    public BoxedDRTA newCommentBoxedDRTA() {
        BoxedDRTA bdrta = new BoxedDRTA();
        RichTextArea rta = bdrta.getRTA();
        rta.setMaxHeight(formulaBoxHeight);
        rta.setMinHeight(formulaBoxHeight);

        RichTextAreaSkin rtaSkin = (RichTextAreaSkin) rta.getSkin();
        rta.prefWidthProperty().bind(Bindings.max(Bindings.add(rtaSkin.nodesWidthProperty(), 3), 100));
        rta.addEventFilter(KeyEvent.ANY, e -> {
            if (e.getCode() == KeyCode.ENTER) e.consume();
        });

        rta.getStylesheets().add("RichTextField.css");
//        rta.setPromptText("Comment");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(bdrta.getDRTA(), ControlType.FIELD);
            }
        });
        return bdrta;
    }

    void initializeViewDetails() {
        //statement
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        double statementInitialHeight = Math.round(statementPrefHeight / PrintUtilities.getPageHeight() * 100.0 );
        statementHeightSpinner = new Spinner<>(0.0, 999.0, statementInitialHeight, 1.0);
        statementHeightSpinner.setPrefWidth(60);
        statementHeightSpinner.setDisable(false);
        statementHeightSpinner.setTooltip(new Tooltip("Width as % of selected paper"));
        statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(PrintUtilities.pageHeightProperty(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        statementHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = statementHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = statementHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        statementRTA.maxWidthProperty().bind(PrintUtilities.pageWidthProperty());
        statementWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        statementWidthSpinner.setPrefWidth(60);
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

        double commentInitialHeight = Math.round(commentPrefHeight / PrintUtilities.getPageHeight() * 100.0 );
        commentHeightSpinner = new Spinner<>(0.0, 999.0, commentInitialHeight, 1.0);
        commentHeightSpinner.setPrefWidth(60);
        commentHeightSpinner.setDisable(false);
        commentHeightSpinner.setTooltip(new Tooltip("Width as % of selected paper"));
        commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(PrintUtilities.pageHeightProperty(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        commentHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = commentHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = commentHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        commentRTA.maxWidthProperty().bind(PrintUtilities.pageWidthProperty());
        commentRTA.minWidthProperty().bind(PrintUtilities.pageWidthProperty());
        commentWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        commentWidthSpinner.setPrefWidth(60);
        commentWidthSpinner.setDisable(true);
        commentWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        commentRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != commentRTA) {
                currentSpinnerNode = commentRTA;
                mainView.updateSizeSpinners(commentHeightSpinner, commentWidthSpinner);
            }
        });

        //explain
        RichTextArea explainRTA = explainDRTA.getEditor();
        explainRTA.getStylesheets().add("slappTextArea.css");
        explainRTA.setPromptText(explainPrompt);

        double explainInitialHeight = Math.round(explainPrefHeight / PrintUtilities.getPageHeight() * 100.0 );
        explainHeightSpinner = new Spinner<>(0.0, 999.0, explainInitialHeight, 1.0);
        explainHeightSpinner.setPrefWidth(60);
        explainHeightSpinner.setDisable(false);
        explainHeightSpinner.setTooltip(new Tooltip("Width as % of selected paper"));
        explainRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(PrintUtilities.pageHeightProperty(), DoubleProperty.doubleProperty(explainHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        explainHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = explainHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = explainHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        explainRTA.maxWidthProperty().bind(PrintUtilities.pageWidthProperty());
        explainRTA.minWidthProperty().bind(PrintUtilities.pageWidthProperty());
        explainWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        explainWidthSpinner.setPrefWidth(60);
        explainWidthSpinner.setDisable(true);
        explainWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        explainRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != explainRTA) {
                currentSpinnerNode = explainRTA;
                mainView.updateSizeSpinners(explainHeightSpinner, explainWidthSpinner);
            }
        });

        //table grid
        tableGridHeightSpinner = new Spinner<>(0.0,999.0, 0,1.0);
        tableGridHeightSpinner.setPrefWidth(60);
        tableGridHeightSpinner.setDisable(true);
        tableGridHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        tableGrid.heightProperty().addListener((ob, ov, nv) -> {
            tableGridHeightSpinner.getValueFactory().setValue((double) Math.round(tableGrid.getHeight() / PrintUtilities.getPageHeight() * 100));
        });

        tableGridWidthSpinner = new Spinner<>(0.0,999.0, 0,1.0);
        tableGridWidthSpinner.setPrefWidth(60);
        tableGridWidthSpinner.setDisable(true);
        tableGridWidthSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        tableGrid.widthProperty().addListener((ob, ov, nv) -> {
            tableGridWidthSpinner.getValueFactory().setValue((double) Math.round(tableGrid.getWidth() / PrintUtilities.getPageWidth() * 100));
        });

        tableGrid.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != tableGrid) {
                currentSpinnerNode = tableGrid;
                tableGridHeightSpinner.getValueFactory().setValue((double) Math.round(tableGrid.getHeight()/PrintUtilities.getPageHeight() * 100.0));
                tableGridWidthSpinner.getValueFactory().setValue((double) Math.round(tableGrid.getWidth()/PrintUtilities.getPageWidth() * 100.0));
                mainView.updateSizeSpinners(tableGridHeightSpinner, tableGridWidthSpinner);
            }
        });

        //choices (null spinners)
        choicesHeightSpinner = new Spinner<>(0.0, 999.0, 0, 1.0);
        choicesHeightSpinner.setPrefWidth(60);
        choicesHeightSpinner.setDisable(true);
        choicesHeightSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        choiceBox.maxWidthProperty().bind(PrintUtilities.pageWidthProperty());
        choicesWidthSpinner = new Spinner<>(0.0, 999.0, 100.0, 1.0);
        choicesWidthSpinner.setPrefWidth(60);
        choicesWidthSpinner.setDisable(true);
        choicesWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        choiceBox.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != choiceBox) {
                currentSpinnerNode = choiceBox;
                double choicesHeightValue = Math.round(choiceBox.getHeight() / PrintUtilities.getPageHeight() * 100);
                choicesHeightSpinner.getValueFactory().setValue(choicesHeightValue);
                mainView.updateSizeSpinners(choicesHeightSpinner, choicesWidthSpinner);
            }
        });

        //page size listeners
        PrintUtilities.pageHeightProperty().addListener((ob, ov, nv) -> {

            statementRTA.prefHeightProperty().unbind();
            statementHeightSpinner.getValueFactory().setValue((double) Math.round(statementHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            commentRTA.prefHeightProperty().unbind();
            commentHeightSpinner.getValueFactory().setValue((double) Math.round(commentHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            explainRTA.prefHeightProperty().unbind();
            explainHeightSpinner.getValueFactory().setValue((double) Math.round(explainHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            explainRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(explainHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            tableGridHeightSpinner.getValueFactory().setValue((double) Math.round(tableGrid.getHeight() / PrintUtilities.getPageHeight() * 100.0));
            choicesHeightSpinner.getValueFactory().setValue((double) Math.round(choiceBox.getHeight() / PrintUtilities.getPageHeight() * 100.0));

        });

        PrintUtilities.pageWidthProperty().addListener((ob, ov, nv) -> {
            tableGridWidthSpinner.getValueFactory().setValue((double) Math.round(tableGrid.getWidth() / PrintUtilities.getPageWidth() * 100.0));
        });



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

    public List<BoxedDRTA> getBasicFormulasBoxedDRTAs() {return basicFormulasBoxedDRTAs; }

    public Spinner getRowsSpinner() { return rowsSpinner;  }

    public VBox getResultsBox() { return resultsBox; }

    public VBox getCenterBox() {return centerBox; }

    public Label getChoiceLeadLabel() { return choiceLeadLabel; }

    public CheckBox getaCheckBox() { return aCheckBox;  }

    public CheckBox getbCheckBox() { return bCheckBox; }

    public DecoratedRTA getExplainDRTA() {return explainDRTA; }

    public void setExplainPrompt(String explainPrompt) {    this.explainPrompt = explainPrompt; }

    public double getCommentPrefHeight() { return exerciseComment.getEditor().getPrefHeight();  }

    public void setCommentPrefHeight(double commentPrefHeight) { this.commentPrefHeight = commentPrefHeight;  }

    public double getExplainPrefHeight() { return explainDRTA.getEditor().getPrefHeight();  }

    public void setExplainPrefHeight(double explainPrefHeight) { this.explainPrefHeight = explainPrefHeight;  }

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
    public Node getExerciseContentNode() {return centerBox; }


    @Override
    public DoubleProperty getContentHeightProperty() { return tableGrid.prefHeightProperty();  }

    @Override
    public DoubleProperty getContentWidthProperty() {return tableGrid.prefWidthProperty(); }




    @Override
    public double getContentFixedHeight() { return 0.0; }

    @Override
    public Node getExerciseControl() { return controlBox; }

    @Override
    public double getContentWidth() { return endPane.getBoundsInParent().getMaxX();  }
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
