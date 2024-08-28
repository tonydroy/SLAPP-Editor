/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

package slapp.editor.page_editor;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The model for page edit exercise
 */
public class PageEditModel implements ExerciseModel<Document>, Serializable {
    private static final long serialVersionUID = 100L;
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


    /**
     * Set up PageEditModel, including blank page if there are no other page contents
     * @param name the exercise name
     * @param started true if exercise is started, and otherwise false
     * @param prompt prompt to appear on first page of blank pagination
     * @param statementPrefHeight preferred height of statement window
     * @param exerciseStatement the statement Document
     * @param exerciseComment the comment document
     * @param pageContents list of {@link slapp.editor.page_editor.PageContent}
     */
    PageEditModel(String name, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, List<PageContent> pageContents) {
        this.exerciseName = name;
        this.started = started;
        this.contentPrompt = prompt;
        this.statementPrefHeight = statementPrefHeight;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.pageContents = pageContents;
        if (pageContents.isEmpty()) pageContents.add(new PageContent(new Document(), 0.0));
    }

    /**
     * Add blank page content at index of list, pushing other members down
     * @param position the position at which to add the page content
     */
    void addBlankContentPage(int position) {
        pageContents.add(position, new PageContent(new Document(), 0.0));
    }

    /**
     * Members of the page contents list correspond to pages of the pagination
     * @return the page contents list
     */
    List<PageContent> getPageContents() {
        return pageContents;
    }

    /**
     * The prompt to appear on the first page of blank pagination
     * @return
     */
    String getContentPrompt() {
        return contentPrompt;
    }

    /**
     * The preferred height of the comment window
     * @return the height value
     */
    double getCommentPrefHeight() {return commentPrefHeight; }

    /**
     *
     * @param height
     */
    void setCommentPrefHeight(double height) {this.commentPrefHeight = height; }

    double getPaginationPrefHeight() { return paginationPrefHeight;   }

    void setPaginationPrefHeight(double paginationPrefHeight) { this.paginationPrefHeight = paginationPrefHeight; }



    double getCommentTextHeight() {
        return commentTextHeight;
    }

    void setCommentTextHeight(double commentTextHeight) {
        this.commentTextHeight = commentTextHeight;
    }

    double getStatementTextHeight() {
        return statementTextHeight;
    }

   void setStatementTextHeight(double statementTextHeight) {
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
