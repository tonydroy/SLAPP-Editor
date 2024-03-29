package slapp.editor.horizontal_tree;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import slapp.editor.vertical_tree.drag_drop.DragContainer;
import slapp.editor.vertical_tree.drag_drop.Point2dSerial;

public class TreePane extends Pane {

    TreePane self;

    HorizontalTreeView horizontalTreeView;
    BranchNode rootBranchNode;
    Label leftDragLabel;
    private EventHandler<DragEvent> mContextDragOver;
    private EventHandler <DragEvent> mContextDragDropped;
    private Point2D mDragOffset = new Point2D (0.0, 0.0);
    private boolean numberAxis = false;


    TreePane(HorizontalTreeView horizontalTreeView) {
        self = this;
        this.horizontalTreeView = horizontalTreeView;
        rootBranchNode = new BranchNode(null, horizontalTreeView);
        rootBranchNode.setRoot(true);
        rootBranchNode.setRootBump(8.0);

        leftDragLabel = new Label("");
        leftDragLabel.setMaxWidth(10);
        leftDragLabel.setMinWidth(10);
        leftDragLabel.setPrefWidth(10);
        leftDragLabel.setMaxHeight(21);
        leftDragLabel.setMinHeight(21);
        leftDragLabel.setPadding(new Insets(0));

        leftDragLabel.setOnMouseEntered(e -> {
            leftDragLabel.setStyle("-fx-background-color: grey; -fx-background-radius: 5 0 0 5;");
        });
        leftDragLabel.setOnMouseExited(e -> {
            leftDragLabel.setStyle("-fx-background-color: transparent");
        });

        rootBranchNode.getChildren().add(0, leftDragLabel);
        rootBranchNode.setPadding(new Insets(0, 4, 0, 0));


        buildNodeDragHandlers();
    }

    void refresh() {

        double startX = rootBranchNode.getLayoutX();
        double startY = rootBranchNode.getLayoutY();

        this.getChildren().clear();
        rootBranchNode.doLayout(0);
        rootBranchNode.addToPane(self, 0, 0);

        double deltaX = startX - rootBranchNode.getXLayout();
        self.setLayoutX(self.getLayoutX() + deltaX);
        double deltaY = startY - rootBranchNode.getYLayout();
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

                horizontalTreeView.setUndoRedoFlag(true);
                horizontalTreeView.setUndoRedoFlag(false);

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


    public BranchNode getRootBranchNode() { return rootBranchNode; }



}
