package slapp.editor.horizontal_tree;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;

public class HorizontalTreeView implements ExerciseView<DecoratedRTA> {


    @Override
    public String getExerciseName() {
        return null;
    }

    @Override
    public void setExerciseName(String name) {

    }

    @Override
    public DecoratedRTA getExerciseComment() {
        return null;
    }

    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) {

    }

    @Override
    public double getCommentHeight() {
        return 0;
    }

    @Override
    public DecoratedRTA getExerciseStatement() {
        return null;
    }

    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) {

    }

    @Override
    public Node getExerciseStatementNode() {
        return null;
    }

    @Override
    public double getStatementHeight() {
        return 0;
    }

    @Override
    public void setStatementPrefHeight(double height) {

    }

    @Override
    public Node getExerciseContentNode() {
        return null;
    }

    @Override
    public DoubleProperty getContentHeightProperty() {
        return null;
    }

    @Override
    public DoubleProperty getContentWidthProperty() {
        return null;
    }

    @Override
    public double getContentFixedHeight() {
        return 0;
    }

    @Override
    public Node getExerciseControl() {
        return null;
    }

    @Override
    public double getContentWidth() {
        return 0;
    }

    @Override
    public double getContentHeight() {
        return 0;
    }
}
