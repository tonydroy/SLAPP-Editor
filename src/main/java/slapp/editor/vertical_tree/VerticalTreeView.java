package slapp.editor.vertical_tree;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;
import slapp.editor.vertical_tree.drag_drop.RootLayout;

public class VerticalTreeView implements ExerciseView<DecoratedRTA> {

    BorderPane root;
    RootLayout rootLayout;
    DecoratedRTA exerciseComment = new DecoratedRTA();
    DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;

    Node exerciseControlNode;

    VerticalTreeView(MainWindowView mainView) {
        root = new BorderPane();
        rootLayout = new RootLayout();

        root.setCenter(rootLayout);
        VBox controlBox = new VBox(20, rootLayout.getBoxToggle(), rootLayout.getCircleToggle(), rootLayout.getUnderlineToggle(),rootLayout.getStarToggle(), rootLayout.getAnnotationBox());
        controlBox.setAlignment(Pos.BASELINE_RIGHT);
        controlBox.setMargin(rootLayout.getAnnotationBox(), new Insets(10, 0, 0, 0));
 //       controlBox.setMargin(insertLineButton, new Insets(0,0,20, 0));
        controlBox.setPadding(new Insets(200,10,30,80));
        exerciseControlNode = controlBox;

        initializeViewDetails();
    }

    void initializeViewDetails() {
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.setPrefHeight(statementPrefHeight);
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPrefHeight(70.0);
        commentRTA.setPromptText("Comment:");


    }



    @Override
    public String getExerciseName() {
        return null;
    }

    @Override
    public void setExerciseName(String name) {

    }

    @Override
    public DecoratedRTA getExerciseComment() {
        return exerciseComment;
    }

    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) {

    }

    @Override
    public double getCommentHeight() {
        return exerciseComment.getEditor().getHeight();
    }

    @Override
    public DecoratedRTA getExerciseStatement() {
        return exerciseStatement;
    }

    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) {

    }

    @Override
    public Node getExerciseStatementNode() {
        return exerciseStatement.getEditor();
    }

    @Override
    public double getStatementHeight() {
        return exerciseStatement.getEditor().getHeight();
    }

    @Override
    public void setStatementPrefHeight(double height) {

    }

    @Override
    public Node getExerciseContentNode() {
        return new VBox(root);
    }

    @Override
    public DoubleProperty getContentHeightProperty() {
        return rootLayout.prefHeightProperty();
    }

    @Override
    public double getContentFixedHeight() {
        return 0;
    }

    @Override
    public Node getExerciseControl() {
        return exerciseControlNode;
    }
}
