package slapp.editor.abefg_explain;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import slapp.editor.DiskUtilities;
import slapp.editor.EditorAlerts;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class ABEFGexercise implements Exercise<ABEFGmodel, ABEFGview> {
    private MainWindow mainWindow;
    private ABEFGmodel abefgModel;
    private ABEFGmodel originalModel;
    private ABEFGview abefgView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;
    private int lastPageNum = -1;
    private Font labelFont = new Font("Noto Serif Combo", 11);

    public ABEFGexercise(ABEFGmodel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.abefgModel = model;
        this.originalModel = model;
        this.mainView = mainWindow.getMainView();
        this.abefgView = new ABEFGview(mainView);

        setEditView();
    }

    private void setEditView() {

        abefgView.setExerciseName(abefgModel.getExerciseName());
        ABEFGmodelExtra modelFields = abefgModel.getModelFields();

        abefgView.getLeaderLabelAB().setText(modelFields.getLeaderAB());
        abefgView.getLeaderLabelEFG().setText(modelFields.getLeaderEFG());
        CheckBox checkBoxA = abefgView.getCheckBoxA();
        CheckBox checkBoxB = abefgView.getCheckBoxB();
        CheckBox checkBoxE = abefgView.getCheckBoxE();
        CheckBox checkBoxF = abefgView.getCheckBoxF();
        CheckBox checkBoxG = abefgView.getCheckBoxG();

        checkBoxA.setText(modelFields.getPromptA());
        checkBoxA.setSelected(modelFields.getValueA());
        checkBoxA.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) checkBoxB.setSelected(false);
                exerciseModified = true;
            }
        });

        checkBoxB.setText(modelFields.getPromptB());
        checkBoxB.setSelected(modelFields.getValueB());
        checkBoxB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) checkBoxA.setSelected(false);
                exerciseModified = true;
            }
        });

        checkBoxE.setText(modelFields.getPromptE());
        checkBoxE.setSelected(modelFields.getValueE());
        checkBoxE.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) {
                    checkBoxF.setSelected(false);
                    checkBoxG.setSelected(false);
                    exerciseModified = true;
                }
            }
        });

        checkBoxF.setText(modelFields.getPromptF());
        checkBoxF.setSelected(modelFields.getValueF());
        checkBoxF.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) {
                    checkBoxE.setSelected(false);
                    checkBoxG.setSelected(false);
                    exerciseModified = true;
                }
            }
        });

        checkBoxG.setText(modelFields.getPromptG());
        checkBoxE.setSelected(modelFields.getValueG());
        checkBoxG.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv == true) {
                    checkBoxE.setSelected(false);
                    checkBoxF.setSelected(false);
                    exerciseModified = true;
                }
            }
        });

        abefgView.setContentPrompt(abefgModel.getContentPrompt());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(abefgModel.getExerciseStatement());
        abefgView.setStatementPrefHeight(abefgModel.getStatementPrefHeight());
        mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        abefgView.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(abefgModel.getExerciseComment());
        mainView.editorInFocus(commentDRTA, ControlType.AREA);
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        abefgView.setExerciseComment(commentDRTA);

        ArrayList<DecoratedRTA> contentList = new ArrayList<>();
        for (Document doc : abefgModel.getExercisePageDocs()) {
            DecoratedRTA drta = new DecoratedRTA();
            RichTextArea editor = drta.getEditor();
            editor.setDocument(doc);
            editor.getActionFactory().saveNow().execute(new ActionEvent());
            mainView.editorInFocus(drta, ControlType.AREA);
            editor.focusedProperty().addListener((o, ov, nv) -> {
                if (nv) {
                    mainView.editorInFocus(drta, ControlType.AREA);
                }
            });
            contentList.add(drta);
        }
        abefgView.setContentPageList(contentList);

        abefgView.initializeViewDetails();
        abefgView.getAddPageButton().setOnAction(e -> addPageAction());
        abefgView.getRemovePageButton().setOnAction(e -> removePageAction());
    }

    private void addPageAction() {
        int newPageIndex = abefgView.getContentPageIndex() + 1;
        abefgModel.addBlankExercisePage(newPageIndex);

        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea editor = drta.getEditor();
        editor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(drta, ControlType.AREA);
        editor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(drta, ControlType.AREA);
            }
        });
        abefgView.addBlankContentPage(newPageIndex, drta);
    }

    private void removePageAction() {
        if (abefgModel.getExercisePageDocs().size() <= 1) {
            EditorAlerts.showSimpleAlert("Cannot Remove", "Your response must include at least one page.");
        }
        else {
            int currentPageIndex = abefgView.getContentPageIndex();
            boolean okContinue = true;
            if (abefgView.getContentPageList().get(currentPageIndex).getEditor().isModified()) {
                Alert confirm = EditorAlerts.confirmationAlert("Confirm Remove", "This page appears to have been changed.  Continue to remove?");
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() != OK) okContinue = false;
            }
            if (okContinue) {
                abefgModel.getExercisePageDocs().remove(currentPageIndex);
                abefgView.removeContentPage(currentPageIndex);
                exerciseModified = true;
            }
        }
    }

    @Override
    public ABEFGmodel getExerciseModel() {
        return abefgModel;
    }
    @Override
    public ABEFGview getExerciseView() {
        return abefgView;
    }
    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getABEFGmodelFromView());
        if (success) exerciseModified = false;
    }
    @Override
    public void printExercise() {
        List<Node> printNodes = getPrintNodes();
        PrintUtilities.printExercise(printNodes, abefgModel.getExerciseName());
    }
    @Override
    public void exportExerciseToPDF() {
        List<Node> printNodes = getPrintNodes();
        PrintUtilities.exportExerciseToPDF(printNodes, abefgModel.getExerciseName()); }
    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        abefgModel = getABEFGmodelFromView();
        ABEFGexercise exercise = new ABEFGexercise(abefgModel, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth();

        //header node
        Label exerciseName = new Label(abefgModel.getExerciseName());
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
        statementRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementRTA.setPrefWidth(nodeWidth);
        statementRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(statementRTA);

        Separator statementSeparator = new Separator(Orientation.HORIZONTAL);
        statementSeparator.setPrefWidth(100);
        HBox statementSepBox = new HBox(statementSeparator);
        statementSepBox.setAlignment(Pos.CENTER);
        nodeList.add(statementSepBox);


        //content nodes
        ABEFGmodelExtra fields = abefgModel.getModelFields();
        Label leaderLabelAB = new Label(fields.getLeaderAB());
        CheckBox boxA = new CheckBox(fields.getPromptA());
        boxA.setSelected(fields.getValueA());
        CheckBox boxB = new CheckBox(fields.getPromptB());
        boxB.setSelected(fields.getValueB());
        leaderLabelAB.setFont(labelFont); boxA.setFont(labelFont); boxB.setFont(labelFont);
        HBox abBox = new HBox(20);
        abBox.getChildren().addAll(leaderLabelAB, boxA, boxB);

        Label leaderLabelEFG = new Label(fields.getLeaderEFG());
        CheckBox boxE = new CheckBox(fields.getPromptE());
        boxE.setSelected(fields.getValueE());
        CheckBox boxF = new CheckBox(fields.getPromptF());
        boxF.setSelected(fields.getValueF());
        CheckBox boxG = new CheckBox(fields.getPromptG());
        boxG.setSelected(fields.getValueG());
        leaderLabelEFG.setFont(labelFont); boxE.setFont(labelFont); boxF.setFont(labelFont); boxG.setFont(labelFont);
        HBox efgBox = new HBox(20);
        efgBox.getChildren().addAll(leaderLabelEFG, boxE, boxF, boxG);

        VBox checksBox = new VBox(10, abBox, efgBox);
        checksBox.setPadding(new Insets(10,0,20,0));

        nodeList.add(checksBox);

        ArrayList<DecoratedRTA> pageList = exercise.getExerciseView().getContentPageList();
        for (DecoratedRTA drta : pageList) {
            RichTextArea pageRTA = drta.getEditor();
            RichTextAreaSkin pageRTASkin = ((RichTextAreaSkin) pageRTA.getSkin());
            double pageHeight = pageRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            pageRTA.setPrefHeight(pageHeight + 35.0);
            pageRTA.setContentAreaWidth(nodeWidth);
            pageRTA.setPrefWidth(nodeWidth);
            nodeList.add(pageRTA);
        }

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
    public ABEFGexercise resetExercise() {
        RichTextArea commentRTA = abefgView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        originalModel.setExerciseComment(commentDocument);
        ABEFGexercise clearExercise = new ABEFGexercise(originalModel, mainWindow);
        return clearExercise;

    }

    @Override
    public boolean isExerciseModified() {
        RichTextArea commentEditor = abefgView.getExerciseComment().getEditor();
        if (commentEditor.isModified()) exerciseModified = true;

        ArrayList<DecoratedRTA> exerciseContent = abefgView.getContentPageList();
        for (DecoratedRTA drta : exerciseContent) {
            RichTextArea editor = drta.getEditor();
            if (editor.isModified()) {
                exerciseModified = true;
            }
        }
        return exerciseModified;
    }
    @Override
    public void setExerciseModified(boolean modified) {
        this.exerciseModified = modified;
    }
    @Override
    public void updateContentHeight(Node focusedNode, boolean isRequired){
        int contentPageNum = abefgView.getContentPageIndex();
        if (isRequired || mainWindow.getLastFocusOwner() != abefgView.getExerciseContentNode() || lastPageNum != contentPageNum) {
            mainWindow.setLastFocusOwner(abefgView.getExerciseContentNode());
            lastPageNum = contentPageNum;

            ABEFGmodel model = getABEFGmodelFromView();
            ABEFGexercise exercise = new ABEFGexercise(model, mainWindow);
            ArrayList<DecoratedRTA> pageList = exercise.getExerciseView().getContentPageList();
            RichTextArea pageRTA = pageList.get(contentPageNum).getEditor();
            RichTextAreaSkin pageRTASkin = ((RichTextAreaSkin) pageRTA.getSkin());
            double pageHeight = pageRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(pageHeight + 35);
            mainWindow.getLastFocusOwner().requestFocus();
        }
    }
    @Override
    public void updateCommentHeight(boolean isRequired){
        if (isRequired || mainWindow.getLastFocusOwner() != abefgView.getExerciseComment().getEditor()) {
            mainWindow.setLastFocusOwner(abefgView.getExerciseComment().getEditor());
            lastPageNum = -1;

            ABEFGmodel model = getABEFGmodelFromView();
            ABEFGexercise exercise = new ABEFGexercise(model, mainWindow);
            RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
            RichTextAreaSkin commentRTASkin = ((RichTextAreaSkin) commentRTA.getSkin());
            double commentHeight = commentRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(Math.max(70, commentHeight + 35));
            mainWindow.getLastFocusOwner().requestFocus();
        }
    }
    @Override
    public void updateStatementHeight(boolean isRequired){
        if (isRequired || mainWindow.getLastFocusOwner() != abefgView.getExerciseStatementNode()) {
            mainWindow.setLastFocusOwner(abefgView.getExerciseStatementNode());
            lastPageNum = -1;

            abefgModel = getABEFGmodelFromView();
            ABEFGexercise exercise = new ABEFGexercise(abefgModel, mainWindow);
            RichTextArea statementRTA = exercise.getExerciseView().getExerciseStatement().getEditor();
            statementRTA.setEditable(true);
            RichTextAreaSkin statementRTASkin = ((RichTextAreaSkin) statementRTA.getSkin());
            double statementHeight = statementRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(statementHeight + 35);
            mainWindow.getLastFocusOwner().requestFocus();
        }
    }
    @Override
    public ExerciseModel getExerciseModelFromView() {
        return (ExerciseModel) getABEFGmodelFromView();
    }

    private ABEFGmodel getABEFGmodelFromView() {
        RichTextArea commentRTA = abefgView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();

        ArrayList<DecoratedRTA> exerciseContent = abefgView.getContentPageList();
        ArrayList<Document> contentList = new ArrayList<>();
        for (DecoratedRTA drta : exerciseContent) {
            RichTextArea editor = drta.getEditor();
            if (editor.isModified()) exerciseModified = true;
            editor.getActionFactory().saveNow().execute(new ActionEvent());
            contentList.add(editor.getDocument());
        }
        String name = abefgModel.getExerciseName();
        String prompt = abefgModel.getContentPrompt();

        ABEFGmodelExtra fields = abefgModel.getModelFields();
        String leaderAB = fields.getLeaderAB();
        String promptA = fields.getPromptA();
        boolean valueA = abefgView.getCheckBoxA().isSelected();
        String promptB = fields.getPromptB();
        boolean valueB = abefgView.getCheckBoxB().isSelected();

        String leaderEFG = fields.getLeaderEFG();
        String promptE = fields.getPromptE();
        boolean valueE = abefgView.getCheckBoxE().isSelected();
        String promptF = fields.getPromptF();
        boolean valueF = abefgView.getCheckBoxF().isSelected();
        String promptG = fields.getPromptG();
        boolean valueG = abefgView.getCheckBoxG().isSelected();


        ABEFGmodelExtra extra = new ABEFGmodelExtra(leaderAB, promptA, valueA, promptB, valueB, leaderEFG, promptE, valueE, promptF, valueF, promptG, valueG);

        boolean started = (abefgModel.isStarted() || exerciseModified);
        abefgModel.setStarted(started);
        double statementHeight = abefgView.getExerciseStatement().getEditor().getPrefHeight();
        Document statementDocument = abefgModel.getExerciseStatement();
        ABEFGmodel newModel = new ABEFGmodel(name, extra, started, prompt, statementHeight, statementDocument, commentDocument, contentList);

        return newModel;
    }

}

