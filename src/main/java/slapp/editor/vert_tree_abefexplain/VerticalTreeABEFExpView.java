package slapp.editor.vert_tree_abefexplain;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;
import slapp.editor.vert_tree_abefexplain.ABEFExpRootLayout;

public class VerticalTreeABEFExpView implements ExerciseView<DecoratedRTA> {

    private MainWindowView mainView;
    private String exerciseName = new String("");
    private Label abChoiceLeadLabel = new Label();
    private CheckBox aCheckBox = new CheckBox();
    private CheckBox bCheckBox = new CheckBox();
    private HBox choiceBox1;
    private Label efChoiceLeadLabel = new Label();
    private CheckBox eCheckBox = new CheckBox();
    private CheckBox fCheckBox = new CheckBox();
    private HBox choiceBox2;


    private BorderPane root;
    private ABEFExpRootLayout rootLayout;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA explainDRTA = new DecoratedRTA();
    private VBox controlBox = new VBox(25);
    private Button undoButton;
    private Button redoButton;
    public BooleanProperty undoRedoFlag = new SimpleBooleanProperty();
    Node exerciseControlNode;

    VerticalTreeABEFExpView(MainWindowView mainView) {
        this.mainView = mainView;

        Font labelFont = new Font("Noto Serif Combo", 11);
        abChoiceLeadLabel.setFont(labelFont); aCheckBox.setFont(labelFont); bCheckBox.setFont(labelFont);
        choiceBox1 = new HBox(20, abChoiceLeadLabel, aCheckBox, bCheckBox);
        choiceBox1.setPadding(new Insets(7,7,7,10));
        choiceBox1.setStyle("-fx-border-color: lightgrey; -fx-background-color: white;");
        efChoiceLeadLabel.setFont(labelFont); eCheckBox.setFont(labelFont); fCheckBox.setFont(labelFont);
        choiceBox2 = new HBox(20, efChoiceLeadLabel, eCheckBox, fCheckBox);
        choiceBox2.setPadding(new Insets(7,7,7,10));
        choiceBox2.setStyle("-fx-border-color: lightgrey; -fx-background-color: white;");
        VBox choicesBox = new VBox(choiceBox1, choiceBox2);

        root = new BorderPane();
        rootLayout = new ABEFExpRootLayout(this);

        VBox contentBox = new VBox(10, rootLayout, choicesBox, explainDRTA.getEditor());
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
        explainRTA.setPromptText("Explain:");
    }

    public MainWindowView getMainView() {
        return mainView;
    }

    public ABEFExpRootLayout getRootLayout() {return rootLayout;}

    public VBox getControlBox() {  return controlBox;  }

    public Button getUndoButton() { return undoButton;   }

    public Button getRedoButton() { return redoButton;   }

    public boolean isUndoRedoFlag() {    return undoRedoFlag.get();    }

    public BooleanProperty undoRedoFlagProperty() {    return undoRedoFlag;    }

    public void setUndoRedoFlag(boolean undoRedoFlag) {    this.undoRedoFlag.set(undoRedoFlag);    }

    public DecoratedRTA getExplainDRTA() { return explainDRTA; }

    public Label getEFChoiceLeadLabel() { return efChoiceLeadLabel;  }
    public CheckBox geteCheckBox() {  return eCheckBox;  }
    public CheckBox getfCheckBox() {  return fCheckBox;  }
    public Label getABChoiceLeadLabel() { return abChoiceLeadLabel;  }
    public CheckBox getaCheckBox() {  return aCheckBox;  }
    public CheckBox getbCheckBox() {  return bCheckBox;  }

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
        return 120;
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