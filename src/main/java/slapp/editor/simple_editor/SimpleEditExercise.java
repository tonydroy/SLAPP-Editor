package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Pagination;
import slapp.editor.EditorAlerts;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;

import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class SimpleEditExercise implements Exercise<SimpleEditModel, SimpleEditView> {
    MainWindowController mainController;
    SimpleEditModel model;
    SimpleEditView view;

    MainWindowView mainView;


    public SimpleEditExercise(SimpleEditModel model, MainWindowController mainController) {
        this.mainController = mainController;
        this.model = model;
        this.mainView = mainController.getMainView();
        this.view = new SimpleEditView(mainView);


        setView(model);
    }

    void setView(SimpleEditModel editModel) {


        view.setExerciseName(editModel.getExerciseName());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(editModel.getExerciseStatement());
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
    public MainWindowController getMainWindowController() { return mainController; }


    @Override
    public void saveExercise() {    }
    @Override
    public void printExercise() {    }
    @Override
    public void printAssignment() {    }
}
