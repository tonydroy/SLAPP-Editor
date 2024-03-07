package slapp.editor.main_window;

import javafx.scene.Node;

import java.util.List;

public interface Exercise<T,U> {

    T getExerciseModel();
    U getExerciseView();
    void saveExercise(boolean saveAs);
    List<Node> getPrintNodes();
    Exercise<T,U> resetExercise();
    boolean isExerciseModified();
    void setExerciseModified(boolean modified);
    ExerciseModel<T> getExerciseModelFromView();

}
