package slapp.editor.main_window;

import slapp.editor.EditorAlerts;
import slapp.editor.simple_editor.SimpleEditCreate;
import slapp.editor.simple_editor.SimpleEditExercise;
import slapp.editor.simple_editor.SimpleEditModel;

public class TypeSelectorFactories {
    private MainWindow mainWindow;

    TypeSelectorFactories(MainWindow mainController) {
        this.mainWindow = mainController;
    }

    public Exercise getExerciseFromModelObject(Object objectModel) {
        String modelClassName = objectModel.getClass().getSimpleName();

        switch (modelClassName) {

            case "SimpleEditModel": {
                SimpleEditModel editModel = (SimpleEditModel) objectModel;
                return new SimpleEditExercise(editModel, mainWindow);
            }
            default: {
                EditorAlerts.showSimpleAlert("Cannot Open", "I do not recognize this as a SLAPP exercise file");
                return null;
            }
        }

    }

    public void createRevisedExerciseFromModelObject(Object objectModel) {
        String modelClassName = objectModel.getClass().getSimpleName();
        switch (modelClassName) {

            case "SimpleEditModel": {
                SimpleEditModel editModel = (SimpleEditModel) objectModel;
                SimpleEditExercise editExercise = new SimpleEditExercise(editModel, mainWindow);
                if (editExercise.getExerciseModel().isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                SimpleEditCreate simpleEditCreate = new SimpleEditCreate(mainWindow, editExercise);
                break;
            }

            default: {
                EditorAlerts.showSimpleAlert("Cannot Open", "I do not recognize this as a SLAPP exercise file.");
            }

        }
    }


    public void createExerciseOfType(ExerciseType type) {

        switch(type) {

            case SIMPLE_EDITOR: {
                SimpleEditCreate simpleEditCreate = new SimpleEditCreate(mainWindow);
                break;
            }
            case AB_EXPLAIN: {
                break;
            }

        }
    }




}
