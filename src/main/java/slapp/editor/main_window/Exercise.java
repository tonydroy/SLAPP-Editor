package slapp.editor.main_window;

public interface Exercise<T,U> {

    MainWindowController getMainWindowController();
    T getExerciseModel();
    void setExerciseModel(T model);
    U getExerciseView();
    void setExerciseView(U view);

    void saveExercise();
    void printExercise();
    void printAssignment();





}
