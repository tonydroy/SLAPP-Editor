package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import slapp.editor.decorated_rta.DecoratedRTA;

public class SlappLogoView implements ExerciseView<Label, Label> {

    String name = "";
    Label exerciseStatement;
    Label exerciseContent;
    DecoratedRTA exerciseComment;
    Node exerciseControl;
    MainWindowView mainView;

    SlappLogoView(MainWindowView mainView) {
        this.mainView = mainView;
        this.exerciseStatement = new Label("");
        this.exerciseContent = new Label("");
        this.exerciseComment = new DecoratedRTA();
        this.exerciseControl = new Region();
        exerciseStatement.setVisible(false);
        exerciseStatement.setManaged(false);
        exerciseContent.setVisible(false);
        exerciseContent.setManaged(false);

        exerciseComment.getEditor().setDocument(new Document("Logo Here"));
        exerciseComment.getEditor().setEditable(false);

        exerciseComment.getEditor().setPrefHeight(500);
        exerciseComment.getEditor().focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(exerciseComment);
            }
        });
    }

    @Override
    public double getStatementHeight() { return 0.0; }
    @Override
    public double getCommentHeight() { return 0.0; }
    @Override
    public String getExerciseName() { return name; }
    @Override
    public void setExerciseName(String name) {this.name = name; }
    @Override
    public Label getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(Label exerciseStatement) {}
    @Override
    public Label getExerciseContent() { return exerciseContent; }
    @Override
    public void setExerciseContent(Label exerciseContent) {}
    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment; }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) {}
    @Override
    public Node getExerciseControl() { return exerciseControl; }
    @Override
    public void setExerciseControl(Node exerciseControl) {}
    @Override
    public Node getExerciseStatementNode() { return exerciseStatement; }
    @Override
    public Node getExerciseContentNode() { return exerciseContent; }
    @Override
    public DoubleProperty getContentHeightProperty() {
        return exerciseContent.prefHeightProperty();
    }
    @Override
    public void setContentPrompt(String prompt) {}
}
