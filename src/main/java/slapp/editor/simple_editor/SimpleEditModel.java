package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;

public class SimpleEditModel implements ExerciseModel<Document, ArrayList<Document>>, Serializable {

    private ExerciseType exerciseType = ExerciseType.SIMPLE_EDITOR;
    private String exerciseName = new String("");
    private boolean started = false;
    private String prompt = "";
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private ArrayList<Document> exerciseContent = new ArrayList<>();

    public SimpleEditModel() {}
    public SimpleEditModel(String name, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, ArrayList<Document> exerciseContent) {
        this.exerciseName = name;
        this.started = started;
        this.prompt = prompt;
        this.statementPrefHeight = statementPrefHeight;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.exerciseContent = exerciseContent;
        if (exerciseContent.isEmpty()) exerciseContent.add(new Document());
    }

    void addBlankContentPage(int position) {
        exerciseContent.add(position, new Document());
    }
    void removeContentPage(int position) {
        exerciseContent.remove(position);
    }
    Document getContentPage(int position) {
        return exerciseContent.get(position);
    }

    public SimpleEditModel getContentClearedModel() {
        ArrayList<Document> clearList = new ArrayList<>();
        clearList.add(new Document());
        exerciseContent = clearList;
        return this;
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
    public Document getExerciseStatement() {
        return exerciseStatement;
    }
    @Override
    public void setExerciseStatement(Document exerciseStatement) {
        this.exerciseStatement = exerciseStatement;
    }
    @Override
    public ArrayList<Document> getExerciseContent() {
        return exerciseContent;
    }
    @Override
    public void setExerciseContent(ArrayList<Document> exerciseContent) {
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

    @Override
    public String getContentPrompt() {
        return prompt;
    }

    @Override
    public void setContentPrompt(String prompt) {
        this.prompt = prompt;
    }

    public double getStatementPrefHeight() {
        return statementPrefHeight;
    }

    public void setStatementPrefHeight(double statementPrefHeight) {
        this.statementPrefHeight = statementPrefHeight;
    }

    @Override
    public String toString() {
        String name = getExerciseName();
        return name;
    }
}
