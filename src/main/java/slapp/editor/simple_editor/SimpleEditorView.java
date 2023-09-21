package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.scene.Node;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;

public class SimpleEditorView implements ExerciseView {

    RichTextArea exerciseStatement;
    RichTextArea exerciseContent;
    RichTextArea exerciseComment;



    @Override
    public Node getExerciseStatementNode() {
        return exerciseStatement;
    }
    @Override
    public Node getExerciseContentNode() {
        return exerciseContent;
    }
    @Override
    public Node getExerciseCommentNode() {
        return exerciseComment;
    }
}
