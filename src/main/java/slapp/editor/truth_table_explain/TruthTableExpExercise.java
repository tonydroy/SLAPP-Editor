package slapp.editor.truth_table_explain;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import slapp.editor.DiskUtilities;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.BoxedDRTA;
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
        commentEditor.getActionFactory().saveNow().execute(new ActionEvent());
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



        DecoratedRTA explainDRTA = truthTableExpView.getExplainDRTA();
        RichTextArea explainEditor = explainDRTA.getEditor();
        explainEditor.setDocument(truthTableExpModel.getExplainDocument());
        explainEditor.setPromptText("Explain:");
        explainEditor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(explainDRTA, ControlType.AREA);
        explainEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(explainDRTA, ControlType.AREA);
            }
        });

        //initialize basic formulas control
        List<Document> basicFormulaDocs = truthTableExpModel.getBasicFormulas();
        if (!basicFormulaDocs.isEmpty()) truthTableExpView.getBasicFormulasBoxedDRTAs().clear();
        for (Document doc : basicFormulaDocs ) {
            BoxedDRTA bdrta = truthTableExpView.newFormulaBoxedDRTA();
            RichTextArea rta = bdrta.getRTA();
            rta.setDocument(doc);
            truthTableExpView.getBasicFormulasBoxedDRTAs().add(bdrta);
        }
        truthTableExpView.updateBasicFormulasPaneFromList();
        truthTableExpView.getRowsSpinner().getValueFactory().setValue(tableRows);

        Button setupTableButton = truthTableExpView.getSetupTableButton();
        setupTableButton.setOnAction(e -> {
            tableRows = (int) truthTableExpView.getRowsSpinner().getValue();
            truthTableExpModel.setTableRows(tableRows);
            truthTableExpView.setTableRows(tableRows);

            List<BoxedDRTA> basicFormulasBoxedDRTAs = truthTableExpView.getBasicFormulasBoxedDRTAs();
            List<Document> newBasicFormulaDocs = new ArrayList<>();
            for (BoxedDRTA bdrta : basicFormulasBoxedDRTAs) {
                RichTextArea rta = bdrta.getRTA();
                rta.getActionFactory().saveNow().execute(new ActionEvent());
                Document formulaDoc = rta.getDocument();
                newBasicFormulaDocs.add(formulaDoc);
            }
            truthTableExpModel.setBasicFormulas(newBasicFormulaDocs);
            setupHeadItemsFromModel();
            truthTableExpModel.setEmptyTableContents(tableColumns);
            updateViewTableItems();
            truthTableExpView.updateTableGridFromTableItems();
            exerciseModified = true;
        });

        //table contents

        updateViewTableItems();
        truthTableExpView.updateTableGridFromTableItems();

        truthTableExpView.initializeViewDetails();
    }

    private void updateViewTableItems() {
        setupHeadItemsFromModel();

        Document[] commentDocs = truthTableExpModel.getRowComments();
        BoxedDRTA[] commentBoxedDRTAs = new BoxedDRTA[tableRows];
        for (int i = 0; i < commentDocs.length; i++) {
            Document doc = commentDocs[i];
            BoxedDRTA bdrta = truthTableExpView.newCommentBoxedDRTA();
            RichTextArea rta = bdrta.getRTA();
            rta.setDocument(doc);
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            commentBoxedDRTAs[i] = bdrta;
        }
        truthTableExpView.setRowCommentsArray(commentBoxedDRTAs);


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
                    charField.textProperty().addListener((ob, ov, nv) -> exerciseModified = true);
                    column[j] = charField;
                }
                columns[i] = column;
                ToggleButton highlightButton = truthTableExpView.newHighlightButton(i);
                highlightButton.setSelected(truthTableExpModel.getColumnHighlights()[i]);
                highlightButton.selectedProperty().addListener((ob, ov, nv) -> exerciseModified = true);
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
    public void printExercise() { PrintUtilities.printExercise(getPrintNodes(), truthTableExpModel.getExerciseName()); }

    @Override
    public void exportExerciseToPDF() { PrintUtilities.exportExerciseToPDF(getPrintNodes(), truthTableExpModel.getExerciseName());  }

    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        truthTableExpModel = getTruthTableExpModelFromView();
        TruthTableExpExercise exercise = new TruthTableExpExercise(truthTableExpModel, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth();

        //header node
        Label exerciseName = new Label(truthTableExpModel.getExerciseName());
        exerciseName.setStyle("-fx-font-weight: bold;");
        HBox hbox = new HBox(exerciseName);
        hbox.setPadding(new Insets(0,0,10,0));

        Group headerRoot = new Group();
        Scene headerScene = new Scene(headerRoot);
        headerRoot.getChildren().add(hbox);
        headerRoot.applyCss();
        headerRoot.layout();
        double boxHeight = hbox.getHeight();
        hbox.setPrefHeight(boxHeight);
        nodeList.add(hbox);
        nodeList.add(new Separator(Orientation.HORIZONTAL));

        //statement node
        RichTextArea statementRTA = exercise.getExerciseView().getExerciseStatement().getEditor();
        statementRTA.setEditable(true);
        RichTextAreaSkin statementRTASkin = ((RichTextAreaSkin) statementRTA.getSkin());
        double statementHeight = statementRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
        statementRTA.setPrefHeight(statementHeight + 35.0);
        statementRTA.setContentAreaWidth(nodeWidth);
        statementRTA.setPrefWidth(nodeWidth);
        statementRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(statementRTA);

        Separator statementSeparator = new Separator(Orientation.HORIZONTAL);
        statementSeparator.setPrefWidth(100);
        HBox statementSepBox = new HBox(statementSeparator);
        statementSepBox.setAlignment(Pos.CENTER);
        nodeList.add(statementSepBox);

        //content node
        GridPane tablePane = exercise.getExerciseView().getTableGrid();
        ObservableList<Node> gridItems = tablePane.getChildren();
        ToggleButton[] buttons = exercise.getExerciseView().getHighlightButtons();
        for (int i = 0; i < buttons.length; i ++) {
            tablePane.getChildren().remove(buttons[i]);
        }
        tablePane.setPadding(new Insets(15, 0, 15, 0));
        HBox gridBox = new HBox(tablePane);
        gridBox.setAlignment(Pos.CENTER);
        nodeList.add(gridBox);

        Label leaderLabel = new Label(truthTableExpModel.getChoiceLead());
        CheckBox boxA = new CheckBox(truthTableExpModel.getaPrompt());
        boxA.setSelected(truthTableExpModel.isaSelected());
        CheckBox boxB = new CheckBox(truthTableExpModel.getbPrompt());
        boxB.setSelected(truthTableExpModel.isbSelected());
        Font labelFont = new Font("Noto Serif Combo", 11);
        leaderLabel.setFont(labelFont); boxA.setFont(labelFont); boxB.setFont(labelFont);

        HBox abBox = new HBox(20);
        abBox.setPadding(new Insets(10,10,10,0));
        abBox.getChildren().addAll(leaderLabel, boxA, boxB);
        nodeList.add(abBox);

        RichTextArea explanationRTA = exercise.getExerciseView().getExplainDRTA().getEditor();
        RichTextAreaSkin explanationRTASkin = ((RichTextAreaSkin) explanationRTA.getSkin());
        double explanationHeight = explanationRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
        explanationRTA.setPrefHeight(explanationHeight + 35.0);
        explanationRTA.setContentAreaWidth(nodeWidth);
        explanationRTA.setPrefWidth(nodeWidth);
        explanationRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(explanationRTA);

        Separator contentSeparator = new Separator(Orientation.HORIZONTAL);
        contentSeparator.setStyle("-fx-stroke-dash-array:0.1 5.0");
        contentSeparator.setPrefWidth(100);
        HBox contentSepBox = new HBox(contentSeparator);
        contentSepBox.setAlignment(Pos.CENTER);
        nodeList.add(contentSepBox);

        //comment node
        RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
        RichTextAreaSkin commentRTASkin = ((RichTextAreaSkin) commentRTA.getSkin());
        double commentHeight = commentRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
        commentRTA.setPrefHeight(Math.max(70, commentHeight + 35.0));
        commentRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        commentRTA.setPrefWidth(nodeWidth);
        commentRTA.getStylesheets().clear(); commentRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(commentRTA);

        return nodeList;
    }

    @Override
    public Exercise<TruthTableExpModel, TruthTableExpView> resetExercise() {
        RichTextArea commentRTA = truthTableExpView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        originalModel.setExerciseComment(commentDocument);
        TruthTableExpExercise clearExercise = new TruthTableExpExercise(originalModel, mainWindow);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {

        RichTextArea commentEditor = truthTableExpView.getExerciseComment().getEditor();
        if (commentEditor.isModified()) exerciseModified = true;

        RichTextArea explanationEditor = truthTableExpView.getExplainDRTA().getEditor();
        if (explanationEditor.isModified()) exerciseModified = true;

        BoxedDRTA[] rowComments = truthTableExpView.getRowCommentsArray();
        for (int i = 0; i < rowComments.length; i++) {
            RichTextArea rowCommentEditor = rowComments[i].getRTA();
            if (rowCommentEditor.isModified()) exerciseModified = true;
        }

        return exerciseModified;
    }

    @Override
    public void setExerciseModified(boolean modified) { exerciseModified = modified; }

    @Override
    public void updateContentHeight(Node focusedNode, boolean isRequired) {
        if (isContainer(truthTableExpView.getTableGrid(), focusedNode)) {
            double tableHeight = 0;
            for (int i = 0; i < tableRows + 3; i++) tableHeight = tableHeight + truthTableExpView.getSizers()[i].getHeight();
            mainWindow.getMainView().updatePageSizeLabels(tableHeight + 20);
        }
        else if (isContainer(truthTableExpView.getResultsBox(), focusedNode)) {
            TruthTableExpModel model = getTruthTableExpModelFromView();
            TruthTableExpExercise exercise = new TruthTableExpExercise(model, mainWindow);
            RichTextArea explanationRTA = exercise.getExerciseView().getExplainDRTA().getEditor();
            RichTextAreaSkin explanationRTASkin = ((RichTextAreaSkin) explanationRTA.getSkin());
            double explanationHeight = explanationRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(explanationHeight + 20);
            mainWindow.getLastFocusOwner().requestFocus();
        }
    }

    private boolean isContainer(Node container, Node element) {
        if (element == null)
            return false;
        Node current = element;
        while (current != null) {
            if (current == container)
                return true;
            current = current.getParent();
        }
        return false;
    }

    @Override
    public void updateCommentHeight(boolean isRequired) {
        if (isRequired || mainWindow.getLastFocusOwner() != truthTableExpView.getExerciseComment().getEditor()) {
            mainWindow.setLastFocusOwner(truthTableExpView.getExerciseComment().getEditor());

            TruthTableExpModel model = getTruthTableExpModelFromView();
            TruthTableExpExercise exercise = new TruthTableExpExercise(model, mainWindow);
            RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
            RichTextAreaSkin commentRTASkin = ((RichTextAreaSkin) commentRTA.getSkin());
            double commentHeight = commentRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(Math.max(70, commentHeight + 35));
            mainWindow.getLastFocusOwner().requestFocus();
        }
    }

    @Override
    public void updateStatementHeight(boolean isRequired) {
        if (isRequired || mainWindow.getLastFocusOwner() != truthTableExpView.getExerciseStatementNode()) {
            mainWindow.setLastFocusOwner(truthTableExpView.getExerciseStatementNode());

            TruthTableExpModel model = getTruthTableExpModelFromView();
            TruthTableExpExercise exercise = new TruthTableExpExercise(model, mainWindow);
            RichTextArea statementRTA = exercise.getExerciseView().getExerciseStatement().getEditor();
            statementRTA.setEditable(true);
            RichTextAreaSkin statementRTASkin = ((RichTextAreaSkin) statementRTA.getSkin());
            double statementHeight = statementRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(statementHeight + 35);
            mainWindow.getLastFocusOwner().requestFocus();
        }
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

        List<BoxedDRTA> basicBoxedDRTAs = truthTableExpView.getBasicFormulasBoxedDRTAs();
        List<Document> basicDocs = new ArrayList<>();
        for (BoxedDRTA bdrta : basicBoxedDRTAs) {
            RichTextArea rta = bdrta.getRTA();
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

        BoxedDRTA[] lineCommentBoxedDRTAs = truthTableExpView.getRowCommentsArray();
        Document[] commentDocs = new Document[tableRows];
        for (int i = 0; i < tableRows; i++) {
            BoxedDRTA bdrta = lineCommentBoxedDRTAs[i];
            RichTextArea rta = bdrta.getRTA();
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
