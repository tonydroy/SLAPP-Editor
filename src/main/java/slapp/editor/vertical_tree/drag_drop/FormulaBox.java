package slapp.editor.vertical_tree.drag_drop;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class FormulaBox extends AnchorPane {

    private AnchorPane top_link_handle;
    private AnchorPane bottom_link_handle;
    private Label leftDragLabel;
    private Label closeLabel;

    private EventHandler <MouseEvent> mLinkHandleDragDetected;
    private EventHandler <DragEvent> mLinkHandleDragDropped;
    private EventHandler <DragEvent> mContextLinkDragOver;
    private EventHandler <DragEvent> mContextLinkDragDropped;
    private EventHandler<DragEvent> mContextDragOver;
    private EventHandler <DragEvent> mContextDragDropped;
    private NodeLink mDragLink = null;
    private AnchorPane right_pane = null;
    private final List<String> mLinkIds = new ArrayList<>();

    private DragIconType mType = DragIconType.dashed_line;

    private Point2D mDragOffset = new Point2D (0.0, 0.0);

    private final FormulaBox self;



    FormulaBox() {
        self = this;

        top_link_handle = new AnchorPane();
        top_link_handle.setPrefHeight(9);
        top_link_handle.setMaxWidth(30);
        top_link_handle.setOnMouseEntered(e -> top_link_handle.setStyle("-fx-background-color: grey; -fx-background-radius: 5 5 0 0") );
        top_link_handle.setOnDragOver(e -> top_link_handle.setStyle("-fx-background-color: grey; -fx-background-radius: 5 5 0 0") );
        top_link_handle.setOnMouseExited(e ->  top_link_handle.setStyle("-fx-background-color: transparent") );
        top_link_handle.setOnDragDetected(e ->  top_link_handle.setStyle("-fx-background-color: transparent"));

        bottom_link_handle = new AnchorPane();
        bottom_link_handle.setPrefHeight(9);
        bottom_link_handle.setMaxWidth(30);
        bottom_link_handle.setStyle("-fx-background-radius: 0 0 5 5");
        bottom_link_handle.setOnMouseEntered(e -> bottom_link_handle.setStyle("-fx-background-color: grey; -fx-background-radius: 0 0 5 5"));
        bottom_link_handle.setOnDragOver(e -> bottom_link_handle.setStyle("-fx-background-color: grey; -fx-background-radius: 0 0 5 5") );
        bottom_link_handle.setOnMouseExited(e -> bottom_link_handle.setStyle("fx-background-color: transparent"));
        bottom_link_handle.setOnDragDetected(e ->  bottom_link_handle.setStyle("-fx-background-color: transparent"));


        leftDragLabel = new Label("");
        leftDragLabel.setMaxWidth(10);
        leftDragLabel.setMinWidth(10);
        leftDragLabel.setMaxHeight(10);
        leftDragLabel.setPadding(new Insets(0));
        //       leftDragLabel.setStyle("-fx-background-color: red");

        closeLabel = new Label();
        closeLabel.setMaxHeight(10);
        closeLabel.setMaxWidth(10);
        closeLabel.setPadding(new Insets(0));

        GridPane labelPane = new GridPane();
        ColumnConstraints columnConstraints = new ColumnConstraints(10);
        columnConstraints.setHgrow(Priority.NEVER);
        labelPane.getColumnConstraints().add(columnConstraints);
        RowConstraints closeRowConstraints = new RowConstraints(10);
        closeRowConstraints.setVgrow(Priority.NEVER);
        RowConstraints moveRowConstraints = new RowConstraints(10);
        moveRowConstraints.setVgrow(Priority.NEVER);
        labelPane.getRowConstraints().addAll(closeRowConstraints, moveRowConstraints);
        labelPane.add(closeLabel, 0, 0); labelPane.add(leftDragLabel, 0, 1);


        //      VBox labelBox = new VBox(closeLabel, leftDragLabel);


        leftDragLabel.setOnMouseEntered(e -> {
            leftDragLabel.setStyle("-fx-background-color: grey; -fx-background-radius: 0 0 0 5;");
            closeLabel.setStyle("-fx-background-color: black; -fx-background-radius: 5 0 0 0;");
//            leftDragLabel.setCursor(Cursor.MOVE);
        });
        leftDragLabel.setOnMouseExited(e -> {
            leftDragLabel.setStyle("-fx-background-color: transparent");
            closeLabel.setStyle("-fx-background-color:transparent");
            //           leftDragLabel.setCursor(Cursor.DEFAULT);
        });

        closeLabel.setOnMouseEntered(e -> {
            leftDragLabel.setStyle("-fx-background-color: grey; -fx-background-radius: 0 0 0 5");
            closeLabel.setStyle("-fx-background-color: black; -fx-background-radius: 5 0 0 0");
//            leftDragLabel.setCursor(Cursor.MOVE);
        });
        closeLabel.setOnMouseExited(e -> {
            leftDragLabel.setStyle("-fx-background-color: transparent");
            closeLabel.setStyle("-fx-background-color:transparent");
            //           leftDragLabel.setCursor(Cursor.DEFAULT);
        });


        BoxedDRTA formulaBox = newFormulaBoxedDRTA();
        RightDragResizer.makeResizable(formulaBox.getRTA());

        VBox centerBox = new VBox(top_link_handle, formulaBox.getRTA(), bottom_link_handle);
        centerBox.setAlignment(Pos.CENTER);

        HBox mainBox = new HBox(labelPane, centerBox);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setMargin(labelPane, new Insets(10, 0, 0, 0));



        /*
        HBox centerBox = new HBox(labelPane, formulaBox.getRTA());
        centerBox.setHgrow(formulaBox.getRTA(), Priority.ALWAYS);

        VBox mainBox = new VBox(top_link_handle, centerBox, bottom_link_handle);
        mainBox.setAlignment(Pos.CENTER);
         */


        self.getChildren().addAll(mainBox);
        self.setBottomAnchor(mainBox, 0.0); self.setLeftAnchor(mainBox, 0.0); self.setTopAnchor(mainBox, 0.0); self.setRightAnchor(mainBox, 0.0);

        setId(UUID.randomUUID().toString());
        initialize();
    }

    private void initialize() {
        buildNodeDragHandlers();
        buildLinkDragHandlers();

        top_link_handle.setOnDragDetected(mLinkHandleDragDetected);
        bottom_link_handle.setOnDragDetected(mLinkHandleDragDetected);

        top_link_handle.setOnDragDropped(mLinkHandleDragDropped);
        bottom_link_handle.setOnDragDropped(mLinkHandleDragDropped);

        mDragLink = new NodeLink();
        mDragLink.setVisible(false);

        parentProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                right_pane = (AnchorPane) getParent();
            }
        });
    }

    public void registerLink(String linkId) {mLinkIds.add(linkId); }

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

                relocateToGridPoint2(
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
                self.setCursor(Cursor.DEFAULT);


            }
        };


        //close button click
        closeLabel.setOnMouseClicked( new EventHandler <MouseEvent> () {
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



        leftDragLabel.setOnDragDetected ( new EventHandler <MouseEvent> () {

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

    private void buildLinkDragHandlers() {

        mLinkHandleDragDetected = new EventHandler <MouseEvent> () {

            @Override
            public void handle(MouseEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                getParent().setOnDragOver(mContextLinkDragOver);
                getParent().setOnDragDropped(mContextLinkDragDropped);

                //Set up user-draggable link
                right_pane.getChildren().add(0,mDragLink);

                mDragLink.setVisible(false);

                Point2D p = new Point2D(
                        getLayoutX() + (getWidth() / 2.0),
                        getLayoutY() + (getHeight() / 2.0)
                );

                mDragLink.setStart(p);

                //Drag content code
                ClipboardContent content = new ClipboardContent();
                DragContainer container = new DragContainer();


                //pass the UUID of the source node for later lookup
                container.addData("source", getId());

                content.put(DragContainer.AddLink, container);

                startDragAndDrop (TransferMode.ANY).setContent(content);

                event.consume();
            }
        };

        mLinkHandleDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {

                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //get the drag data.  If it's null, abort.
                //This isn't the drag event we're looking for.
                DragContainer container =
                        (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);

                if (container == null)
                    return;

                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                mDragLink.setVisible(false);
                right_pane.getChildren().remove(0);

                AnchorPane link_handle = (AnchorPane) event.getSource();

                ClipboardContent content = new ClipboardContent();

                //pass the UUID of the target node for later lookup
                container.addData("target", getId());

                content.put(DragContainer.AddLink, container);

                event.getDragboard().setContent(content);
                event.setDropCompleted(true);
                event.consume();
            }
        };

        mContextLinkDragOver = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);

                //Relocate end of user-draggable link
                if (!mDragLink.isVisible())
                    mDragLink.setVisible(true);

                mDragLink.setEnd(new Point2D(event.getX(), event.getY()));

                event.consume();

            }
        };

        //drop event for link creation
        mContextLinkDragDropped = new EventHandler <DragEvent> () {

            @Override
            public void handle(DragEvent event) {


                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);

                //hide the draggable NodeLink and remove it from the right-hand AnchorPane's children
                mDragLink.setVisible(false);
                right_pane.getChildren().remove(0);

                event.setDropCompleted(true);
                event.consume();
            }

        };

    }


    public void relocateToGridPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);


        double localY = Math.round((localCoords.getY() - 8) / 24.0) * 24.0 + 1;

        relocate (
                (int) localCoords.getX() - 36,
                //         (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),
                (int) (localY - (getBoundsInLocal().getHeight() / 2 ))


                //             (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2 ))
        );
    }


    // I don't understand why both this and the following method are required to drop on line - should be same w/o offset localY??
    public void relocateToGridPoint2 (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);


        double localY = Math.round((localCoords.getY() + 4) / 24.0) * 24.0 - 8;

        relocate (
                (int) localCoords.getX(),
                //        (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),
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
                (int) localCoords.getX(),
                //           (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),
                (int) ((localCoords.getY() - (getBoundsInLocal().getHeight()) / 2 ))
        );
    }

    private BoxedDRTA newFormulaBoxedDRTA() {
        BoxedDRTA boxedDRTA = new BoxedDRTA();
        DecoratedRTA drta = boxedDRTA.getDRTA();
        drta.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea rta = boxedDRTA.getRTA();
        rta.setMaxHeight(24);
        rta.setMinHeight(24);
        rta.setPrefWidth(36);
     //   rta.getStylesheets().add("slappDerivation.css");
       rta.getStylesheets().add("formulaBox.css");
        rta.setPromptText("X");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
//                editorInFocus(drta, ControlType.FIELD);             **needs to be revived once integrated into SLAPP**
            }
        });
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        return boxedDRTA;
    }



}
