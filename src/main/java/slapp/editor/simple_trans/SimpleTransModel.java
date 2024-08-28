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

package slapp.editor.simple_trans;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;

public class SimpleTransModel implements ExerciseModel<Document>, Serializable {
    private static final long serialVersionUID = 100L;
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

    private Document exerciseInterpretation = new Document("");
    private double interpretationPrefHeight = 200;
    private double interpretationTextHeight = 0;

    private Document exerciseResponse = new Document("");
    private double responsePrefHeight = 60;
    private double responseTextHeight = 0;


    public SimpleTransModel(String name) {
        exerciseName = name;

    }


    double getCommentTextHeight() {
        return commentTextHeight;
    }
    void setCommentTextHeight(double commentTextHeight) {
        this.commentTextHeight = commentTextHeight;
    }

    void setExerciseStatement(Document exerciseStatement) {  this.exerciseStatement = exerciseStatement;  }
    double getStatementTextHeight() {
        return statementTextHeight;
    }
    void setStatementTextHeight(double statementTextHeight) {    this.statementTextHeight = statementTextHeight;  }
    double getCommentPrefHeight() {     return commentPrefHeight;  }
    void setCommentPrefHeight(double commentPrefHeight) {    this.commentPrefHeight = commentPrefHeight;  }
    Document getExerciseResponse() {   return exerciseResponse;  }
    void setExerciseResponse(Document exerciseResponse) {   this.exerciseResponse = exerciseResponse;  }
    double getResponsePrefHeight() {     return responsePrefHeight;  }
    void setResponsePrefHeight(double responsePrefHeight) {    this.responsePrefHeight = responsePrefHeight;  }
    double getResponseTextHeight() {     return responseTextHeight;  }
    void setResponseTextHeight(double responseTextHeight) {     this.responseTextHeight = responseTextHeight;  }

    Document getExerciseInterpretation() {   return exerciseInterpretation;  }
    void setExerciseInterpretation(Document exerciseInterpretation) {   this.exerciseInterpretation = exerciseInterpretation;  }
    double getInterpretationPrefHeight() {     return interpretationPrefHeight;  }
    void setInterpretationPrefHeight(double interpretationPrefHeight) {    this.interpretationPrefHeight = interpretationPrefHeight;  }
    double getInterpretationTextHeight() {     return interpretationTextHeight;  }
    void setInterpretationTextHeight(double interpretationTextHeight) {     this.interpretationTextHeight = interpretationTextHeight;  }




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
