package slapp.editor.truth_table;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class TruthTableExercise implements Exercise<TruthTableModel, TruthTableView> {
    private MainWindow mainWindow;
    private TruthTableModel truthTableModel;
    private TruthTableModel originalModel;
    private TruthTableView truthTableView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;
    private ParseDocForTTable docParser;
    private int tableRows = 0;
    private int tableColumns = 0;

    public TruthTableExercise(TruthTableModel truthTableModel, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.truthTableModel = truthTableModel;
        this.originalModel = truthTableModel;
        this.mainView = mainWindow.getMainView();
        this.truthTableView = new TruthTableView(mainView);
        docParser = new ParseDocForTTable(truthTableModel.getUnaryOperators(), truthTableModel.getBinaryOperators());

        setTruthTableView();
    }

    private void setTruthTableView() {
        truthTableView.setExerciseName(truthTableModel.getExerciseName());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(truthTableModel.getExerciseStatement());
        truthTableView.setStatementPrefHeight(truthTableModel.getStatementPrefHeight());
        mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        truthTableView.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(truthTableModel.getExerciseComment());
        mainView.editorInFocus(commentDRTA, ControlType.AREA);
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        truthTableView.setExerciseComment(commentDRTA);

        truthTableView.getChoiceLeadLabel().setText(truthTableModel.getChoiceLead());
        CheckBox aCheckBox = truthTableView.getaCheckBox();
        CheckBox bCheckBox = truthTableView.getbCheckBox();
        aCheckBox.setText(truthTableModel.getaPrompt());
        aCheckBox.setSelected(truthTableModel.isaSelected());
        aCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) bCheckBox.setSelected(false);
                exerciseModified = true;
            }
        });
        bCheckBox.setText(truthTableModel.getbPrompt());
        bCheckBox.setSelected(truthTableModel.isbSelected());
        bCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) aCheckBox.setSelected(false);
                exerciseModified = true;
            }
        });

        if (truthTableModel.isShowChoiceArea()) {
            truthTableView.getCenterBox().getChildren().add(truthTableView.getResultsBox());
            truthTableView.setContentFixedHeight(50);
        }


        DecoratedRTA explainDRTA = truthTableView.getExplainDRTA();
        RichTextArea explainEditor = explainDRTA.getEditor();
        explainEditor.setPromptText("Explain:");
        mainView.editorInFocus(explainDRTA, ControlType.AREA);
        explainEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(explainDRTA, ControlType.AREA);
            }
        });

        //initialize basic formulas setup
        List<Document> basicFormulaDocs = truthTableModel.getBasicFormulas();
        if (!basicFormulaDocs.isEmpty()) truthTableView.getBasicFormulasDRTAList().clear();
        for (Document doc : basicFormulaDocs ) {
            DecoratedRTA drta = truthTableView.newDRTAField();
            RichTextArea rta = drta.getEditor();
            rta.setDocument(doc);
            truthTableView.getBasicFormulasDRTAList().add(drta);
        }
        truthTableView.updateBasicFormulasPaneFromList();
        List<List<Character>> tableValues = truthTableModel.getTableValues();
        if (!tableValues.isEmpty()) tableRows = tableValues.get(0).size();
        truthTableView.getRowsSpinner().getValueFactory().setValue(tableRows);

        //main formula head items
        List<Document> mainFormulas = truthTableModel.getMainFormulas();
        for (int i = 0; i < mainFormulas.size() - 1; i++) {
            List<TableHeadItem> mainHeadItems = docParser.generateHeadItems(mainFormulas.get(i));
            truthTableView.getMainFormulasHeadItems().addAll(mainHeadItems);
            TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
            spaceColHead.setBlankColumn(true);
            truthTableView.getMainFormulasHeadItems().add(spaceColHead);
        }
        if (truthTableModel.isConclusionDivider()) {
            ColumnConstraints dividerConstraints = new ColumnConstraints(10);
            dividerConstraints.setHalignment(HPos.LEFT);
            TableHeadItem dividerHeadItem = new TableHeadItem(new TextFlow(new Text("/")), dividerConstraints);
            dividerHeadItem.setBlankColumn(true);
            truthTableView.getMainFormulasHeadItems().add(dividerHeadItem);
        }
        List<TableHeadItem> finalHeadItems = docParser.generateHeadItems(mainFormulas.get(mainFormulas.size() - 1));
        truthTableView.getMainFormulasHeadItems().addAll(finalHeadItems);

        //basic formulas head items
        List<Document> basicFormulas = truthTableModel.getBasicFormulas();
        for (int i = 0; i < basicFormulas.size(); i++) {
            List<TableHeadItem> basicHeadItems = docParser.generateHeadItems(basicFormulas.get(i));
            truthTableView.getBasicFormulasHeadItems().addAll(basicHeadItems);
            TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
            spaceColHead.setBlankColumn(true);
            truthTableView.getBasicFormulasHeadItems().add(spaceColHead);
        }

        //table fields

        int modelIndex = 0;
        List<TableHeadItem> baseColumnHeads = truthTableView.getBasicFormulasHeadItems();
        int numBaseColumnHeads = baseColumnHeads.size();
        for (int i = 0; i < numBaseColumnHeads; i++) {
            if (!baseColumnHeads.get(i).isBlankColumn()) {
                List<TextField> columnFields = new ArrayList<>();
                for (int j = 0; j < tableRows; j++) {
                    TextField singleCharTextField = newSingleCharTextField();
                    singleCharTextField.setText(Character.toString(truthTableModel.getTableValues().get(modelIndex).get(j)));
                    columnFields.add(singleCharTextField);

                    ToggleButton highlightButton = newHighlightButton(i);
                    highlightButton.setSelected(truthTableModel.getColumnHighlights().get(modelIndex));
                    truthTableView.getHighlightButtons().add(i, highlightButton);
                    modelIndex++;
                }
                truthTableView.getTableFields().add(i, columnFields);
            }
        }
        List<TableHeadItem> mainColumnHeads = truthTableView.getMainFormulasHeadItems();
        for (int i = 0; i < mainColumnHeads.size(); i++) {
            if (!mainColumnHeads.get(i).isBlankColumn()) {
                for (int j = 0; j < tableRows; j++) {
                    TextField singleCharTextField = newSingleCharTextField();
                    singleCharTextField.setText(Character.toString(truthTableModel.getTableValues().get(modelIndex).get(j)));
                    truthTableView.getTableFields().get(numBaseColumnHeads + i).add(singleCharTextField);
                    ToggleButton highlightButton = newHighlightButton(numBaseColumnHeads + i);
                    highlightButton.setSelected(truthTableModel.getColumnHighlights().get(modelIndex));
                    truthTableView.getHighlightButtons().add(numBaseColumnHeads + i, highlightButton);
                    modelIndex++;
                }
            }
        }

        //comment fields
        for (int i = 0; i < tableRows; i++) {
            DecoratedRTA rowCommentDRTA = truthTableView.newDRTAField();
            RichTextArea rowCommentRTA = rowCommentDRTA.getEditor();
            rowCommentRTA.setDocument(truthTableModel.getRowComments().get(i));
            truthTableView.getRowCommentsList().add(rowCommentDRTA);
        }




        Button setupTableButton = truthTableView.getSetupTableButton();
        setupTableButton.setOnAction(e -> {
            List<DecoratedRTA> basicFormulasDRTAList = truthTableView.getBasicFormulasDRTAList();
            List<TableHeadItem> basicHeadItems = new ArrayList<>();
            for (DecoratedRTA drta : basicFormulasDRTAList) {
                RichTextArea rta = drta.getEditor();
                rta.getActionFactory().saveNow().execute(new ActionEvent());
                Document formulaDoc = rta.getDocument();
                List<TableHeadItem> headItems = docParser.generateHeadItems(formulaDoc);
                for (TableHeadItem item : headItems) basicHeadItems.add(item);
            }
            truthTableView.setBasicFormulasHeadItems(basicHeadItems);

            //deal with rows and setup table

        });






        truthTableView.initializeViewDetails();
    }

    TextField newSingleCharTextField() {
        TextField singleCharField = new TextField();

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

        return singleCharField;
    }

    ToggleButton newHighlightButton(int index) {
        return new ToggleButton();
    }

    @Override
    public TruthTableModel getExerciseModel() { return truthTableModel;  }

    @Override
    public TruthTableView getExerciseView() { return truthTableView; }


    @Override
    public void saveExercise(boolean saveAs) {
    }

    @Override
    public void printExercise() {
    }

    @Override
    public void exportExerciseToPDF() {
    }

    @Override
    public List<Node> getPrintNodes() {
        return null;
    }

    @Override
    public Exercise<TruthTableModel, TruthTableView> getContentClearExercise() {
        return null;
    }

    @Override
    public boolean isExerciseModified() {
        return false;
    }

    @Override
    public void setExerciseModified(boolean modified) { exerciseModified = modified; }

    @Override
    public void updateContentHeight(Node focusedNode, boolean isRequired) {
    }

    @Override
    public void updateCommentHeight(boolean isRequired) {
    }

    @Override
    public void updateStatementHeight(boolean isRequired) {
    }

    @Override
    public ExerciseModel<TruthTableModel> getExerciseModelFromView() {
        return null;
    }
}
