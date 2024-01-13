package slapp.editor.vertical_tree.drag_drop;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ListIterator;

public class DashedLine extends AnchorPane {

    private Label leftDragLabel;

    private EventHandler<DragEvent> mContextDragOver;
    private EventHandler <DragEvent> mContextDragDropped;

    private DragIconType mType = DragIconType.dashed_line;

    private Point2D mDragOffset = new Point2D (0.0, 0.0);

    private final DashedLine self;



    DashedLine() {
        self = this;

        leftDragLabel = new Label("label");
        leftDragLabel.setMaxWidth(10);




        Line line = new Line(4, 12, 60, 12);
        line.getStrokeDashArray().addAll(5.0, 5.0);

        AnchorPane mainPane = new AnchorPane();
        self.setMinHeight(24.0);
        self.setPrefWidth(64);
        //       linesBox.setStyle("-fx-border-color: blue; -fx-border-width: 2 2 2 2;");


        line.endXProperty().bind(self.widthProperty().subtract(4.0));
        DragResizer.makeResizable(self);
        self.getChildren().addAll(line);
        self.setLeftAnchor(line, 5.0);



        initialize();
    }

    private void initialize() {
        buildNodeDragHandlers();
    }

    public void setType (DragIconType type) {
        mType = type;
    }

    public void buildNodeDragHandlers() {

        mContextDragOver = new EventHandler <DragEvent>() {

            //dragover to handle node dragging in the right pane view
            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);
                relocateToPoint(new Point2dSerial( event.getSceneX(), event.getSceneY()));

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

                relocateToGridPoint(
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
                self.setCursor(Cursor.DEFAULT);


            }
        };

        /*
        //close button click
        close_button.setOnMouseClicked( new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {
                AnchorPane parent  = (AnchorPane) self.getParent();
                parent.getChildren().remove(self);

                //iterate each link id connected to this node
                //find it's corresponding component in the right-hand
                //AnchorPane and delete it.

                //Note:  other nodes connected to these links are not being
                //notified that the link has been removed.
                for (ListIterator<String> iterId = mLinkIds.listIterator();
                     iterId.hasNext();) {

                    String id = iterId.next();

                    for (ListIterator <Node> iterNode = parent.getChildren().listIterator();
                         iterNode.hasNext();) {

                        Node node = iterNode.next();

                        if (node.getId() == null)
                            continue;

                        if (node.getId().equals(id))
                            iterNode.remove();
                    }

                    iterId.remove();
                }
            }

        });

         */

        self.setOnDragDetected ( new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver (mContextDragOver);
                getParent().setOnDragDropped (mContextDragDropped);


                //begin drag ops
                mDragOffset = new Point2D(event.getX(), event.getY());

                relocateToPoint(
                        new Point2D(event.getSceneX(), event.getSceneY())
                );

                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", mType.toString());
                content.put(DragContainer.AddNode, container);

                startDragAndDrop (TransferMode.ANY).setContent(content);

                event.consume();
            }

        });

    }

    public void relocateToGridPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);


        double localY = Math.round(localCoords.getY() / 24.0) * 24.0;

        relocate (
                (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),
                (int) (localY - (getBoundsInLocal().getHeight() / 2 ))


                //             (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2 ))
        );
    }



    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);


 //       double localY = Math.round(localCoords.getY() / 24.0) * 24.0;

        relocate (
                (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),
                (int) ((localCoords.getY() - (getBoundsInLocal().getHeight()) / 2 ))
        );
    }

}
