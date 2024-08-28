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

package slapp.editor.derivation_explain;

import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import slapp.editor.derivation.ModelLine;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DrvtnExpModel implements ExerciseModel<Document>, Serializable {
    private static final long serialVersionUID = 100L;
    private String exerciseName = new String("");
    private ExerciseType exerciseType  = ExerciseType.DRVTN_EXP;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;

    private double splitPanePrefWidth;
    private double gridWidth = 0;
    private String contentPrompt ="";
    private boolean isLeftmostScopeLine = true;
    private boolean defaultShelf = true;
    private RichTextAreaSkin.KeyMapValue keyboardSelector;
    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;
    private Document exerciseComment = new Document();
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private Document explanationDocument = new Document();
    private double explanationPrefHeight = 120;
    private double explanationTextHeight = 0;

    private List<ModelLine> derivationLines = new ArrayList<>();


    public DrvtnExpModel(String name, boolean started, double statementPrefHeight, double gridWidth, String contentPrompt, boolean isLeftmostScopeLine, boolean defaultShelf, RichTextAreaSkin.KeyMapValue keyboardSelector,
                         Document exerciseStatement, Document exerciseComment, Document explanationDocument, List<ModelLine> derivationLines) {
        this.exerciseName = name;
        this.started = started;
        this.statementPrefHeight = statementPrefHeight;
        this.gridWidth = gridWidth;
        this.contentPrompt = contentPrompt;
        this.isLeftmostScopeLine = isLeftmostScopeLine;
        this.keyboardSelector = keyboardSelector;
        this.defaultShelf = defaultShelf;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.explanationDocument = explanationDocument;
        this.derivationLines = derivationLines;
    }

    boolean isLeftmostScopeLine() { return isLeftmostScopeLine; }
    boolean isDefaultShelf() { return defaultShelf; }
    double getGridWidth() {return gridWidth; }
    Document getExplanationDocument() { return explanationDocument;  }
    List<ModelLine> getDerivationLines() { return derivationLines; }
    String getContentPrompt() { return contentPrompt; }
    RichTextAreaSkin.KeyMapValue getKeyboardSelector() {return keyboardSelector;}

    double getCommentPrefHeight() {
        return commentPrefHeight;
    }

    void setCommentPrefHeight(double commentPrefHeight) {
        this.commentPrefHeight = commentPrefHeight;
    }

    double getExplanationPrefHeight() {    return explanationPrefHeight;  }
    void setExplanationPrefHeight(double explanationPrefHeight) {  this.explanationPrefHeight = explanationPrefHeight;   }
    double getSplitPanePrefWidth() {    return splitPanePrefWidth;  }
    void setSplitPanePrefWidth(double splitPanePrefWidth) {    this.splitPanePrefWidth = splitPanePrefWidth;  }

    double getStatementTextHeight() {    return statementTextHeight;  }

    void setStatementTextHeight(double statementTextHeight) {  this.statementTextHeight = statementTextHeight;  }

    double getCommentTextHeight() {    return commentTextHeight;  }

    void setCommentTextHeight(double commentTextHeight) {    this.commentTextHeight = commentTextHeight;   }

    double getExplanationTextHeight() {     return explanationTextHeight;  }

    void setExplanationTextHeight(double explanationTextHeight) {   this.explanationTextHeight = explanationTextHeight;   }

    @Override
    public String getExerciseName() { return exerciseName; }
    @Override
    public ExerciseType getExerciseType() { return exerciseType; }
    @Override
    public boolean isStarted() { return started; }
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
    public void setStatementPrefHeight(double height) { this.statementPrefHeight = height; }

    @Override
    public ExerciseModel<Document> getOriginalModel() { return originalModel;  }

    public void setOriginalModel(ExerciseModel<Document> originalModel) { this.originalModel = originalModel;  }

    @Override
    public String toString() { return exerciseName; }

}
