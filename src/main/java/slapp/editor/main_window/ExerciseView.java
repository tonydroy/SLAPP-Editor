package slapp.editor.main_window;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import slapp.editor.decorated_rta.DecoratedRTA;

public interface ExerciseView<T,U> {

    String getExerciseName();
    void setExerciseName(String name);
    DecoratedRTA getExerciseComment();
    void setExerciseComment(DecoratedRTA exerciseComment);
    double getCommentHeight();
    T getExerciseStatement();
    void setExerciseStatement(T exerciseStatement);
    Node getExerciseStatementNode();
    double getStatementHeight();
    void setStatementPrefHeight(double height);
    U getExerciseContent();
    void setExerciseContent(U contentSplitPane);
    Node getExerciseContentNode();
    void setContentPrompt(String prompt);
    DoubleProperty getContentHeightProperty();
    double getContentFixedHeight();
    Node getExerciseControl();

}
