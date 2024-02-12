package slapp.editor.front_page;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

/**
 * Simple view including just comment area, to support FrontPageExercise
 * (need to update "logo here" to some final displayed content)
 */

public class FrontPageView implements ExerciseView<Label> {

    private String name = "";
    private Label exerciseStatement;
    private DecoratedRTA exerciseComment;
    private Node exerciseControl;
    private MainWindowView mainView;

    private FrontPageAnimation frontAnimation = new FrontPageAnimation();

    private VBox exerciseContent;

    private boolean played = false;

    private ChangeListener contentFocusListener;



    public FrontPageView(MainWindowView mainView) {
        this.mainView = mainView;
        exerciseStatement = new Label("");
        exerciseComment = new DecoratedRTA();

        exerciseStatement.setVisible(false);
        exerciseStatement.setManaged(false);
        exerciseComment.getEditor().setVisible(false);
        exerciseComment.getEditor().setManaged(false);

        exerciseContent = frontAnimation.getFrontPageBox();

        Pane spacerPane = new Pane();
        double centeringWidth = (mainView.getMinStageWidth() - exerciseContent.getPrefWidth()) / 2.0;
        spacerPane.setMinWidth(centeringWidth);
        exerciseControl = spacerPane;



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

        contentFocusListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                exerciseContent.focusedProperty().removeListener(contentFocusListener);
                frontAnimation.playFrontAnimation();
            }
        };
        exerciseContent.focusedProperty().addListener(contentFocusListener);
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
    public Node getExerciseContentNode() { return exerciseContent; }


    @Override
    public DoubleProperty getContentHeightProperty() { return exerciseContent.prefHeightProperty(); }
    @Override
    public DoubleProperty getContentWidthProperty() {return exerciseComment.getEditor().prefWidthProperty(); }
    @Override
    public double getContentFixedHeight() { return -40.0; }
    @Override
    public Node getExerciseControl() { return exerciseControl; }
    @Override
    public double getContentWidth() {
        return 0;
    }
    @Override
    public double getContentHeight() { return 0.0; }
}
