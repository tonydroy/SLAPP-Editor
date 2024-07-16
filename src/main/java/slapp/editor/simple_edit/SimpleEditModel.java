package slapp.editor.simple_edit;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;
import slapp.editor.page_editor.PageContent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleEditModel implements ExerciseModel <Document>, Serializable {

    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.SIMPLE_EDIT;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;
    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;
    private Document exerciseComment = new Document("");
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private Document exerciseResponse = new Document("");
    private double responsePrefHeight = 350;
    private double responseTextHeight = 0;
    private String responsePrompt = "";

    public SimpleEditModel(String name, String prompt) {
        exerciseName = name;
        responsePrompt = prompt;
    }


    public double getCommentTextHeight() {
        return commentTextHeight;
    }
    public void setCommentTextHeight(double commentTextHeight) {
        this.commentTextHeight = commentTextHeight;
    }

    public void setExerciseStatement(Document exerciseStatement) {  this.exerciseStatement = exerciseStatement;  }
    public double getStatementTextHeight() {
        return statementTextHeight;
    }
    public void setStatementTextHeight(double statementTextHeight) {    this.statementTextHeight = statementTextHeight;  }
    public double getCommentPrefHeight() {     return commentPrefHeight;  }
    public void setCommentPrefHeight(double commentPrefHeight) {    this.commentPrefHeight = commentPrefHeight;  }
    public Document getExerciseResponse() {   return exerciseResponse;  }
    public void setExerciseResponse(Document exerciseResponse) {   this.exerciseResponse = exerciseResponse;  }
    public double getResponsePrefHeight() {     return responsePrefHeight;  }
    public void setResponsePrefHeight(double responsePrefHeight) {    this.responsePrefHeight = responsePrefHeight;  }
    public double getResponseTextHeight() {     return responseTextHeight;  }
    public void setResponseTextHeight(double responseTextHeight) {     this.responseTextHeight = responseTextHeight;  }
    public String getResponsePrompt() {    return responsePrompt;   }

    @Override
    public String getExerciseName() {return exerciseName; }
    public void setExerciseName(String exerciseName) {    this.exerciseName = exerciseName;   }
    @Override
    public ExerciseType getExerciseType() { return exerciseType; }
    @Override
    public boolean isStarted() { return started;  }
    @Override
    public void setStarted(boolean started) { this.started = started;  }
    @Override
    public Document getExerciseComment() { return exerciseComment;  }
    @Override
    public void setExerciseComment(Document document) {this.exerciseComment = document; }
    @Override
    public Document getExerciseStatement() { return exerciseStatement; }
    @Override
    public double getStatementPrefHeight() {return statementPrefHeight;  }
    @Override
    public void setStatementPrefHeight(double statementPrefHeight) {this.statementPrefHeight = statementPrefHeight; }
    @Override
    public ExerciseModel<Document> getOriginalModel() { return (ExerciseModel) originalModel; }
    public void setOriginalModel(ExerciseModel<Document> originalModel) { this.originalModel = originalModel; }
    @Override
    public String toString() { return exerciseName; }

}
