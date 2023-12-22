package slapp.editor.derivation;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DerivationModel implements ExerciseModel<Document>, Serializable {
    private String exerciseName = new String("");
    private boolean started = false;
    private double statementPrefHeight = 80;
    private double gridWidth = 0;
    private boolean isLeftmostScopeLine = true;
    private boolean defaultShelf = true;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private List<ModelLine> derivationLines = new ArrayList<>();


    public DerivationModel(String name, boolean started, double statementPrefHeight, double gridWidth, boolean isLeftmostScopeLine, boolean defaultShelf, Document exerciseStatement, Document exerciseComment, List<ModelLine> derivationLines) {
        this.exerciseName = name;
        this.started = started;
        this.statementPrefHeight = statementPrefHeight;
        this.gridWidth = gridWidth;
        this.isLeftmostScopeLine = isLeftmostScopeLine();
        this.defaultShelf = defaultShelf;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.derivationLines = derivationLines;
    }

    public boolean isLeftmostScopeLine() { return isLeftmostScopeLine; }
    public boolean isDefaultShelf() { return defaultShelf; }
    public double getGridWidth() {return gridWidth; }
    public List<ModelLine> getDerivationLines() { return derivationLines; }

    @Override
    public String getExerciseName() { return exerciseName; }
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
    public String toString() { return exerciseName; }

}
