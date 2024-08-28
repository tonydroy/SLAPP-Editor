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

package slapp.editor.truth_table_explain;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TruthTableExpModel implements ExerciseModel<Document>, Serializable {
    private static final long serialVersionUID = 100L;
    private String exerciseName = new String("");
    private String explainPrompt = "";
    private ExerciseType exerciseType = ExerciseType.TRUTH_TABLE_ABEXP;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;

    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;
    private Document exerciseComment = new Document();
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private Document explainDocument = new Document();
    private double explainPrefHeight = 60;
    private double explainTextHeight = 0;

    private List<String> unaryOperators = new ArrayList<>();
    private List<String> binaryOperators = new ArrayList<>();

    private List<Document> mainFormulas = new ArrayList<>();
    private List<Document> basicFormulas = new ArrayList<>();
    private String[][]  tableValues;   //[w][h]
    private double gridWidth;
    private Document[] rowComments; //[h]
    private boolean[] columnHighlights; //[w]
    private boolean conclusionDivider = false;
    private String choiceLead = new String("");
    private String aPrompt = new String("");
    private boolean aSelected = false;
    private String bPrompt = new String("");
    private boolean bSelected = false;

    private int tableRows = 0;


    public TruthTableExpModel(){}

    void setEmptyTableContents(int columns) {
        String[][] mainValues = new String[columns][tableRows];
        for (int i = 0; i < columns; i++) {
            String[] column = new String[tableRows];
            for (int j = 0; j < tableRows; j++) {
                column[j] = "";
            }
            mainValues[i] = column;
        }
        tableValues = mainValues;

        boolean[] highlights = new boolean[columns];
        for (int i = 0; i < columns; i++) {
            highlights[i] = false;
        }
        columnHighlights = highlights;

        Document[] cmts = new Document[tableRows];
        for (int i = 0; i < tableRows; i++) {
            cmts[i] = new Document();
        }
        rowComments = cmts;
    }


    void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    void setExerciseStatement(Document exerciseStatement) {
        this.exerciseStatement = exerciseStatement;
    }

    void setUnaryOperators(List<String> unaryOperators) {
        this.unaryOperators = unaryOperators;
    }

    void setBinaryOperators(List<String> binaryOperators) {
        this.binaryOperators = binaryOperators;
    }

    void setMainFormulas(List<Document> mainFormulas) {
        this.mainFormulas = mainFormulas;
    }

    void setTableValues(String[][] tableValues) {
        this.tableValues = tableValues;
    }

    void setRowComments(Document[] rowComments) {
        this.rowComments = rowComments;
    }

    void setColumnHighlights(boolean[] columnHighlights) {
        this.columnHighlights = columnHighlights;
    }

    void setConclusionDivider(boolean conclusionDivider) {
        this.conclusionDivider = conclusionDivider;
    }

    void setChoiceLead(String choiceLead) {
        this.choiceLead = choiceLead;
    }

    void setaPrompt(String aPrompt) {
        this.aPrompt = aPrompt;
    }

    void setaSelected(boolean aSelected) {
        this.aSelected = aSelected;
    }

    void setbPrompt(String bPrompt) {
        this.bPrompt = bPrompt;
    }

    void setbSelected(boolean bSelected) {
        this.bSelected = bSelected;
    }

    void setExplainDocument(Document explainDocument) {
        this.explainDocument = explainDocument;
    }

    void setTableRows(int tableRows) { this.tableRows = tableRows;  }

    int getTableRows() { return tableRows; }
    Document[] getRowComments() { return rowComments;}
    boolean[] getColumnHighlights() { return columnHighlights; }
    String[][] getTableValues() { return tableValues; }
    boolean isConclusionDivider() { return conclusionDivider; }
    List<Document> getMainFormulas() { return mainFormulas; }
    List<String> getUnaryOperators() { return unaryOperators; }
    List<String> getBinaryOperators() { return binaryOperators; }
    void setBasicFormulas(List<Document> basicFormulas) { this.basicFormulas = basicFormulas;  }
    List<Document> getBasicFormulas() { return basicFormulas; }
    Document getExplainDocument() { return explainDocument; }
    String getChoiceLead() {return choiceLead; }
    String getaPrompt() { return aPrompt; }
    boolean isaSelected() { return aSelected; }
    String getbPrompt() { return bPrompt; }
    boolean isbSelected() {return bSelected; }
    String getExplainPrompt() {    return explainPrompt;  }
    void setExplainPrompt(String explainPrompt) {    this.explainPrompt = explainPrompt;  }
    double getCommentPrefHeight() {    return commentPrefHeight;  }
    void setCommentPrefHeight(double commentPrefHeight) {  this.commentPrefHeight = commentPrefHeight; }
    double getExplainPrefHeight() {  return explainPrefHeight;  }
    void setExplainPrefHeight(double explainPrefHeight) { this.explainPrefHeight = explainPrefHeight;   }

    double getStatementTextHeight() {    return statementTextHeight;  }

    void setStatementTextHeight(double statementTextHeight) {    this.statementTextHeight = statementTextHeight;  }

    double getCommentTextHeight() {     return commentTextHeight;  }

    void setCommentTextHeight(double commentTextHeight) {     this.commentTextHeight = commentTextHeight;  }

    double getExplainTextHeight() {    return explainTextHeight;  }

    void setExplainTextHeight(double explainTextHeight) {    this.explainTextHeight = explainTextHeight;  }

    double getGridWidth() {    return gridWidth;  }

    void setGridWidth(double gridWidth) {     this.gridWidth = gridWidth;  }

    @Override
    public String getExerciseName() {
        return exerciseName;
    }
    @Override
    public ExerciseType getExerciseType() { return exerciseType; }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void setStarted(boolean started) { this.started = started; }

    @Override
    public Document getExerciseComment() {
        return exerciseComment;
    }

    @Override
    public Document getExerciseStatement() {
        return exerciseStatement;
    }

    @Override
    public void setExerciseComment(Document comment) { exerciseComment = comment;    }

    @Override
    public double getStatementPrefHeight() {
        return statementPrefHeight;
    }

    @Override
    public void setStatementPrefHeight(double height) { statementPrefHeight = height;  }

    @Override
    public ExerciseModel<Document> getOriginalModel() {  return originalModel;  }

    public void setOriginalModel(ExerciseModel<Document> originalModel) {  this.originalModel = originalModel;  }

    @Override
    public String toString() {
        return exerciseName;
    }

}
