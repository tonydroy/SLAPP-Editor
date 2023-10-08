package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.simple_editor.SimpleEditExercise;
import slapp.editor.simple_editor.SimpleEditModel;

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
        mainView.getNewExerciseItem().setOnAction(e -> dummyNewExercise());
    }

    void dummyNewExercise() {
        ExerciseType exerciseType = ExerciseTypePopup.getType();
        TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
        typeFactories.createExerciseOfType(exerciseType);
    }

    public void getNewExercise(Exercise exercise){
//        SimpleEditModel dummy = new SimpleEditModel("test", false, 0, new Document("exercise statement"), new Document(), new Document());
        currentExercise = exercise;
        mainView.setCurrentExerciseView((ExerciseView) currentExercise.getExerciseView());
        mainView.setupExercise();
    }

    public MainWindowView getMainView() { return mainView; }





}
