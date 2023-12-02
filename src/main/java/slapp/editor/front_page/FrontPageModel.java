package slapp.editor.front_page;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

/**
 * Dummy model to support FrontPageExercise
 */
public class FrontPageModel implements ExerciseModel<Document, Document> {

    @Override
    public String getExerciseName() { return ""; }
    @Override
    public boolean isStarted() { return false; }
    @Override
    public void setStarted(boolean started) { }
    @Override
    public Document getExerciseComment() { return null; }
    @Override
    public void setExerciseComment(Document document) {}
    @Override
    public Document getExerciseStatement() { return null; }
    @Override
    public double getStatementPrefHeight() { return 0; }
    @Override
    public void setStatementPrefHeight(double height) { }
    @Override
    public Document getExerciseContent() { return null; }
    @Override
    public String getContentPrompt() { return null; }
    @Override
    public String toString() { return ""; }
}
