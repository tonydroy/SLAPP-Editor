package slapp.editor.vert_tree_explain;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

public class VerticalTreeExpView implements ExerciseView<DecoratedRTA> {

    private MainWindowView mainView;
    private String exerciseName = new String("");
    private BorderPane root;
    private ExpRootLayout rootLayout;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA explainDRTA = new DecoratedRTA();
    private String explainPrompt ="";
    private VBox controlBox = new VBox(25);
    private Button undoButton;
    private Button redoButton;
    public BooleanProperty undoRedoFlag = new SimpleBooleanProperty();
    Node exerciseControlNode;

    VerticalTreeExpView(MainWindowView mainView) {
        this.mainView = mainView;
        root = new BorderPane();
        rootLayout = new ExpRootLayout(this);

        VBox contentBox = new VBox(10, rootLayout, explainDRTA.getEditor());
        root.setCenter(contentBox);

        undoButton = new Button("Undo");
        redoButton = new Button("Redo");
        undoButton.setPrefWidth(64);
        redoButton.setPrefWidth(64);
        undoButton.setPrefHeight(28);
        redoButton.setPrefHeight(28);

        controlBox.getChildren().addAll(undoButton, redoButton);
        controlBox.setAlignment(Pos.BASELINE_RIGHT);
        controlBox.setPadding(new Insets(100,10,0,80));
        exerciseControlNode = controlBox;
    }

    void initializeViewDetails() {
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.setPrefHeight(statementPrefHeight);
        statementRTA.setMinHeight(statementPrefHeight);
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPrefHeight(70.0);
        commentRTA.setMinHeight(70.0);
        commentRTA.setPromptText("Comment:");

        RichTextArea explainRTA = explainDRTA.getEditor();
        explainRTA.getStylesheets().add("slappTextArea.css");
        explainRTA.setPrefHeight(80.0);
        explainRTA.setMinHeight(80.0);
        explainRTA.setPromptText(explainPrompt);
    }

    public MainWindowView getMainView() {
        return mainView;
    }

    public ExpRootLayout getRootLayout() {return rootLayout;}

    public VBox getControlBox() {  return controlBox;  }

    public Button getUndoButton() { return undoButton;   }

    public Button getRedoButton() { return redoButton;   }

    public boolean isUndoRedoFlag() {    return undoRedoFlag.get();    }

    public BooleanProperty undoRedoFlagProperty() {    return undoRedoFlag;    }

    public void setUndoRedoFlag(boolean undoRedoFlag) {    this.undoRedoFlag.set(undoRedoFlag);    }

    public DecoratedRTA getExplainDRTA() { return explainDRTA; }
    public void setExplainPrompt(String prompt) {this.explainPrompt = prompt;}

    @Override
    public String getExerciseName() { return exerciseName; }

    @Override
    public void setExerciseName(String name) {exerciseName = name; }

    @Override
    public DecoratedRTA getExerciseComment() {
        return exerciseComment;
    }

    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment;  }

    @Override
    public double getCommentHeight() {
        return exerciseComment.getEditor().getHeight();
    }

    @Override
    public DecoratedRTA getExerciseStatement() {
        return exerciseStatement;
    }

    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) { this.exerciseStatement = exerciseStatement; }

    @Override
    public Node getExerciseStatementNode() { return exerciseStatement.getEditor();   }

    @Override
    public double getStatementHeight() {
        return exerciseStatement.getEditor().getHeight();
    }

    @Override
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
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
    public DoubleProperty getContentWidthProperty() {return rootLayout.prefWidthProperty(); }

    @Override
    public double getContentFixedHeight() {
        return 50;
    }

    @Override
    public Node getExerciseControl() {
        return exerciseControlNode;
    }
    @Override
    public double getContentWidth() {    return rootLayout.getMain_pane().getWidth(); }
    @Override
    public double getContentHeight() { return rootLayout.getMain_pane().getHeight(); }
}
