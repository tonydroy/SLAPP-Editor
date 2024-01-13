package slapp.editor.vertical_tree.drag_drop;


import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class RootLayout extends AnchorPane {
    SplitPane base_pane;
    ScrollPane scroll_pane;
    AnchorPane right_pane;
    VBox left_pane;

    private DragIcon mDragOverIcon = null;
    private EventHandler<DragEvent> mIconDragOverRoot = null;
    private EventHandler<DragEvent> mIconDragDropped = null;
    private EventHandler<DragEvent> mIconDragOverRightPane = null;

    public RootLayout() {
        scroll_pane = new ScrollPane();
        left_pane = new VBox();
        scroll_pane.setContent(left_pane);
        right_pane = new AnchorPane();
        base_pane = new SplitPane();
        base_pane.getItems().addAll(scroll_pane, right_pane);
        this.getChildren().add(base_pane);

        setupWindow();
        initialize();

    }

    private void setupWindow() {

        this.getStylesheets().add("/drag_drop.css");
        right_pane.setStyle("-fx-background-color: white," +
                "linear-gradient(from 0.5px 0.0px to 24.5px  0.0px, repeat, #f5f5f5 1%, transparent 5%)," +
                "linear-gradient(from 0.0px 0.5px to  0.0px 24.5px, repeat, #f5f5f5 1%, transparent 5%);");


        this.setMinWidth(200); this.setMinHeight(200);
        this.setBottomAnchor(base_pane, 0.0); this.setTopAnchor(base_pane, 0.0); this.setLeftAnchor(base_pane, 0.0); this.setRightAnchor(base_pane, 0.0);
        scroll_pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); scroll_pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll_pane.setPrefWidth(100); scroll_pane.setMinWidth(100); scroll_pane.setMaxWidth(100);
        scroll_pane.setPadding(new Insets(6,0,0,8));
        left_pane.setSpacing(10);

        base_pane.setResizableWithParent(right_pane, true);







    }



    public void initialize() {


        //Add one icon that will be used for the drag-drop process
        //This is added as a child to the root anchorpane so it can be visible
        //on both sides of the split pane.
        mDragOverIcon = new DragIcon();

        mDragOverIcon.setVisible(false);
        mDragOverIcon.setOpacity(0.65);
        getChildren().add(mDragOverIcon);

        //populate left pane with multiple colored icons for testing
        for (int i = 0; i < 8; i++) {

            DragIcon icn = new DragIcon();

            addDragDetection(icn);

            icn.setType(DragIconType.values()[i]);
            left_pane.getChildren().add(icn);
        }
        buildDragHandlers();
    }

    private void addDragDetection(DragIcon dragIcon) {
        dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {
            @Override
            public void handle(MouseEvent event) {

                // set drag event handlers on their respective objects
                base_pane.setOnDragOver(mIconDragOverRoot);
                right_pane.setOnDragOver(mIconDragOverRightPane);
                right_pane.setOnDragDropped(mIconDragDropped);

                // get a reference to the clicked DragIcon object
                DragIcon icn = (DragIcon) event.getSource();

                //begin drag ops
                mDragOverIcon.setType(icn.getType());
                mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();

                container.addData ("type", mDragOverIcon.getType().toString());
                content.put(DragContainer.AddNode, container);

                mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
                mDragOverIcon.setVisible(true);
                mDragOverIcon.setMouseTransparent(true);
                event.consume();
            }
        });
    }

    private void buildDragHandlers() {

        //drag over transition to move widget form left pane to right pane
        mIconDragOverRoot = new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

                //turn on transfer mode and track in the right-pane's context
                //if (and only if) the mouse cursor falls within the right pane's bounds.
                if (!right_pane.boundsInLocalProperty().get().contains(p)) {

                    event.acceptTransferModes(TransferMode.ANY);
                    mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                    return;
                }

                event.consume();
            }
        };

        mIconDragOverRightPane = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);

                //convert the mouse coordinates to scene coordinates,
                //then convert back to coordinates that are relative to
                //the parent of mDragIcon.  Since mDragIcon is a child of the root
                //pane, coodinates must be in the root pane's coordinate system to work
                //properly.
                mDragOverIcon.relocateToPoint(
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
                event.consume();
            }
        };

        mIconDragDropped = new EventHandler <DragEvent> () {
            @Override
            public void handle(DragEvent event) {

                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

                container.addData("scene_coords",
                        new Point2D(event.getSceneX(), event.getSceneY()));

                ClipboardContent content = new ClipboardContent();
                content.put(DragContainer.AddNode, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
            }
        };

        this.setOnDragDone (new EventHandler <DragEvent> (){
            @Override
            public void handle (DragEvent event) {

                right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
                right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
                base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);

                mDragOverIcon.setVisible(false);

                //Create node drag operation
                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddNode);

                if (container != null) {
                    if (container.getValue("scene_coords") != null) {

                        if (container.getValue("type").equals(DragIconType.dashed_line.toString())) {
                            DashedLine line = new DashedLine();
                            right_pane.getChildren().add(line);
                            Point2D cursorPoint = container.getValue("scene_coords");
                            line.relocateToGridPoint(new Point2D(cursorPoint.getX() - 28, cursorPoint.getY()));
                        }


                        else if (container.getValue("type").equals(DragIconType.cubic_curve.toString())) {
                            CubicCurveDemo curve = new CubicCurveDemo();

                            right_pane.getChildren().add(curve);

                            Point2D cursorPoint = container.getValue("scene_coords");

                            curve.relocateToPoint(
                                    new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                            );
                        }


                        else {

                            DraggableNode node = new DraggableNode();

                            node.setType(DragIconType.valueOf(container.getValue("type")));
                            right_pane.getChildren().add(node);

                            Point2D cursorPoint = container.getValue("scene_coords");

                            node.relocateToPoint(
                                    new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
                            );
                        }
                    }
                }

                /*
				//Move node drag operation
				container =
						(DragContainer) event.getDragboard().getContent(DragContainer.DragNode);

				if (container != null) {
					if (container.getValue("type") != null)
						System.out.println ("Moved node " + container.getValue("type"));
				}
                 */


                //AddLink drag operation
                container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

                if (container != null) {

                    //bind the ends of our link to the nodes whose id's are stored in the drag container
                    String sourceId = container.getValue("source");
                    String targetId = container.getValue("target");

                    if (sourceId != null && targetId != null) {

                        //	System.out.println(container.getData());
                        NodeLink link = new NodeLink();

                        //add our link at the top of the rendering order so it's rendered first
                        right_pane.getChildren().add(0,link);

                        DraggableNode source = null;
                        DraggableNode target = null;

                        for (Node n: right_pane.getChildren()) {

                            if (n.getId() == null)
                                continue;

                            if (n.getId().equals(sourceId))
                                source = (DraggableNode) n;

                            if (n.getId().equals(targetId))
                                target = (DraggableNode) n;

                        }

                        if (source != null && target != null)
                            link.bindEnds(source, target);
                    }

                }

                event.consume();
            }
        });
    }

    public SplitPane getBase_pane() {
        return base_pane;
    }

    public AnchorPane getRight_pane() {
        return right_pane;
    }

    public VBox getLeft_pane() {
        return left_pane;
    }
}