package slapp.editor.abefg_explain;


import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class ABEFGmodel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private ABEFGmodelExtra modelFields = new ABEFGmodelExtra();
    private boolean started = false;
    private String prompt = "";
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private ArrayList<Document> exercisePageDocs = new ArrayList<>();

    public ABEFGmodel(String name, ABEFGmodelExtra modelFields, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, ArrayList<Document> exercisePageDocs) {
        this.exerciseName = name;
        this.modelFields = modelFields;
        this.started = started;
        this.prompt = prompt;
        this.statementPrefHeight = statementPrefHeight;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.exercisePageDocs = exercisePageDocs;
        if (exercisePageDocs.isEmpty()) exercisePageDocs.add(new Document());
    }

    void addBlankExercisePage(int position) { exercisePageDocs.add(position, new Document()); }
    public String getContentPrompt() {
        return prompt;
    }
    ABEFGmodelExtra getModelFields() {   return modelFields; }

    @Override
    public String getExerciseName() { return exerciseName; }
    @Override
    public boolean isStarted() {return started;}
    @Override
    public void setStarted(boolean started) { this.started = started; }
    @Override
    public Document getExerciseComment() { return exerciseComment; }
    @Override
    public void setExerciseComment(Document document) {this.exerciseComment = document; }
    @Override
    public Document getExerciseStatement() { return exerciseStatement; }
    @Override
    public double getStatementPrefHeight() { return statementPrefHeight; }
    @Override
    public void setStatementPrefHeight(double statementPrefHeight) { this.statementPrefHeight = statementPrefHeight; }

    public ArrayList<Document> getExercisePageDocs() {
        return exercisePageDocs;
    }
    @Override
    public String toString() {
        String name = getExerciseName();
        return name;
    }

}

