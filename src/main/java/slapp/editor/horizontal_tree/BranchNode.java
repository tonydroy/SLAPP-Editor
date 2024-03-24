package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;

import java.util.ArrayList;

public class BranchNode extends HBox {
    BranchNode self;
    boolean root = false;
    BranchNode container;
    HorizontalTreeView horizontalTreeView;
    BoxedDRTA formulaBoxedDRTA;
    BoxedDRTA connectorBoxedDRTA;
    TextField annotationField = new TextField();
    ArrayList<BranchNode> dependents = new ArrayList<>();
    static double offsetY = 38;
    static double leafPos = -38;
    double minLayoutY;
    double maxLayoutY;
    double layoutX;
    double layoutY;
    boolean annotation = false;
    boolean formulaNode = true;
    boolean indefiniteNode = false;
    boolean dotDivider;



    double annotationWidth  = 28;
    double rootBump = 0;
    double annBump = 0;


    public BranchNode(BranchNode container, HorizontalTreeView horizontalTreeView) {
        super();
        this.container = container;
        this.horizontalTreeView = horizontalTreeView;
        self = this;
        formulaBoxedDRTA = newFormulaBoxedDRTA();
        connectorBoxedDRTA = newFormulaBoxedDRTA();

        self.getChildren().add(formulaBoxedDRTA.getBoxedRTA());
        HrzRightDragResizer resizer = new HrzRightDragResizer(horizontalTreeView);
        resizer.makeResizable(formulaBoxedDRTA.getRTA());
        self.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1.5 0");
        self.setPadding(new Insets(0, 4, 0, 2));



    }

    void doLayout(double xVal) {
        leafPos = -38;
        setLayout(xVal);
    }

    double setLayout(double xVal) {
        layoutX = xVal;
        if (dependents.isEmpty()) {
            leafPos = leafPos + offsetY;
            layoutY = leafPos;
            return layoutY;
        }
        else {
            BranchNode initialNode = dependents.get(0);
            minLayoutY = initialNode.setLayout(xVal + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 36);
            maxLayoutY = minLayoutY;
            for (int i = 1; i < dependents.size(); i++) {
                BranchNode node = dependents.get(i);
                maxLayoutY = node.setLayout(xVal + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 36);
            }
            layoutY = minLayoutY + (maxLayoutY - minLayoutY)/2.0;
            return layoutY;
        }
    }

    void addToPane (Pane pane, double offsetX, double offsetY) {
        pane.getChildren().add(self);
        self.setLayoutX(layoutX + offsetX);
        self.setLayoutY(layoutY + offsetY);
        if (dotDivider) {
            Line dotLine = new Line(0,0,0,27);
            dotLine.getStrokeDashArray().addAll(1.0, 4.0);
            pane.getChildren().add(dotLine);
            dotLine.setLayoutX(self.layoutX + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 9);
            dotLine.setLayoutY(self.layoutY + 14.0);
        }
        if (!dependents.isEmpty()) {
            if (dependents.get(0).isFormulaNode()) {
                offsetY = 0.0;
                if (dependents.size() == 1) {
                    VBox simpleConnector = newSimpleConnectBox();
                    pane.getChildren().add(simpleConnector);
                    BranchNode dependent = dependents.get(0);
                    simpleConnector.setLayoutX(dependent.getXLayout() - 30.0);
                    simpleConnector.setLayoutY(dependent.getYLayout() + 14.0);
                }
                else if (dependents.size() > 1) {
                    BranchNode topNode = dependents.get(0);
                    BranchNode bottomNode = dependents.get(dependents.size() - 1);
                    double top = topNode.getYLayout();
                    double bottom = bottomNode.getYLayout();
                    HBox bracketBox = newBracketBox(top, bottom);
                    pane.getChildren().add(bracketBox);
                    bracketBox.setLayoutX(topNode.getXLayout() - 30);
                    bracketBox.setLayoutY(topNode.getYLayout() + 27.5);
                }
            }
            else {
                offsetY = 14.0;
                Pane branchPane = newTermBranch();
                pane.getChildren().add(branchPane);
                BranchNode topNode = dependents.get(0);
                double xDiff = 31.0;
                if (dotDivider) xDiff = 24.0;
                branchPane.setLayoutX(topNode.getXLayout() - xDiff);
                branchPane.setLayoutY(topNode.getYLayout() + 28);
            }
        }

        for (int i = 0; i < dependents.size(); i++) {
            BranchNode node = dependents.get(i);
            node.addToPane(pane, offsetX, offsetY);
        }
    }

    private BoxedDRTA newFormulaBoxedDRTA() {
        BoxedDRTA boxedDRTA = new BoxedDRTA();
        DecoratedRTA drta = boxedDRTA.getDRTA();
        drta.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea rta = boxedDRTA.getRTA();
        rta.setMaxHeight(27);
        rta.setMinHeight(27);
        rta.setPrefWidth(48);
        rta.getStylesheets().add("RichTExtField.css");
        rta.setPromptText("");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                horizontalTreeView.getMainView().editorInFocus(drta, ControlType.FIELD);
            }
        });
        return boxedDRTA;
    }

    private Pane newTermBranch() {
        double top = dependents.get(0).getYLayout();
        double bottom  = dependents.get(dependents.size() - 1).getYLayout();
        double center = (bottom - top)/2;
        Pane branchPane = new Pane();
        double xEnd = 31;
        if (dotDivider) xEnd = 24;
        for (BranchNode node : dependents) {
            Line line = new Line(0, center, xEnd, node.getYLayout() - top);
            branchPane.getChildren().add(line);
        }
        return branchPane;
    }

    private VBox newSimpleConnectBox() {

        connectorBoxedDRTA.getRTA().setPrefWidth(30);
        VBox connectBox = new VBox(connectorBoxedDRTA.getBoxedRTA());
        connectBox.setAlignment(Pos.CENTER);
        return connectBox;
    }

    private HBox newBracketBox(double top, double bottom) {
        double height = bottom - top;
        connectorBoxedDRTA.getRTA().setPrefWidth(24);
        VBox rtaBox = new VBox(connectorBoxedDRTA.getBoxedRTA());
        rtaBox.setAlignment(Pos.CENTER);
        Line stub = new Line(0, 0, 3, 0);
        stub.setStyle("-fx-stroke-width: 1.5");
        Line bracket = new Line(0,0, 0, height);
        bracket.setStyle("-fx-stroke-width: 1.5");
        HBox bracketBox = new HBox(rtaBox, stub, bracket);
        bracketBox.setAlignment(Pos.CENTER);
        return bracketBox;
    }



    void processAnnotationRequest(boolean add) {
        if (add) {
            if (!annotation) {
                addAnnotation();
                annBump = annotationWidth;
            }
        }
        else {
            if (annotation) {
                self.getChildren().remove(annotationField);
                annotation = false;
                annBump = 0;
            }
        }
    }

    public void addAnnotation() {
        if (!annotation) {
            annotationField.setPrefWidth(annotationWidth);
            annotationField.setPrefHeight(15);
            annotationField.setFont(new Font("Noto Sans", 10));
            annotationField.setPadding(new Insets(0));

            self.getChildren().add(annotationField);
            self.setMargin(annotationField, new Insets(0, 0, 8, 0));
            annotation = true;
        }
    }

    public boolean isAnnotation() {    return annotation;  }

    public void setAnnotation(boolean annotation) {   this.annotation = annotation; }

    public TextField getAnnotationField() {    return annotationField;  }


    public boolean isIndefiniteNode() {    return indefiniteNode;   }

    public boolean isDotDivider() {    return dotDivider;  }

    public boolean isRoot() {     return root;  }

    public void setIndefiniteNode(boolean indefiniteNode) {    this.indefiniteNode = indefiniteNode;  }

    public BoxedDRTA getFormulaBoxedDRTA() {  return formulaBoxedDRTA;  }

    public BoxedDRTA getConnectorBoxedDRTA() {     return connectorBoxedDRTA;  }

    public BranchNode getContainer() {return container; }
    public double getXLayout() { return layoutX; }

    public void setRoot(boolean root) {    this.root = root;}

    public double getYLayout() { return layoutY; }
    public ArrayList<BranchNode> getDependents() {  return dependents;  }

    public void setDotDivider(boolean dotDivider) { this.dotDivider = dotDivider; }

    public void setRootBump(double rootBump) {    this.rootBump = rootBump;  }

    public boolean isFormulaNode() {  return formulaNode; }

    public void setFormulaNode(boolean formulaNode) { this.formulaNode = formulaNode; }
}
