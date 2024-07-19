package slapp.editor.derivation;

import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import slapp.editor.PrintUtilities;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DerivationModel implements ExerciseModel<Document>, Serializable {
    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.DERIVATION;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;


    private double splitPanePrefWidth = PrintUtilities.getPageWidth();

    private double gridWidth = 0;
    private boolean isLeftmostScopeLine = true;
    private boolean defaultShelf = true;
    private RichTextAreaSkin.KeyMapValue keyboardSelector;
    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;
    private Document exerciseComment = new Document();
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private List<ModelLine> derivationLines = new ArrayList<>();


    public DerivationModel(String name, boolean started, double statementPrefHeight, double gridWidth, boolean isLeftmostScopeLine, boolean defaultShelf, RichTextAreaSkin.KeyMapValue keyboardSelector,
                           Document exerciseStatement, Document exerciseComment, List<ModelLine> derivationLines) {
        this.exerciseName = name;
        this.started = started;
        this.statementPrefHeight = statementPrefHeight;
        this.gridWidth = gridWidth;
        this.isLeftmostScopeLine = isLeftmostScopeLine;
        this.defaultShelf = defaultShelf;
        this.keyboardSelector = keyboardSelector;
        this.exerciseStatement = exerciseStatement;
        this.exerciseComment = exerciseComment;
        this.derivationLines = derivationLines;
        this.splitPanePrefWidth = PrintUtilities.getPageWidth();
    }

    public boolean isLeftmostScopeLine() { return isLeftmostScopeLine; }
    public boolean isDefaultShelf() { return defaultShelf; }
    public double getGridWidth() {return gridWidth; }
    public List<ModelLine> getDerivationLines() { return derivationLines; }
    public RichTextAreaSkin.KeyMapValue getKeyboardSelector() {return keyboardSelector;}
    public double getCommentPrefHeight() { return commentPrefHeight;  }
    public void setCommentPrefHeight(double commentPrefHeight) { this.commentPrefHeight = commentPrefHeight; }
    public double getSplitPanePrefWidth() {   return splitPanePrefWidth;  }
    public void setSplitPanePrefWidth(double splitPanePrefWidth) {  this.splitPanePrefWidth = splitPanePrefWidth;    }

    public double getStatementTextHeight() {     return statementTextHeight;   }

    public void setStatementTextHeight(double statementTextHeight) {     this.statementTextHeight = statementTextHeight;   }

    public double getCommentTextHeight() {     return commentTextHeight;   }

    public void setCommentTextHeight(double commentTextHeight) {     this.commentTextHeight = commentTextHeight;   }

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
    public ExerciseModel<Document> getOriginalModel() {  return originalModel;  }

    public void setOriginalModel(ExerciseModel<Document> originalModel) {this.originalModel = originalModel;}

    @Override
    public String toString() { return exerciseName; }

}
