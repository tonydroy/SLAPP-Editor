package slapp.editor.truth_table_explain;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import slapp.editor.DiskUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import slapp.editor.truth_table.ParseDocForTTable;
import slapp.editor.truth_table.TableHeadItem;

import java.util.ArrayList;
import java.util.List;

public class TruthTableExpExercise implements Exercise<TruthTableExpModel, TruthTableExpView> {
    private MainWindow mainWindow;
    private TruthTableExpModel truthTableExpModel;
    private TruthTableExpModel originalModel;
    private TruthTableExpView truthTableExpView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;
    private ParseDocForTTable docParser;
    private int tableColumns = 0;
    private int tableRows = 0;


    //applies when table elements are set to model with rows != 0
    public TruthTableExpExercise(TruthTableExpModel truthTableExpModel, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.truthTableExpModel = truthTableExpModel;
        this.mainView = mainWindow.getMainView();
        this.truthTableExpView = new TruthTableExpView(mainView);
        docParser = new ParseDocForTTable(truthTableExpModel.getUnaryOperators(), truthTableExpModel.getBinaryOperators());
        tableRows = truthTableExpModel.getTableRows();
        this.originalModel = truthTableExpModel;


        setTruthTableView();
    }

    //applies when table elements need to be set to empty table
    public TruthTableExpExercise(TruthTableExpModel truthTableExpModel, MainWindow mainWindow, boolean create) {
        this.mainWindow = mainWindow;
        this.truthTableExpModel = truthTableExpModel;
        this.mainView = mainWindow.getMainView();
        this.truthTableExpView = new TruthTableExpView(mainView);
        docParser = new ParseDocForTTable(truthTableExpModel.getUnaryOperators(), truthTableExpModel.getBinaryOperators());
        tableRows = truthTableExpModel.getTableRows();
        generateEmptyTableModel();                                  //** the difference
        this.originalModel = truthTableExpModel;


        setTruthTableView();
    }

    public void generateEmptyTableModel() {
        setupHeadItemsFromModel();
        truthTableExpModel.setEmptyTableContents(tableColumns);
    }


    private void setTruthTableView() {
        truthTableExpView.setExerciseName(truthTableExpModel.getExerciseName());
        truthTableExpView.setTableRows(tableRows);

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(truthTableExpModel.getExerciseStatement());
        truthTableExpView.setStatementPrefHeight(truthTableExpModel.getStatementPrefHeight());
        mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        truthTableExpView.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(truthTableExpModel.getExerciseComment());
        mainView.editorInFocus(commentDRTA, ControlType.AREA);
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        truthTableExpView.setExerciseComment(commentDRTA);

        truthTableExpView.getChoiceLeadLabel().setText(truthTableExpModel.getChoiceLead());
        CheckBox aCheckBox = truthTableExpView.getaCheckBox();
        CheckBox bCheckBox = truthTableExpView.getbCheckBox();
        aCheckBox.setText(truthTableExpModel.getaPrompt());
        aCheckBox.setSelected(truthTableExpModel.isaSelected());
        aCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) bCheckBox.setSelected(false);
                exerciseModified = true;
            }
        });
        bCheckBox.setText(truthTableExpModel.getbPrompt());
        bCheckBox.setSelected(truthTableExpModel.isbSelected());
        bCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) aCheckBox.setSelected(false);
                exerciseModified = true;
            }
        });


        truthTableExpView.getCenterBox().getChildren().add(truthTableExpView.getResultsBox());
        truthTableExpView.setContentFixedHeight(50);


        DecoratedRTA explainDRTA = truthTableExpView.getExplainDRTA();
        RichTextArea explainEditor = explainDRTA.getEditor();
        explainEditor.setDocument(truthTableExpModel.getExplainDocument());
        explainEditor.setPromptText("Explain:");
        mainView.editorInFocus(explainDRTA, ControlType.AREA);
        explainEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(explainDRTA, ControlType.AREA);
            }
        });

        //initialize basic formulas control
        List<Document> basicFormulaDocs = truthTableExpModel.getBasicFormulas();
        if (!basicFormulaDocs.isEmpty()) truthTableExpView.getBasicFormulasDRTAList().clear();
        for (Document doc : basicFormulaDocs ) {
            DecoratedRTA drta = truthTableExpView.newFormulaDRTAField();
            RichTextArea rta = drta.getEditor();
            rta.setDocument(doc);
            truthTableExpView.getBasicFormulasDRTAList().add(drta);
        }
        truthTableExpView.updateBasicFormulasPaneFromList();
        truthTableExpView.getRowsSpinner().getValueFactory().setValue(tableRows);

        Button setupTableButton = truthTableExpView.getSetupTableButton();
        setupTableButton.setOnAction(e -> {
            tableRows = (int) truthTableExpView.getRowsSpinner().getValue();
            truthTableExpModel.setTableRows(tableRows);
            truthTableExpView.setTableRows(tableRows);

            List<DecoratedRTA> basicFormulasDRTAList = truthTableExpView.getBasicFormulasDRTAList();
            List<Document> newBasicFormulaDocs = new ArrayList<>();
            for (DecoratedRTA drta : basicFormulasDRTAList) {
                RichTextArea rta = drta.getEditor();
                rta.getActionFactory().saveNow().execute(new ActionEvent());
                Document formulaDoc = rta.getDocument();
                newBasicFormulaDocs.add(formulaDoc);
            }
            truthTableExpModel.setBasicFormulas(newBasicFormulaDocs);
            setupHeadItemsFromModel();
            truthTableExpModel.setEmptyTableContents(tableColumns);
            updateViewTableItems();
            truthTableExpView.updateTableGridFromTableItems();
        });

        //table contents

        updateViewTableItems();
        truthTableExpView.updateTableGridFromTableItems();

        truthTableExpView.initializeViewDetails();
    }

    private void updateViewTableItems() {
        setupHeadItemsFromModel();

        Document[] commentDocs = truthTableExpModel.getRowComments();
        DecoratedRTA[] commentDRTAs = new DecoratedRTA[tableRows];
        for (int i = 0; i < commentDocs.length; i++) {
            Document doc = commentDocs[i];
            DecoratedRTA drta = truthTableExpView.newCommentDRTAField();
            RichTextArea rta = drta.getEditor();
            rta.setDocument(doc);
            commentDRTAs[i] = drta;
        }
        truthTableExpView.setRowCommentsArray(commentDRTAs);


        ToggleButton[] buttons = new ToggleButton[tableColumns];
        TextField[][] columns = new TextField[tableColumns][tableRows];
        List<TableHeadItem> tableHeadItems = truthTableExpView.getTableHeadItemsList();


        for (int i = 0; i < tableColumns; i++) {
            TableHeadItem headItem = tableHeadItems.get(i);
            if (!headItem.isBlankColumn()) {
                TextField[] column = new TextField[tableRows];
                for (int j = 0; j < tableRows; j++) {
                    TextField charField = truthTableExpView.newSingleCharTextField(i, j);
                    charField.setText(truthTableExpModel.getTableValues()[i][j]);
                    column[j] = charField;
                }
                columns[i] = column;
                ToggleButton highlightButton = truthTableExpView.newHighlightButton(i);
                highlightButton.setSelected(truthTableExpModel.getColumnHighlights()[i]);
                buttons[i] = highlightButton;

            }
        }
        truthTableExpView.setTableFields(columns);
        truthTableExpView.setHighlightButtons(buttons);
    }

    private void setupHeadItemsFromModel() {

        List<TableHeadItem> headList = new ArrayList<>();

        //basic formulas head items
        List<Document> basicFormulas = truthTableExpModel.getBasicFormulas();
        for (int i = 0; i < basicFormulas.size(); i++) {
            List<TableHeadItem> basicHeadItems = docParser.generateHeadItems(basicFormulas.get(i));
            headList.addAll(basicHeadItems);
            TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
            spaceColHead.setBlankColumn(true);
            headList.add(spaceColHead);
        }
        if (basicFormulas.size() == 0) {
            TableHeadItem stubHead = new TableHeadItem(new TextFlow(new Text("  ")), new ColumnConstraints(10));
            stubHead.setBlankColumn(true);
            headList.add(stubHead);
        }
        TableHeadItem dividerHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
        dividerHead.setDividerColumn(true); dividerHead.setBlankColumn(true);
        headList.add(dividerHead);


        //main formula head items
        List<Document> mainFormulas = truthTableExpModel.getMainFormulas();
        for (int i = 0; i < mainFormulas.size() - 1; i++) {
            List<TableHeadItem> mainHeadItems = docParser.generateHeadItems(mainFormulas.get(i));
            headList.addAll(mainHeadItems);
            TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
            spaceColHead.setBlankColumn(true);
            headList.add(spaceColHead);
        }
        if (truthTableExpModel.isConclusionDivider()) {
            ColumnConstraints slashItemConstraints = new ColumnConstraints(10);
            slashItemConstraints.setHalignment(HPos.RIGHT);
            TableHeadItem slashHeadItem = new TableHeadItem(new TextFlow(new Text("/")), slashItemConstraints);
            slashHeadItem.setBlankColumn(true);
            headList.add(slashHeadItem);
        }
        if (mainFormulas.size() > 0) {
            List<TableHeadItem> finalHeadItems = docParser.generateHeadItems(mainFormulas.get(mainFormulas.size() - 1));
            headList.addAll(finalHeadItems);
        }
        TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), new ColumnConstraints(10));
        spaceColHead.setBlankColumn(true);
        headList.add(spaceColHead);

        truthTableExpView.setTableHeadItemsList(headList);
        tableColumns = headList.size();
    }

    @Override
    public TruthTableExpModel getExerciseModel() { return truthTableExpModel;  }
    @Override
    public TruthTableExpView getExerciseView() { return truthTableExpView; }

    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getTruthTableExpModelFromView());
        if (success) exerciseModified = false;
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
    public Exercise<TruthTableExpModel, TruthTableExpView> getContentClearExercise() {
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
    public ExerciseModel<TruthTableExpModel> getExerciseModelFromView() {
        return (ExerciseModel) getTruthTableExpModelFromView();
    }

    private TruthTableExpModel getTruthTableExpModelFromView() {
        TruthTableExpModel model = new TruthTableExpModel();
        model.setExerciseName(truthTableExpView.getExerciseName());
        model.setStarted(truthTableExpModel.isStarted() || exerciseModified);
        model.setStatementPrefHeight(truthTableExpView.getExerciseStatement().getEditor().getPrefHeight());
        model.setExerciseStatement(truthTableExpModel.getExerciseStatement());

        RichTextArea commentRTA = truthTableExpView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseComment(commentRTA.getDocument());

        model.setUnaryOperators(truthTableExpModel.getUnaryOperators());
        model.setBinaryOperators(truthTableExpModel.getBinaryOperators());
        model.setMainFormulas(truthTableExpModel.getMainFormulas());

        List<DecoratedRTA> basicDRTAs = truthTableExpView.getBasicFormulasDRTAList();
        List<Document> basicDocs = new ArrayList<>();
        for (DecoratedRTA drta : basicDRTAs) {
            RichTextArea rta = drta.getEditor();
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            Document doc = rta.getDocument();
            basicDocs.add(doc);
        }
        model.setBasicFormulas(basicDocs);

        String[][] tableStrVals = new String[tableColumns][tableRows];
        TextField[][] tableFields = truthTableExpView.getTableFields();
        for (int i = 0; i < tableColumns; i ++) {
            String[] column = new String[tableRows];
            for (int j = 0; j < tableRows; j++) {
                if (tableFields[i][j] != null) {
                    String value = tableFields[i][j].getText();
                    column[j] = value;
                }
            }
            tableStrVals[i] = column;
        }
        model.setTableValues(tableStrVals);

        DecoratedRTA[] lineCommentDRTAs = truthTableExpView.getRowCommentsArray();
        Document[] commentDocs = new Document[tableRows];
        for (int i = 0; i < tableRows; i++) {
            DecoratedRTA drta = lineCommentDRTAs[i];
            RichTextArea rta = drta.getEditor();
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            Document doc = rta.getDocument();
            commentDocs[i] = doc;
        }
        model.setRowComments(commentDocs);

        ToggleButton[] highlightButtons = truthTableExpView.getHighlightButtons();
        boolean[] highlightValues = new boolean[tableColumns];
        for (int i = 0; i < tableColumns; i++) {
            if (highlightButtons[i] != null) {
                highlightValues[i] = highlightButtons[i].isSelected();
            }
        }
        model.setColumnHighlights(highlightValues);

        model.setConclusionDivider(truthTableExpModel.isConclusionDivider());
        model.setChoiceLead(truthTableExpModel.getChoiceLead());
        model.setaPrompt(truthTableExpModel.getaPrompt());
        model.setaSelected(truthTableExpView.getaCheckBox().isSelected());
        model.setbPrompt(truthTableExpModel.getbPrompt());
        model.setbSelected(truthTableExpView.getbCheckBox().isSelected());

        RichTextArea explainRTA = truthTableExpView.getExplainDRTA().getEditor();
        explainRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExplainDocument(explainRTA.getDocument());
        model.setTableRows(tableRows);

        return model;
    }

}
