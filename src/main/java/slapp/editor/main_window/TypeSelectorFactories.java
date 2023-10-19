package slapp.editor.main_window;

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
                System.out.println("something wrong");
                return null;
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
