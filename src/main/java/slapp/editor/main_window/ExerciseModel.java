package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;
import java.io.Serializable;

public interface ExerciseModel<T, U> extends Serializable {

    String getExerciseName();
    boolean isStarted();
    void setStarted(boolean started);
    Document getExerciseComment();
    T getExerciseStatement();
    void setExerciseComment(T statement);
    double getStatementPrefHeight();
    void setStatementPrefHeight(double height);
    U getExerciseContent();
    String getContentPrompt();
    String toString();

}


