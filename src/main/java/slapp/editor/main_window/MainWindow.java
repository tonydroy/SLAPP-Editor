package slapp.editor.main_window;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import slapp.editor.DiskUtilities;
import slapp.editor.EditorAlerts;

import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;


public class MainWindow {

    MainWindowView mainView;
    Exercise currentExercise;
    Assignment currentAssignment = null;
    int assignmentIndex = 0;


    public MainWindow() {
        mainView = new MainWindowView(this);
        setup();
    }

    private void setup() {

        mainView.getCreateNewExerciseItem().setOnAction(e -> createNewExercise());
        mainView.getCreateRevisedExerciseItem().setOnAction(e -> createRevisedExercise());
        mainView.getSaveExerciseItem().setOnAction(e -> saveExercise(false));
        mainView.getSaveAsExerciseItem().setOnAction(e -> saveExercise(true));
        mainView.getOpenExerciseItem().setOnAction(e -> openExercise());
        mainView.getClearExerciseItem().setOnAction(e -> clearExercise());
        mainView.getCloseExerciseItem().setOnAction(e -> closeExercise());
        mainView.getNewAssignmentItem().setOnAction(e -> newAssignment());
        mainView.getSaveAssignmentItem().setOnAction(e -> saveAssignment(false));
        mainView.getSaveAsAssignmentItem().setOnAction(e -> saveAssignment(true));
        mainView.getOpenAssignmentItem().setOnAction(e -> openAssignment());
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



    public void setUpExercise(Exercise exercise){
        currentExercise = exercise;
        mainView.setCurrentExerciseView((ExerciseView) currentExercise.getExerciseView());
        mainView.setupExercise();
        mainView.getSaveButton().setOnAction(e -> saveAction());
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

    private boolean checkContinue(String title, String content) {
        boolean okContinue = true;
        if (currentExercise.isContentModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okContinue = false;
        }
        return okContinue;
    }

    public void newAssignment(){}
    public void openAssignment(){}




    public MainWindowView getMainView() { return mainView; }







}
