/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
    static double offsetY = 48;  //34
    static double leafPos = -48;
    double minYlayout;
    double maxYlayout;
    double Xlayout;
    double Ylayout;
    boolean annotation = false;
    boolean formulaNode = true;
    boolean indefiniteNode = false;
    boolean dotDivider;
    double annotationWidth  = 28;
    double rootBump = 0;
    double annBump = 0;
    double formulaBoxHeight = 22.5;
    ChangeListener annotationListener;
    ChangeListener formulaFocusListener;
    ChangeListener connectorFocusListener;



    public BranchNode(BranchNode container, HorizontalTreeView horizontalTreeView) {
        super();
        this.container = container;
        this.horizontalTreeView = horizontalTreeView;
        self = this;
        formulaBoxedDRTA = newFormulaBoxedDRTA();
        connectorBoxedDRTA = newFormulaBoxedDRTA();


        self.getChildren().add(formulaBoxedDRTA.getBoxedRTA());



        //
//        formulaBoxedDRTA.getBoxedRTA().setHgrow(formulaBoxedDRTA.getRTA(), Priority.ALWAYS);
//        self.setHgrow(formulaBoxedDRTA.getBoxedRTA(), Priority.ALWAYS);
        //

//        HrzRightDragResizer resizer = new HrzRightDragResizer(horizontalTreeView);
//        resizer.makeResizable(self, formulaBoxedDRTA.getRTA());

 //       resizer.makeResizable(this);

        self.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1.5 0");
        self.setPadding(new Insets(0, 4, 0, 2));

        annotationField.setPrefWidth(annotationWidth);
        annotationField.setPrefHeight(15);
        annotationField.setFont(new Font("NotoSans", 10));
        annotationField.setPadding(new Insets(0));

        annotationField.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) horizontalTreeView.getMainView().textFieldInFocus();
            else {
                horizontalTreeView.setUndoRedoFlag(true);
                horizontalTreeView.setUndoRedoFlag(false);
            }
        });

        formulaFocusListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv) {
                    horizontalTreeView.getMainView().editorInFocus(formulaBoxedDRTA.getDRTA(), ControlType.FIELD);
                }
                else {
                    if (formulaBoxedDRTA.getRTA().isModified()) {
                        horizontalTreeView.setUndoRedoFlag(true);
                        horizontalTreeView.setUndoRedoFlag(false);
                        formulaBoxedDRTA.getRTA().getActionFactory().saveNow().execute(new ActionEvent());
                    }
                }
            }
        };
        formulaBoxedDRTA.getRTA().focusedProperty().addListener(formulaFocusListener);

        connectorFocusListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue ob, Boolean ov, Boolean nv) {
                if (nv) {
                    horizontalTreeView.getMainView().editorInFocus(connectorBoxedDRTA.getDRTA(), ControlType.FIELD);
                }
                else {
                    if (connectorBoxedDRTA.getRTA().isModified()) {
                        horizontalTreeView.setUndoRedoFlag(true);
                        horizontalTreeView.setUndoRedoFlag(false);
                        connectorBoxedDRTA.getRTA().getActionFactory().saveNow().execute(new ActionEvent());
                    }
                }
            }
        };
        connectorBoxedDRTA.getRTA().focusedProperty().addListener(connectorFocusListener);

        annotationField.textProperty().addListener((ob, ov, nv) -> {
            horizontalTreeView.setAnnotationModified(true);
        });
    }

    void doLayout(double xVal) {
        leafPos = -48;
        setLayout(xVal);
    }

    double setLayout(double xVal) {
        Xlayout = xVal;
        if (dependents.isEmpty()) {
            leafPos = leafPos + offsetY;
            Ylayout = leafPos;
            if (!formulaNode) Ylayout -= formulaBoxHeight/2; //Ylayout -= 14;
            return Ylayout;
        }
        else {
            BranchNode initialNode = dependents.get(0);

            if (!initialNode.isFormulaNode())   minYlayout = initialNode.setLayout(xVal + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 36);
            else
            minYlayout = initialNode.setLayout(xVal + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + connectorBoxedDRTA.getRTA().getPrefWidth() + 9);

            maxYlayout = minYlayout;
            for (int i = 1; i < dependents.size(); i++) {
                BranchNode node = dependents.get(i);


              if (!initialNode.isFormulaNode()) maxYlayout = node.setLayout(xVal + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 36);
              else
                maxYlayout = node.setLayout(xVal + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + connectorBoxedDRTA.getRTA().getPrefWidth() + 9);
            }
            Ylayout = minYlayout + (maxYlayout - minYlayout)/2.0;
            return Ylayout;
        }
    }

    void addToPane (Pane pane, double offsetX, double offsetY) {
        pane.getChildren().add(self);
        self.setLayoutX(Xlayout + offsetX);
        self.setLayoutY(Ylayout + offsetY);

        if (dotDivider) {
            Line dotLine = new Line(0,0,0,27);
            dotLine.getStrokeDashArray().addAll(1.0, 4.0);
            pane.getChildren().add(dotLine);
            dotLine.setLayoutX(self.Xlayout + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 9);
            dotLine.setLayoutY(self.Ylayout + formulaBoxHeight/2);
        }
        if (!dependents.isEmpty()) {
            if (dependents.get(0).isFormulaNode()) {
                offsetY = 0.0;
                if (dependents.size() == 1) {

                    HBox simpleConnector = connectorBoxedDRTA.getBoxedRTA();

//                    VBox simpleConnector = newSimpleConnectBox();
                    pane.getChildren().add(simpleConnector);
                    BranchNode dependent = dependents.get(0);

//                    simpleConnector.setLayoutX(dependent.getXLayout() - 30.0);

                    simpleConnector.setLayoutX(Xlayout + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 8);
                    simpleConnector.setLayoutY(dependent.getYLayout() + formulaBoxHeight * 3/8);
                }
                else if (dependents.size() > 1) {
                    BranchNode topNode = dependents.get(0);
                    BranchNode bottomNode = dependents.get(dependents.size() - 1);
                    double top = topNode.getYLayout();
                    double bottom = bottomNode.getYLayout();
                    HBox bracketBox = newBracketBox(top, bottom);
                    pane.getChildren().add(bracketBox);

     //               bracketBox.setLayoutX(topNode.getXLayout() - 30);

                    bracketBox.setLayoutX(Xlayout + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + 3);
                    bracketBox.setLayoutY(topNode.getYLayout() + formulaBoxHeight + .8);
                }
            }
            else {
                offsetY = formulaBoxHeight/2;
                Pane branchPane = newTermBranch();
                pane.getChildren().add(branchPane);
                BranchNode topNode = dependents.get(0);
 //               double xDiff = 31.0;
 //               if (dotDivider) xDiff = 24.0;

                double dotsBump = 6;
                if (dotDivider) dotsBump = 12;


               branchPane.setLayoutX(Xlayout + formulaBoxedDRTA.getRTA().getPrefWidth() + annBump + rootBump + dotsBump );
 //               branchPane.setLayoutX(topNode.getXLayout() - xDiff);
                branchPane.setLayoutY(topNode.getYLayout() + formulaBoxHeight + 1.5);
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
        rta.setMaxHeight(formulaBoxHeight);
        rta.setMinHeight(formulaBoxHeight);

        RichTextAreaSkin rtaSkin = (RichTextAreaSkin) rta.getSkin();
        rta.prefWidthProperty().bind(Bindings.max(Bindings.add(rtaSkin.nodesWidthProperty(), 3), 10));
        rta.prefWidthProperty().addListener((ob, ov, nv) -> {
            horizontalTreeView.refreshTreePanes();
        });
        rta.addEventFilter(KeyEvent.ANY, e -> {
            if (e.getCode() == KeyCode.ENTER) e.consume();
        });
//        rta.setPrefWidth(48);




        rta.getStylesheets().add("RichTExtField.css");
        rta.setPromptText("");


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

//        connectorBoxedDRTA.getRTA().setPrefWidth(30);
        VBox connectBox = new VBox(connectorBoxedDRTA.getBoxedRTA());
        connectBox.setAlignment(Pos.CENTER);
        return connectBox;
    }

    private HBox newBracketBox(double top, double bottom) {
        double height = bottom - top;
//        connectorBoxedDRTA.getRTA().setPrefWidth(24);
        connectorBoxedDRTA.getBoxedRTA().setPadding(new Insets(0,0,6,0));


//        VBox rtaBox = new VBox(connectorBoxedDRTA.getBoxedRTA());
//        rtaBox.setAlignment(Pos.CENTER);
//        HBox rtaBox = connectorBoxedDRTA.getBoxedRTA();
        Group rtaBox = new Group(connectorBoxedDRTA.getBoxedRTA());


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
            self.getChildren().add(annotationField);
            self.setMargin(annotationField, new Insets(0, 0, 8, 0));
            annotation = true;
        }
    }

    public boolean isAnnotation() {    return annotation;  }

    public void setAnnotation(boolean annotation) {   this.annotation = annotation; }

    public TextField getAnnotationField() {    return annotationField;  }

    public void setAnnBump(double annBump) {    this.annBump = annBump;   }

    public double getAnnotationWidth() {     return annotationWidth;  }

    public boolean isIndefiniteNode() {    return indefiniteNode;   }

    public boolean isDotDivider() {    return dotDivider;  }

    public boolean isRoot() {     return root;  }

    public void setIndefiniteNode(boolean indefiniteNode) {    this.indefiniteNode = indefiniteNode;  }

    public BoxedDRTA getFormulaBoxedDRTA() {  return formulaBoxedDRTA;  }

    public BoxedDRTA getConnectorBoxedDRTA() {     return connectorBoxedDRTA;  }

    public BranchNode getContainer() {return container; }
    public double getXLayout() { return Xlayout; }

    public void setRoot(boolean root) {    this.root = root;}

    public double getYLayout() { return Ylayout; }
    public ArrayList<BranchNode> getDependents() {  return dependents;  }

    public void setDotDivider(boolean dotDivider) { this.dotDivider = dotDivider; }

    public void setRootBump(double rootBump) {    this.rootBump = rootBump;  }

    public boolean isFormulaNode() {  return formulaNode; }

    public void setFormulaNode(boolean formulaNode) { this.formulaNode = formulaNode; }

    public ChangeListener getFormulaFocusListener() {   return formulaFocusListener;  }

    public ChangeListener getConnectorFocusListener() {     return connectorFocusListener;  }
}
