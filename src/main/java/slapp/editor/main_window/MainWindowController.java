package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import slapp.editor.EditorMain;
import slapp.editor.simple_editor.SimpleEditExercise;
import javafx.stage.Popup;
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
        mainView.getNewExerciseItem().setOnAction(e -> getNewExercise());
    }

    void getNewExercise(){
        SimpleEditModel dummy = new SimpleEditModel("test", false, 0, new Document("exercise statement"), new Document(), new Document());
        currentExercise = new SimpleEditExercise(dummy, this);
        mainView.setCurrentExerciseView((ExerciseView) currentExercise.getExerciseView());
        mainView.setupExercise();
    }

    public MainWindowView getMainView() { return mainView; }





}
