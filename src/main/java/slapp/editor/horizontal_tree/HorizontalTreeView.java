package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private VBox controlBox = new VBox(15);
    private double statementPrefHeight = 80;


    private AnchorPane mainPane;
    private VBox centerBox;

    private Button undoButton;
    private Button redoButton;
    private ToggleButton formulaNodeToggle;
    private ToggleButton oneBranchToggle;
    private ToggleButton twoBranchToggle;
    private ToggleButton threeBranchToggle;
    private ToggleButton indefiniteBranchToggle;
    private ToggleButton verticalDotsToggle;
    private ToggleButton oneBranchTermToggle;
    private ToggleButton twoBranchTermToggle;
    private ToggleButton annotationToggle;
    private Button annotationPlus;
    private Button annotationMinus;
    private ToggleButton rulerButton;

    private Rectangle numClipRec;
    private Region numAxisPane;
    private NumberAxis axis;


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
        Rectangle rectangle = new Rectangle(30,12);
        rectangle.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        Group boxToggleGraphic = new Group(rectangle);
        formulaNodeToggle.setGraphic(boxToggleGraphic);
        formulaNodeToggle.setTooltip(new Tooltip("Add (left click) or remove (right click) box with its branches"));
        formulaNodeToggle.setToggleGroup(buttonGroup);

        oneBranchToggle = new ToggleButton();
        oneBranchToggle.setPrefWidth(64);
        oneBranchToggle.setMinHeight(28);
        oneBranchToggle.setMaxHeight(28);
        Line oneLine = new Line(0, 0, 27, 0);

        oneLine.setStyle("-fx-stroke-width: 1.5");
        oneBranchToggle.setGraphic(new Group(oneLine));
        oneBranchToggle.setTooltip(new Tooltip("Add branch to selected node"));
        oneBranchToggle.setToggleGroup(buttonGroup);

        twoBranchToggle = new ToggleButton();
        twoBranchToggle.setPrefWidth(64);
        twoBranchToggle.setMinHeight(28);
        twoBranchToggle.setMaxHeight(28);
        Line twoStub = new Line(0,0,3,0);
        Line twoBrack = new Line(0,0,0, 9);
        Line twoLine1 = new Line(0, 0, 24, 0);
        Line twoLine2 = new Line(0, 0, 24, 0 );
        VBox twoVBox = new VBox(7, twoLine1, twoLine2);
        twoVBox.setAlignment(Pos.CENTER);
        twoStub.setStyle("-fx-stroke-width: 1.5");
        twoBrack.setStyle("-fx-stroke-width: 1.5");
        twoLine1.setStyle("-fx-stroke-width: 1.5");
        twoLine2.setStyle("-fx-stroke-width: 1.5");
        HBox twoHBox = new HBox(twoStub, twoBrack, twoVBox);
        twoHBox.setAlignment(Pos.CENTER);
        twoBranchToggle.setGraphic(twoHBox);
        twoBranchToggle.setTooltip(new Tooltip("Add branches to selected node"));
        twoBranchToggle.setToggleGroup(buttonGroup);

        threeBranchToggle = new ToggleButton();
        threeBranchToggle.setPrefWidth(64);
        threeBranchToggle.setMinHeight(28);
        threeBranchToggle.setMaxHeight(28);
        Line threeStub = new Line(0,0,3,0);
        Line threeBrack = new Line(0,0,0, 11);
        Line threeLine1 = new Line(0, 0, 24, 0);
        Line threeLine2 = new Line(0, 0, 24, 0 );
        Line threeLine3 = new Line(0,0,24,0);
        VBox threeVBox = new VBox(4, threeLine1, threeLine2, threeLine3);
        threeVBox.setAlignment(Pos.CENTER);
        threeStub.setStyle("-fx-stroke-width: 1.5");
        threeBrack.setStyle("-fx-stroke-width: 1.5");
        threeLine1.setStyle("-fx-stroke-width: 1.5");
        threeLine2.setStyle("-fx-stroke-width: 1.5");
        threeLine3.setStyle("-fx-stroke-width: 1.5");
        HBox threeHBox = new HBox(threeStub, threeBrack, threeVBox);
        threeHBox.setAlignment(Pos.CENTER);
        threeBranchToggle.setGraphic(threeHBox);
        threeBranchToggle.setTooltip(new Tooltip("Add branches to selected node"));
        threeBranchToggle.setToggleGroup(buttonGroup);

        indefiniteBranchToggle = new ToggleButton();
        indefiniteBranchToggle.setPrefWidth(64);
        indefiniteBranchToggle.setMinHeight(28);
        indefiniteBranchToggle.setMaxHeight(28);
        Line indefStub = new Line(0,6,3,6);
        Line indefBrack = new Line(3, 0,3, 12);
        Line indefLine = new Line(3,0,24,0);

        indefStub.setStyle("-fx-stroke-width: 1.5");
        indefBrack.setStyle("-fx-stroke-width: 1.5");
        indefLine.setStyle("-fx-stroke-width: 1.5");

        Line indefDots = new Line(10,4,10,14);
        indefDots.getStrokeDashArray().addAll(1.0,3.0);
        Group indefPane = new Group(indefStub, indefBrack, indefLine, indefDots);

        HBox indefHBox = new HBox(indefPane);
        indefHBox.setAlignment(Pos.CENTER);
        indefiniteBranchToggle.setAlignment(Pos.CENTER);


        indefiniteBranchToggle.setGraphic(indefPane);
        indefiniteBranchToggle.setTooltip(new Tooltip("Add indefinite branch to selected node"));
        indefiniteBranchToggle.setToggleGroup(buttonGroup);
        oneBranchTermToggle = new ToggleButton();
        oneBranchTermToggle.setPrefWidth(64);
        oneBranchTermToggle.setMinHeight(28);
        oneBranchTermToggle.setMaxHeight(28);
        Line oneTermLine = new Line(0,0,20,0);
        oneTermLine.setStyle("-fx-stroke-width: 1.5");
        Rectangle oneTermRec = new Rectangle(10,10);
        oneTermRec.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        HBox oneBranchHBox = new HBox(oneTermLine, oneTermRec);
        oneBranchHBox.setAlignment(Pos.CENTER);
        oneBranchTermToggle.setGraphic(oneBranchHBox);
        oneBranchTermToggle.setTooltip(new Tooltip("Branch to term from selected node"));
        oneBranchTermToggle.setToggleGroup(buttonGroup);

        twoBranchTermToggle = new ToggleButton();
        twoBranchTermToggle.setPrefWidth(64);
        twoBranchTermToggle.setMinHeight(28);
        twoBranchTermToggle.setMaxHeight(28);
        Line twoTermLine1 = new Line(0, 7, 20, 2);
        Line twoTermLine2 = new Line(0, 7, 20, 12);
        VBox twoTermVBox1 = new VBox(twoTermLine1, twoTermLine2);
        twoTermVBox1.setAlignment(Pos.CENTER);
        Rectangle twoTermRec1 = new Rectangle(7,7);
        twoTermRec1.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        Rectangle twoTermRec2 = new Rectangle(7, 7);
        twoTermRec2.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        VBox twoTermVBox2 = new VBox(2, twoTermRec1, twoTermRec2);
        twoTermVBox2.setAlignment(Pos.CENTER);
        HBox twoTermHBox = new HBox(twoTermVBox1, twoTermVBox2);
        twoTermHBox.setAlignment(Pos.CENTER);
        twoBranchTermToggle.setGraphic(twoTermHBox);
        twoBranchTermToggle.setTooltip(new Tooltip("Branch to terms from selected node"));
        twoBranchTermToggle.setToggleGroup(buttonGroup);

        verticalDotsToggle = new ToggleButton();
        verticalDotsToggle.setPrefWidth(64);
        verticalDotsToggle.setMinHeight(28);
        verticalDotsToggle.setMaxHeight(28);
        Line vDotsLine = new Line(0,0,0,16);
        vDotsLine.getStrokeDashArray().addAll(1.0, 3.0);
        vDotsLine.setStyle("-fx-stroke-width: 1.5");
        VBox verticalDotsVBox = new VBox(vDotsLine);
        verticalDotsVBox.setAlignment(Pos.CENTER);
        verticalDotsVBox.setPadding(new Insets(1,0,0,0));
        HBox verticalDotsHBox = new HBox(verticalDotsVBox);
        verticalDotsHBox.setAlignment(Pos.CENTER);
        verticalDotsToggle.setGraphic(verticalDotsHBox);
        verticalDotsToggle.setTooltip(new Tooltip("Dots to divide term from formula branches"));
        verticalDotsToggle.setToggleGroup(buttonGroup);





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
        annotationToggle.setToggleGroup(buttonGroup);

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

        rulerButton = new ToggleButton();
        rulerButton.setPrefWidth(64);
        rulerButton.setMinHeight(28);
        rulerButton.setMaxHeight(28);
        Line ruler = new Line(0,3,30, 3);
        Line tick1 = new Line(5,0,5,6);
        Line tick2 = new Line(15, 0, 15, 6);
        Line tick3 = new Line(25, 0, 25, 6);
        ruler.setStyle("-fx-stroke-width: 0.75");
        tick1.setStyle("-fx-stroke-width: 0.75");
        tick2.setStyle("-fx-stroke-width: 0.75");
        tick3.setStyle("-fx-stroke-width: 0.75");

        Pane rulerPane = new Pane(ruler, tick1, tick2, tick3);
        HBox rulerHBox = new HBox(rulerPane);
        rulerHBox.setAlignment(Pos.CENTER);
        VBox rulerVBox = new VBox(rulerHBox);
        rulerVBox.setAlignment(Pos.CENTER);
        rulerButton.setGraphic(rulerVBox);
        rulerButton.setTooltip(new Tooltip("Add (right-click) or remove (left-click) horizontal ruler on tree"));




        controlBox.getChildren().addAll(undoButton, redoButton, formulaNodeToggle, oneBranchToggle, twoBranchToggle, threeBranchToggle, indefiniteBranchToggle, oneBranchTermToggle, twoBranchTermToggle, verticalDotsToggle, rulerButton, annotationBox);
        controlBox.setMargin(annotationBox, new Insets(15, 0, 0, 0));
//        controlBox.setMargin(indefiniteBranchToggle, new Insets(12, 0, -10, 0));

        controlBox.setAlignment(Pos.BASELINE_RIGHT);
        controlBox.setPadding(new Insets(20,10,0,80));
        exerciseControlNode = controlBox;

    }

    NumberAxis createAxis() {
        NumberAxis axis = new NumberAxis(0,150,5);
        axis.setMinWidth(Control.USE_PREF_SIZE);
        axis.setPrefWidth(3000);
        axis.setMaxWidth(Control.USE_PREF_SIZE);
        axis.tickLabelFontProperty().set(Font.font(8));
        axis.setTickLength(6);

        return axis;
    }

    void clipChildren(Region region) {
        final Rectangle outputClip = new Rectangle();
        region.setClip(outputClip);
        region.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
    }

    Region createClipped() {
        NumberAxis axis = createAxis();
        final Pane pane = new Pane(axis);
        axis.setLayoutX(10);
        pane.prefWidthProperty().bind(mainPane.prefWidthProperty());
        pane.setPrefHeight(25.0);
        clipChildren(pane);
        return pane;
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

    public ToggleButton getOneBranchToggle() {  return oneBranchToggle;  }

    public ToggleButton getTwoBranchToggle() { return twoBranchToggle; }

    public ToggleButton getThreeBranchToggle() { return threeBranchToggle;  }

    public ToggleButton getIndefiniteBranchToggle() { return indefiniteBranchToggle;  }

    public ToggleButton getAnnotationToggle() { return annotationToggle;  }

    public Button getAnnotationPlus() { return annotationPlus;    }

    public Button getAnnotationMinus() { return annotationMinus; }

    public ToggleButton getRulerButton() { return rulerButton;  }

    public ToggleButton getVerticalDotsToggle() {   return verticalDotsToggle;  }

    public ToggleButton getOneBranchTermToggle() {   return oneBranchTermToggle;  }

    public ToggleButton getTwoBranchTermToggle() {    return twoBranchTermToggle;  }

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
