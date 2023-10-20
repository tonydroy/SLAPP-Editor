package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import slapp.editor.EditorAlerts;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;

import java.util.ArrayList;
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
    @Override
    public void printExercise() {    }

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


}
