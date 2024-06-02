package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleEditModel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.SIMPLE_EDITOR;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;
    private String contentPrompt = "";
    private double statementPrefHeight = 80;
    private double commentPrefHeight = 60;
    private double paginationPrefHeight = 450.0;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document("");
    private List<Document> exercisePageDocs = new ArrayList<>();

    public SimpleEditModel(String name, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, List<Document> exercisePageDocs) {
        this.exerciseName = name;
        this.started = started;
        this.contentPrompt = prompt;
        this.statementPrefHeight = statementPrefHeight;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.exercisePageDocs = exercisePageDocs;
        if (exercisePageDocs.isEmpty()) exercisePageDocs.add(new Document());
    }

    void addBlankContentPage(int position) { exercisePageDocs.add(position, new Document());  }
    public List<Document> getExercisePageDocs() {
        return exercisePageDocs;
    }
    public String getContentPrompt() {
        return contentPrompt;
    }

    public double getCommentPrefHeight() {return commentPrefHeight; }
    public void setCommentPrefHeight(double height) {this.commentPrefHeight = height; }

    public double getPaginationPrefHeight() { return paginationPrefHeight;   }

    public void setPaginationPrefHeight(double paginationPrefHeight) { this.paginationPrefHeight = paginationPrefHeight; }

    @Override
    public String getExerciseName() {return exerciseName; }
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
