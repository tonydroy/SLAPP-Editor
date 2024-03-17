package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;

public class HorizontalTreeView implements ExerciseView<DecoratedRTA> {

    private MainWindowView mainView;
    private String exerciseName = new String("");
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private DecoratedRTA explainDRTA = new DecoratedRTA();
    private VBox controlBox = new VBox(25);
    private double statementPrefHeight = 80;

    private AnchorPane mainPane;
    private VBox centerBox;

    private Button undoButton;
    private Button redoButton;
    private ToggleButton formulaNodeToggle;
    private ToggleButton twoBranchToggle;
    private ToggleButton annotationToggle;
    private Button annotationPlus;
    private Button annotationMinus;


    private ToggleGroup buttonGroup = new ToggleGroup();
    private Node exerciseControlNode;

    private List<TreePane> treePanes = new ArrayList<>();

    HorizontalTreeView(MainWindowView mainView) {
        this.mainView = mainView;
        mainPane = new AnchorPane();
        mainPane.setStyle("-fx-border-width: 2 2 2 2; -fx-border-color: lightgrey; -fx-background-color: white");
        centerBox = new VBox(3, mainPane, explainDRTA.getEditor());
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");
        undoButton.setPrefWidth(64);
        redoButton.setPrefWidth(64);
        undoButton.setPrefHeight(28);
        redoButton.setPrefHeight(28);

        formulaNodeToggle = new ToggleButton();
        formulaNodeToggle.setPrefWidth(64);
        formulaNodeToggle.setPrefHeight(28);
        formulaNodeToggle.setMinHeight(28);
        formulaNodeToggle.setMaxHeight(28);
        Rectangle rectangle = new Rectangle(30,15);
        rectangle.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        HBox boxToggleGraphic = new HBox(rectangle);
        boxToggleGraphic.setAlignment(Pos.CENTER);
        formulaNodeToggle.setGraphic(boxToggleGraphic);
        formulaNodeToggle.setTooltip(new Tooltip("Add (left click) or remove (right click) box with its branches."));
        formulaNodeToggle.setToggleGroup(buttonGroup);

        twoBranchToggle = new ToggleButton();
        twoBranchToggle.setPrefWidth(64);
        twoBranchToggle.setMinHeight(28);
        twoBranchToggle.setMaxHeight(28);
        Line line1 = new Line(3, 13, 46, 13);
        Line line2 = new Line(3, 7, 46, 7 );
        Pane twoBranchTogglePane = new Pane(line1, line2);
        twoBranchToggle.setGraphic(twoBranchTogglePane);
        twoBranchToggle.setTooltip(new Tooltip("Add branches to selected node (left click)."));
        twoBranchToggle.setToggleGroup(buttonGroup);

        annotationToggle = new ToggleButton();
        annotationToggle.setPrefWidth(44);
        annotationToggle.setPrefHeight(30);
        annotationToggle.setMinHeight(30);
        annotationToggle.setMaxHeight(30);
        AnchorPane boxesPane = new AnchorPane();
        boxesPane.setPadding(new Insets(0));
        Rectangle bigBox = new Rectangle(15,10);
        bigBox.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        Rectangle littleBox = new Rectangle(7,7);
        littleBox.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        boxesPane.getChildren().addAll(bigBox, littleBox);
        boxesPane.setTopAnchor(bigBox, 8.0);
        boxesPane.setLeftAnchor(littleBox, 15.0);
        boxesPane.setTopAnchor(littleBox, 3.0);
        HBox buttonBox = new HBox(boxesPane);
        buttonBox.setAlignment(Pos.CENTER);
        annotationToggle.setGraphic(buttonBox);
        annotationToggle.setTooltip(new Tooltip("Add (left click) or remove (right click) annotation box"));

        annotationPlus = new Button("+");
        annotationPlus.setFont(new Font(10));
        annotationPlus.setPadding(new Insets(0));
        annotationPlus.setPrefWidth(20); annotationPlus.setPrefHeight(12);
        annotationMinus = new Button("-");
        annotationMinus.setFont(new Font(10));
        annotationMinus.setPadding(new Insets(0));
        annotationMinus.setPrefWidth(20); annotationMinus.setPrefHeight(12);
        VBox annotationButtons = new VBox(annotationPlus, annotationMinus);
        HBox annotationBox = new HBox(annotationToggle, annotationButtons);
        annotationBox.setMaxHeight(30);




        controlBox.getChildren().addAll(undoButton, redoButton, formulaNodeToggle, twoBranchToggle, annotationBox);
        controlBox.setMargin(annotationBox, new Insets(10, 0, 0, 0));
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

    void refreshTreePanes() {
        for (TreePane pane : treePanes) {
            pane.refresh();
        }
    }


    public List<TreePane> getTreePanes() { return treePanes; }

    public MainWindowView getMainView() { return mainView; }

    public DecoratedRTA getExplainDRTA() { return explainDRTA; }

    public AnchorPane getMainPane() { return mainPane;}

    public ToggleButton getFormulaNodeToggle() {return formulaNodeToggle; }

    public ToggleButton getTwoBranchToggle() { return twoBranchToggle; }

    public ToggleButton getAnnotationToggle() { return annotationToggle;  }

    public Button getAnnotationPlus() { return annotationPlus;    }

    public Button getAnnotationMinus() { return annotationMinus; }

    @Override
    public String getExerciseName() { return exerciseName;  }

    @Override
    public void setExerciseName(String name) { exerciseName = name;  }

    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment;  }

    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment;  }

    @Override
    public double getCommentHeight() { return exerciseComment.getEditor().getHeight();  }

    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }

    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) { this.exerciseStatement = exerciseStatement;  }

    @Override
    public Node getExerciseStatementNode() { return exerciseStatement.getEditor();  }

    @Override
    public double getStatementHeight() { return exerciseStatement.getEditor().getHeight();  }

    @Override
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
    }

    @Override
    public Node getExerciseContentNode() { return centerBox; }

    @Override
    public DoubleProperty getContentHeightProperty() { return mainPane.prefHeightProperty();  }

    @Override
    public DoubleProperty getContentWidthProperty() { return mainPane.prefWidthProperty(); }

    @Override
    public double getContentFixedHeight() {  return 30;  }

    @Override
    public Node getExerciseControl() { return exerciseControlNode;  }

    @Override
    public double getContentWidth() {
        return 0;
    }

    @Override
    public double getContentHeight() {
        return 0;
    }
}
