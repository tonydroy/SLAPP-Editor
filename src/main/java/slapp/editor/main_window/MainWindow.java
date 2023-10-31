package slapp.editor.main_window;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import slapp.editor.DiskUtilities;
import slapp.editor.EditorAlerts;
import slapp.editor.PrintUtilities;
import slapp.editor.front_page.FrontPageExercise;

import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;


public class MainWindow {

    MainWindowView mainView;
    Exercise currentExercise;
    Assignment currentAssignment = null;
    int assignmentIndex = 0;
    ChangeListener<Node> focusListener;
    Node lastFocusOwner;


    public MainWindow() {
        mainView = new MainWindowView(this);
        setupMainWindow();

        focusListener = (ob, ov, nv) ->  {
            if (nv.focusedProperty().get() == true) {
                lastFocusOwner = ov;
                updateNodeContainerHeight(nv, false);
            }
        };
        setUpExercise(new FrontPageExercise(this));
        mainView.getMainScene().focusOwnerProperty().addListener(focusListener);
    }

    /* Comment:
    I do not understand how the focusOwnerProperty listener works.  In particular, a single listener responds to
    focus changes on buttons and such, but not to focus changes on the comment, statement or content.  With a
    second assignment of the same (!) listener as in setUpExercise below it fires twice but on all nodes -- a
    remove command prevents adding another fire each time the exercise is changed.  WTF?
     */

    private void setupMainWindow() {

        mainView.getCreateNewExerciseItem().setOnAction(e -> createNewExercise());
        mainView.getCreateRevisedExerciseItem().setOnAction(e -> createRevisedExercise());
        mainView.getSaveExerciseItem().setOnAction(e -> saveExercise(false));
        mainView.getSaveAsExerciseItem().setOnAction(e -> saveExercise(true));
        mainView.getOpenExerciseItem().setOnAction(e -> openExercise());
        mainView.getClearExerciseItem().setOnAction(e -> clearExercise());
        mainView.getCloseExerciseItem().setOnAction(e -> closeExercise());
        mainView.getPrintExerciseItem().setOnAction(e -> printExercise());
        mainView.getNewAssignmentItem().setOnAction(e -> newAssignment());
        mainView.getSaveAssignmentItem().setOnAction(e -> saveAssignment(false));
        mainView.getSaveAsAssignmentItem().setOnAction(e -> saveAssignment(true));
        mainView.getOpenAssignmentItem().setOnAction(e -> openAssignment());
        mainView.getPageSetupItem().setOnAction(e -> pageSetup());
        mainView.getPrintExerciseItemPM().setOnAction(e -> printExercise());

        mainView.getUpdateHeightButton().setOnAction(e -> {
            updateNodeContainerHeight(lastFocusOwner, true);
        });

    }


    public void setUpExercise(Exercise exercise) {
        mainView.getMainScene().focusOwnerProperty().removeListener(focusListener);
        currentExercise = exercise;
        mainView.setupExercise();
        mainView.getSaveButton().setOnAction(e -> saveAction());
        mainView.getMainScene().focusOwnerProperty().get();
        mainView.getMainScene().focusOwnerProperty().addListener(focusListener);
    }



    public void restoreCurrentExercise() {
        if (currentAssignment != null) {
            setUpExercise(currentAssignment.getExercise(assignmentIndex));
        }
    }


    private void createNewExercise() {
        if (currentExercise == null || checkContinue("Confirm Create", "This exercise appears to have been changed, and will be overwritten by the new one.  Continue to create exercise?")) {
            ExerciseType exerciseType = ExerciseTypePopup.getType();
            if (exerciseType != null) {
                TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
                typeFactories.createExerciseOfType(exerciseType);
            }
        }
    }

    private void createRevisedExercise() {
        if (currentExercise == null || checkContinue("Confirm Create", "This exercise appears to have been changed, and will be overwritten by the new one.  Continue to create exercise?")) {
            Object exerciseModelObject = DiskUtilities.openExerciseModelObject();
            if (exerciseModelObject != null) {
                TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
                typeFactories.createRevisedExerciseFromModelObject(exerciseModelObject);
            }
        }
    }



    public void saveAction(){
        if (currentAssignment != null) {
            if (!currentAssignment.getAssignmentName().isEmpty()) {
                saveAssignment(false);
            }
        }
        else if (currentExercise != null) {
            if (!((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName().isEmpty()) {
                currentExercise.saveExercise(false);
            }
            else EditorAlerts.showSimpleAlert("Cannot Save", "No named assignment or exercise to save.");
        }
    }
    public void saveExercise(boolean saveAs) {
        if (currentExercise != null) {
            if (!((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName().isEmpty()) {
                currentExercise.saveExercise(saveAs);
            }
            else EditorAlerts.showSimpleAlert("Cannot Save", "No named exercise to save.");
        }
    }
    private void clearExercise() {
        Exercise clearExercise = currentExercise.getContentClearExercise();
        setUpExercise(clearExercise);
    }

    private void closeExercise() {
        if (checkContinue("Confirm Close", "This exercise appears to have been changed.  Continue to close exercise?")) {
            Exercise emptyExercise = currentExercise.getEmptyExercise();
            setUpExercise(emptyExercise);
        }
    }

    public void saveAssignment(boolean saveAs){ System.out.println("save assignment action"); }

    public void openExercise(){
        if (currentExercise == null || checkContinue("Confirm Open", "This exercise appears to have been changed, and will be overwritten by the new one.  Continue to open exercise?")) {
            Object exerciseModelObject = DiskUtilities.openExerciseModelObject();
            if (exerciseModelObject != null) {
                TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
                Exercise exercise = typeFactories.getExerciseFromModelObject(exerciseModelObject);
                if (exercise != null)
                    setUpExercise(exercise);
            }
        }
    }

    private void printExercise() {
        currentExercise.printExercise();
    }

    private boolean checkContinue(String title, String content) {
        boolean okContinue = true;
        if (currentExercise.isContentModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okContinue = false;
        }
        return okContinue;
    }

    private void updateNodeContainerHeight(Node element, boolean isRequired) {
        if (isContainer(mainView.getContentNode(), element)) currentExercise.updateContentHeight(isRequired);
        else if (isContainer(mainView.getCommentNode(), element)) currentExercise.updateCommentHeight(isRequired);
        else if (isContainer(mainView.getStatementNode(), element)) currentExercise.updateStatementHeight(isRequired);
    }

    private boolean isContainer(Node container, Node element) {
        if (element == null)
            return false;
        Node current = element;
        while (current != null) {
            if (current == container)
                return true;
            current = current.getParent();
        }
        return false;
    }

    private void pageSetup() {
        PrintUtilities.updatePageLayout();
        mainView.setupExercise();
        mainView.updateZoom(mainView.getZoomSpinner().getValue());
        mainView.updatePageHeightLabel(PrintUtilities.getPageHeight());
    }

    public void newAssignment(){}
    public void openAssignment(){}




    public MainWindowView getMainView() { return mainView; }







}
