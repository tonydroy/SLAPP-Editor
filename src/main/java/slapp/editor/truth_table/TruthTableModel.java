package slapp.editor.truth_table;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TruthTableModel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private boolean started = false;
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private List<Document> headerFormulas = new ArrayList<>();
    private List<Document> basicFormulas = new ArrayList<>();
    private char[] operators;
    private char[][] tableValues;   //[h][w]
    private Document[] rowComments; //[h]
    private boolean[] columnHeighlight; //[w]
    private String choiceLead = new String("");
    private String aPrompt = new String("");
    private boolean aValue = false;
    private String bPrompt = new String("");
    private boolean bValue = false;
    private Document explainArea = new Document();
    private String explainPrompt = new String("");

    @Override
    public String getExerciseName() {
        return null;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public void setStarted(boolean started) {

    }

    @Override
    public Document getExerciseComment() {
        return null;
    }

    @Override
    public Document getExerciseStatement() {
        return null;
    }

    @Override
    public void setExerciseComment(Document statement) {

    }

    @Override
    public double getStatementPrefHeight() {
        return 0;
    }

    @Override
    public void setStatementPrefHeight(double height) {

    }
}
