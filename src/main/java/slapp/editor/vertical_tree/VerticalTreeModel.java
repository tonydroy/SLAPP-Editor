package slapp.editor.vertical_tree;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;

public class VerticalTreeModel implements ExerciseModel<Document>, Serializable {

    public VerticalTreeModel(){}



    @Override
    public String getExerciseName() {
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
}
