package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;

public class SimpleEditExercise implements Exercise<SimpleEditModel, SimpleEditView> {
    MainWindowController mainController;
    SimpleEditModel model;
    SimpleEditView view;


    public SimpleEditExercise(SimpleEditModel model, MainWindowController mainController) {
        this.mainController = mainController;
        this.model = model;
        this.view = new SimpleEditView(this);
        setView();
    }

    void setView() {
        view.updateExerciseStatement(model.getExerciseStatement());
        view.updateExerciseContent(model.getExerciseContent());
        view.updateExerciseComment(model.getExerciseComment());
        view.setExerciseName(model.getExerciseName());
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
