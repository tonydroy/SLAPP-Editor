package slapp.editor.main_window;

import slapp.editor.EditorAlerts;
import slapp.editor.ab_explain.ABcreate;
import slapp.editor.ab_explain.ABexercise;
import slapp.editor.ab_explain.ABmodel;
import slapp.editor.abefg_explain.ABEFGcreate;
import slapp.editor.abefg_explain.ABEFGexercise;
import slapp.editor.abefg_explain.ABEFGmodel;
import slapp.editor.derivation.DerivationCreate;
import slapp.editor.derivation.DerivationExercise;
import slapp.editor.derivation.DerivationModel;
import slapp.editor.derivation_explain.DrvtnExpCreate;
import slapp.editor.derivation_explain.DrvtnExpExercise;
import slapp.editor.derivation_explain.DrvtnExpModel;
import slapp.editor.simple_editor.SimpleEditCreate;
import slapp.editor.simple_editor.SimpleEditExercise;
import slapp.editor.simple_editor.SimpleEditModel;
import slapp.editor.truth_table.TruthTableCreate;
import slapp.editor.truth_table.TruthTableExercise;
import slapp.editor.truth_table.TruthTableModel;
import slapp.editor.truth_table_explain.TruthTableExpCreate;
import slapp.editor.truth_table_explain.TruthTableExpExercise;
import slapp.editor.truth_table_explain.TruthTableExpModel;
import slapp.editor.vert_tree_abefexplain.VerticalTreeABEFExpCreate;
import slapp.editor.vert_tree_abefexplain.VerticalTreeABEFExpExercise;
import slapp.editor.vert_tree_abefexplain.VerticalTreeABEFExpModel;
import slapp.editor.vert_tree_abexplain.VerticalTreeABExpCreate;
import slapp.editor.vert_tree_abexplain.VerticalTreeABExpExercise;
import slapp.editor.vert_tree_abexplain.VerticalTreeABExpModel;
import slapp.editor.vert_tree_explain.VerticalTreeExpCreate;
import slapp.editor.vert_tree_explain.VerticalTreeExpExercise;
import slapp.editor.vert_tree_explain.VerticalTreeExpModel;
import slapp.editor.vertical_tree.VerticalTreeCreate;
import slapp.editor.vertical_tree.VerticalTreeExercise;
import slapp.editor.vertical_tree.VerticalTreeModel;

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
            case "DerivationModel": {
                DerivationModel derivationModel = (DerivationModel) objectModel;
                return new DerivationExercise(derivationModel, mainWindow);
            }
            case "DrvtnExpModel": {
                DrvtnExpModel drvtnExpModel = (DrvtnExpModel) objectModel;
                return new DrvtnExpExercise(drvtnExpModel, mainWindow);
            }
            case "TruthTableModel": {
                TruthTableModel truthTableModel = (TruthTableModel) objectModel;
                return new TruthTableExercise(truthTableModel, mainWindow);
            }
            case "TruthTableExpModel": {
                TruthTableExpModel truthTableExpModel = (TruthTableExpModel) objectModel;
                return new TruthTableExpExercise(truthTableExpModel, mainWindow);
            }
            case "VerticalTreeModel": {
                VerticalTreeModel verticalTreeModel = (VerticalTreeModel) objectModel;
                return new VerticalTreeExercise(verticalTreeModel, mainWindow);
            }
            case "VerticalTreeExpModel": {
                VerticalTreeExpModel verticalTreeExpModel = (VerticalTreeExpModel) objectModel;
                return new VerticalTreeExpExercise(verticalTreeExpModel, mainWindow);
            }
            case "VerticalTreeABExpModel": {
                VerticalTreeABExpModel verticalTreeABExpModel = (VerticalTreeABExpModel) objectModel;
                return new VerticalTreeABExpExercise(verticalTreeABExpModel, mainWindow);
            }
            case "VerticalTreeABEFExpModel": {
                VerticalTreeABEFExpModel verticalTreeABEFExpModel = (VerticalTreeABEFExpModel) objectModel;
                return new VerticalTreeABEFExpExercise(verticalTreeABEFExpModel, mainWindow);
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
                if (editModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                SimpleEditCreate simpleEditCreate = new SimpleEditCreate(mainWindow, editModel);
                break;
            }
            case "ABmodel": {
                ABmodel abModel = (ABmodel) objectModel;
                if (abModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                ABcreate abCreate = new ABcreate(mainWindow, abModel);
                break;
            }
            case "ABEFGmodel": {
                ABEFGmodel abefgModel = (ABEFGmodel) objectModel;
                if (abefgModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                ABEFGcreate abefgCreate = new ABEFGcreate(mainWindow, abefgModel);
                break;
            }
            case "DerivationModel": {
                DerivationModel derivationModel = (DerivationModel) objectModel;
                if (derivationModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                DerivationCreate derivationCreate = new DerivationCreate(mainWindow, derivationModel);
                break;
            }
            case "DrvtnExpModel": {
                DrvtnExpModel drvtnExpModel = (DrvtnExpModel) objectModel;
                if (drvtnExpModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                DrvtnExpCreate drvtnExpCreate = new DrvtnExpCreate(mainWindow, drvtnExpModel);
                break;
            }
            case "TruthTableModel": {
                TruthTableModel truthTableModel = (TruthTableModel) objectModel;
                if (truthTableModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                TruthTableCreate truthTableCreate = new TruthTableCreate(mainWindow, truthTableModel);
                break;
            }
            case "TruthTableExpModel": {
                TruthTableExpModel truthTableExpModel = (TruthTableExpModel) objectModel;
                if (truthTableExpModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                TruthTableExpCreate truthTableExpCreate = new TruthTableExpCreate(mainWindow, truthTableExpModel);
                break;
            }
            case "VerticalTreeModel": {
                VerticalTreeModel verticalTreeModel = (VerticalTreeModel) objectModel;
                if (verticalTreeModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                VerticalTreeCreate verticalTreeCreate = new VerticalTreeCreate(mainWindow, verticalTreeModel);
                break;
            }
            case "VerticalTreeExpModel": {
                VerticalTreeExpModel verticalTreeExpModel = (VerticalTreeExpModel) objectModel;
                if (verticalTreeExpModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                VerticalTreeExpCreate verticalTreeExpCreate = new VerticalTreeExpCreate(mainWindow, verticalTreeExpModel);
                break;
            }
            case "VerticalTreeABExpModel": {
                VerticalTreeABExpModel verticalTreeABExpModel = (VerticalTreeABExpModel) objectModel;
                if (verticalTreeABExpModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                VerticalTreeABExpCreate verticalTreeABExpCreate = new VerticalTreeABExpCreate(mainWindow, verticalTreeABExpModel);
                break;
            }
            case "VerticalTreeABEFExpModel": {
                VerticalTreeABEFExpModel verticalTreeABEFExpModel = (VerticalTreeABEFExpModel) objectModel;
                if (verticalTreeABEFExpModel.isStarted()) {
                    EditorAlerts.showSimpleAlert("Cannot Open", "This exercise appears to have the content area modified.  Cannot open in create window.");
                    break;
                }
                VerticalTreeABEFExpCreate verticalTreeABEFExpCreate = new VerticalTreeABEFExpCreate(mainWindow, verticalTreeABEFExpModel);
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
            case ABEFG_EXPLAIN: {
                ABEFGcreate abefgCreate = new ABEFGcreate(mainWindow);
                break;
            }
            case DERIVATION: {
                DerivationCreate derivationCreate = new DerivationCreate(mainWindow);
                break;
            }
            case DRVTN_EXP: {
                DrvtnExpCreate drvtnExpCreate = new DrvtnExpCreate(mainWindow);
                break;
            }
            case SIMPLE_EDITOR: {
                SimpleEditCreate simpleEditCreate = new SimpleEditCreate(mainWindow);
                break;
            }
            case TRUTH_TABLE: {
                TruthTableCreate truthTableCreate = new TruthTableCreate(mainWindow);
                break;
            }
            case TRUTH_TABLE_ABEXP: {
                TruthTableExpCreate truthTableExpCreate = new TruthTableExpCreate(mainWindow);
                break;
            }
            case VERTICAL_TREE: {
                VerticalTreeCreate verticalTreeCreate = new VerticalTreeCreate(mainWindow);
                break;
            }
            case VERTICAL_TREE_EXP: {
                VerticalTreeExpCreate verticalTreeExpCreate = new VerticalTreeExpCreate(mainWindow);
                break;
            }
            case VERTICAL_TREE_ABEXP: {
                VerticalTreeABExpCreate verticalTreeABExpCreate = new VerticalTreeABExpCreate(mainWindow);
                break;
            }
            case VERTICAL_TREE_ABEFEXP: {
                VerticalTreeABEFExpCreate verticalTreeABEFExpCreate = new VerticalTreeABEFExpCreate(mainWindow);
                break;
            }


        }
    }




}
