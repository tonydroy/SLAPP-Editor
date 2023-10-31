package slapp.editor.main_window;

public interface Exercise<T,U> {

    MainWindow getMainWindowController();
    T getExerciseModel();
    void setExerciseModel(T model);
    U getExerciseView();
    void setExerciseView(U view);

    void saveExercise(boolean saveAs);
    void printExercise();
    Exercise<T,U> getContentClearExercise();

    Exercise<T,U> getEmptyExercise();
    boolean isContentModified();

    void updateContentHeight(boolean isRequired);
    void updateCommentHeight(boolean isRequired);
    void updateStatementHeight(boolean isRequired);






}
