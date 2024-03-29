package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HorizontalTreeView implements ExerciseView<DecoratedRTA> {

    private HorizontalTreeView self;

    private MainWindowView mainView;
    private String exerciseName = new String("");
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private DecoratedRTA explainDRTA = new DecoratedRTA();
    private VBox controlBox = new VBox(15);
    private double statementPrefHeight = 80;
    private boolean annotationModified = false;



    private AnchorPane mainPane;
    private VBox centerBox;

    private Button undoButton;
    private Button redoButton;
    public BooleanProperty undoRedoFlag = new SimpleBooleanProperty();
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
    private ToggleGroup buttonGroup = new ToggleGroup();
    private Node exerciseControlNode;
    private List<TreePane> treePanes = new ArrayList<>();
    EventHandler formulaNodeClickFilter;
    private EventHandler oneBranchClickFilter;
    private EventHandler twoBranchClickFilter;
    private EventHandler threeBranchClickFilter;
    private EventHandler indefinateBranchClickFilter;
    private EventHandler annotationClickFilter;
    private EventHandler dotsClickFilter;
    private EventHandler oneBranchTermClickFilter;
    private EventHandler twoBranchTermClickFilter;

    private Ruler axisNode;
//    private Region axisNode;
    private boolean axis = false;


    private static BranchNode clickNode = null;

    HorizontalTreeView(MainWindowView mainView) {
        self = this;
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

        axisNode = new Ruler();
//        axisNode = createClipped();

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

        Region spacer = new Region();
        spacer.setPrefWidth(5);
        spacer.setMinWidth(5);
        spacer.setMaxWidth(5);
        controlBox.getChildren().addAll(undoButton, redoButton, formulaNodeToggle, oneBranchToggle, twoBranchToggle, threeBranchToggle, indefiniteBranchToggle, verticalDotsToggle, oneBranchTermToggle, twoBranchTermToggle, spacer, annotationBox, rulerButton);
//        controlBox.setMargin(annotationBox, new Insets(0, 0, -20, 0));
//        controlBox.setMargin(indefiniteBranchToggle, new Insets(12, 0, -10, 0));
        controlBox.setMargin(rulerButton, new Insets(-10, 0, 0, 0));

        controlBox.setAlignment(Pos.BASELINE_RIGHT);
        controlBox.setPadding(new Insets(20,10,0,80));
        exerciseControlNode = controlBox;

    }

    public void deselectToggles() {
        formulaNodeToggle.setSelected(false);
        oneBranchToggle.setSelected(false);
        twoBranchToggle.setSelected(false);
        threeBranchToggle.setSelected(false);
        indefiniteBranchToggle.setSelected(false);
        verticalDotsToggle.setSelected(false);
        oneBranchTermToggle.setSelected(false);
        twoBranchTermToggle.setSelected(false);
        annotationToggle.setSelected(false);
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

        mainPane.prefWidthProperty().addListener((ob, ov, nv) -> {
            axisNode.updateRuler(mainPane.getPrefWidth());
        });

        formulaNodeClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    TreePane treePane = new TreePane(self);
                    treePanes.add(treePane);
                    mainPane.getChildren().add(treePane);
                    refreshTreePanes();
                    treePane.relocateToGridPoint(new Point2D(event.getX(), event.getY()));
                    setUndoRedoFlag(true);
                    setUndoRedoFlag(false);
                }

                else if (event.getButton() == MouseButton.SECONDARY) {
                    for (TreePane pane : treePanes) {
                        BranchNode rootNode = pane.getRootBranchNode();
                        if (inHierarchy(event.getPickResult().getIntersectedNode(), rootNode)) {
                            treePanes.remove(pane);
                            mainPane.getChildren().remove(pane);
                            formulaNodeToggle.setSelected(false);
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                            break;
                        } else {
                            setClickedNode(event, rootNode);
                            if (clickNode != null && clickNode != rootNode) {
                                clickNode.getContainer().getDependents().remove(clickNode);
                                pane.refresh();
                                formulaNodeToggle.setSelected(false);
                                setUndoRedoFlag(true);
                                setUndoRedoFlag(false);
                                break;
                            }
                        }
                    }
                }
                formulaNodeToggle.setSelected(false);
            }
        };
        formulaNodeToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, formulaNodeClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, formulaNodeClickFilter);
        });

        oneBranchClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : treePanes) {
                        BranchNode rootNode = pane.getRootBranchNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null && clickNode.isFormulaNode() && formulaDependents(clickNode.getDependents())) {
                            BranchNode branch1 = new BranchNode(clickNode, self);
                            clickNode.getDependents().add(branch1);
                            pane.refresh();
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                        }
                    }
                }
            }
        };

        oneBranchToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, oneBranchClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, oneBranchClickFilter);
        });

        twoBranchClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : treePanes) {
                        BranchNode rootNode = pane.getRootBranchNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null && clickNode.isFormulaNode() && formulaDependents(clickNode.getDependents())) {
                            BranchNode branch1 = new BranchNode(clickNode, self);
                            BranchNode branch2 = new BranchNode(clickNode, self);
                            clickNode.getDependents().addAll(Arrays.asList(branch1, branch2));
                            pane.refresh();
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                        }
                    }
                }
            }
        };

        twoBranchToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, twoBranchClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, twoBranchClickFilter);
        });

        threeBranchClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : treePanes) {
                        BranchNode rootNode = pane.getRootBranchNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null && clickNode.isFormulaNode() && formulaDependents(clickNode.getDependents())) {
                            BranchNode branch1 = new BranchNode(clickNode, self);
                            BranchNode branch2 = new BranchNode(clickNode, self);
                            BranchNode branch3 = new BranchNode(clickNode, self);
                            clickNode.getDependents().addAll(Arrays.asList(branch1, branch2, branch3));
                            pane.refresh();
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                        }
                    }
                }
            }
        };

        threeBranchToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, threeBranchClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, threeBranchClickFilter);
        });

        indefinateBranchClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : treePanes) {
                        BranchNode rootNode = pane.getRootBranchNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null && clickNode.isFormulaNode() && formulaDependents(clickNode.getDependents())) {
                            BranchNode branch = new BranchNode(clickNode, self);
                            branch.setIndefiniteNode(true);
                            branch.setStyle("-fx-border-width: 0 0 0 0");
                            RichTextArea rta = branch.getFormulaBoxedDRTA().getRTA();
                            rta.setDocument(new Document(" \u22ee"));
                            rta.setPrefWidth(24);
                            clickNode.getDependents().add(branch);
                            pane.refresh();
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                        }
                    }
                }
            }
        };

        indefiniteBranchToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, indefinateBranchClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, indefinateBranchClickFilter);
        });

        oneBranchTermClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : treePanes) {
                        BranchNode rootNode = pane.getRootBranchNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null && termDependents(clickNode.getDependents())) {
                            BranchNode branch = new BranchNode(clickNode, self);
                            branch.setFormulaNode(false);
                            branch.setStyle("-fx-border-width: 0 0 0 0");
                            RichTextArea rta = branch.getFormulaBoxedDRTA().getRTA();
                            rta.setPrefWidth(24);
                            clickNode.getDependents().add(branch);
                            pane.refresh();
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                        }
                    }
                }
            }
        };

        oneBranchTermToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, oneBranchTermClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, oneBranchTermClickFilter);
        });

        twoBranchTermClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : treePanes) {
                        BranchNode rootNode = pane.getRootBranchNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null && termDependents(clickNode.getDependents())) {
                            BranchNode branch1 = new BranchNode(clickNode, self);
                            BranchNode branch2 = new BranchNode(clickNode, self);
                            branch1.setFormulaNode(false);
                            branch2.setFormulaNode(false);
                            branch1.setStyle("-fx-border-width: 0 0 0 0");
                            branch2.setStyle("-fx-border-width: 0 0 0 0");
                            RichTextArea rta1 = branch1.getFormulaBoxedDRTA().getRTA();
                            RichTextArea rta2 = branch2.getFormulaBoxedDRTA().getRTA();
                            rta1.setPrefWidth(24);
                            rta2.setPrefWidth(24);
                            clickNode.getDependents().addAll(Arrays.asList(branch1, branch2));
                            pane.refresh();
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                        }
                    }
                }
            }
        };

        twoBranchTermToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, twoBranchTermClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, twoBranchTermClickFilter);
        });

        dotsClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                for (TreePane pane : treePanes) {
                    BranchNode rootNode = pane.getRootBranchNode();
                    setClickedNode(event, rootNode);
                    if (clickNode != null && clickNode.isFormulaNode()) {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            clickNode.setDotDivider(true);
                            pane.refresh();
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                        }
                        else {
                            clickNode.setDotDivider(false);
                            pane.refresh();
                            setUndoRedoFlag(true);
                            setUndoRedoFlag(false);
                        }
                    }
                }
            }
        };

        verticalDotsToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, dotsClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, dotsClickFilter);
        });


        annotationClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (TreePane pane : treePanes) {
                    BranchNode rootNode = pane.getRootBranchNode();
                    setClickedNode(event, rootNode);
                    if (clickNode != null) {
                        clickNode.processAnnotationRequest(event.getButton() == MouseButton.PRIMARY);
                        pane.refresh();
                        setUndoRedoFlag(true);
                        setUndoRedoFlag(false);
                        break;
                    }
                }
            }
        };
        annotationToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv)  mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, annotationClickFilter );
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, annotationClickFilter);
        });

        annotationPlus.setOnAction(e -> {
            for (TreePane pane : treePanes) {
                setAnnotations(pane.getRootBranchNode(), true);
                pane.refresh();
                setUndoRedoFlag(true);
                setUndoRedoFlag(false);
            }
            deselectToggles();
        });
        annotationMinus.setOnAction(e -> {
            for (TreePane pane : treePanes) {
                setAnnotations(pane.getRootBranchNode(), false);
                pane.refresh();
                setUndoRedoFlag(true);
                setUndoRedoFlag(false);
            }
            deselectToggles();
        });

        rulerButton.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                if (!axis) {
                    simpleAddAxis();
                    setUndoRedoFlag(true);
                    setUndoRedoFlag(false);
                }
            }
            else {
                if (axis) {
                    simpleRemoveAxis();
                    setUndoRedoFlag(true);
                    setUndoRedoFlag(false);
                }
            }
        });

    }

    void simpleAddAxis() {
        axis = true;
        mainPane.getChildren().add(0, axisNode);
        axisNode.setLayoutX(5.0);
    }
    void simpleRemoveAxis() {
        axis = false;
        mainPane.getChildren().remove(axisNode);
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
        Rectangle outputClip = new Rectangle();
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

    //dependents empty or all formulaNodes
    boolean formulaDependents(ArrayList<BranchNode> dependentList) {
        boolean formulaDependents = true;
        for (BranchNode node : dependentList) {
            if (!node.isFormulaNode()) {
                formulaDependents = false;
                break;
            }
        }
        return formulaDependents;
    }


    //dependents empty or all termNodes
    boolean termDependents(ArrayList<BranchNode> dependentList) {
        boolean termDependents = true;
        for (BranchNode node : dependentList) {
            if (node.isFormulaNode()) {
                termDependents = false;
                break;
            }
        }
        return termDependents;
    }

    void setAnnotations(BranchNode node, boolean add) {
        node.processAnnotationRequest(add);
        for (BranchNode child : node.getDependents()) {
            setAnnotations(child, add);
        }
    }

    public static boolean inHierarchy(Node node, Node potentialHierarchyElement) {
        if (potentialHierarchyElement == null) {
            return true;
        }
        while (node != null) {
            if (node == potentialHierarchyElement) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    void setClickedNode(MouseEvent event, BranchNode node) {
        clickNode = null;
        findClickNodeInTree(event, node);
    }
    void findClickNodeInTree(MouseEvent event, BranchNode node) {
        if ((inHierarchy(event.getPickResult().getIntersectedNode(), node))) {
            clickNode = node;
        }
        else {
            for (int i = 0; i < node.getDependents().size(); i++) {
                BranchNode newNode = node.getDependents().get(i);
                findClickNodeInTree(event, newNode);
            }
        }
    }

    void refreshTreePanes() {

        mainPane.getChildren().clear();
        for (TreePane pane : treePanes) {
            pane.refresh();
            mainPane.getChildren().add(pane);
        }

        if (axis) {
            rulerButton.setSelected(true);
        }
        else {
            rulerButton.setSelected(false);
        }
    }

    public Button getUndoButton() {    return undoButton;  }

    public Button getRedoButton() {  return redoButton;  }

    public BooleanProperty undoRedoFlagProperty() {    return undoRedoFlag;    }

    public void setUndoRedoFlag(boolean undoRedoFlag) {  this.undoRedoFlag.set(undoRedoFlag);  }

    public ToggleButton getRulerButton() {    return rulerButton;  }

    public MainWindowView getMainView() { return mainView; }

    public DecoratedRTA getExplainDRTA() { return explainDRTA; }


    public AnchorPane getMainPane() { return mainPane;}

    public boolean isAxis() {   return axis; }

    public void setAxis(boolean axis) {    this.axis = axis;   }

    public List<TreePane> getTreePanes() { return treePanes;   }

    public boolean isAnnotationModified() {    return annotationModified;  }

    public void setAnnotationModified(boolean annotationModified) {      this.annotationModified = annotationModified;  }

    public Ruler getAxisNode() {     return axisNode; }

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
    public double getContentWidth() { return 0; }

    @Override
    public double getContentHeight() {   return 0;  }
}
