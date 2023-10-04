package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.*;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

public class SimpleEditView implements ExerciseView<DecoratedRTA, DecoratedRTA> {

    String exerciseName;
    DecoratedRTA exerciseStatement;
    DecoratedRTA exerciseContent;
    DecoratedRTA exerciseComment;
    Node exerciseControl = null;
    MainWindowView mainView;

    double statementPrefHeight = 80.0;
    double commentPrefHeight = 80.0;


    public SimpleEditView(SimpleEditExercise exercise) {
        this.mainView = exercise.getMainWindowController().getMainView();
        this.exerciseStatement = new DecoratedRTA();
        this.exerciseContent = new DecoratedRTA();
        this.exerciseComment = new DecoratedRTA();
        setup(exercise);
    }

    private void setup(SimpleEditExercise exercise) {
        RichTextArea exerciseStatementEditor = exerciseStatement.getEditor();
        RichTextArea exerciseContentEditor = exerciseContent.getEditor();
        RichTextArea exerciseCommentEditor = exerciseComment.getEditor();

        exerciseStatementEditor.setPrefHeight(statementPrefHeight);
        exerciseCommentEditor.setPrefHeight(commentPrefHeight);



        exerciseStatementEditor.setPromptText("Problem:");
        exerciseCommentEditor.setPromptText("Comment:");
        exerciseContentEditor.setPromptText("Response:");

        exerciseStatementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(exerciseStatement);
            }
        });
        exerciseContentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(exerciseContent);
            }
        });
        exerciseCommentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(exerciseComment);
            }
        });
    }

    @Override
    public double getStatementHeight() { return statementPrefHeight; }
    @Override
    public double getCommentHeight() { return commentPrefHeight; }



    @Override
    public String getExerciseName() {return exerciseName; }
    @Override
    public void setExerciseName(String name) { this.exerciseName = name; }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) {
        this.exerciseStatement = exerciseStatement;
    }
    @Override
    public DecoratedRTA getExerciseContent() {
        return exerciseContent;
    }
    @Override
    public void setExerciseContent(DecoratedRTA exerciseContent) { this.exerciseContent = exerciseContent; }
    @Override
    public DecoratedRTA getExerciseComment() {
        return exerciseComment;
    }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public Node getExerciseControl() { return exerciseControl; }
    public void setExerciseControl(Node control) { this.exerciseControl = control; }
    public Node getExerciseStatementNode() {
        return exerciseStatement.getEditor();
    }
    public Node getExerciseContentNode() {
        return exerciseContent.getEditor();
    }
    public DoubleProperty getContentHeightProperty() {
        return exerciseContent.getEditor().prefHeightProperty();
    }




}

