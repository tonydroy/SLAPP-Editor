package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;

public class HorizontalTreeModel implements ExerciseModel<Document>, Serializable {

    @Override
    public String getExerciseName() {
        return null;
    }

    @Override
    public ExerciseType getExerciseType() {
        return null;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public void setStarted(boolean started) {

    }

    @Override
    public Document getExerciseComment() {
        return null;
    }

    @Override
    public Document getExerciseStatement() {
        return null;
    }

    @Override
    public void setExerciseComment(Document statement) {

    }

    @Override
    public double getStatementPrefHeight() {
        return 0;
    }

    @Override
    public void setStatementPrefHeight(double height) {

    }

    @Override
    public ExerciseModel<Document> getOriginalModel() {
        return null;
    }

    @Override
    public void setOriginalModel(ExerciseModel<Document> exerciseModel) {

    }
}
