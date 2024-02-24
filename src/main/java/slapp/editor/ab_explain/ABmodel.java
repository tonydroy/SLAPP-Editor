package slapp.editor.ab_explain;


import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ABmodel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private ExerciseType type = ExerciseType.AB_EXPLAIN;
    private ABmodelExtra modelFields = new ABmodelExtra();
    private boolean started = false;
    private String prompt = "";
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private List<Document> exercisePageDocs = new ArrayList<>();

    public ABmodel(String name, ABmodelExtra modelFields, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, List<Document> exercisePageDocs) {
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

    void addBlankExercisePage(int position) { exercisePageDocs.add(position, new Document());  }
    public String getContentPrompt() {
        return prompt;
    }
    ABmodelExtra getModelFields() { return modelFields; }

    @Override
    public String getExerciseName() { return exerciseName; }
    @Override
    public ExerciseType getExerciseType() { return type; }
    @Override
    public boolean isStarted() { return started;    }
    @Override
    public void setStarted(boolean started) {this.started = started; }
    @Override
    public Document getExerciseComment() { return exerciseComment;  }
    @Override
    public void setExerciseComment(Document document) {this.exerciseComment = document; }
    @Override
    public Document getExerciseStatement() { return exerciseStatement;  }
    @Override
    public double getStatementPrefHeight() { return statementPrefHeight;  }
    @Override
    public void setStatementPrefHeight(double statementPrefHeight) { this.statementPrefHeight = statementPrefHeight; }

    public List<Document> getExercisePageDocs() {  return exercisePageDocs; }
    @Override
    public String toString() {
        String name = getExerciseName();
        return name;
    }

}

