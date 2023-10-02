package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;

public interface ExerciseModel<T, U> {

    //insert as stub for future versions
    // int check requests
    // int help requests

    ExerciseType getExerciseType();
    String getExerciseName();
    void setExerciseName(String name);
    boolean isStarted();
    void setStarted(boolean started);
    int getPointsPossible();
    void setPointsPossible(int pointsPossible);
    T getExerciseStatement();
    void setExerciseStatement(T exerciseStatement);
    U getExerciseContent();
    void setExerciseContent(U exerciseContent);
    Document getExerciseComment();
    void setExerciseComment(Document exerciseComment);


}


