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

package slapp.editor.truth_table;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for the truth table exercise
 */
public class TruthTableModel implements ExerciseModel<Document>, Serializable {
    private static final long serialVersionUID = 100L;
    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.TRUTH_TABLE;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;
    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;
    private Document exerciseComment = new Document();
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private List<String> unaryOperators = new ArrayList<>();
    private List<String> binaryOperators = new ArrayList<>();

    private List<Document> mainFormulas = new ArrayList<>();
    private List<Document> basicFormulas = new ArrayList<>();
    private String[][]  tableValues;   //[w][h]
    private double gridWidth;
    private Document[] rowComments; //[h]
    private boolean[] columnHighlights; //[w]
    private boolean conclusionDivider = false;
    private int tableRows = 0;

    /**
     * Construct truth table model
     */
    public TruthTableModel(){}

    /**
     * Fill in empty values for table contents
     * @param columns number of table columns
     */
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

    /**
     * The exercise name (doubles as file name)
     * @param exerciseName the string name
     */
    void setExerciseName(String exerciseName) {     this.exerciseName = exerciseName;   }

    /**
     * The Document for the exercise prompt field
     * @param exerciseStatement the Document
     */
    void setExerciseStatement(Document exerciseStatement) {     this.exerciseStatement = exerciseStatement;   }

    /**
     * The array Document[] of row comments
     * @param rowComments the array
     */
    void setRowComments(Document[] rowComments) {     this.rowComments = rowComments;   }

    /**
     * The number of (non-header, footer) rows for this table
     * @param tableRows the rows value
     */
    void setTableRows(int tableRows) { this.tableRows = tableRows;  }

    /**
     * The number of (non-header, footer) rows for this table
     * @return the rows value
     */
    int getTableRows() { return tableRows; }

    /**
     * The array Document[] of comments on the rows
     * @return the array
     */
    Document[] getRowComments() { return rowComments;}

    /**
     * The array Boolean[] of columns to be shown with highlight
     * @param columnHighlights true if highlight and otherwise false
     */
    void setColumnHighlights(boolean[] columnHighlights) {
        this.columnHighlights = columnHighlights;
    }

    /**
     * The array Boolean[] of columns to be shown with highlight
     * @return true if heighlight and otherwise false
     */
    boolean[] getColumnHighlights() { return columnHighlights; }

    /**
     * The array String[][] of values (usually 'T' or 'F' appearing in the table
     * @param tableValues the array
     */
    void setTableValues(String[][] tableValues) {
        this.tableValues = tableValues;
    }

    /**
     * The array String[][] of values (usually 'T' or 'F' appearing in the table
     * @return the array
     */
    String[][] getTableValues() { return tableValues; }

    /**
     * The conclusion divider adds ' / ' before the final formula column
     * @param conclusionDivider true if is divider and otherwise false
     */
    void setConclusionDivider(boolean conclusionDivider) {
        this.conclusionDivider = conclusionDivider;
    }

    /**
     * The conclusion divider adds ' / ' before the final formula column
     * @return true if is divider and otherwise false
     */
    boolean isConclusionDivider() { return conclusionDivider; }

    /**
     * The list of formulas to be evaluated in the main part of this table
     * @param mainFormulas the list of formula Documents
     */
    void setMainFormulas(List<Document> mainFormulas) {    this.mainFormulas = mainFormulas;  }

    /**
     * The list of formulas to be evaluated in the main part of this table
     * @return the list of formula documents
     */
    List<Document> getMainFormulas() { return mainFormulas; }

    /**
     * The list of unary operators for this sentential language
     * @param unaryOperators the list of Strings
     */
    void setUnaryOperators(List<String> unaryOperators) {     this.unaryOperators = unaryOperators;  }

    /**
     * The list of unary operators for this sentential language
     * @return the list of Strings
     */
    List<String> getUnaryOperators() { return unaryOperators; }

    /**
     * The list of binary operators for this sentential language
     * @param binaryOperators the list of strings
     */
    void setBinaryOperators(List<String> binaryOperators) {      this.binaryOperators = binaryOperators;  }

    /**
     * The list of binary operators for this sentential language
     * @return the list of strings
     */
    List<String> getBinaryOperators() { return binaryOperators; }
    void setBasicFormulas(List<Document> basicFormulas) { this.basicFormulas = basicFormulas;  }
    List<Document> getBasicFormulas() { return basicFormulas; }

    double getCommentPrefHeight() {    return commentPrefHeight;  }

    void setCommentPrefHeight(double commentPrefHeight) {  this.commentPrefHeight = commentPrefHeight;  }

    double getStatementTextHeight() {     return statementTextHeight;  }

    void setStatementTextHeight(double statementTextHeight) {    this.statementTextHeight = statementTextHeight;  }

    double getCommentTextHeight() {    return commentTextHeight;  }

    void setCommentTextHeight(double commentTextHeight) {
        this.commentTextHeight = commentTextHeight;
    }

    void setExerciseType(ExerciseType exerciseType) {     this.exerciseType = exerciseType;  }

    double getGridWidth() {    return gridWidth;   }

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
    public ExerciseModel<Document> getOriginalModel() { return originalModel; }

    public void setOriginalModel(ExerciseModel<Document> originalModel) { this.originalModel = originalModel;  }

    @Override
    public String toString() {
        return exerciseName;
    }

}
