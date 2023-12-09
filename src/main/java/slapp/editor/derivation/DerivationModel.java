package slapp.editor.derivation;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DerivationModel implements ExerciseModel<Document, List<ModelLine>>, Serializable {
    private String exerciseName = new String("");
    private boolean started = false;
    private double statementPrefHeight = 80;
    private double gridWidth = 0;

    private boolean isLeftmostScopeLine = true;
    private boolean defaultShelf = true;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private List<ModelLine> exerciseContent = new ArrayList<>();


    //fake model for test
    public DerivationModel() {
        exerciseName = "test derivation";
        isLeftmostScopeLine = true;
        defaultShelf = true;

        exerciseStatement = new Document("this is a statement");

        exerciseContent.add(new ModelLine(1, new Document("A"), "P", LineType.PREMISE_LINE));
        exerciseContent.add(new ModelLine(1, null, "", LineType.SETUP_SHELF_LINE));
        exerciseContent.add(new ModelLine(1, new Document(), "", LineType.MAIN_CONTENT_LINE));
        exerciseContent.add(new ModelLine(2, new Document("B"), "", LineType.MAIN_CONTENT_LINE));
        exerciseContent.add(new ModelLine(2, null, "", LineType.SHELF_LINE));
        exerciseContent.add(new ModelLine(2, new Document(), "", LineType.MAIN_CONTENT_LINE));
        exerciseContent.add(new ModelLine(2, new Document(), "", LineType.MAIN_CONTENT_LINE));
        exerciseContent.add(new ModelLine(1, new Document("result"), "2,3-4 xx", LineType.CONCLUSION_LINE));


    }


    public DerivationModel(String name, boolean started, double statementPrefHeight, double gridWidth, boolean isLeftmostScopeLine, boolean defaultShelf, Document exerciseStatement, Document exerciseComment, List<ModelLine> exerciseContent) {
        this.exerciseName = name;
        this.started = started;
        this.statementPrefHeight = statementPrefHeight;
        this.isLeftmostScopeLine = isLeftmostScopeLine();
        this.defaultShelf = defaultShelf;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.exerciseContent = exerciseContent;
    }


    public boolean isLeftmostScopeLine() { return isLeftmostScopeLine; }
    public void setLeftmostScopeLine(boolean leftmostScopeLine) { isLeftmostScopeLine = leftmostScopeLine; }
    public boolean isDefaultShelf() { return defaultShelf; }
    public void setDefaultShelf(boolean defaultShelf) { this.defaultShelf = defaultShelf; }

    public double getGridWidth() {return gridWidth; }

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
    public List<ModelLine> getExerciseContent() { return exerciseContent; }
    @Override
    public String getContentPrompt() { return ""; }
    @Override
    public String toString() { return exerciseName; }

}
