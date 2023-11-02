package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;

public interface ExerciseModel<T, U> extends Serializable {

    //insert as stub for future versions
    // int check requests
    // int help requests

    ExerciseType getExerciseType();
    String getExerciseName();
    void setExerciseName(String name);
    boolean isStarted();
    void setStarted(boolean started);
    String getContentPrompt();
    void setContentPrompt(String prompt);
    T getExerciseStatement();
    void setExerciseStatement(T exerciseStatement);
    U getExerciseContent();
    void setExerciseContent(U exerciseContent);
    Document getExerciseComment();
    void setExerciseComment(Document exerciseComment);
    double getStatementPrefHeight();
    void setStatementPrefHeight(double height);
    String toString();



}


