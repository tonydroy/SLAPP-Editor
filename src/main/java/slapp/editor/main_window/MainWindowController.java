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
        System.out.println(exerciseType);
    }

    void getNewExercise(){
        SimpleEditModel dummy = new SimpleEditModel("test", false, 0, new Document("exercise statement"), new Document(), new Document());
        currentExercise = new SimpleEditExercise(dummy, this);
        mainView.setCurrentExerciseView((ExerciseView) currentExercise.getExerciseView());
        mainView.setupExercise();
    }

    public MainWindowView getMainView() { return mainView; }





}
