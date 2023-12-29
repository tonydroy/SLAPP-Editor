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

public class TruthTableExercise implements Exercise<TruthTableModel, TruthTableView> {
    private MainWindow mainWindow;
    private TruthTableModel truthTableModel;
    private TruthTableModel originalModel;
    private TruthTableView truthTableView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;
    private ParseDocForTTable docParser;
    private int tableColumns = 0;
    private int tableRows = 0;


    public TruthTableExercise(TruthTableModel truthTableModel, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.truthTableModel = truthTableModel;
        this.originalModel = truthTableModel;
        this.mainView = mainWindow.getMainView();
        this.truthTableView = new TruthTableView(mainView);
        docParser = new ParseDocForTTable(truthTableModel.getUnaryOperators(), truthTableModel.getBinaryOperators());
        tableRows = truthTableModel.getTableRows();


        setTruthTableView();
    }

    public void generateEmptyTableModel() {
        setupHeadItemsFromModel();
        truthTableModel.setEmptyTableContents(tableColumns);
    }

    private void setTruthTableView() {
        truthTableView.setExerciseName(truthTableModel.getExerciseName());
        truthTableView.setTableRows(tableRows);

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

        //initialize basic formulas control
        List<Document> basicFormulaDocs = truthTableModel.getBasicFormulas();
        if (!basicFormulaDocs.isEmpty()) truthTableView.getBasicFormulasDRTAList().clear();
        for (Document doc : basicFormulaDocs ) {
            DecoratedRTA drta = truthTableView.newFormulaDRTAField();
            RichTextArea rta = drta.getEditor();
            rta.setDocument(doc);
            truthTableView.getBasicFormulasDRTAList().add(drta);
        }
        truthTableView.updateBasicFormulasPaneFromList();
        truthTableView.getRowsSpinner().getValueFactory().setValue(tableRows);

        Button setupTableButton = truthTableView.getSetupTableButton();
        setupTableButton.setOnAction(e -> {
            tableRows = (int) truthTableView.getRowsSpinner().getValue();
            truthTableModel.setTableRows(tableRows);
            truthTableView.setTableRows(tableRows);

            List<DecoratedRTA> basicFormulasDRTAList = truthTableView.getBasicFormulasDRTAList();
            List<Document> newBasicFormulaDocs = new ArrayList<>();
            for (DecoratedRTA drta : basicFormulasDRTAList) {
                RichTextArea rta = drta.getEditor();
                rta.getActionFactory().saveNow().execute(new ActionEvent());
                Document formulaDoc = rta.getDocument();
                newBasicFormulaDocs.add(formulaDoc);
            }
            truthTableModel.setBasicFormulas(newBasicFormulaDocs);
            setupHeadItemsFromModel();
            truthTableModel.setEmptyTableContents(tableColumns);
            updateViewTableItems();
            truthTableView.updateTableGridFromTableItems();
        });

        //table contents

        updateViewTableItems();
        truthTableView.updateTableGridFromTableItems();

        truthTableView.initializeViewDetails();
    }

    private void updateViewTableItems() {
        setupHeadItemsFromModel();

        Document[] commentDocs = truthTableModel.getRowComments();
        DecoratedRTA[] commentDRTAs = new DecoratedRTA[tableRows];
        for (int i = 0; i < commentDocs.length; i++) {
            Document doc = commentDocs[i];
            DecoratedRTA drta = truthTableView.newCommentDRTAField();
            RichTextArea rta = drta.getEditor();
            rta.setDocument(doc);
            commentDRTAs[i] = drta;
        }
        truthTableView.setRowCommentsArray(commentDRTAs);


        ToggleButton[] buttons = new ToggleButton[tableColumns];
        TextField[][] columns = new TextField[tableColumns][tableRows];
        List<TableHeadItem> tableHeadItems = truthTableView.getTableHeadItemsList();


        for (int i = 0; i < tableColumns; i++) {
            TableHeadItem headItem = tableHeadItems.get(i);
            if (!headItem.isBlankColumn()) {
                TextField[] column = new TextField[tableRows];
                for (int j = 0; j < tableRows; j++) {
                    TextField charField = truthTableView.newSingleCharTextField(i, j);
                    charField.setText(truthTableModel.getTableValues()[i][j]);
                    column[j] = charField;
                }
                columns[i] = column;
                buttons[i] = truthTableView.newHighlightButton(i);
            }
        }
        truthTableView.setTableFields(columns);
        truthTableView.setHighlightButtons(buttons);
    }

    private void setupHeadItemsFromModel() {

        List<TableHeadItem> headList = new ArrayList<>();

        //basic formulas head items
        List<Document> basicFormulas = truthTableModel.getBasicFormulas();
        for (int i = 0; i < basicFormulas.size(); i++) {
            List<TableHeadItem> basicHeadItems = docParser.generateHeadItems(basicFormulas.get(i));
            headList.addAll(basicHeadItems);
            TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
            spaceColHead.setBlankColumn(true);
            headList.add(spaceColHead);
        }
        TableHeadItem dividerHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
        dividerHead.setDividerColumn(true); dividerHead.setBlankColumn(true);
        headList.add(dividerHead);


        //main formula head items
        List<Document> mainFormulas = truthTableModel.getMainFormulas();
        for (int i = 0; i < mainFormulas.size() - 1; i++) {
            List<TableHeadItem> mainHeadItems = docParser.generateHeadItems(mainFormulas.get(i));
            headList.addAll(mainHeadItems);
            TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
            spaceColHead.setBlankColumn(true);
            headList.add(spaceColHead);
        }
        if (truthTableModel.isConclusionDivider()) {
            ColumnConstraints slashItemConstraints = new ColumnConstraints(10);
            slashItemConstraints.setHalignment(HPos.RIGHT);
            TableHeadItem slashHeadItem = new TableHeadItem(new TextFlow(new Text("/")), slashItemConstraints);
            slashHeadItem.setBlankColumn(true);
            headList.add(slashHeadItem);
        }
        List<TableHeadItem> finalHeadItems = docParser.generateHeadItems(mainFormulas.get(mainFormulas.size() - 1));
        headList.addAll(finalHeadItems);
        TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
        spaceColHead.setBlankColumn(true);
        headList.add(spaceColHead);

        truthTableView.setTableHeadItemsList(headList);
        tableColumns = headList.size();
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
