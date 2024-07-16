package slapp.editor.free_form;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.SerializationUtils;
import slapp.editor.DiskUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.derivation.DerivationExercise;
import slapp.editor.derivation.DerivationModel;
import slapp.editor.main_window.*;
import slapp.editor.simple_edit.SimpleEditExercise;
import slapp.editor.simple_edit.SimpleEditModel;

import java.util.ArrayList;
import java.util.List;

public class FreeFormExercise implements Exercise<FreeFormModel, FreeFormView> {

    private MainWindow mainWindow;
    private MainWindowView mainView;
    private FreeFormModel freeFormModel;
    private FreeFormView freeFormView;
    private boolean exerciseModified = false;
    private List<Exercise> exerciseList = new ArrayList<>();
    private Exercise activeExercise = null;


    private UndoRedoList<FreeFormModel> undoRedoList = new UndoRedoList<>(20);


    public FreeFormExercise(FreeFormModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        freeFormModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model);}
        if (model.getModelElements().isEmpty()) {
            SimpleEditModel stub = new SimpleEditModel("", "Simple Edit");
            model.getModelElements().add(new ModelElement(stub, 0));
        }

        mainView = mainWindow.getMainView();
        freeFormView = new FreeFormView(mainView);
        setFreeFormView();
        pushUndoRedo();


    }

    private void setFreeFormView() {
        freeFormView.setExerciseName(freeFormModel.getExerciseName());
        freeFormView.setStatementPrefHeight(freeFormModel.getStatementPrefHeight());
        freeFormView. setCommentPrefHeight(freeFormModel.getCommentPrefHeight());

        //statement
        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.getActionFactory().open(freeFormModel.getExerciseStatement()).execute(new ActionEvent());

        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        freeFormView.setExerciseStatement(statementDRTA);

        //comment
        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();

        commentEditor.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            exerciseModified = true;
            double commentTextHeight = mainView.getRTATextHeight(commentEditor);
            freeFormModel.setCommentTextHeight(commentTextHeight);
        });
        commentEditor.getActionFactory().open(freeFormModel.getExerciseComment()).execute(new ActionEvent());

        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        freeFormView.setExerciseComment(commentDRTA);

        //buttons
        freeFormView.getRedoButton().setOnAction(e -> redoAction());
        freeFormView.getUndoButton().setOnAction(e -> undoAction());
        freeFormView.getIndentButton().setOnAction(e -> indentAction());
        freeFormView.getOutdentButton().setOnAction(e -> outdentAction());
        freeFormView.getRemoveButton().setOnAction(e -> removeAction());
        freeFormView.getAddEditButton().setOnAction(e -> addEditAction());
        freeFormView.getAddHTreeButton().setOnAction(e -> addHTreeAction());
        freeFormView.getAddTTableButton().setOnAction(e -> addTTableAction());

        freeFormView.getAddVTreeBaseItalButton().setOnAction(e -> addVTreeBaseItalAction());
        freeFormView.getAddVTreeItalSansButton().setOnAction(e -> addVTreeItalSansAction());
        freeFormView.getAddNDrvtnItalSansButton().setOnAction(e -> addNDrvtnItalSansAction());
        freeFormView.getAddNDrvtnScriptSansButton().setOnAction(e -> addNDrvtnScriptSansAction());
        freeFormView.getAddNDrvtnItalBBButton().setOnAction(e -> addNDrvtnItalBBAction());
        freeFormView.getAddADrvtnItalSansButton().setOnAction(e -> addADrvtnItalSansAction());
        freeFormView.getAddADrvtnScriptSansButton().setOnAction(e -> addADrvtnScriptSansAction());
        freeFormView.getAddADrvtnItalBBButton().setOnAction(e -> addADrvtnItalBBAction());

        //cleanup
        freeFormView.initializeViewDetails();
        populateRightControlBox();
        setElementsFromModel();
        freeFormView.updateContentFromViewElements();



    }

    private void populateRightControlBox() {
        VBox controlBox = (VBox) freeFormView.getRightControl();
        ObservableList<Node> members = controlBox.getChildren();
        members.clear();
        members.addAll(freeFormView.getUndoButton(), freeFormView.getRedoButton());

        for (ElementTypes type : freeFormModel.getElementTypes()) {
            switch(type) {

                case SIMPLE_EDIT: {
                    members.add(freeFormView.getAddEditButton());
                    break;
                }
                case HORIZ_TREE: {
                    members.add(freeFormView.getAddHTreeButton());
                    break;
                }
                case TRUTH_TABLE: {
                    members.add(freeFormView.getAddTTableButton());
                    break;
                }
                case VERT_TREE_BASE_ITAL: {
                    members.add(freeFormView.getAddVTreeBaseItalButton());
                    break;
                }
                case VERT_TREE_ITAL_SANS: {
                    members.add(freeFormView.getAddVTreeItalSansButton());
                    break;
                }
                case N_DERIVATION_ITAL_SANS: {
                    members.add(freeFormView.getAddNDrvtnItalSansButton());
                    break;
                }
                case N_DERIVATION_SCIRPT_SANS: {
                    members.add(freeFormView.getAddNDrvtnScriptSansButton());
                    break;
                }
                case N_DERIVATION_ITAL_BB: {
                    members.add(freeFormView.getAddNDrvtnItalBBButton());
                    break;
                }

                case A_DERIVATION_ITAL_SANS: {
                    members.add(freeFormView.getAddADrvtnItalSansButton());
                    break;
                }
                case A_DERIVATION_SCRIPT_SANS: {
                    members.add(freeFormView.getAddADrvtnScriptSansButton());
                    break;
                }
                case A_DERIVATION_ITAL_BB: {
                    members.add(freeFormView.getAddADrvtnItalBBButton());
                    break;
                }
            }
        }

        members.addAll(freeFormView.getIndentButton(), freeFormView.getOutdentButton(), freeFormView.getRemoveButton());
    }

    private void setElementsFromModel() {
        TypeSelectorFactories typeFactory = new TypeSelectorFactories(mainWindow);
        List<ViewElement> viewElements = freeFormView.getViewElements();
        viewElements.clear();
        exerciseList.clear();

        for (ModelElement modelElement : freeFormModel.getModelElements()) {
            ExerciseModel model = modelElement.getModel();
            int indentLevel = modelElement.getIndentLevel();

            Exercise exer = typeFactory.getExerciseFromModelObject(model);
            Node viewNode = exer.getFFViewNode();
            ExerciseView exerView = (ExerciseView) exer.getExerciseView();

            viewNode.focusedProperty().addListener((ob, ov, nv) -> {
                if (nv) {

                    System.out.println("focus");

                    activeExercise = exer;
                    freeFormView.setExerciseControlNode(exerView.getExerciseControl());
                    mainWindow.getMainView().setUpLeftControl(freeFormView.getExerciseControl());
  //                  mainWindow.getMainView().setupExercise();
                }
            });

            exerciseList.add(exer);
            ViewElement viewElement = new ViewElement(viewNode, indentLevel);
            viewElements.add(viewElement);
        }

    }

    private void redoAction() {
        FreeFormModel redoElement = undoRedoList.getRedoElement();
        if (redoElement != null) {
            freeFormModel = (FreeFormModel) SerializationUtils.clone(redoElement);
            setElementsFromModel();
            freeFormView.updateContentFromViewElements();
            updateUndoRedoButtons();
        }
    }

    private void undoAction() {
        FreeFormModel undoElement = undoRedoList.getUndoElement();
        if (undoElement != null) {
            freeFormModel = (FreeFormModel) SerializationUtils.clone(undoElement);
            setElementsFromModel();
            freeFormView.updateContentFromViewElements();
            updateUndoRedoButtons();
        }
    }

    private void updateUndoRedoButtons() {
        freeFormView.getUndoButton().setDisable(!undoRedoList.canUndo());
        freeFormView.getRedoButton().setDisable(!undoRedoList.canRedo());
    }

    private void pushUndoRedo() {
        FreeFormModel model = getFreeFormModelFromView();
        FreeFormModel deepCopy = (FreeFormModel) SerializationUtils.clone(model);
        undoRedoList.push(deepCopy);
        updateUndoRedoButtons();
    }

    private void indentAction() {}

    private void outdentAction() {}

    private void removeAction()  {}

    private void addEditAction() {
        freeFormModel = getFreeFormModelFromView();

        SimpleEditModel newMod = new SimpleEditModel("", "Simple Edit");
        int index = exerciseList.indexOf(activeExercise);
        ModelElement element = new ModelElement(newMod, 0);
        freeFormModel.getModelElements().add(++index, element);

        setElementsFromModel();
        freeFormView.updateContentFromViewElements();
        pushUndoRedo();
        exerciseList.get(index).getFFViewNode().requestFocus();

    }

    private void addTTableAction() {}

    private void addHTreeAction() {}

    private void addVTreeBaseItalAction() {}

    private void addVTreeItalSansAction() {}

    private void addNDrvtnItalSansAction() {}
    private void addNDrvtnScriptSansAction() {}
    private void addNDrvtnItalBBAction() {}
    private void addADrvtnItalSansAction() {}
    private void addADrvtnScriptSansAction() {}
    private void addADrvtnItalBBAction() {}


    @Override
    public Node getFFViewNode() {return null;}
    @Override
    public Node getFFPrintNode() {return null;}
    @Override
    public FreeFormModel getExerciseModel() { return freeFormModel;   }
    @Override
    public FreeFormView getExerciseView() { return freeFormView;   }

    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getFreeFormModelFromView());
        if (success) {
            exerciseModified = false;
            for (Exercise exercise : exerciseList) exercise.setExerciseModified(false);
        }
    }

    @Override
    public List<Node> getPrintNodes() {
        return null;
    }

    @Override
    public Exercise<FreeFormModel, FreeFormView> resetExercise() {
        RichTextArea commentRTA = freeFormView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        FreeFormModel originalModel = (FreeFormModel) (freeFormModel.getOriginalModel());
        originalModel.setExerciseComment(commentDocument);
        FreeFormExercise clearExercise = new FreeFormExercise(originalModel, mainWindow);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {
        for (Exercise exercise : exerciseList) {
            if (exercise.isExerciseModified()) exerciseModified = true;
        }
        return exerciseModified;
    }

    @Override
    public void setExerciseModified(boolean modified) { exerciseModified = modified;   }

    @Override
    public ExerciseModel<FreeFormModel> getExerciseModelFromView() {
        return (ExerciseModel) getFreeFormModelFromView();
    }

    private FreeFormModel getFreeFormModelFromView() {
        FreeFormModel model = new FreeFormModel(freeFormModel.getExerciseName(), freeFormModel.getElementTypes());
        model.setOriginalModel(freeFormModel.getOriginalModel());
        model.setStarted(freeFormModel.isStarted() || exerciseModified);
        model.setExerciseStatement(freeFormModel.getExerciseStatement());

        model.setStatementPrefHeight(freeFormView.getExerciseStatement().getEditor().getPrefHeight());
        model.setStatementTextHeight(freeFormModel.getStatementTextHeight());

        RichTextArea commentRTA =freeFormView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseComment(commentRTA.getDocument());
        model.setCommentPrefHeight(freeFormView.getCommentPrefHeight());
        model.setCommentTextHeight(freeFormModel.getCommentTextHeight());

        List<ModelElement> modelElements = new ArrayList<>();
        for (int i = 0; i < exerciseList.size(); i++) {
            Exercise exer = exerciseList.get(i);
            ExerciseModel exerModel = exer.getExerciseModelFromView();
            int indent = freeFormView.getViewElements().get(i).getIndentLevel();
            ModelElement element = new ModelElement(exerModel, indent);
            modelElements.add(element);
        }
        model.setModelElements(modelElements);

        return model;
    }


}
