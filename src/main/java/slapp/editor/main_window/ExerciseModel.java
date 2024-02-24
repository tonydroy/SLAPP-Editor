package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;
import java.io.Serializable;

public interface ExerciseModel<T> extends Serializable {

    String getExerciseName();
    ExerciseType getExerciseType();
    boolean isStarted();
    void setStarted(boolean started);
    Document getExerciseComment();
    T getExerciseStatement();
    void setExerciseComment(T statement);
    double getStatementPrefHeight();
    void setStatementPrefHeight(double height);
    String toString();

}


