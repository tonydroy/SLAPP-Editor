package slapp.editor.main_window;

import slapp.editor.simple_editor.SimpleEditCreate;

public class TypeSelectorFactories {
    private MainWindowController mainController;

    TypeSelectorFactories(MainWindowController mainController) {
        this.mainController = mainController;
    }

    public Exercise castModelToType(ExerciseModel model) {
        return null;
    }

    public void createExerciseOfType(ExerciseType type) {

        switch(type) {

            case SIMPLE_EDITOR: {
                SimpleEditCreate simpleEditCreate = new SimpleEditCreate(mainController);
                break;
            }
            case AB_EXPLAIN: {
                break;
            }

        }
    }


}
