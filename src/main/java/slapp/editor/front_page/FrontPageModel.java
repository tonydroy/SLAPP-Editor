package slapp.editor.front_page;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.util.ArrayList;

public class FrontPageModel implements ExerciseModel<Document, Document> {
    @Override
    public ExerciseType getExerciseType() {
        return null;
    }

    @Override
    public String getExerciseName() {
        return "";
    }

    @Override
    public void setExerciseName(String name) {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public void setStarted(boolean started) {

    }

    @Override
    public String getContentPrompt() {
        return null;
    }

    @Override
    public void setContentPrompt(String prompt) {

    }

    @Override
    public Document getExerciseStatement() {
        return null;
    }

    @Override
    public void setExerciseStatement(Document exerciseStatement) {

    }

    @Override
    public Document getExerciseContent() {
        return null;
    }

    @Override
    public void setExerciseContent(Document exerciseContent) {

    }

    @Override
    public Document getExerciseComment() {
        return null;
    }

    @Override
    public void setExerciseComment(Document exerciseComment) {

    }

    @Override
    public double getStatementPrefHeight() {
        return 0;
    }

    @Override
    public void setStatementPrefHeight(double height) {

    }
}
