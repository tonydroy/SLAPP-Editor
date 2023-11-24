package slapp.editor.main_window;

import slapp.editor.EditorAlerts;
import slapp.editor.ab_explain.ABcreate;
import slapp.editor.ab_explain.ABexercise;
import slapp.editor.ab_explain.ABmodel;
import slapp.editor.abefg_explain.ABEFGcreate;
import slapp.editor.abefg_explain.ABEFGexercise;
import slapp.editor.abefg_explain.ABEFGmodel;
import slapp.editor.derivation.DerivationExercise;
import slapp.editor.derivation.DerivationModel;
import slapp.editor.simple_editor.SimpleEditCreate;
import slapp.editor.simple_editor.SimpleEditExercise;
import slapp.editor.simple_editor.SimpleEditModel;

public class TypeSelectorFactories {
    private MainWindow mainWindow;

    public TypeSelectorFactories(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public Exercise getExerciseFromModelObject(Object objectModel) {
        String modelClassName = objectModel.getClass().getSimpleName();

        switch (modelClassName) {

            case "SimpleEditModel": {
                SimpleEditModel editModel = (SimpleEditModel) objectModel;
                return new SimpleEditExercise(editModel, mainWindow);
            }
            case "ABmodel": {
                ABmodel abModel = (ABmodel) objectModel;
                return new ABexercise(abModel, mainWindow);
            }
            case "ABEFGmodel": {
                ABEFGmodel abefgModel = (ABEFGmodel) objectModel;
                return new ABEFGexercise(abefgModel, mainWindow);
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
            case "ABmodel": {
                ABmodel abModel = (ABmodel) objectModel;
                ABexercise abExercise = new ABexercise(abModel, mainWindow);
                if (abExercise.getExerciseModel().isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                ABcreate abCreate = new ABcreate(mainWindow, abExercise);
                break;
            }
            case "ABEFGmodel": {
                ABEFGmodel abefgModel = (ABEFGmodel) objectModel;
                ABEFGexercise abefgExercise = new ABEFGexercise(abefgModel, mainWindow);
                if (abefgExercise.getExerciseModel().isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                ABEFGcreate abefgCreate = new ABEFGcreate(mainWindow, abefgExercise);
                break;
            }

            default: {
                EditorAlerts.showSimpleAlert("Cannot Open", "I do not recognize this as a SLAPP exercise file.");
            }
        }
    }


    public void createExerciseOfType(ExerciseType type) {

        switch(type) {

            case AB_EXPLAIN: {
                ABcreate abCreate = new ABcreate(mainWindow);
                break;
            }
            case DERIVATION: {
                mainWindow.currentExercise = new DerivationExercise(new DerivationModel(), mainWindow);   //hijack for testing
                mainWindow.getMainView().setupExercise();
                mainWindow.getMainView().setCenterVgrow();
                break;
            }
            case ABEFG_EXPLAIN: {
                ABEFGcreate abefgCreate = new ABEFGcreate(mainWindow);
                break;
            }
            case SIMPLE_EDITOR: {
                SimpleEditCreate simpleEditCreate = new SimpleEditCreate(mainWindow);
                break;
            }


        }
    }




}
