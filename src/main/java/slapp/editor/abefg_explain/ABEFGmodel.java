package slapp.editor.abefg_explain;


import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class ABEFGmodel implements ExerciseModel<Document, ArrayList<Document>>, Serializable {

    private String exerciseName = new String("");
    private ABEFGmodelExtra modelFields = new ABEFGmodelExtra();
    private boolean started = false;
    private String prompt = "";
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private ArrayList<Document> exerciseContent = new ArrayList<>();

    public ABEFGmodel(String name, ABEFGmodelExtra modelFields, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, ArrayList<Document> exerciseContent) {
        this.exerciseName = name;
        this.modelFields = modelFields;
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

    ABEFGmodel getContentClearedModel() {
        ArrayList<Document> clearList = new ArrayList<>();
        clearList.add(new Document());
        exerciseContent = clearList;
        modelFields.setValueA(false);
        modelFields.setValueB(false);
        modelFields.setValueE(false);
        modelFields.setValueF(false);
        modelFields.setValueF(false);
        return this;
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
    public ArrayList<Document> getExerciseContent() {
        return exerciseContent;
    }
    @Override
    public String getContentPrompt() {
        return prompt;
    }
    ABEFGmodelExtra getModelFields() {   return modelFields; }
    @Override
    public String toString() {
        String name = getExerciseName();
        return name;
    }

}

