package slapp.editor.truth_table;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import slapp.editor.EditorAlerts;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;

public class TruthTableView implements ExerciseView<DecoratedRTA> {
    private MainWindowView mainView;
    private String exerciseName = new String();
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private DecoratedRTA explainDRTA = new DecoratedRTA();
    private String explainPrompt = "";
    private GridPane basicFormulasPane;
    private List<DecoratedRTA> basicFormulasDRTAList;
    private VBox controlBox;
    private Spinner rowsSpinner;
    Button setupTableButton;
    private Label choiceLeadLabel = new Label();
    private CheckBox aCheckBox = new CheckBox();
    private CheckBox bCheckBox = new CheckBox();
    private GridPane tablePane;
    private VBox resultsBox;
    private HBox choiceBox;
    private VBox centerBox;
    private double contentFixedHeight = 0;
    private List<TableHeadItem> basicFormulasHeadItems = new ArrayList<>();
    private List<TableHeadItem> mainFormulasHeadItems = new ArrayList<>();
    private List<List<TextField>> tableFields = new ArrayList<>(); //list of text field columns
    private List<DecoratedRTA> rowCommentsList = new ArrayList<>();
    private List<ToggleButton> highlightButtons = new ArrayList<>();






    TruthTableView(MainWindowView mainView) {
        this.mainView = mainView;


        basicFormulasPane = new GridPane();
        basicFormulasPane.setVgap(10);
        basicFormulasDRTAList = new ArrayList<>();

        Label basicFormulasLabel = new Label("Basic Sentences:");
        Button addBasicFormulaButton = new Button("+");
        Button removeBasicFormulaButton = new Button("-");
        addBasicFormulaButton.setFont(new Font(16));
        addBasicFormulaButton.setPadding(new Insets(0,5,0,5));
        removeBasicFormulaButton.setFont(new Font(16));
        removeBasicFormulaButton.setPadding(new Insets(1,8,1,8));

        HBox controlButtonBox = new HBox(20, addBasicFormulaButton, removeBasicFormulaButton);
        VBox upperControlBox = new VBox(10, basicFormulasLabel, controlButtonBox);

        basicFormulasDRTAList.add(newDRTAField());
        updateBasicFormulasPaneFromList();

        addBasicFormulaButton.setOnAction(e -> {
            basicFormulasDRTAList.add(newDRTAField());
            updateBasicFormulasPaneFromList();
        });
        removeBasicFormulaButton.setOnAction(e -> {
           int index = basicFormulasDRTAList.size();
           index--;
           if (index > 0) {
               basicFormulasDRTAList.remove(index);
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
        controlBox.setPadding(new Insets(80, 0, 20, 20));

        choiceBox = new HBox(20, choiceLeadLabel, aCheckBox, bCheckBox);
        resultsBox = new VBox(10, choiceBox, explainDRTA.getEditor());
        tablePane = new GridPane();
        centerBox = new VBox(10, tablePane);



    }

    public void updateBasicFormulasPaneFromList() {
        basicFormulasPane.getChildren().clear();
        for (int i = 0; i < basicFormulasDRTAList.size(); i++) {
            basicFormulasPane.add(basicFormulasDRTAList.get(i).getEditor(),0, i);
        }
    }

    public DecoratedRTA newDRTAField() {
        DecoratedRTA drta = new DecoratedRTA();
        drta.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea rta = drta.getEditor();
        rta.setMaxHeight(27);
        rta.setMinHeight(27);
        rta.setContentAreaWidth(200);
        rta.setPrefWidth(100);
        rta.getStylesheets().add("RichTextField.css");
        rta.setPromptText("Formula");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(drta, ControlType.FIELD);
            }
        });
        return drta;
    }

    void initializeViewDetails() {
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.setPrefHeight(statementPrefHeight);
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPrefHeight(70.0);
        commentRTA.setPromptText("Comment:");

        RichTextArea explainRTA = explainDRTA.getEditor();
        explainRTA.getStylesheets().add("slappTextArea.css");
        explainRTA.setPrefHeight(60.0);
        explainRTA.setPromptText("Explain:");







    }

    public List<DecoratedRTA> getRowCommentsList() { return rowCommentsList; }

    public List<ToggleButton> getHighlightButtons() { return highlightButtons; }

    public List<List<TextField>> getTableFields() { return tableFields; }

    public List<TableHeadItem> getMainFormulasHeadItems() { return mainFormulasHeadItems; }

    public List<TableHeadItem> getBasicFormulasHeadItems() { return basicFormulasHeadItems; }

    public void setBasicFormulasHeadItems(List<TableHeadItem> basicFormulasHeadItems) {this.basicFormulasHeadItems = basicFormulasHeadItems;   }

    public Button getSetupTableButton() { return setupTableButton; }

    public List<DecoratedRTA> getBasicFormulasDRTAList() {return basicFormulasDRTAList; }

    public Spinner getRowsSpinner() { return rowsSpinner;  }

    public void setContentFixedHeight(double contentFixedHeight) { this.contentFixedHeight = contentFixedHeight; }

    public VBox getResultsBox() { return resultsBox; }

    public VBox getCenterBox() {return centerBox; }

    public Label getChoiceLeadLabel() { return choiceLeadLabel; }

    public CheckBox getaCheckBox() { return aCheckBox;  }

    public CheckBox getbCheckBox() { return bCheckBox; }

 //   public void setExplainDRTA(DecoratedRTA explain) { explainDRTA = explain; }


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
    public DoubleProperty getContentHeightProperty() { return tablePane.prefHeightProperty(); }

    @Override
    public double getContentFixedHeight() { return contentFixedHeight; }


    @Override
    public Node getExerciseControl() { return controlBox; }
}
