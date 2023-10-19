package slapp.editor.main_window;

import slapp.editor.DiskUtilities;
import slapp.editor.EditorAlerts;
import slapp.editor.simple_editor.SimpleEditExercise;


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

        mainView.getNewExerciseItem().setOnAction(e -> generateNewExercise());
        mainView.getSaveExerciseItem().setOnAction(e -> saveExercise(false));
        mainView.getSaveAsExerciseItem().setOnAction(e -> saveExercise(true));
        mainView.getOpenExerciseItem().setOnAction(e -> openExercise());
        mainView.getClearExerciseItem().setOnAction(e -> clearExercise());
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


    private void generateNewExercise() {
        ExerciseType exerciseType = ExerciseTypePopup.getType();
        if (exerciseType != null) {
            TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
            typeFactories.createExerciseOfType(exerciseType);
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
        Exercise emptyExercise = currentExercise.getEmptyExercise();
        setUpExercise(emptyExercise);
    }

    public void saveAssignment(boolean saveAs){ System.out.println("save assignment action"); }

    public void openExercise(){
        Object exerciseModelObject = DiskUtilities.openExerciseModelObject();
        if (exerciseModelObject != null) {
            TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
            Exercise exercise = typeFactories.getExerciseFromModelObject(exerciseModelObject);
            if (exercise != null)
                setUpExercise(exercise);
            else
                EditorAlerts.showSimpleAlert("Cannot open", "I do not recognize this file as a SLAPP exercise");
        }
    }
    public void newAssignment(){}
    public void openAssignment(){}




    public MainWindowView getMainView() { return mainView; }







}
