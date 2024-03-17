package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;

public class HorizontalTreeModel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.HORIZONTAL_TREE;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private Document explainDocument = new Document();

    public Document getExplainDocument() {return explainDocument; }

    @Override
    public String getExerciseName() { return exerciseName; }

    @Override
    public ExerciseType getExerciseType() { return exerciseType;  }

    @Override
    public boolean isStarted() { return started; }

    @Override
    public void setStarted(boolean started) { this.started = started;  }

    @Override
    public Document getExerciseComment() { return exerciseComment; }

    @Override
    public Document getExerciseStatement() { return exerciseStatement; }

    @Override
    public void setExerciseComment(Document document) { this.exerciseComment = document;  }

    @Override
    public double getStatementPrefHeight() { return statementPrefHeight;  }

    @Override
    public void setStatementPrefHeight(double height) { this.statementPrefHeight = height; }

    @Override
    public ExerciseModel<Document> getOriginalModel() { return originalModel; }

    @Override
    public void setOriginalModel(ExerciseModel<Document> exerciseModel) { this.originalModel = exerciseModel; }


}
