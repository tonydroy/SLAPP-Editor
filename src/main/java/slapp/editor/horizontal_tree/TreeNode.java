package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;

import java.util.ArrayList;

public class TreeNode extends HBox {
    TreeNode self;
    boolean root = false;
    TreeNode container;
    HorizontalTreeView horizontalTreeView;
    BoxedDRTA boxedDRTA;
    ArrayList<TreeNode> dependents = new ArrayList<>();
    static double offsetY = 38;
    static double leafPos = -38;
    double minLayoutY;
    double maxLayoutY;
    double layoutX;
    double layoutY;
    boolean annotation = false;
    boolean withDots;
    TextField annotationField;
    double annotationWidth  = 28;

    double rootBump = 0;
    double annBump = 0;


    public TreeNode(TreeNode container, HorizontalTreeView horizontalTreeView) {
        super();
        this.container = container;
        this.horizontalTreeView = horizontalTreeView;
        self = this;
        boxedDRTA = newFormulaBoxedDRTA();

        self.getChildren().add(boxedDRTA.getBoxedRTA());
        HrzRightDragResizer resizer = new HrzRightDragResizer(horizontalTreeView);
        resizer.makeResizable(boxedDRTA.getRTA());
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
            TreeNode initialNode = dependents.get(0);
            minLayoutY = initialNode.setLayout(xVal + boxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 36);
            maxLayoutY = minLayoutY;
            for (int i = 1; i < dependents.size(); i++) {
                TreeNode node = dependents.get(i);
                maxLayoutY = node.setLayout(xVal + boxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 36);
            }
            layoutY = minLayoutY + (maxLayoutY - minLayoutY)/2.0;
            return layoutY;
        }
    }

    void addToPane (Pane pane, double offsetX, double offsetY) {
        pane.getChildren().add(self);
        self.setLayoutX(layoutX + offsetX);
        self.setLayoutY(layoutY + offsetY);
        if (withDots) {
            Line dotLine = new Line(0,0,0,27);
            dotLine.getStrokeDashArray().addAll(1.0, 4.0);
            pane.getChildren().add(dotLine);
            dotLine.setLayoutX(self.layoutX + boxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 9);
            dotLine.setLayoutY(self.layoutY + 14.0);
        }
        if (dependents.size() == 1) {
            VBox simpleConnector = newSimpleConnectBox();
            pane.getChildren().add(simpleConnector);
            TreeNode dependent = dependents.get(0);
            simpleConnector.setLayoutX(dependent.getXLayout() - 30.0);
            simpleConnector.setLayoutY(dependent.getYLayout() + 14.0);
        }
        if (dependents.size() > 1) {
            TreeNode topNode = dependents.get(0);
            TreeNode bottomNode = dependents.get(dependents.size() - 1);
            double top = topNode.getYLayout();
            double bottom = bottomNode.getYLayout();
            HBox bracketBox = newBracketBox(top, bottom);
            pane.getChildren().add(bracketBox);
            bracketBox.setLayoutX(topNode.getXLayout() - 30);
            bracketBox.setLayoutY(topNode.getYLayout() + 27.5);
        }

        for (int i = 0; i < dependents.size(); i++) {
            TreeNode node = dependents.get(i);
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

    private VBox newSimpleConnectBox() {
        BoxedDRTA boxedDRTA = newFormulaBoxedDRTA();
        boxedDRTA.getRTA().setPrefWidth(30);
        VBox connectBox = new VBox(boxedDRTA.getBoxedRTA());
        connectBox.setAlignment(Pos.CENTER);
        return connectBox;
    }

    private HBox newBracketBox(double top, double bottom) {
        double height = bottom - top;
        BoxedDRTA boxedDRTA = newFormulaBoxedDRTA();
        boxedDRTA.getRTA().setPrefWidth(24);
        VBox rtaBox = new VBox(boxedDRTA.getBoxedRTA());
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
            annotationField = new TextField();
            annotationField.setPrefWidth(annotationWidth);
            annotationField.setPrefHeight(15);
            annotationField.setFont(new Font("Noto Sans", 10));
            annotationField.setPadding(new Insets(0));

            self.getChildren().add(annotationField);
            self.setMargin(annotationField, new Insets(0, 0, 8, 0));
            annotation = true;
        }
    }

    public BoxedDRTA getBoxedDRTA() {  return boxedDRTA;  }

    public TreeNode getContainer() {return container; }
    public double getXLayout() { return layoutX; }

    public void setRoot(boolean root) {    this.root = root;}

    public double getYLayout() { return layoutY; }
    public ArrayList<TreeNode> getDependents() {  return dependents;  }

    public void setWithDots(boolean withDots) { this.withDots = withDots; }

    public void setRootBump(double rootBump) {    this.rootBump = rootBump;  }
}
