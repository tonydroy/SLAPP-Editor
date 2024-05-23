package slapp.editor.truth_table_generate;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.application.Platform;
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

public class TruthTableGenExercise implements Exercise<TruthTableGenModel, TruthTableGenView> {
    private MainWindow mainWindow;
    private TruthTableGenModel truthTableGenModel;
    private TruthTableGenView truthTableGenView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;
    private ParseDocForTTable docParser;
    private int tableColumns = 0;
    private int tableRows = 0;
    private ColumnConstraints spacerConstraint;



    //applies when table elements are set to model with rows != 0
    public TruthTableGenExercise(TruthTableGenModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.truthTableGenModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.truthTableGenView = new TruthTableGenView(mainView);
        docParser = new ParseDocForTTable(truthTableGenModel.getUnaryOperators(), truthTableGenModel.getBinaryOperators());
        tableRows = truthTableGenModel.getTableRows();
        setTruthTableView();

    }

    //applies when table elements need to be set to empty table
    public TruthTableGenExercise(TruthTableGenModel model, MainWindow mainWindow, boolean create) {
        this.mainWindow = mainWindow;
        this.truthTableGenModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.truthTableGenView = new TruthTableGenView(mainView);
        docParser = new ParseDocForTTable(truthTableGenModel.getUnaryOperators(), truthTableGenModel.getBinaryOperators());
        tableRows = truthTableGenModel.getTableRows();
        generateEmptyTableModel();                                 //** the difference
        setTruthTableView();
    }

    public void generateEmptyTableModel() {
        setupHeadItemsFromModel();
        truthTableGenModel.setEmptyTableContents(tableColumns);
        truthTableGenView.getTableGrid().setPrefHeight(100);
    }

    private void setTruthTableView() {
        spacerConstraint = new ColumnConstraints(10);

        truthTableGenView.setExerciseName(truthTableGenModel.getExerciseName());
        truthTableGenView.setExplainPrompt(truthTableGenModel.getExplainPrompt());
        truthTableGenView.setGeneratePrompt(truthTableGenModel.getGeneratePrompt());
        truthTableGenView.setTableRows(tableRows);

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(truthTableGenModel.getExerciseStatement());
        truthTableGenView.setStatementPrefHeight(truthTableGenModel.getStatementPrefHeight());
        mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        truthTableGenView.setExerciseStatement(statementDRTA);




        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(truthTableGenModel.getExerciseComment());
        commentEditor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(commentDRTA, ControlType.AREA);
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        truthTableGenView.setExerciseComment(commentDRTA);

        truthTableGenView.getChoiceLeadLabel().setText(truthTableGenModel.getChoiceLead());
        CheckBox aCheckBox = truthTableGenView.getaCheckBox();
        CheckBox bCheckBox = truthTableGenView.getbCheckBox();
        aCheckBox.setText(truthTableGenModel.getaPrompt());
        aCheckBox.setSelected(truthTableGenModel.isaSelected());
        aCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) bCheckBox.setSelected(false);
                exerciseModified = true;
            }
        });
        bCheckBox.setText(truthTableGenModel.getbPrompt());
        bCheckBox.setSelected(truthTableGenModel.isbSelected());
        bCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) aCheckBox.setSelected(false);
                exerciseModified = true;
            }
        });



        DecoratedRTA explainDRTA = truthTableGenView.getExplainDRTA();
        RichTextArea explainEditor = explainDRTA.getEditor();
        explainEditor.setDocument(truthTableGenModel.getExplainDocument());
        explainEditor.setPromptText("Explain:");
        explainEditor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(explainDRTA, ControlType.AREA);
        explainEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(explainDRTA, ControlType.AREA);
            }
        });

        DecoratedRTA interpretationDRTA = truthTableGenView.getExerciseInterpretation();
        RichTextArea interpretationEditor = interpretationDRTA.getEditor();
        interpretationEditor.setDocument(truthTableGenModel.getExerciseInterpretation());
        interpretationEditor.setPromptText("Interpretation/Translation:");
        interpretationEditor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(interpretationDRTA, ControlType.AREA);
        interpretationEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(interpretationDRTA, ControlType.AREA);
            }
        });



        //initialize basic and main formula controls
        List<Document> basicFormulaDocs = truthTableGenModel.getBasicFormulas();
        if (!basicFormulaDocs.isEmpty()) truthTableGenView.getBasicFormulasBoxedDRTAs().clear();
        for (Document doc : basicFormulaDocs ) {
            BoxedDRTA bdrta = truthTableGenView.newFormulaBoxedDRTA();
            RichTextArea rta = bdrta.getRTA();
            rta.setDocument(doc);
            truthTableGenView.getBasicFormulasBoxedDRTAs().add(bdrta);
        }
        truthTableGenView.updateBasicFormulasPaneFromList();

        List<Document> mainFormulaDocs = truthTableGenModel.getMainFormulas();
        if (!mainFormulaDocs.isEmpty()) truthTableGenView.getMainFormulasBoxedDRTAs().clear();
        for (Document doc : mainFormulaDocs ) {
            BoxedDRTA bdrta = truthTableGenView.newFormulaBoxedDRTA();
            RichTextArea rta = bdrta.getRTA();
            rta.setDocument(doc);
            truthTableGenView.getMainFormulasBoxedDRTAs().add(bdrta);
        }
        truthTableGenView.updateMainFormulasPaneFromList();


        truthTableGenView.getRowsSpinner().getValueFactory().setValue(tableRows);

        Button setupTableButton = truthTableGenView.getSetupTableButton();
        setupTableButton.setOnAction(e -> {
            tableRows = (int) truthTableGenView.getRowsSpinner().getValue();
            truthTableGenModel.setTableRows(tableRows);
            truthTableGenView.setTableRows(tableRows);

            List<BoxedDRTA> basicFormulasBoxedDRTAs = truthTableGenView.getBasicFormulasBoxedDRTAs();
            List<Document> newBasicFormulaDocs = new ArrayList<>();
            for (BoxedDRTA bdrta : basicFormulasBoxedDRTAs) {
                RichTextArea rta = bdrta.getRTA();
                rta.getActionFactory().saveNow().execute(new ActionEvent());
                Document formulaDoc = rta.getDocument();
                newBasicFormulaDocs.add(formulaDoc);
            }
            truthTableGenModel.setBasicFormulas(newBasicFormulaDocs);

            List<BoxedDRTA> mainFormulasBoxedDRTAs = truthTableGenView.getMainFormulasBoxedDRTAs();
            List<Document> newMainFormulaDocs = new ArrayList<>();
            for (BoxedDRTA bdrta : mainFormulasBoxedDRTAs) {
                RichTextArea rta = bdrta.getRTA();
                rta.getActionFactory().saveNow().execute(new ActionEvent());
                Document formulaDoc = rta.getDocument();
                newMainFormulaDocs.add(formulaDoc);
            }
            truthTableGenModel.setMainFormulas(newMainFormulaDocs);

            setupHeadItemsFromModel();
            truthTableGenModel.setEmptyTableContents(tableColumns);
            updateViewTableItems();
            truthTableGenView.updateTableGridFromTableItems();
            exerciseModified = true;
            Platform.runLater(() -> {
                mainView.updateContentWidthProperty();
                mainView.updateContentHeightProperty();
            });
        });

        //table contents

        updateViewTableItems();
        truthTableGenView.updateTableGridFromTableItems();

        truthTableGenView.initializeViewDetails();
    }

    private void updateViewTableItems() {
        setupHeadItemsFromModel();

        Document[] commentDocs = truthTableGenModel.getRowComments();
        BoxedDRTA[] commentBoxedDRTAs = new BoxedDRTA[tableRows];
        for (int i = 0; i < commentDocs.length; i++) {
            Document doc = commentDocs[i];
            BoxedDRTA bdrta = truthTableGenView.newCommentBoxedDRTA();
            RichTextArea rta = bdrta.getRTA();
            rta.setDocument(doc);
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            commentBoxedDRTAs[i] = bdrta;
        }
        truthTableGenView.setRowCommentsArray(commentBoxedDRTAs);


        ToggleButton[] buttons = new ToggleButton[tableColumns];
        TextField[][] columns = new TextField[tableColumns][tableRows];
        List<TableHeadItem> tableHeadItems = truthTableGenView.getTableHeadItemsList();


        for (int i = 0; i < tableColumns; i++) {
            TableHeadItem headItem = tableHeadItems.get(i);
            if (!headItem.isBlankColumn()) {
                TextField[] column = new TextField[tableRows];
                for (int j = 0; j < tableRows; j++) {
                    TextField charField = truthTableGenView.newSingleCharTextField(i, j);
                    charField.setText(truthTableGenModel.getTableValues()[i][j]);
                    charField.textProperty().addListener((ob, ov, nv) -> exerciseModified = true);
                    column[j] = charField;
                }
                columns[i] = column;
                ToggleButton highlightButton = truthTableGenView.newHighlightButton(i);
                highlightButton.setSelected(truthTableGenModel.getColumnHighlights()[i]);
                highlightButton.selectedProperty().addListener((ob, ov, nv) -> exerciseModified = true);
                buttons[i] = highlightButton;

            }
        }
        truthTableGenView.setTableFields(columns);
        truthTableGenView.setHighlightButtons(buttons);
    }

    private void setupHeadItemsFromModel() {

        List<TableHeadItem> headList = new ArrayList<>();

        //basic formulas head items
        List<Document> basicFormulas = truthTableGenModel.getBasicFormulas();
        for (int i = 0; i < basicFormulas.size(); i++) {
            List<TableHeadItem> basicHeadItems = docParser.generateHeadItems(basicFormulas.get(i));
            headList.addAll(basicHeadItems);
            TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), spacerConstraint);
            spaceColHead.setBlankColumn(true);
            headList.add(spaceColHead);
        }
        if (basicFormulas.size() == 0) {
            TableHeadItem stubHead = new TableHeadItem(new TextFlow(new Text("  ")), spacerConstraint);
            stubHead.setBlankColumn(true);
            headList.add(stubHead);
        }
        TableHeadItem dividerHead = new TableHeadItem(new TextFlow(new Text("")), spacerConstraint);
        dividerHead.setDividerColumn(true); dividerHead.setBlankColumn(true);
        headList.add(dividerHead);


        //main formula head items
        List<Document> mainFormulas = truthTableGenModel.getMainFormulas();
        for (int i = 0; i < mainFormulas.size() - 1; i++) {
            List<TableHeadItem> mainHeadItems = docParser.generateHeadItems(mainFormulas.get(i));
            headList.addAll(mainHeadItems);
            TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), spacerConstraint);
            spaceColHead.setBlankColumn(true);
            headList.add(spaceColHead);
        }
        if (truthTableGenModel.isConclusionDivider()) {
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
        TableHeadItem spaceColHead = new TableHeadItem(new TextFlow(new Text("")), spacerConstraint);
        spaceColHead.setBlankColumn(true);
        headList.add(spaceColHead);

        truthTableGenView.setTableHeadItemsList(headList);
        tableColumns = headList.size();
    }

    @Override
    public TruthTableGenModel getExerciseModel() { return truthTableGenModel;  }

    @Override
    public TruthTableGenView getExerciseView() { return truthTableGenView; }

    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getTruthTableGenModelFromView());
        if (success) exerciseModified = false;
    }



    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        truthTableGenModel = getTruthTableGenModelFromView();
        TruthTableGenExercise exercise = new TruthTableGenExercise(truthTableGenModel, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth();

        //header node
        Label exerciseName = new Label(truthTableGenModel.getExerciseName());
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

        //interpretation node
        RichTextArea interpretationRTA = exercise.getExerciseView().getExerciseInterpretation().getEditor();
        RichTextAreaSkin interpretationRTASkin = ((RichTextAreaSkin) interpretationRTA.getSkin());
        double interpretationHeight = interpretationRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
        interpretationRTA.setPrefHeight(Math.max(70, interpretationHeight + 35.0));
        interpretationRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        interpretationRTA.setPrefWidth(nodeWidth);
        interpretationRTA.getStylesheets().clear(); interpretationRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(interpretationRTA);

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

        Label leaderLabel = new Label(truthTableGenModel.getChoiceLead());
        CheckBox boxA = new CheckBox(truthTableGenModel.getaPrompt());
        boxA.setSelected(truthTableGenModel.isaSelected());
        CheckBox boxB = new CheckBox(truthTableGenModel.getbPrompt());
        boxB.setSelected(truthTableGenModel.isbSelected());
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
    public Exercise<TruthTableGenModel, TruthTableGenView> resetExercise() {
        RichTextArea commentRTA = truthTableGenView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        TruthTableGenModel originalModel = (TruthTableGenModel) (truthTableGenModel.getOriginalModel());
        originalModel.setExerciseComment(commentDocument);
        TruthTableGenExercise clearExercise = new TruthTableGenExercise(originalModel, mainWindow, true);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {

        RichTextArea commentEditor = truthTableGenView.getExerciseComment().getEditor();
        if (commentEditor.isModified()) exerciseModified = true;

        RichTextArea interpretationEditor = truthTableGenView.getExerciseInterpretation().getEditor();
        if (interpretationEditor.isModified()) exerciseModified = true;

        RichTextArea explanationEditor = truthTableGenView.getExplainDRTA().getEditor();
        if (explanationEditor.isModified()) exerciseModified = true;

        BoxedDRTA[] rowComments = truthTableGenView.getRowCommentsArray();
        for (int i = 0; i < rowComments.length; i++) {
            RichTextArea rowCommentEditor = rowComments[i].getRTA();
            if (rowCommentEditor.isModified()) exerciseModified = true;
        }

        return exerciseModified;
    }

    @Override
    public void setExerciseModified(boolean modified) { exerciseModified = modified; }

    @Override
    public ExerciseModel<TruthTableGenModel> getExerciseModelFromView() {
        return (ExerciseModel) getTruthTableGenModelFromView();
    }

    private TruthTableGenModel getTruthTableGenModelFromView() {
        TruthTableGenModel model = new TruthTableGenModel();
        model.setExerciseName(truthTableGenView.getExerciseName());
        model.setGeneratePrompt(truthTableGenModel.getGeneratePrompt());
        model.setExplainPrompt(truthTableGenModel.getExplainPrompt());
        model.setOriginalModel(truthTableGenModel.getOriginalModel());
        model.setStarted(truthTableGenModel.isStarted() || exerciseModified);
        model.setStatementPrefHeight(truthTableGenView.getExerciseStatement().getEditor().getPrefHeight());
        model.setExerciseStatement(truthTableGenModel.getExerciseStatement());

        RichTextArea commentRTA = truthTableGenView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseComment(commentRTA.getDocument());

        RichTextArea interpretationRTA = truthTableGenView.getExerciseInterpretation().getEditor();
        interpretationRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseInterpretation(interpretationRTA.getDocument());



        model.setUnaryOperators(truthTableGenModel.getUnaryOperators());
        model.setBinaryOperators(truthTableGenModel.getBinaryOperators());
        model.setMainFormulas(truthTableGenModel.getMainFormulas());

        List<BoxedDRTA> basicBoxedDRTAs = truthTableGenView.getBasicFormulasBoxedDRTAs();
        List<Document> basicDocs = new ArrayList<>();
        for (BoxedDRTA bdrta : basicBoxedDRTAs) {
            RichTextArea rta = bdrta.getRTA();
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            Document doc = rta.getDocument();
            basicDocs.add(doc);
        }
        model.setBasicFormulas(basicDocs);

        List<BoxedDRTA> mainBoxedDRTAs = truthTableGenView.getMainFormulasBoxedDRTAs();
        List<Document> mainDocs = new ArrayList<>();
        for (BoxedDRTA bdrta : mainBoxedDRTAs) {
            RichTextArea rta = bdrta.getRTA();
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            Document doc = rta.getDocument();
            mainDocs.add(doc);
        }
        model.setMainFormulas(mainDocs);


        String[][] tableStrVals = new String[tableColumns][tableRows];
        TextField[][] tableFields = truthTableGenView.getTableFields();
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

        BoxedDRTA[] lineCommentBoxedDRTAs = truthTableGenView.getRowCommentsArray();
        Document[] commentDocs = new Document[tableRows];
        for (int i = 0; i < tableRows; i++) {
            BoxedDRTA bdrta = lineCommentBoxedDRTAs[i];
            RichTextArea rta = bdrta.getRTA();
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            Document doc = rta.getDocument();
            commentDocs[i] = doc;
        }
        model.setRowComments(commentDocs);

        ToggleButton[] highlightButtons = truthTableGenView.getHighlightButtons();
        boolean[] highlightValues = new boolean[tableColumns];
        for (int i = 0; i < tableColumns; i++) {
            if (highlightButtons[i] != null) {
                highlightValues[i] = highlightButtons[i].isSelected();
            }
        }
        model.setColumnHighlights(highlightValues);

        model.setConclusionDivider(truthTableGenModel.isConclusionDivider());
        model.setChoiceLead(truthTableGenModel.getChoiceLead());
        model.setaPrompt(truthTableGenModel.getaPrompt());
        model.setaSelected(truthTableGenView.getaCheckBox().isSelected());
        model.setbPrompt(truthTableGenModel.getbPrompt());
        model.setbSelected(truthTableGenView.getbCheckBox().isSelected());

        RichTextArea explainRTA = truthTableGenView.getExplainDRTA().getEditor();
        explainRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExplainDocument(explainRTA.getDocument());
        model.setTableRows(tableRows);

        return model;
    }

}
