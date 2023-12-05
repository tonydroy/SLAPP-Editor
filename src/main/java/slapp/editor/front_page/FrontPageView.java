package slapp.editor.front_page;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

/**
 * Simple view including just comment area, to support FrontPageExercise
 * (need to update "logo here" to some final displayed content)
 */

public class FrontPageView implements ExerciseView<Label, Label> {

    private String name = "";
    private Label exerciseStatement;
    private Label exerciseContent;
    private DecoratedRTA exerciseComment;
    private Node exerciseControl;
    private MainWindowView mainView;

    public FrontPageView(MainWindowView mainView) {
        this.mainView = mainView;
        this.exerciseStatement = new Label("");
        this.exerciseContent = new Label("");
        this.exerciseComment = new DecoratedRTA();
        this.exerciseControl = new Region();
        exerciseStatement.setVisible(false);
        exerciseStatement.setManaged(false);
        exerciseContent.setVisible(false);
        exerciseContent.setManaged(false);

        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.setDocument(new Document("Logo Here"));
        commentRTA.setEditable(false);
        commentRTA.setFocusTraversable(false);
        commentRTA.setMouseTransparent(true);
        commentRTA.setPrefHeight(500);

        commentRTA.setStyle("-fx-padding: 5; -fx-background-color: WHITE; -fx-border-width: 2; -fx-border-color: LIGHTGREY; ");
  //      commentRTA.setStyle(".rich-text-area:focused {-fx-padding: 5; -fx-border-width: 2; -fx-border-color: LIGHTBLUE; }");

        exerciseComment.getFontsToolbar().setFocusTraversable(false);
        exerciseComment.getFontsToolbar().setMouseTransparent(true);
        exerciseComment.getParagraphToolbar().setFocusTraversable(false);
        exerciseComment.getParagraphToolbar().setMouseTransparent(true);
        exerciseComment.getEditToolbar().setFocusTraversable(false);
        exerciseComment.getEditToolbar().setMouseTransparent(true);
        mainView.editorInFocus(exerciseComment, ControlType.NONE);
        exerciseComment.getEditor().focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(exerciseComment, ControlType.NONE);
            }
        });
    }

    @Override
    public String getExerciseName() { return name; }
    @Override
    public void setExerciseName(String name) {this.name = name; }
    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment; }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) {}
    @Override
    public double getCommentHeight() { return 0.0; }
    @Override
    public Label getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(Label exerciseStatement) {}
    @Override
    public Node getExerciseStatementNode() { return exerciseStatement; }
    @Override
    public double getStatementHeight() { return 0.0; }
    @Override
    public void setStatementPrefHeight(double height) { }
    @Override
    public Label getExerciseContent() { return exerciseContent; }
    @Override
    public void setExerciseContent(Label exerciseContent) {}
    @Override
    public Node getExerciseContentNode() { return exerciseContent; }
    @Override
    public void setContentPrompt(String prompt) {}
    @Override
    public DoubleProperty getContentHeightProperty() { return exerciseContent.prefHeightProperty(); }
    @Override
    public double getContentFixedHeight() { return 0.0; }
    @Override
    public Node getExerciseControl() { return exerciseControl; }

}
