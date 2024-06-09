package slapp.editor.truth_table;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.application.Platform;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import slapp.editor.DiskUtilities;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import slapp.editor.vertical_tree.VerticalTreeExercise;
import slapp.editor.vertical_tree.VerticalTreeModel;

import java.util.ArrayList;
import java.util.List;

public class TruthTableExercise implements Exercise<TruthTableModel, TruthTableView> {
    private MainWindow mainWindow;
    private TruthTableModel truthTableModel;
    private TruthTableView truthTableView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;
    private ParseDocForTTable docParser;
    private int tableColumns = 0;
    private int tableRows = 0;


    //applies when table elements are set to model with rows != 0
    public TruthTableExercise(TruthTableModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.truthTableModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.truthTableView = new TruthTableView(mainView);
        docParser = new ParseDocForTTable(truthTableModel.getUnaryOperators(), truthTableModel.getBinaryOperators());
        tableRows = truthTableModel.getTableRows();
        setTruthTableView();
    }

    //applies when table elements need to be set to empty table
    public TruthTableExercise(TruthTableModel model, MainWindow mainWindow, boolean create) {
        this.mainWindow = mainWindow;
        this.truthTableModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.truthTableView = new TruthTableView(mainView);
        docParser = new ParseDocForTTable(truthTableModel.getUnaryOperators(), truthTableModel.getBinaryOperators());
        tableRows = truthTableModel.getTableRows();
        generateEmptyTableModel();                                 //** the difference
        setTruthTableView();
    }

    public void generateEmptyTableModel() {
        setupHeadItemsFromModel();
        truthTableModel.setEmptyTableContents(tableColumns);
    }

    private void setTruthTableView() {
        truthTableView.setExerciseName(truthTableModel.getExerciseName());
        truthTableView.setStatementPrefHeight(truthTableModel.getStatementPrefHeight());
        truthTableView.setCommentPrefHeight(truthTableModel.getCommentPrefHeight());


        truthTableView.setTableRows(tableRows);

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(truthTableModel.getExerciseStatement());

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
        commentEditor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(commentDRTA, ControlType.AREA);
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        truthTableView.setExerciseComment(commentDRTA);





        //initialize basic formulas control
        List<Document> basicFormulaDocs = truthTableModel.getBasicFormulas();
        if (!basicFormulaDocs.isEmpty()) truthTableView.getBasicFormulasBoxedDRTAList().clear();
        for (Document doc : basicFormulaDocs ) {
            BoxedDRTA bdrta = truthTableView.newFormulaDRTAField();
            RichTextArea rta = bdrta.getRTA();
            rta.setDocument(doc);
            truthTableView.getBasicFormulasBoxedDRTAList().add(bdrta);
        }
        truthTableView.updateBasicFormulasPaneFromList();
        truthTableView.getRowsSpinner().getValueFactory().setValue(tableRows);

        Button setupTableButton = truthTableView.getSetupTableButton();
        setupTableButton.setOnAction(e -> {
            tableRows = (int) truthTableView.getRowsSpinner().getValue();
            truthTableModel.setTableRows(tableRows);
            truthTableView.setTableRows(tableRows);

            List<BoxedDRTA> basicFormulasBoxedDRTAList = truthTableView.getBasicFormulasBoxedDRTAList();
            List<Document> newBasicFormulaDocs = new ArrayList<>();
            for (BoxedDRTA bdrta : basicFormulasBoxedDRTAList) {
                RichTextArea rta = bdrta.getRTA();
                rta.getActionFactory().saveNow().execute(new ActionEvent());
                Document formulaDoc = rta.getDocument();
                newBasicFormulaDocs.add(formulaDoc);
            }
            truthTableModel.setBasicFormulas(newBasicFormulaDocs);
            setupHeadItemsFromModel();
            truthTableModel.setEmptyTableContents(tableColumns);
            updateViewTableItems();
            truthTableView.updateTableGridFromTableItems();
            exerciseModified = true;
            Platform.runLater(() -> {
                mainView.updateContentWidthProperty();
                mainView.updateContentHeightProperty();
            });
        });

        //table contents

        updateViewTableItems();
        truthTableView.updateTableGridFromTableItems();

        truthTableView.initializeViewDetails();
    }

    private void updateViewTableItems() {
        setupHeadItemsFromModel();

        Document[] commentDocs = truthTableModel.getRowComments();
        BoxedDRTA[] commentBoxedDRTAs = new BoxedDRTA[tableRows];
        for (int i = 0; i < commentDocs.length; i++) {
            Document doc = commentDocs[i];
            BoxedDRTA bdrta = truthTableView.newCommentBoxedDRTA();
            RichTextArea rta = bdrta.getRTA();
            rta.setDocument(doc);
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            commentBoxedDRTAs[i] = bdrta;
        }
        truthTableView.setRowCommentsArray(commentBoxedDRTAs);


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
                    charField.textProperty().addListener((ob, ov, nv) -> exerciseModified = true);
                    column[j] = charField;
                }
                columns[i] = column;
                ToggleButton highlightButton = truthTableView.newHighlightButton(i);
                highlightButton.setSelected(truthTableModel.getColumnHighlights()[i]);
                highlightButton.selectedProperty().addListener((ob, ov, nv) -> exerciseModified = true);
                buttons[i] = highlightButton;

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
        if (basicFormulas.size() == 0) {
            TableHeadItem stubHead = new TableHeadItem(new TextFlow(new Text("  ")), new ColumnConstraints(10));
            stubHead.setBlankColumn(true);
            headList.add(stubHead);
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
        if (mainFormulas.size() > 0) {
            List<TableHeadItem> finalHeadItems = docParser.generateHeadItems(mainFormulas.get(mainFormulas.size() - 1));
            headList.addAll(finalHeadItems);
        }
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
        boolean success = DiskUtilities.saveExercise(saveAs, getTruthTableExpModelFromView());
        if (success) exerciseModified = false;
    }




    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        truthTableModel = getTruthTableExpModelFromView();
        TruthTableExercise exercise = new TruthTableExercise(truthTableModel, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth();

        //header node
        Label exerciseName = new Label(truthTableModel.getExerciseName());
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
        statementRTA.prefHeightProperty().unbind();
        statementRTA.prefWidthProperty().unbind();
        double statementHeight = mainView.getRTATextHeight(statementRTA);
        statementRTA.setPrefHeight(statementHeight + 35.0);
        statementRTA.setContentAreaWidth(nodeWidth);
        statementRTA.setPrefWidth(nodeWidth);
        statementRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(statementRTA);

        Separator statementSeparator = new Separator(Orientation.HORIZONTAL);
        statementSeparator.setPrefWidth(100);
        HBox statementSepBox = new HBox(statementSeparator);
        statementSepBox.setMinWidth(PrintUtilities.getPageWidth());
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

        Separator contentSeparator = new Separator(Orientation.HORIZONTAL);
        contentSeparator.setStyle("-fx-stroke-dash-array:0.1 5.0");
        contentSeparator.setPrefWidth(100);
        HBox contentSepBox = new HBox(contentSeparator);
        contentSepBox.setMinWidth(PrintUtilities.getPageWidth());
        contentSepBox.setAlignment(Pos.CENTER);
        nodeList.add(contentSepBox);

        //comment node
        RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
        commentRTA.prefHeightProperty().unbind();
        commentRTA.prefWidthProperty().unbind();
        double commentHeight = mainView.getRTATextHeight(commentRTA);
        commentRTA.setPrefHeight(Math.max(70, commentHeight + 35.0));
        commentRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        commentRTA.setPrefWidth(nodeWidth);
        commentRTA.getStylesheets().clear(); commentRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(commentRTA);

        return nodeList;
    }

    @Override
    public Exercise<TruthTableModel, TruthTableView> resetExercise() {
        RichTextArea commentRTA = truthTableView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        TruthTableModel originalModel = (TruthTableModel) (truthTableModel.getOriginalModel());
        originalModel.setExerciseComment(commentDocument);
        TruthTableExercise clearExercise = new TruthTableExercise(originalModel, mainWindow, true);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {

        RichTextArea commentEditor = truthTableView.getExerciseComment().getEditor();
        if (commentEditor.isModified()) exerciseModified = true;



        BoxedDRTA[] rowComments = truthTableView.getRowCommentsArray();
        for (int i = 0; i < rowComments.length; i++) {
            RichTextArea rowCommentEditor = rowComments[i].getRTA();
            if (rowCommentEditor.isModified()) exerciseModified = true;
        }

        return exerciseModified;
    }

    @Override
    public void setExerciseModified(boolean modified) { exerciseModified = modified; }

    @Override
    public ExerciseModel<TruthTableModel> getExerciseModelFromView() {
        return (ExerciseModel) getTruthTableExpModelFromView();
    }

    private TruthTableModel getTruthTableExpModelFromView() {
        TruthTableModel model = new TruthTableModel();
        model.setExerciseName(truthTableView.getExerciseName());
        model.setOriginalModel(truthTableModel.getOriginalModel());
        model.setStarted(truthTableModel.isStarted() || exerciseModified);
        model.setStatementPrefHeight(truthTableView.getExerciseStatement().getEditor().getPrefHeight());
        model.setCommentPrefHeight(truthTableView.getCommentPrefHeight());
        model.setExerciseStatement(truthTableModel.getExerciseStatement());

        RichTextArea commentRTA = truthTableView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseComment(commentRTA.getDocument());

        model.setUnaryOperators(truthTableModel.getUnaryOperators());
        model.setBinaryOperators(truthTableModel.getBinaryOperators());
        model.setMainFormulas(truthTableModel.getMainFormulas());

        List<BoxedDRTA> basicBoxedDRTAs = truthTableView.getBasicFormulasBoxedDRTAList();
        List<Document> basicDocs = new ArrayList<>();
        for (BoxedDRTA bdrta : basicBoxedDRTAs) {
            RichTextArea rta = bdrta.getRTA();
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            Document doc = rta.getDocument();
            basicDocs.add(doc);
        }
        model.setBasicFormulas(basicDocs);

        String[][] tableStrVals = new String[tableColumns][tableRows];
        TextField[][] tableFields = truthTableView.getTableFields();
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

        BoxedDRTA[] rowCommentsArray = truthTableView.getRowCommentsArray();
        Document[] commentDocs = new Document[tableRows];
        for (int i = 0; i < tableRows; i++) {
            BoxedDRTA bdrta = rowCommentsArray[i];
            RichTextArea rta = bdrta.getRTA();
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            Document doc = rta.getDocument();
            commentDocs[i] = doc;
        }
        model.setRowComments(commentDocs);

        ToggleButton[] highlightButtons = truthTableView.getHighlightButtons();
        boolean[] highlightValues = new boolean[tableColumns];
        for (int i = 0; i < tableColumns; i++) {
            if (highlightButtons[i] != null) {
                highlightValues[i] = highlightButtons[i].isSelected();
            }
        }
        model.setColumnHighlights(highlightValues);

        model.setConclusionDivider(truthTableModel.isConclusionDivider());
        model.setTableRows(tableRows);


        return model;
    }

}
