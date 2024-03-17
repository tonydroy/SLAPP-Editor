package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;

import java.util.ArrayList;

public class TreeNode extends AnchorPane {
    TreeNode self;
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

    public TreeNode(TreeNode container, HorizontalTreeView horizontalTreeView) {
        super();
        this.container = container;
        this.horizontalTreeView = horizontalTreeView;
        self = this;
        boxedDRTA = newFormulaBoxedDRTA();

        self.getChildren().add(boxedDRTA.getBoxedRTA());
        setLeftAnchor(boxedDRTA.getBoxedRTA(), 2.0);
        HrzRightDragResizer resizer = new HrzRightDragResizer(horizontalTreeView);
        resizer.makeResizable(boxedDRTA.getRTA());
        self.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1.5 0");
        self.setPadding(new Insets(0, 5, 0, 5));
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
            minLayoutY = initialNode.setLayout(xVal + boxedDRTA.getRTA().getPrefWidth() + 15);
            maxLayoutY = minLayoutY;
            for (int i = 1; i < dependents.size(); i++) {
                TreeNode node = dependents.get(i);
                maxLayoutY = node.setLayout(xVal + boxedDRTA.getRTA().getPrefWidth() + 15);
            }
            layoutY = minLayoutY + (maxLayoutY - minLayoutY)/2.0;
            return layoutY;
        }
    }

    void addToPane (Pane pane, double offsetX, double offsetY) {
        pane.getChildren().add(self);
        self.setLayoutX(layoutX + offsetX);
        self.setLayoutY(layoutY + offsetY);

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

    void processAnnotationRequest(boolean add) {

    }

    public TreeNode getContainer() {return container; }

    public double getXLayout() { return layoutX; }

    public double getYLayout() { return layoutY; }


    public ArrayList<TreeNode> getDependents() {  return dependents;  }
}
