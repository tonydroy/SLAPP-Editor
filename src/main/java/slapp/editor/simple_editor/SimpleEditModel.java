package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;

public class SimpleEditModel implements ExerciseModel<Document, Document>, Serializable {

    private ExerciseType exerciseType = ExerciseType.SIMPLE_EDITOR;
    private String exerciseName = new String("");
    private boolean started = false;
    private int pointsPossible = 0;
    private Document exerciseStatement = new Document();
    private Document exerciseContent = new Document();
    private Document exerciseComment = new Document();


    public SimpleEditModel() {}
    public SimpleEditModel(String name, boolean started, int pointsPossible, Document exerciseStatement, Document exerciseContent, Document exerciseComment) {
        this.exerciseName = name;
        this.started = started;
        this.pointsPossible = pointsPossible;
        this.exerciseStatement = exerciseStatement;
        this.exerciseContent = exerciseContent;
        this.exerciseComment = exerciseComment;
    }

    @Override
    public ExerciseType getExerciseType() {
        return exerciseType;
    }
    @Override
    public String getExerciseName() {
        return exerciseName;
    }
    @Override
    public void setExerciseName(String name) {
        this.exerciseName = name;
    }
    @Override
    public boolean isStarted() {
        return started;
    }
    @Override
    public void setStarted(boolean started) {
        this.started = started;
    }
    @Override
    public int getPointsPossible() { return pointsPossible; }
    @Override
    public void setPointsPossible(int pointsPossible) { this.pointsPossible = pointsPossible; }
    @Override
    public Document getExerciseStatement() {
        return exerciseStatement;
    }
    @Override
    public void setExerciseStatement(Document exerciseStatement) {
        this.exerciseStatement = exerciseStatement;
    }
    @Override
    public Document getExerciseContent() {
        return exerciseContent;
    }
    @Override
    public void setExerciseContent(Document exerciseContent) {
        this.exerciseContent = exerciseContent;
    }
    @Override
    public Document getExerciseComment() {
        return exerciseComment;
    }
    @Override
    public void setExerciseComment(Document exerciseComment) {
        this.exerciseComment = exerciseComment;
    }
}
