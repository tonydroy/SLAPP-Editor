package slapp.editor.main_window;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import slapp.editor.decorated_rta.DecoratedRTA;

public interface ExerciseView<T> {

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
    Node getExerciseContentNode();
    DoubleProperty getContentHeightProperty();
    DoubleProperty getContentWidthProperty();
    double getContentFixedHeight();
    Node getExerciseControl();
    double getContentWidth();

}
