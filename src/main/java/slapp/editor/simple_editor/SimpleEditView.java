package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

public class SimpleEditView implements ExerciseView {

    DecoratedRTA exerciseStatement;
    DecoratedRTA exerciseContent;
    DecoratedRTA exerciseComment;
    MainWindowView mainView;


    public SimpleEditView(MainWindowView mainView) {
        this.exerciseStatement = new DecoratedRTA();
        this.exerciseContent = new DecoratedRTA();
        this.exerciseComment = new DecoratedRTA();
        this.mainView = mainView;
        setup();
    }

    private void setup() {
        RichTextArea exerciseStatementEditor = exerciseStatement.getEditor();
        RichTextArea exerciseContentEditor = exerciseContent.getEditor();
        RichTextArea exerciseCommentEditor = exerciseComment.getEditor();

        exerciseStatementEditor.setPrefHeight(70);
        exerciseCommentEditor.setPrefHeight(70);

//        exerciseCommentEditor.setPrefWidth(PrintUtilities.getPageWidth() );
//        double pageWidth = PrintUtilities.getPageWidth() * 12.0/16.0;
//        exerciseContentEditor.setPrefWidth(pageWidth);
//        exerciseContentEditor.setContentAreaWidth(pageWidth - 20); this does not force scrollbar when window is too small

        exerciseStatementEditor.setPromptText("Problem:");
        exerciseCommentEditor.setPromptText("Comment:");
        exerciseContentEditor.setPromptText("Response:");

        exerciseStatementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(exerciseStatementEditor, exerciseStatement.getEditToolbar(), exerciseStatement.getFontsToolbar(), exerciseStatement.getParagraphToolbar());
            }
        });
        exerciseContentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(exerciseContentEditor, exerciseContent.getEditToolbar(), exerciseContent.getFontsToolbar(), exerciseContent.getParagraphToolbar());
            }
        });
        exerciseCommentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(exerciseCommentEditor, exerciseComment.getEditToolbar(), exerciseComment.getFontsToolbar(), exerciseComment.getParagraphToolbar());
            }
        });

    }

    @Override
    public Node getExerciseStatementNode() { return exerciseStatement.getEditor(); }
    @Override
    public Node getExerciseContentNode() {
        return exerciseContent.getEditor();
    }
    @Override
    public DecoratedRTA getExerciseComment() {
        return exerciseComment;
    }
    public DoubleProperty getContentHeightProperty() { return exerciseContent.getEditor().prefHeightProperty(); }


}

