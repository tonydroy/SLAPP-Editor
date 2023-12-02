package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleEditModel implements ExerciseModel<Document, List<Document>>, Serializable {

    private String exerciseName = new String("");
    private boolean started = false;
    private String contentPrompt = "";
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private List<Document> exerciseContent = new ArrayList<>();

    public SimpleEditModel(String name, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, List<Document> exerciseContent) {
        this.exerciseName = name;
        this.started = started;
        this.contentPrompt = prompt;
        this.statementPrefHeight = statementPrefHeight;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.exerciseContent = exerciseContent;
        if (exerciseContent.isEmpty()) exerciseContent.add(new Document());
    }

    void addBlankContentPage(int position) {
        exerciseContent.add(position, new Document());
    }


    @Override
    public String getExerciseName() {
        return exerciseName;
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
    public Document getExerciseComment() {
        return exerciseComment;
    }
    @Override
    public void setExerciseComment(Document document) {this.exerciseComment = document; }
    @Override
    public Document getExerciseStatement() {
        return exerciseStatement;
    }
    @Override
    public double getStatementPrefHeight() {
        return statementPrefHeight;
    }
    @Override
    public void setStatementPrefHeight(double statementPrefHeight) {
        this.statementPrefHeight = statementPrefHeight;
    }
    @Override
    public List<Document> getExerciseContent() {
        return exerciseContent;
    }
    @Override
    public String getContentPrompt() {
        return contentPrompt;
    }
    @Override
    public String toString() { return exerciseName; }

}
