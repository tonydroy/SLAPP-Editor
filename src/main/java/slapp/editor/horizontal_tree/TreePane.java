package slapp.editor.horizontal_tree;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import slapp.editor.vertical_tree.drag_drop.DragContainer;
import slapp.editor.vertical_tree.drag_drop.Point2dSerial;

import java.util.ListIterator;

public class TreePane extends Pane {

    TreePane self;

    HorizontalTreeView horizontalTreeView;
    TreeNode rootTreeNode;
    Label leftDragLabel;
    private EventHandler<DragEvent> mContextDragOver;
    private EventHandler <DragEvent> mContextDragDropped;
    private Point2D mDragOffset = new Point2D (0.0, 0.0);
    private NumberAxis numAxis;
    private boolean numberAxis = false;


    TreePane(HorizontalTreeView horizontalTreeView) {
        self = this;
        this.horizontalTreeView = horizontalTreeView;
        rootTreeNode = new TreeNode(null, horizontalTreeView);
        rootTreeNode.setRoot(true);

        leftDragLabel = new Label("");
        leftDragLabel.setMaxWidth(10);
        leftDragLabel.setMinWidth(10);
        leftDragLabel.setPrefWidth(10);
        leftDragLabel.setMaxHeight(26);
        leftDragLabel.setMinHeight(26);
        leftDragLabel.setPadding(new Insets(0));

        leftDragLabel.setOnMouseEntered(e -> {
            leftDragLabel.setStyle("-fx-background-color: grey; -fx-background-radius: 5 0 0 5;");
        });
        leftDragLabel.setOnMouseExited(e -> {
            leftDragLabel.setStyle("-fx-background-color: transparent");
        });

        rootTreeNode.getChildren().add(0, leftDragLabel);
        rootTreeNode.setPadding(new Insets(0, 4, 0, 0));


        buildNodeDragHandlers();



    }

    void refresh() {
        double startX = rootTreeNode.getLayoutX();
        double startY = rootTreeNode.getLayoutY();

        this.getChildren().clear();
        rootTreeNode.doLayout(0);
        rootTreeNode.addToPane(self, 0, 0);

        double deltaX = startX - rootTreeNode.getXLayout();
        self.setLayoutX(self.getLayoutX() + deltaX);
        double deltaY = startY - rootTreeNode.getYLayout();
        self.setLayoutY(self.getLayoutY() + deltaY);

        double newY = self.getLayoutY();
        if (newY < 5.0) self.setLayoutY(5.0001);
    }




    public void buildNodeDragHandlers() {
        mContextDragOver = new EventHandler<DragEvent>() {

            //dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);

                if (event.getY() - self.getLayoutBounds().getHeight()/2 > 5.0) {
                    relocateToPoint(new Point2dSerial(event.getSceneX(), event.getSceneY()));
                }
                else {
                    relocateToGridPoint(new Point2D(event.getX(),5.0001));
                }
                event.consume();
            }
        };

        //dragdrop for node dragging
        mContextDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                event.setDropCompleted(true);

                relocateToGridPoint(new Point2D(event.getX(), event.getY()));
                relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));

                self.setCursor(Cursor.DEFAULT);
            }
        };




        leftDragLabel.setOnDragDetected ( new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {


                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                getParent().setOnDragOver (mContextDragOver);
                getParent().setOnDragDropped (mContextDragDropped);


                //begin drag ops
 //               mDragOffset = new Point2D(event.getX(), event.getY());

                if (self.getLayoutY() > 5.0) {
                    relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                }
                else {
                    relocateToGridPoint(new Point2D(event.getX(), 5.001));
                }



                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", "formula box");
                content.put(DragContainer.AddNode, container);

                startDragAndDrop (TransferMode.ANY).setContent(content);
                event.consume();
            }

        });

    }


    public void relocateToGridPoint(Point2D p) {
        //for object dropped onto pane
        relocate ((int) p.getX(), p.getY());
//        Point2D localCoords = getParent().sceneToLocal(p);
//        relocate ((int) localCoords.getX(),  (int) localCoords.getY() );
    }

    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);


        //       double localY = Math.round(localCoords.getY() / 24.0) * 24.0;

        relocate (
                (int) localCoords.getX(),
                //           (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),
                (int) ((localCoords.getY() - (getBoundsInLocal().getHeight()) / 2 ))
        );
    }


    public TreeNode getRootTreeNode() { return rootTreeNode; }
}
