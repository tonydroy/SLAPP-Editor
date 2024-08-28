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

package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.PrintUtilities;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HorizontalTreeModel implements ExerciseModel<Document>, Serializable {
    private static final long serialVersionUID = 100L;

    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.HORIZONTAL_TREE;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;

    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;
    private Document exerciseComment = new Document();
    private double commentPrefHeight = 60;
    private double commentTextHeight = 80;
    private Document explainDocument = new Document();
    private double explainPrefHeight = 70;
    private double explainTextHeight = 0;
    private String explainPrompt = "";

    private List<TreeModel> treeModels = new ArrayList<>();
    private double mainPaneWidth;
    private boolean axis = false;

    public HorizontalTreeModel() {}


    Document getExplainDocument() {return explainDocument; }

    String getExplainPrompt() {   return explainPrompt;  }

    void setExplainPrompt(String explainPrompt) { this.explainPrompt = explainPrompt; }

    void setExerciseName(String exerciseName) { this.exerciseName = exerciseName;  }

    void setExerciseStatement(Document exerciseStatement) {   this.exerciseStatement = exerciseStatement;    }

    void setExplainDocument(Document explainDocument) { this.explainDocument = explainDocument;  }

    void setAxis(boolean axis) { this.axis = axis; }

    boolean isAxis() {     return axis;  }


    List<TreeModel> getTreeModels() {  return treeModels;  }
    double getCommentPrefHeight() {     return commentPrefHeight;  }

    void setCommentPrefHeight(double commentPrefHeight) {     this.commentPrefHeight = commentPrefHeight;   }

    double getExplainPrefHeight() {     return explainPrefHeight;  }

    void setExplainPrefHeight(double explainPrefHeight) {     this.explainPrefHeight = explainPrefHeight;   }

    double getStatementTextHeight() {     return statementTextHeight;  }

    void setStatementTextHeight(double statementTextHeight) {     this.statementTextHeight = statementTextHeight;   }

    double getCommentTextHeight() {     return commentTextHeight;  }

    void setCommentTextHeight(double commentTextHeight) {     this.commentTextHeight = commentTextHeight;   }

    double getExplainTextHeight() {     return explainTextHeight;  }

    void setExplainTextHeight(double explainTextHeight) {     this.explainTextHeight = explainTextHeight;   }

    double getMainPaneWidth() {     return mainPaneWidth;  }

    void setMainPaneWidth(double mainPaneWidth) {     this.mainPaneWidth = mainPaneWidth;  }

    @Override
    public String getExerciseName() { return exerciseName; }

    @Override
    public ExerciseType getExerciseType() { return exerciseType;  }

    @Override
    public boolean isStarted() { return started; }

    @Override
    public void setStarted(boolean started) { this.started = started;  }

    @Override
    public Document getExerciseComment() { return exerciseComment; }

    @Override
    public Document getExerciseStatement() { return exerciseStatement; }

    @Override
    public void setExerciseComment(Document document) { this.exerciseComment = document;  }

    @Override
    public double getStatementPrefHeight() { return statementPrefHeight;  }

    @Override
    public void setStatementPrefHeight(double height) { this.statementPrefHeight = height; }

    @Override
    public ExerciseModel<Document> getOriginalModel() { return originalModel; }

    @Override
    public void setOriginalModel(ExerciseModel<Document> exerciseModel) { this.originalModel = exerciseModel; }
    @Override
    public String toString() { return exerciseName; }


}
