package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import slapp.editor.EditorAlerts;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import slapp.editor.DiskUtilities;
import slapp.editor.vertical_tree.VerticalTreeExercise;
import slapp.editor.vertical_tree.VerticalTreeModel;

import static javafx.scene.control.ButtonType.OK;

public class SimpleEditExercise implements Exercise<SimpleEditModel, SimpleEditView> {
    private MainWindow mainWindow;
    private SimpleEditModel editModel;
    private SimpleEditView editView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;
    private int lastPageNum = -1;

    public SimpleEditExercise(SimpleEditModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.editModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.editView = new SimpleEditView(mainView);

        setEditView();
    }

    private void setEditView() {

        editView.setExerciseName(editModel.getExerciseName());
        editView.setContentPrompt(editModel.getContentPrompt());
        editView.setStatementPrefHeight(editModel.getStatementPrefHeight());
        editView.setCommentPrefHeight(editModel.getCommentPrefHeight());
        editView.setPaginationPrefHeight(editModel.getPaginationPrefHeight());

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
        commentEditor.getActionFactory().open(editModel.getExerciseComment()).execute(new ActionEvent());
        commentEditor.getActionFactory().saveNow().execute(new ActionEvent());

        mainView.editorInFocus(commentDRTA, ControlType.AREA);
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        editView.setExerciseComment(commentDRTA);

        //pagination
        ArrayList<DecoratedRTA> contentList = new ArrayList<>();
        for (Document doc : editModel.getExercisePageDocs()) {
            DecoratedRTA drta = new DecoratedRTA();
            RichTextArea editor = drta.getEditor();
            editor.getActionFactory().open(doc).execute(new ActionEvent());
            editor.getActionFactory().saveNow().execute(new ActionEvent());

            mainView.editorInFocus(drta, ControlType.AREA);
            editor.focusedProperty().addListener((o, ov, nv) -> {
                if (nv) {
                    mainView.editorInFocus(drta, ControlType.AREA);
                }
            });
            contentList.add(drta);
        }
        editView.setContentPageList(contentList);

        //cleanup
        editView.initializeViewDetails();
        editView.getAddPageButton().setOnAction(e -> addPageAction());
        editView.getRemovePageButton().setOnAction(e -> removePageAction());
    }

    private void addPageAction() {
        int newPageIndex = editView.getContentPageIndex() + 1;
        editModel.addBlankContentPage(newPageIndex);

        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea editor = drta.getEditor();
        editor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(drta, ControlType.AREA);
        editor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(drta, ControlType.AREA);
            }
        });
        editView.addBlankContentPage(newPageIndex, drta);
        Platform.runLater(() -> {
            mainView.updateContentWidthProperty();
            mainView.updateContentHeightProperty();
        });
    }

    private void removePageAction() {
        if (editModel.getExercisePageDocs().size() <= 1) {
            EditorAlerts.showSimpleAlert("Cannot Remove", "Your response must include at least one page.");
        }
        else {
            int currentPageIndex = editView.getContentPageIndex();
            boolean okContinue = true;
            if (editView.getContentPageList().get(currentPageIndex).getEditor().isModified()) {
                Alert confirm = EditorAlerts.confirmationAlert("Confirm Remove", "This page appears to have been changed.  Continue to remove?");
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() != OK) okContinue = false;
            }
            if (okContinue) {
                editModel.getExercisePageDocs().remove(currentPageIndex);
                editView.removeContentPage(currentPageIndex);
                exerciseModified = true;
                Platform.runLater(() -> {
                    mainView.updateContentWidthProperty();
                    mainView.updateContentHeightProperty();
                });
            }
        }
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
        editModel = getSimpleEditModelFromView();
        SimpleEditExercise exercise = new SimpleEditExercise(editModel, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth() / mainWindow.getBaseScale();

        //header node
        Label exerciseName = new Label(editModel.getExerciseName());
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
        RichTextArea statementRTA = exercise.getExerciseView().getExerciseStatement().getEditor();
        statementRTA.prefHeightProperty().unbind();
        statementRTA.minWidthProperty().unbind();
        double statementHeight = mainView.getRTATextHeight(statementRTA);
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


        //content nodes
        List<DecoratedRTA> pageList = exercise.getExerciseView().getContentPageList();
        for (DecoratedRTA drta : pageList) {
            RichTextArea pageRTA = drta.getEditor();
            pageRTA.prefHeightProperty().unbind();
            double pageHeight = mainView.getRTATextHeight(pageRTA);
            pageRTA.setPrefHeight(pageHeight + 35.0);
            pageRTA.setContentAreaWidth(nodeWidth);
            pageRTA.setPrefWidth(nodeWidth);
            nodeList.add(pageRTA);
        }

        Separator contentSeparator = new Separator(Orientation.HORIZONTAL);
        contentSeparator.setStyle("-fx-stroke-dash-array:0.1 5.0");
        contentSeparator.setPrefWidth(100);
        HBox contentSepBox = new HBox(contentSeparator);
        contentSepBox.setMinWidth(nodeWidth);
        contentSepBox.setAlignment(Pos.CENTER);
        nodeList.add(contentSepBox);

        //comment node
        RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
        commentRTA.prefHeightProperty().unbind();
        commentRTA.minWidthProperty().unbind();
        double commentHeight = mainView.getRTATextHeight(commentRTA);
        commentRTA.setPrefHeight(commentHeight + 35.0);
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

        List<DecoratedRTA> exerciseContent = editView.getContentPageList();
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
    public ExerciseModel getExerciseModelFromView() {
        return (ExerciseModel) getSimpleEditModelFromView();
    }

    private SimpleEditModel getSimpleEditModelFromView() {
        RichTextArea commentRTA = editView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();

        List<DecoratedRTA> exerciseContent = editView.getContentPageList();
        List<Document> contentList = new ArrayList<>();
        for (DecoratedRTA drta : exerciseContent) {
            RichTextArea editor = drta.getEditor();
            if (editor.isModified()) exerciseModified = true;
            editor.getActionFactory().saveNow().execute(new ActionEvent());
            contentList.add(editor.getDocument());
        }
        String name = editModel.getExerciseName();
        String prompt = editModel.getContentPrompt();
        boolean started = (editModel.isStarted() || exerciseModified);
        editModel.setStarted(started);
        double statementHeight = editView.getExerciseStatement().getEditor().getPrefHeight();
        Document statementDocument = editModel.getExerciseStatement();
        SimpleEditModel newModel = new SimpleEditModel(name, started, prompt, statementHeight, statementDocument, commentDocument, contentList);
        newModel.setOriginalModel(editModel.getOriginalModel());
        newModel.setCommentPrefHeight(editView.getCommentPrefHeight());
        newModel.setPaginationPrefHeight(editView.getPaginationPrefHeight());

        return newModel;
    }

}
