/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.
If not, see <https://www.gnu.org/licenses/>.
 */

package slapp.editor.abefg_explain;


import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;
import slapp.editor.page_editor.PageContent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ABEFGmodel implements ExerciseModel<Document>, Serializable {
    private static final long serialVersionUID = 100L;
    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.ABEFG_EXPLAIN;
    private ExerciseModel<Document> originalModel = null;
    private ABEFGmodelExtra modelFields = new ABEFGmodelExtra();
    private boolean started = false;
    private String prompt = "";

    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;

    private Document exerciseComment = new Document();
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private List<PageContent> pageContents = new ArrayList<>();
    private double paginationPrefHeight = 450;


    public ABEFGmodel(String name, ABEFGmodelExtra modelFields, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, List<PageContent> pageContents) {
        this.exerciseName = name;
        this.modelFields = modelFields;
        this.started = started;
        this.prompt = prompt;
        this.statementPrefHeight = statementPrefHeight;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.pageContents = pageContents;
        if (pageContents.isEmpty()) pageContents.add(new PageContent(new Document(), 0.0));
    }

    void addBlankContentPage(int position) { pageContents.add(position, new PageContent(new Document(), 0.0)); }
    public String getContentPrompt() {
        return prompt;
    }
    ABEFGmodelExtra getModelFields() {   return modelFields; }

    public double getCommentPrefHeight() {return commentPrefHeight;  }

    public void setCommentPrefHeight(double commentPrefHeight) { this.commentPrefHeight = commentPrefHeight; }

    public double getPaginationPrefHeight() { return paginationPrefHeight; }

    public void setPaginationPrefHeight(double paginationPrefHeight) { this.paginationPrefHeight = paginationPrefHeight;  }

    public double getStatementTextHeight() {    return statementTextHeight;  }

    public void setStatementTextHeight(double statementTextHeight) {   this.statementTextHeight = statementTextHeight;  }

    public double getCommentTextHeight() {     return commentTextHeight;   }

    public void setCommentTextHeight(double commentTextHeight) {     this.commentTextHeight = commentTextHeight;   }

    @Override
    public String getExerciseName() { return exerciseName; }
    @Override
    public ExerciseType getExerciseType() { return exerciseType; }
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

    public List<PageContent> getPageContents() { return pageContents;  }

    @Override
    public ExerciseModel<Document> getOriginalModel() {  return originalModel;  }

    @Override
    public void setOriginalModel(ExerciseModel<Document> originalModel) { this.originalModel = originalModel;  }

    @Override
    public String toString() {
        String name = getExerciseName();
        return name;
    }

}

