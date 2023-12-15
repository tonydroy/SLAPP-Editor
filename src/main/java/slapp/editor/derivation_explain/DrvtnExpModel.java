package slapp.editor.derivation_explain;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.derivation.ModelLine;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DrvtnExpModel implements ExerciseModel<Document, List<ModelLine>>, Serializable {
    private String exerciseName = new String("");
    private boolean started = false;
    private double statementPrefHeight = 80;
    private double gridWidth = 0;
    private String contentPrompt ="";
    private boolean isLeftmostScopeLine = true;
    private boolean defaultShelf = true;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private Document explanationDocument = new Document();
    private List<ModelLine> derivationLines = new ArrayList<>();


    public DrvtnExpModel(String name, boolean started, double statementPrefHeight, double gridWidth, String contentPrompt, boolean isLeftmostScopeLine, boolean defaultShelf,
                         Document exerciseStatement, Document exerciseComment, Document explanationDocument, List<ModelLine> derivationLines) {
        this.exerciseName = name;
        this.started = started;
        this.statementPrefHeight = statementPrefHeight;
        this.gridWidth = gridWidth;
        this.contentPrompt = contentPrompt;
        this.isLeftmostScopeLine = isLeftmostScopeLine;
        this.defaultShelf = defaultShelf;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.explanationDocument = explanationDocument;
        this.derivationLines = derivationLines;
    }

    public boolean isLeftmostScopeLine() { return isLeftmostScopeLine; }
    public boolean isDefaultShelf() { return defaultShelf; }
    public double getGridWidth() {return gridWidth; }
    public Document getExplanationDocument() { return explanationDocument;  }

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
    public List<ModelLine> getExerciseContent() { return derivationLines; }
    @Override
    public String getContentPrompt() { return contentPrompt; }
    @Override
    public String toString() { return exerciseName; }

}
