package slapp.editor.truth_table;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TruthTableModel implements ExerciseModel<Document>, Serializable {

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


    public TruthTableModel(){}

    public void setEmptyTableContents(int columns) {
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


    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setExerciseStatement(Document exerciseStatement) {
        this.exerciseStatement = exerciseStatement;
    }

    public void setUnaryOperators(List<String> unaryOperators) {
        this.unaryOperators = unaryOperators;
    }

    public void setBinaryOperators(List<String> binaryOperators) {
        this.binaryOperators = binaryOperators;
    }

    public void setMainFormulas(List<Document> mainFormulas) {
        this.mainFormulas = mainFormulas;
    }

    public void setTableValues(String[][] tableValues) {
        this.tableValues = tableValues;
    }

    public void setRowComments(Document[] rowComments) {
        this.rowComments = rowComments;
    }

    public void setColumnHighlights(boolean[] columnHighlights) {
        this.columnHighlights = columnHighlights;
    }

    public void setConclusionDivider(boolean conclusionDivider) {
        this.conclusionDivider = conclusionDivider;
    }

    public void setTableRows(int tableRows) { this.tableRows = tableRows;  }

    public int getTableRows() { return tableRows; }
    public Document[] getRowComments() { return rowComments;}
    public boolean[] getColumnHighlights() { return columnHighlights; }
    public String[][] getTableValues() { return tableValues; }
    public boolean isConclusionDivider() { return conclusionDivider; }
    public List<Document> getMainFormulas() { return mainFormulas; }
    public List<String> getUnaryOperators() { return unaryOperators; }
    public List<String> getBinaryOperators() { return binaryOperators; }
    public void setBasicFormulas(List<Document> basicFormulas) { this.basicFormulas = basicFormulas;  }
    public List<Document> getBasicFormulas() { return basicFormulas; }

    public double getCommentPrefHeight() {    return commentPrefHeight;  }

    public void setCommentPrefHeight(double commentPrefHeight) {  this.commentPrefHeight = commentPrefHeight;  }

    public double getStatementTextHeight() {     return statementTextHeight;  }

    public void setStatementTextHeight(double statementTextHeight) {    this.statementTextHeight = statementTextHeight;  }

    public double getCommentTextHeight() {    return commentTextHeight;  }

    public void setCommentTextHeight(double commentTextHeight) {
        this.commentTextHeight = commentTextHeight;
    }

    public void setExerciseType(ExerciseType exerciseType) {     this.exerciseType = exerciseType;  }

    public double getGridWidth() {    return gridWidth;   }

    public void setGridWidth(double gridWidth) {     this.gridWidth = gridWidth;  }

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
