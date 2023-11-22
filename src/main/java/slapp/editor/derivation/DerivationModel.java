package slapp.editor.derivation;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.util.ArrayList;
import java.util.List;

public class DerivationModel implements ExerciseModel<Document, List<ModelLine>> {
    private String exerciseName = new String("");
    private boolean started = false;
    private String contentPrompt = "";
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private List<ModelLine> exerciseContent = new ArrayList<>();
    private boolean isLeftmostScopeLine = true;
    private boolean defaultShelf = true;

    //fake model for test
    public DerivationModel() {
        exerciseName = "test derivation";
        isLeftmostScopeLine = true;

        exerciseContent.add(new ModelLine(1, new Document("A"), "P", LineType.CONTENT_LINE, true));
        exerciseContent.add(new ModelLine(1, null, "", LineType.SHELF_LINE, true));
        exerciseContent.add(new ModelLine(2, new Document("B"), "", LineType.CONTENT_LINE, false));
        exerciseContent.add(new ModelLine(2, null, "", LineType.SHELF_LINE, false));
        exerciseContent.add(new ModelLine(2, new Document(), "", LineType.CONTENT_LINE, false));
        exerciseContent.add(new ModelLine(2, new Document(), "", LineType.CONTENT_LINE, false));
        exerciseContent.add(new ModelLine(1, new Document("result"), "1,2-3 XE", LineType.CONTENT_LINE, true));


    }



    public DerivationModel(String name, boolean started, String prompt, double statementPrefHeight, Document exerciseStatement, Document exerciseComment, List<ModelLine> exerciseContent) {
        this.exerciseName = name;
        this.started = started;
        this.contentPrompt = prompt;
        this.statementPrefHeight = statementPrefHeight;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.exerciseContent = exerciseContent;
    }

    public boolean isLeftmostScopeLine() { return isLeftmostScopeLine; }
    public void setLeftmostScopeLine(boolean leftmostScopeLine) { isLeftmostScopeLine = leftmostScopeLine; }
    public boolean isDefaultShelf() { return defaultShelf; }
    public void setDefaultShelf(boolean defaultShelf) { this.defaultShelf = defaultShelf; }

    @Override
    public String getExerciseName() { return exerciseName; }
    @Override
    public boolean isStarted() { return started; }
    @Override
    public void setStarted(boolean started) { this.started = started; }
    @Override
    public Document getExerciseComment() { return exerciseComment; }
    @Override
    public Document getExerciseStatement() { return exerciseStatement; }
    @Override
    public double getStatementPrefHeight() { return statementPrefHeight; }
    @Override
    public void setStatementPrefHeight(double height) { this.statementPrefHeight = height; }
    @Override
    public List<ModelLine> getExerciseContent() { return exerciseContent; }
    @Override
    public String getContentPrompt() { return contentPrompt; }
    @Override
    public String toString() { return exerciseName; }

}
