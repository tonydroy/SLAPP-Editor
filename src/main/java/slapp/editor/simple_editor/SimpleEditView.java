package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
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

        exerciseStatementEditor.setPrefWidth(PrintUtilities.getPageWidth());
        exerciseContentEditor.setPrefWidth(PrintUtilities.getPageWidth());
        exerciseStatementEditor.setPrefHeight(80.0);
        exerciseCommentEditor.setPrefHeight(80.0);




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

    public void updateExerciseStatement(Document statementDocument) {
        exerciseStatement.getEditor().setDocument(statementDocument);
//        exerciseStatement.getEditor().setEditable(false);
    }
    public void updateExerciseContent(Document contentDocument) {
        exerciseContent.getEditor().setDocument(contentDocument);
    }
    public void updateExerciseComment(Document commentDocument) {
        exerciseComment.getEditor().setDocument(commentDocument);
    }

    @Override
    public double getStatementHeight() { return exerciseStatement.getEditor().getHeight(); }
    @Override
    public double getCommentHeight() { return exerciseComment.getEditor().getHeight(); }



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

    public void setStatementPrefHeight(double height) {
        exerciseStatement.getEditor().setPrefHeight(height);
    }




}

