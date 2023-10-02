package slapp.editor.main_window;

public interface Exercise<T,U> {



    T getExerciseModel();
    U getExerciseView();
    MainWindowController getMainWindowController();


    void setExerciseModel(T model);
    void setExerciseView(U view);

    void saveExercise();
    void printExercise();
    void printAssignment();





}
