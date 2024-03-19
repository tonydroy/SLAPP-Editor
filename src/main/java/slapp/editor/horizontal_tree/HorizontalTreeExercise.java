package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.action.TextDecorateAction;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.TextDecoration;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import slapp.editor.vertical_tree.drag_drop.TreeFormulaBox;

import java.util.Arrays;
import java.util.List;

public class HorizontalTreeExercise implements Exercise<HorizontalTreeModel, HorizontalTreeView> {

    private MainWindow mainWindow;
    private MainWindowView mainView;
    private HorizontalTreeModel horizontalTreeModel;
    private HorizontalTreeView horizontalTreeView;
    private boolean exerciseModified = false;

    private EventHandler formulaNodeClickFilter;
    private EventHandler oneBranchClickFilter;
    private EventHandler twoBranchClickFilter;
    private EventHandler threeBranchClickFilter;
    private EventHandler indefinateBranchClickFilter;
    private EventHandler annotationClickFilter;
    private static TreeNode clickNode = null;



    public HorizontalTreeExercise(HorizontalTreeModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.horizontalTreeModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.horizontalTreeView = new HorizontalTreeView(mainView);

        setHorizontalTreeView();

    }


    private void setHorizontalTreeView() {
        horizontalTreeView.setExerciseName(horizontalTreeModel.getExerciseName());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(horizontalTreeModel.getExerciseStatement());
        horizontalTreeView.setStatementPrefHeight(horizontalTreeModel.getStatementPrefHeight());
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        horizontalTreeView.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setPromptText("Comment: ");
        commentEditor.setDocument(horizontalTreeModel.getExerciseComment());
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        horizontalTreeView.setExerciseComment(commentDRTA);

        DecoratedRTA explainDRTA = horizontalTreeView.getExplainDRTA();
        RichTextArea explainEditor = explainDRTA.getEditor();
        explainEditor.setDocument(horizontalTreeModel.getExplainDocument());
        explainEditor.setPromptText("Explain:");
        explainEditor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(explainDRTA, ControlType.AREA);
        explainEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(explainDRTA, ControlType.AREA);
            }
        });



        AnchorPane mainPane = horizontalTreeView.getMainPane();
        formulaNodeClickFilter = new EventHandler<MouseEvent>() {
           @Override
            public void handle(MouseEvent event) {
               if (event.getButton() == MouseButton.PRIMARY) {
                   TreePane treePane = new TreePane(horizontalTreeView);
                   horizontalTreeView.getTreePanes().add(treePane);
                   mainPane.getChildren().add(treePane);
                   horizontalTreeView.refreshTreePanes();
                   treePane.relocateToGridPoint(new Point2D(event.getX(), event.getY()));
               }

               else if (event.getButton() == MouseButton.SECONDARY) {
                   for (TreePane pane : horizontalTreeView.getTreePanes()) {
                       TreeNode rootNode = pane.getRootTreeNode();
                       if (inHierarchy(event.getPickResult().getIntersectedNode(), rootNode)) {
                           horizontalTreeView.getTreePanes().remove(pane);
                           mainPane.getChildren().remove(pane);
                           horizontalTreeView.getFormulaNodeToggle().setSelected(false);
                           break;
                       } else {
                           setClickedNode(event, rootNode);
                           if (clickNode != null && clickNode != rootNode) {
                               clickNode.getContainer().getDependents().remove(clickNode);
                               pane.refresh();
                               horizontalTreeView.getFormulaNodeToggle().setSelected(false);
                               break;
                           }
                       }
                   }
               }
               horizontalTreeView.getFormulaNodeToggle().setSelected(false);
           }
        };
        horizontalTreeView.getFormulaNodeToggle().selectedProperty().addListener((ob, ov, nv) -> {
           if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, formulaNodeClickFilter);
           else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, formulaNodeClickFilter);
        });

        oneBranchClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : horizontalTreeView.getTreePanes()) {
                        TreeNode rootNode = pane.getRootTreeNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null) {
                            TreeNode branch1 = new TreeNode(clickNode, horizontalTreeView);
                            clickNode.getDependents().add(branch1);
                            pane.refresh();
                        }
                    }
                }
            }
        };

        horizontalTreeView.getOneBranchToggle().selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, oneBranchClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, oneBranchClickFilter);
        });

        twoBranchClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : horizontalTreeView.getTreePanes()) {
                        TreeNode rootNode = pane.getRootTreeNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null) {
                            TreeNode branch1 = new TreeNode(clickNode, horizontalTreeView);
                            TreeNode branch2 = new TreeNode(clickNode, horizontalTreeView);
                            clickNode.getDependents().addAll(Arrays.asList(branch1, branch2));
                            pane.refresh();
                        }
                    }
                }
            }
        };

        horizontalTreeView.getTwoBranchToggle().selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, twoBranchClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, twoBranchClickFilter);
        });

        threeBranchClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : horizontalTreeView.getTreePanes()) {
                        TreeNode rootNode = pane.getRootTreeNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null) {
                            TreeNode branch1 = new TreeNode(clickNode, horizontalTreeView);
                            TreeNode branch2 = new TreeNode(clickNode, horizontalTreeView);
                            TreeNode branch3 = new TreeNode(clickNode, horizontalTreeView);
                            clickNode.getDependents().addAll(Arrays.asList(branch1, branch2, branch3));
                            pane.refresh();
                        }
                    }
                }
            }
        };

        horizontalTreeView.getThreeBranchToggle().selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, threeBranchClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, threeBranchClickFilter);
        });


        indefinateBranchClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (TreePane pane : horizontalTreeView.getTreePanes()) {
                        TreeNode rootNode = pane.getRootTreeNode();
                        setClickedNode(event, rootNode);
                        if (clickNode != null) {
                            TreeNode branch = new TreeNode(clickNode, horizontalTreeView);
                            branch.setStyle("-fx-border-width: 0 0 0 0");
                            RichTextArea rta = branch.getBoxedDRTA().getRTA();
                            rta.setDocument(new Document(" \u22ee"));
                            rta.setPrefWidth(24);
                            clickNode.getDependents().add(branch);
                            pane.refresh();
                        }
                    }
                }
            }
        };

        horizontalTreeView.getIndefiniteBranchToggle().selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, indefinateBranchClickFilter);
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, indefinateBranchClickFilter);
        });



        annotationClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (TreePane pane : horizontalTreeView.getTreePanes()) {
                    TreeNode rootNode = pane.getRootTreeNode();
                    setClickedNode(event, rootNode);
                    if (clickNode != null) {
                        clickNode.processAnnotationRequest(event.getButton() == MouseButton.PRIMARY);
                        pane.refresh();
                        break;
                    }
                }
            }
        };
        ToggleButton annotationToggle = horizontalTreeView.getAnnotationToggle();
        annotationToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv)  mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, annotationClickFilter );
            else mainPane.removeEventFilter(MouseEvent.MOUSE_PRESSED, annotationClickFilter);
        });

        Button annotationPlus = horizontalTreeView.getAnnotationPlus();
        annotationPlus.setOnAction(e -> {
            for (TreePane pane : horizontalTreeView.getTreePanes()) {
                setAnnotations(pane.getRootTreeNode(), true);
                pane.refresh();
            }
            annotationToggle.setSelected(false);
        });

        Button annotationMinus = horizontalTreeView.getAnnotationMinus();
        annotationMinus.setOnAction(e -> {
            for (TreePane pane : horizontalTreeView.getTreePanes()) {
                setAnnotations(pane.getRootTreeNode(), false);
                pane.refresh();
            }
            annotationToggle.setSelected(false);
        });


        ToggleButton rulerButton = horizontalTreeView.getRulerButton();
        rulerButton.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                mainPane.getChildren().add(horizontalTreeView.getNumAxisPane());
            }
            else {
                mainPane.getChildren().remove(horizontalTreeView.getNumAxisPane());
            }
        });





        horizontalTreeView.initializeViewDetails();
    }

    void setAnnotations(TreeNode node, boolean add) {
        node.processAnnotationRequest(add);
        for (TreeNode child : node.getDependents()) {
            setAnnotations(child, add);
        }
    }


    void setClickedNode(MouseEvent event, TreeNode node) {
        clickNode = null;
        findClickNodeInTree(event, node);
    }
    void findClickNodeInTree(MouseEvent event, TreeNode node) {
        if ((inHierarchy(event.getPickResult().getIntersectedNode(), node))) {
            clickNode = node;
        }
        else {
            for (int i = 0; i < node.getDependents().size(); i++) {
                TreeNode newNode = node.getDependents().get(i);
                findClickNodeInTree(event, newNode);
            }
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

    @Override
    public HorizontalTreeModel getExerciseModel() { return horizontalTreeModel;  }

    @Override
    public HorizontalTreeView getExerciseView() { return horizontalTreeView;  }

    @Override
    public void saveExercise(boolean saveAs) {

    }

    @Override
    public List<Node> getPrintNodes() {
        return null;
    }

    @Override
    public Exercise<HorizontalTreeModel, HorizontalTreeView> resetExercise() {
        return null;
    }

    @Override
    public boolean isExerciseModified() {
        return false;
    }

    @Override
    public void setExerciseModified(boolean modified) { exerciseModified = modified;   }

    @Override
    public ExerciseModel<HorizontalTreeModel> getExerciseModelFromView() {
        return null;
    }
}
