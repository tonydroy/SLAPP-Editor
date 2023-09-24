package slapp.editor.simple_editor;

import slapp.editor.main_window.*;

public class SimpleEditExercise implements Exercise {
    MainWindowView mainView;
    ExerciseModel model;
    ExerciseView view;
    ExerciseController controller;

    public SimpleEditExercise(MainWindowView mainView) {
        this.mainView = mainView;
        this.model = new SimpleEditModel();
        this.view = new SimpleEditView(mainView);
        this.controller = new SimpleEditController();
    }


    @Override
    public ExerciseType getType() {
        return null;
    }

    @Override
    public ExerciseModel getExerciseModel() {
        return model;
    }

    @Override
    public ExerciseView getExerciseView() {
        return view;
    }

    @Override
    public ExerciseController getExerciseController() {
        return controller;
    }

    @Override
    public void setExerciseModel(ExerciseModel model) {
        this.model = model;
    }

    @Override
    public void setExerciseView(ExerciseView view) {
        this.view = view;
    }

    @Override
    public void setExerciseController(ExerciseController controller) {
        this.controller = controller;
    }
}
