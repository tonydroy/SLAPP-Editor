package slapp.editor.page_editor;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageEditModel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.PAGE_EDIT;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;


    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;

    private Document exerciseComment = new Document("");
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private List<PageContent> pageContents = new ArrayList<>();
    private double paginationPrefHeight = 450.0;
    private String contentPrompt = "";

    public PageEditModel(String name, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, List<PageContent> pageContents) {
        this.exerciseName = name;
        this.started = started;
        this.contentPrompt = prompt;
        this.statementPrefHeight = statementPrefHeight;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.pageContents = pageContents;
        if (pageContents.isEmpty()) pageContents.add(new PageContent(new Document(), 0.0));
    }

    void addBlankContentPage(int position) { pageContents.add(position, new PageContent(new Document(), 0.0));  }
    public List<PageContent> getPageContents() {
        return pageContents;
    }
    public String getContentPrompt() {
        return contentPrompt;
    }

    public double getCommentPrefHeight() {return commentPrefHeight; }
    public void setCommentPrefHeight(double height) {this.commentPrefHeight = height; }

    public double getPaginationPrefHeight() { return paginationPrefHeight;   }

    public void setPaginationPrefHeight(double paginationPrefHeight) { this.paginationPrefHeight = paginationPrefHeight; }



    public double getCommentTextHeight() {
        return commentTextHeight;
    }

    public void setCommentTextHeight(double commentTextHeight) {
        this.commentTextHeight = commentTextHeight;
    }

    public double getStatementTextHeight() {
        return statementTextHeight;
    }

    public void setStatementTextHeight(double statementTextHeight) {
        this.statementTextHeight = statementTextHeight;
    }

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
