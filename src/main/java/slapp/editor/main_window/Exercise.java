package slapp.editor.main_window;

import javafx.scene.Node;

import java.util.List;

public interface Exercise<T,U> {

    T getExerciseModel();
    U getExerciseView();
    void saveExercise(boolean saveAs);
    void printExercise();
    void exportExerciseToPDF();
    List<Node> getPrintNodes();
    Exercise<T,U> resetExercise();
    boolean isExerciseModified();
    void setExerciseModified(boolean modified);
//    void updateContentHeight(Node focusedNode, boolean isRequired);
//    void updateCommentHeight(boolean isRequired);
//    void updateStatementHeight(boolean isRequired);
    ExerciseModel<T> getExerciseModelFromView();

}
