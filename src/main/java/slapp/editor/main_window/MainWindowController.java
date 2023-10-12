package slapp.editor.main_window;

public class MainWindowController {

    MainWindowView mainView;
    MainWindowModel mainModel;
    Exercise currentExercise;

    public MainWindowController() {
        mainModel = new MainWindowModel();
        mainView = new MainWindowView(this);

        setup();
    }

    void setup() {
        mainView.getNewExerciseItem().setOnAction(e -> generateNewExercise());
    }

    void generateNewExercise() {
        ExerciseType exerciseType = ExerciseTypePopup.getType();
        if (exerciseType != null) {
            TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
            typeFactories.createExerciseOfType(exerciseType);
        }
    }

    public void setUpNewExercise(Exercise exercise){
        currentExercise = exercise;
        mainView.setCurrentExerciseView((ExerciseView) currentExercise.getExerciseView());
        mainView.setupExercise();
    }

    public MainWindowView getMainView() { return mainView; }





}
