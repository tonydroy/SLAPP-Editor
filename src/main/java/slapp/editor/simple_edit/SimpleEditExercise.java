package slapp.editor.simple_edit;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import slapp.editor.DiskUtilities;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import slapp.editor.page_editor.PageEditExercise;
import slapp.editor.page_editor.PageEditModel;
import slapp.editor.page_editor.PageEditView;

import java.util.ArrayList;
import java.util.List;

public class SimpleEditExercise implements Exercise<SimpleEditModel, SimpleEditView> {

    private MainWindow mainWindow;
    private SimpleEditModel editModel;
    private SimpleEditView editView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;


    public SimpleEditExercise(SimpleEditModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.editModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        mainView = mainWindow.getMainView();
        editView = new SimpleEditView(mainView);

        setEditView();
    }

    private void setEditView() {

        editView.setExerciseName(editModel.getExerciseName());
        editView.setResponsePrompt(editModel.getResponsePrompt());
        editView.setStatementPrefHeight(editModel.getStatementPrefHeight());
        editView.setCommentPrefHeight(editModel.getCommentPrefHeight());
        editView.setResponsePrefHeight(editModel.getResponsePrefHeight());

        //statement
        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.getActionFactory().open(editModel.getExerciseStatement()).execute(new ActionEvent());

        mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        editView.setExerciseStatement(statementDRTA);

        //comment
        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();

        commentEditor.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            exerciseModified = true;
            double commentTextHeight = mainView.getRTATextHeight(commentEditor);
            editModel.setCommentTextHeight(commentTextHeight);
        });
        commentEditor.getActionFactory().open(editModel.getExerciseComment()).execute(new ActionEvent());

        mainView.editorInFocus(commentDRTA, ControlType.AREA);
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        editView.setExerciseComment(commentDRTA);

        //response
        DecoratedRTA responseDRTA = new DecoratedRTA();
        RichTextArea responseEditor = responseDRTA.getEditor();

        responseEditor.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            exerciseModified = true;
            double responseTextHeight = mainView.getRTATextHeight(responseEditor);
            editModel.setResponseTextHeight(responseTextHeight);
        });
        responseEditor.getActionFactory().open(editModel.getExerciseResponse()).execute(new ActionEvent());

        mainView.editorInFocus(responseDRTA, ControlType.AREA);
        responseEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(responseDRTA, ControlType.AREA);
            }
        });
        editView.setExerciseResponse(responseDRTA);

        //cleanup
        editView.initializeViewDetails();
    }

    @Override
    public SimpleEditModel getExerciseModel() {
        return editModel;
    }
    @Override
    public SimpleEditView getExerciseView() {
        return editView;
    }
    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getSimpleEditModelFromView());
        if (success) exerciseModified = false;
    }

    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        SimpleEditModel printModel = editModel;
        SimpleEditExercise printExercise = this;
        double nodeWidth = PrintUtilities.getPageWidth() / mainWindow.getBaseScale();

        //header node
        Label exerciseName = new Label(printModel.getExerciseName());
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
        Separator headerSeparator = new Separator(Orientation.HORIZONTAL);
        headerSeparator.setPrefWidth(nodeWidth);
        nodeList.add(headerSeparator);

        //statement node
        RichTextArea statementRTA = printExercise.getExerciseView().getExerciseStatement().getEditor();
        statementRTA.prefHeightProperty().unbind();
        statementRTA.minWidthProperty().unbind();
        double statementHeight = printModel.getStatementTextHeight();
        statementRTA.setPrefHeight(statementHeight + 35.0);
        statementRTA.setContentAreaWidth(nodeWidth);
        statementRTA.setMinWidth(nodeWidth);
        statementRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(statementRTA);

        Separator statementSeparator = new Separator(Orientation.HORIZONTAL);
        statementSeparator.setPrefWidth(100);
        HBox statementSepBox = new HBox(statementSeparator);
        statementSepBox.setMinWidth(nodeWidth);
        statementSepBox.setAlignment(Pos.CENTER);
        nodeList.add(statementSepBox);

        //response node
        RichTextArea responseRTA = printExercise.getExerciseView().getExerciseResponse().getEditor();
        responseRTA.prefHeightProperty().unbind();
        responseRTA.minWidthProperty().unbind();
        responseRTA.setPrefHeight(printModel.getResponseTextHeight() + 35.0);
        responseRTA.setContentAreaWidth(nodeWidth);
        responseRTA.setMinWidth(nodeWidth);
        responseRTA.getStylesheets().clear(); responseRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(responseRTA);

        Separator responseSeparator = new Separator(Orientation.HORIZONTAL);
        responseSeparator.setPrefWidth(100);
        HBox responseSepBox = new HBox(responseSeparator);
        responseSepBox.setMinWidth(nodeWidth);
        responseSepBox.setAlignment(Pos.CENTER);
        nodeList.add(responseSepBox);

        //comment node
        RichTextArea commentRTA = printExercise.getExerciseView().getExerciseComment().getEditor();
        commentRTA.prefHeightProperty().unbind();
        commentRTA.minWidthProperty().unbind();
        commentRTA.setPrefHeight(printModel.getCommentTextHeight() + 35.0);
        commentRTA.setContentAreaWidth(nodeWidth);
        commentRTA.setMinWidth(nodeWidth);
        commentRTA.getStylesheets().clear(); commentRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(commentRTA);

        return nodeList;
    }

    @Override
    public SimpleEditExercise resetExercise() {
        RichTextArea commentRTA = editView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        SimpleEditModel originalModel = (SimpleEditModel) (editModel.getOriginalModel());
        originalModel.setExerciseComment(commentDocument);
        SimpleEditExercise clearExercise = new SimpleEditExercise(originalModel, mainWindow);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {
        RichTextArea commentEditor = editView.getExerciseComment().getEditor();
        if (commentEditor.isModified()) exerciseModified = true;
        RichTextArea responseEditor = editView.getExerciseResponse().getEditor();
        if (responseEditor.isModified()) exerciseModified = true;

        return exerciseModified;
    }

    @Override
    public void setExerciseModified(boolean modified) {
        this.exerciseModified = modified;
    }
    @Override
    public Node getFFViewNode() {   return editView.getFFViewNode();}
    @Override
    public Node getFFPrintNode() {
        SimpleEditModel printModel = editModel;
        SimpleEditExercise printExercise = this;
        double nodeWidth = PrintUtilities.getPageWidth() / mainWindow.getBaseScale();

        RichTextArea responseRTA = printExercise.getExerciseView().getExerciseResponse().getEditor();
        responseRTA.prefHeightProperty().unbind();
        responseRTA.minWidthProperty().unbind();
        responseRTA.setPrefHeight(printModel.getResponseTextHeight() + 15);
        responseRTA.setContentAreaWidth(nodeWidth);
        responseRTA.setMinWidth(nodeWidth);
        responseRTA.getStylesheets().clear(); responseRTA.getStylesheets().add("richTextAreaPrinter.css");

        return responseRTA;
    }

    @Override
    public ExerciseModel getExerciseModelFromView() {
        return (ExerciseModel) getSimpleEditModelFromView();
    }

    private SimpleEditModel getSimpleEditModelFromView() {
        SimpleEditModel model = new SimpleEditModel(editModel.getExerciseName(), editModel.getResponsePrompt());
        model.setOriginalModel(editModel.getOriginalModel());

        model.setExerciseStatement(editModel.getExerciseStatement());
        model.setStatementPrefHeight(editView.getExerciseStatement().getEditor().getPrefHeight());
        model.setStatementTextHeight(editModel.getStatementTextHeight());

        RichTextArea commentRTA = editView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        model.setExerciseComment(commentDocument);
        model.setCommentPrefHeight(editView.getCommentPrefHeight());
        model.setCommentTextHeight(editModel.getCommentTextHeight());

        RichTextArea responseRTA = editView.getExerciseResponse().getEditor();
        responseRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document responseDocument = responseRTA.getDocument();
        model.setExerciseResponse(responseDocument);
        model.setResponsePrefHeight(editView.getResponsePrefHeight());
        model.setResponseTextHeight(editModel.getResponseTextHeight());


        boolean started = (editModel.isStarted() || exerciseModified);
        model.setStarted(started); editModel.setStarted(started);

        return model;
    }



}
