package slapp.editor.main_window;

import javafx.scene.Node;

import java.util.List;

public interface Exercise<T,U> {

    MainWindow getMainWindowController();
    T getExerciseModel();
    void setExerciseModel(T model);
    U getExerciseView();
    void setExerciseView(U view);

    void saveExercise(boolean saveAs);
    void printExercise();
    void exportToPDF();
    Exercise<T,U> getContentClearExercise();

    Exercise<T,U> getEmptyExercise();
    boolean isExerciseModified();
    void setExerciseModified(boolean modified);
    void updateContentHeight(boolean isRequired);
    void updateCommentHeight(boolean isRequired);
    void updateStatementHeight(boolean isRequired);
    ExerciseModel<T, U> getExerciseModelFromView();
    List<Node> getPrintNodes();








}
