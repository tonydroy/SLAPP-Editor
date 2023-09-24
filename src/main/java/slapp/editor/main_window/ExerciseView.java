package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import slapp.editor.decorated_rta.DecoratedRTA;

public interface ExerciseView {

    Node getExerciseStatementNode();
    Node getExerciseContentNode();
    DecoratedRTA getExerciseComment();

    DoubleProperty getContentHeightProperty();
}
