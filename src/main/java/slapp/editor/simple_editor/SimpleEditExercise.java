package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import slapp.editor.DiskUtilities;

import static javafx.scene.control.ButtonType.OK;

public class SimpleEditExercise implements Exercise<SimpleEditModel, SimpleEditView> {
    MainWindow mainWindow;
    SimpleEditModel model;
    SimpleEditView view;

    MainWindowView mainView;






    public SimpleEditExercise(SimpleEditModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.model = model;
        this.mainView = mainWindow.getMainView();
        this.view = new SimpleEditView(mainView);

        setView(model);
    }

    void setView(SimpleEditModel editModel) {


        view.setExerciseName(editModel.getExerciseName());
        view.setContentPrompt(editModel.getContentPrompt());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(editModel.getExerciseStatement());
        view.setStatementPrefHeight(editModel.getStatementPrefHeight());
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA);
            }
        });
        view.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(editModel.getExerciseComment());
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA);
            }
        });
        view.setExerciseComment(commentDRTA);

        ArrayList<DecoratedRTA> contentList = new ArrayList<>();
        for (Document doc : editModel.getExerciseContent()) {
            DecoratedRTA drta = new DecoratedRTA();
            RichTextArea editor = drta.getEditor();
            editor.setDocument(doc);
            editor.getActionFactory().saveNow().execute(new ActionEvent());
            editor.focusedProperty().addListener((o, ov, nv) -> {
                if (nv) {
                    mainView.editorInFocus(drta);
                }
            });
            contentList.add(drta);
        }
        view.setExerciseContent(contentList);

        view.initializeViewDetails();

        view.getAddPageButton().setOnAction(e -> addPageAction());
        view.getRemovePageButton().setOnAction(e -> removePageAction());
    }

    private void addPageAction() {
        int newPageIndex = view.getContentPageIndex() + 1;
        model.addBlankContentPage(newPageIndex);

        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea editor = drta.getEditor();

        editor.getActionFactory().saveNow().execute(new ActionEvent());

        editor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(drta);
            }
        });
        view.addBlankContentPage(newPageIndex, drta);
    }

    private void removePageAction() {
        if (model.getExerciseContent().size() <= 1) {
            EditorAlerts.showSimpleAlert("Cannot Remove", "Your response must include at least one page.");
        }
        else {
            int currentPageIndex = view.getContentPageIndex();
            boolean okContinue = true;
            if (view.getExerciseContent().get(currentPageIndex).getEditor().isModified()) {
                Alert confirm = EditorAlerts.confirmationAlert("Confirm Remove", "This page appears to have been changed.  Continue to remove?");
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() != OK) okContinue = false;
            }
            if (okContinue) {
                model.getExerciseContent().remove(currentPageIndex);
                view.removeContentPage(currentPageIndex);
            }
        }
    }

    @Override
    public SimpleEditModel getExerciseModel() {
        return model;
    }
    @Override
    public void setExerciseModel(SimpleEditModel model) {
        this.model = model;
    }
    @Override
    public SimpleEditView getExerciseView() {
        return view;
    }
    @Override
    public void setExerciseView(SimpleEditView view) {
        this.view = view;
    }
    @Override
    public MainWindow getMainWindowController() { return mainWindow; }


    @Override
    public void saveExercise(boolean saveAs) {
        DiskUtilities.saveExercise(saveAs, getModelFromView()); }


    private SimpleEditModel getModelFromView() {
        RichTextArea commentRTA = view.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();

        boolean changed = false;
        ArrayList<DecoratedRTA> exerciseContent = view.getExerciseContent();
        ArrayList<Document> contentList = new ArrayList<>();
        for (DecoratedRTA drta : exerciseContent) {
            RichTextArea editor = drta.getEditor();
            if (editor.isModified()) changed = true;
            editor.getActionFactory().saveNow().execute(new ActionEvent());
            contentList.add(editor.getDocument());
        }
        String name = model.getExerciseName();
        String prompt = model.getContentPrompt();
        boolean started = (model.isStarted() || changed);
        model.setStarted(started);
        double statementHeight = view.getExerciseStatement().getEditor().getPrefHeight();
        Document statementDocument = model.getExerciseStatement();
        SimpleEditModel newModel = new SimpleEditModel(name, started, prompt, statementHeight, statementDocument, commentDocument, contentList);
        return newModel;
    }

    public SimpleEditExercise getContentClearExercise() {
        SimpleEditModel currentModel = getModelFromView();
        SimpleEditExercise clearExercise = new SimpleEditExercise(currentModel.getContentClearedModel(), mainWindow);
        return clearExercise;
    }

    public SimpleEditExercise getEmptyExercise() {
        SimpleEditModel emptyModel = new SimpleEditModel("",false,"",80,new Document(), new Document(), new ArrayList<>());
        SimpleEditExercise emptyExercise = new SimpleEditExercise(emptyModel, mainWindow);
        return emptyExercise;
    }

    @Override
    public boolean isContentModified() {
        boolean modified = false;
        ArrayList<DecoratedRTA> exerciseContent = view.getExerciseContent();
        for (DecoratedRTA drta : exerciseContent) {
            RichTextArea editor = drta.getEditor();
            if (editor.isModified()) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void printExercise() {
        PrintUtilities.printExercise(getPrintNodes());
    }

    private ArrayList<Node> getPrintNodes() {
        ArrayList<Node> nodeList = new ArrayList<>();
        model = getModelFromView();
        SimpleEditExercise exercise = new SimpleEditExercise(model, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth();

        //header node
        Label exerciseName = new Label(model.getExerciseName());
        exerciseName.setStyle("-fx-font-weight: bold;");
        Region spacer = new Region();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        Label exerciseDate = new Label(dtf.format(LocalDateTime.now()));
        HBox hbox = new HBox(exerciseName, spacer, exerciseDate);
        hbox.setHgrow(spacer, Priority.ALWAYS);
        hbox.setPadding(new Insets(0,0,10,0));
        hbox.setPrefWidth(nodeWidth);

        Group headerRoot = new Group();
        Scene headerScene = new Scene(headerRoot);
        headerRoot.getChildren().add(hbox);
        headerRoot.applyCss();
        headerRoot.layout();
        double boxHeight = hbox.getHeight();
        hbox.setPrefHeight(boxHeight);
        nodeList.add(hbox);
        nodeList.add(new Separator(Orientation.HORIZONTAL));

        //comment node
        RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
        RichTextAreaSkin commentRTASkin = ((RichTextAreaSkin) commentRTA.getSkin());
        double commentHeight = Math.min(PrintUtilities.getPageHeight(), commentRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        commentRTA.setPrefHeight(Math.max(70, commentHeight + 35.0));
        commentRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        commentRTA.setPrefWidth(nodeWidth);
//        commentRTA.setPadding(new Insets(0, 0,10,0));
        nodeList.add(commentRTA);
        nodeList.add(new Separator(Orientation.HORIZONTAL));

        //statement node
        RichTextArea statementRTA = exercise.getExerciseView().getExerciseStatement().getEditor();
        RichTextAreaSkin statementRTASkin = ((RichTextAreaSkin) statementRTA.getSkin());
        double statementHeight = Math.min(PrintUtilities.getPageHeight(), statementRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        statementRTA.setPrefHeight(statementHeight + 35.0);
        statementRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementRTA.setPrefWidth(nodeWidth);
 //       statementRTA.setPadding(new Insets(0, 0,10,0));
        nodeList.add(statementRTA);
        nodeList.add(new Separator(Orientation.HORIZONTAL));

        //content nodes
        ArrayList<DecoratedRTA> pageList = exercise.getExerciseView().getExerciseContent();
        for (DecoratedRTA drta : pageList) {
            RichTextArea pageRTA = drta.getEditor();
            RichTextAreaSkin pageRTASkin = ((RichTextAreaSkin) pageRTA.getSkin());
            double pageHeight = Math.min(PrintUtilities.getPageHeight(), pageRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
            pageRTA.setPrefHeight(pageHeight + 35.0);
            pageRTA.setContentAreaWidth(nodeWidth);
            pageRTA.setPrefWidth(nodeWidth);


//            pageRTA.setPadding(new Insets(0,0,10,0));
            nodeList.add(pageRTA);
        }

        return nodeList;
    }


}
