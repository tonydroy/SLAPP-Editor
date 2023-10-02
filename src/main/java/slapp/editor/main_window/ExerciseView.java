package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import slapp.editor.decorated_rta.DecoratedRTA;

public interface ExerciseView<T,U> {
    double getStatementHeight();
    double getCommentHeight();
    String getExerciseName();
    void setExerciseName(String name);
    T getExerciseStatement();
    void setExerciseStatement(T exerciseStatement);
    U getExerciseContent();
    void setExerciseContent(U exerciseContent);
    DecoratedRTA getExerciseComment();
    void setExerciseComment(DecoratedRTA exerciseComment);
    Node getExerciseControl();
    void setExerciseControl(Node exerciseControl);
    Node getExerciseStatementNode();
    Node getExerciseContentNode();
    DoubleProperty getContentHeightProperty();


}
