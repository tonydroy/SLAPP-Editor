package slapp.editor.vertical_tree.drag_drop;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import slapp.editor.EditorAlerts;
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

//    private ClickableNodeLink mDragLink = null;
    private NodeLink mDragLink = null;
    private AnchorPane right_pane = null;
    private final List<String> mLinkIds = new ArrayList<>();

    private DragIconType mType = DragIconType.dashed_line;

    private Point2D mDragOffset = new Point2D (0.0, 0.0);

    private final FormulaBox self;

    private BoxedDRTA formulaBox;

    private HBox mainBox;
    private GridPane labelPane;
    private VBox centerBox;
    private VBox middleBox;
    private AnchorPane linesPane = new AnchorPane();



    Rectangle oval = new Rectangle();
    EventHandler circleKeyFilter;
    int circleStage = 0;
    Label[] circleMarkers;
    Double[] circleXAnchors = new Double[2];
    EventHandler ulineKeyFilter;
    int ulineStage = 0;
    Label[] ulineMarkers;
    Double[] ulineXAnchors = new Double[2];
    double ulineSpace = 3.0;
    List<Integer> baseline = new ArrayList<>();







    FormulaBox() {
        self = this;
        circleMarkers = new Label[]{new Label("|"), new Label("|")};
        ulineMarkers = new Label[]{new Label("|"), new Label("|")};



        top_link_handle = new AnchorPane();
        top_link_handle.setPrefHeight(9);
        top_link_handle.setMaxWidth(30);
        top_link_handle.setOnMouseEntered(e -> top_link_handle.setStyle("-fx-background-color: grey; -fx-background-radius: 5 5 0 0") );
        top_link_handle.setOnDragOver(e -> top_link_handle.setStyle("-fx-background-color: grey; -fx-background-radius: 5 5 0 0") );
        top_link_handle.setOnMouseExited(e ->  top_link_handle.setStyle("-fx-background-color: transparent") );
        top_link_handle.setOnDragExited(e ->  top_link_handle.setStyle("-fx-background-color: transparent" ));

        bottom_link_handle = new AnchorPane();
        bottom_link_handle.setPrefHeight(9);
        bottom_link_handle.setMaxWidth(30);
        bottom_link_handle.setStyle("-fx-background-radius: 0 0 5 5");
        bottom_link_handle.setOnMouseEntered(e -> bottom_link_handle.setStyle("-fx-background-color: grey; -fx-background-radius: 0 0 5 5"));
        bottom_link_handle.setOnDragOver(e -> bottom_link_handle.setStyle("-fx-background-color: grey; -fx-background-radius: 0 0 5 5") );
        bottom_link_handle.setOnMouseExited(e -> bottom_link_handle.setStyle("fx-background-color: transparent"));
        bottom_link_handle.setOnDragExited(e ->  bottom_link_handle.setStyle("-fx-background-color: transparent"));



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

        labelPane = new GridPane();
        ColumnConstraints columnConstraints = new ColumnConstraints(10);
        columnConstraints.setHgrow(Priority.NEVER);
        labelPane.getColumnConstraints().add(columnConstraints);
        RowConstraints closeRowConstraints = new RowConstraints(10);
        closeRowConstraints.setVgrow(Priority.NEVER);
        RowConstraints moveRowConstraints = new RowConstraints(10);
        moveRowConstraints.setVgrow(Priority.NEVER);
        labelPane.getRowConstraints().addAll(closeRowConstraints, moveRowConstraints);
        labelPane.add(closeLabel, 0, 0); labelPane.add(leftDragLabel, 0, 1);


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


        formulaBox = newFormulaBoxedDRTA();
        RightDragResizer.makeResizable(formulaBox.getRTA());

        middleBox = new VBox(formulaBox.getBoxedRTA(), linesPane);


        centerBox = new VBox(top_link_handle, middleBox, bottom_link_handle);
        centerBox.setAlignment(Pos.CENTER);

        mainBox = new HBox(labelPane, centerBox);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setMargin(labelPane, new Insets(10, 0, 0, 0));
 //       mainBox.setMargin(annotationField, new Insets(0,0, 14, 0));

        self.getChildren().addAll(mainBox);
//        self.setBottomAnchor(mainBox, 0.0);
        self.setLeftAnchor(mainBox, 0.0);
        self.setTopAnchor(mainBox, 0.0);
 //       self.setRightAnchor(mainBox, 0.0);

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

  //      mDragLink = new ClickableNodeLink();
        mDragLink = new NodeLink();
        mDragLink.setVisible(false);

        parentProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                right_pane = (AnchorPane) getParent();
            }
        });

        circleKeyFilter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                RichTextArea rta = formulaBox.getRTA();
                KeyCode code = e.getCode();
                Bounds rtaBounds = self.sceneToLocal(rta.localToScene(rta.getBoundsInLocal()));
                if (code == KeyCode.ESCAPE) {
                    if (circleStage <= 2) {
                        Bounds caretBounds = ((RichTextAreaSkin) formulaBox.getRTA().getSkin()).getCaretPosition();
                        Bounds newCaretBounds = rta.sceneToLocal(caretBounds);
                        double xAnchor = newCaretBounds.getMaxX() + rtaBounds.getMinX() - 1.0;
                        double yAnchor = newCaretBounds.getMaxY() + rtaBounds.getMinY();
                        if (circleStage < 2) {
                            Label marker = circleMarkers[circleStage];
                            circleXAnchors[circleStage] = xAnchor;
                            self.getChildren().add(marker);
                            self.setLeftAnchor(marker, xAnchor);
                            self.setTopAnchor(marker, yAnchor);
                            circleStage++;
                        } else {
                            double minX = Math.min(circleXAnchors[0], circleXAnchors[1]);
                            double maxX = Math.max(circleXAnchors[0], circleXAnchors[1]);
                            self.getChildren().removeAll(circleMarkers);
                            self.getChildren().add(oval);
                            oval.setWidth(maxX - minX);

                            oval.setHeight(rtaBounds.getHeight() - 6.0);
                            oval.setStyle("-fx-fill: transparent; -fx-stroke: black; -fx-stroke-width: 1;");
                            oval.setArcHeight(rtaBounds.getHeight() - 6.0);
                            oval.setArcWidth((maxX - minX));
                            self.setLeftAnchor(oval, minX);
                            self.setTopAnchor(oval, rtaBounds.getMinY() + 2.0 );
                            circleStage++;
                        }
                    } else {
                        EditorAlerts.fleetingPopup("Text field has at most one circle annotation.");
                    }
                    e.consume();
                }
            }
        };

        ulineKeyFilter = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                RichTextArea rta = formulaBox.getRTA();
                KeyCode code = e.getCode();
                Bounds rtaBounds = self.sceneToLocal(rta.localToScene(rta.getBoundsInLocal()));
                if (code == KeyCode.ESCAPE) {
                    if (ulineStage <= 2) {
                        Bounds caretBounds = ((RichTextAreaSkin) formulaBox.getRTA().getSkin()).getCaretPosition();
                        Bounds newCaretBounds = rta.sceneToLocal(caretBounds);
                        double xAnchor = newCaretBounds.getMaxX() + rtaBounds.getMinX() - 1.0;
                        double yAnchor = newCaretBounds.getMaxY() + rtaBounds.getMinY();
                        if (ulineStage < 2) {
                            Label marker = ulineMarkers[ulineStage];
                            ulineXAnchors[ulineStage] = xAnchor;
                            self.getChildren().add(marker);
                            self.setLeftAnchor(marker, xAnchor);
                            self.setTopAnchor(marker, yAnchor);
                            ulineStage++;
                        } else {
                            double minX = Math.min(ulineXAnchors[0], ulineXAnchors[1]);
                            double maxX = Math.max(ulineXAnchors[0], ulineXAnchors[1]);
                            self.getChildren().removeAll(ulineMarkers);
                            setLine(minX - rtaBounds.getMinX(), maxX - rtaBounds.getMinX());
                            ulineStage = 0;
                        }
                    } else {
                        throw new IndexOutOfBoundsException("Index out of bounds: ulineKeyFilter.");
                    }
                    e.consume();
                }
            }
        };


    }

    private void setLine(double startX, double endX) {
        //make sure there is a baseline for new line
        int intStartX = (int) Math.round(startX);
        int intEndX = (int) Math.round(endX);

        for (int i = baseline.size(); i <= intEndX; i++) {
            baseline.add(-((int) ulineSpace));
        }

        //find base for line
        int maxBase = 0;
        for (int i = intStartX; i <= intEndX; i++) {
            if (baseline.get(i) > maxBase) {
                maxBase = baseline.get(i);
            }
        }

        //get yPosition of new line and update baseline
        double yPos = (double) maxBase + ulineSpace;
        for (int i = intStartX; i <= intEndX; i++) {
            baseline.set(i, (int) Math.round(yPos));
        }

        //add line to linesPane
        Line line = new Line(0, 0, endX - startX, 0);
        linesPane.getChildren().add(line);
        linesPane.setLeftAnchor(line, startX);
        linesPane.setBottomAnchor(line, yPos);
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
                        getLayoutY() + (middleBox.getHeight()/2) + 9

 //                       getLayoutY() + (getHeight() / 2.0)
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

     //           bottom_link_handle.setStyle("-fx-background-color: transparent");
     //           top_link_handle.setStyle("-fx-background-color: transparent");

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
        double localY = Math.round((localCoords.getY() - 16) / 24.0) * 24.0;

        relocate (
                (int) localCoords.getX() - 36,
                (int) (localY - 7 )
        );
    }


    // I don't understand why both this and the following method are required to drop on line - should be same w/o offset localY??
    public void relocateToGridPoint2 (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);
        double localY = Math.round((localCoords.getY() - 16) / 24.0) * 24.0 ;

        relocate (
                (int) localCoords.getX(),
                (int) (localY - 7)
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
        rta.setPromptText("");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
//                editorInFocus(drta, ControlType.FIELD);             **needs to be revived once integrated into SLAPP**
            }
        });
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        return boxedDRTA;
    }

    void processBoxRequest(boolean add) {
        if (add) {
//            formulaBox.getRTA().getStylesheets().clear();
//            formulaBox.getRTA().getStylesheets().add("boldFormulaBox.css");
            middleBox.setStyle("-fx-border-color: black; -fx-border-width: 1 1 1 1");
        } else {
//            formulaBox.getRTA().getStylesheets().clear();
//            formulaBox.getRTA().getStylesheets().add("formulaBox.css");
            middleBox.setStyle("-fx-border-width: 0 0 0 0");

        }
    }

    void processStarRequest(boolean add) {
        if (add) {
            Label star = new Label("\u2605");
            mainBox.getChildren().clear();
            mainBox.getChildren().addAll(labelPane, centerBox, star);
            mainBox.setMargin(star, new Insets(0,0, 14, 0));

        } else {
            mainBox.getChildren().clear();
            mainBox.getChildren().addAll(labelPane, centerBox);
        }
    }

    void processAnnotationRequest(boolean add) {
        if (add) {
            TextField field = new TextField();
            field.setPrefWidth(28);
            field.setPrefHeight(15);
            field.setFont(new Font("Noto Sans", 10));
            field.setPadding(new Insets(0));
            mainBox.getChildren().clear();
            mainBox.getChildren().addAll(labelPane, centerBox, field);
            mainBox.setMargin(field, new Insets(0,0, 14, 0));

        } else {
            mainBox.getChildren().clear();
            mainBox.getChildren().addAll(labelPane, centerBox);
        }
    }



    void processCircleRequest(boolean add) {
        if (add) {
            RichTextArea rta = formulaBox.getRTA();
            rta.requestFocus();
            self.addEventFilter(KeyEvent.KEY_PRESSED, circleKeyFilter);
        } else {
            self.removeEventFilter(KeyEvent.KEY_PRESSED, circleKeyFilter);
            self.getChildren().removeAll(circleMarkers[0], circleMarkers[1], oval);
            circleStage = 0;
        }
    }
    void processUnderlineRequest(boolean add) {
        if (add) {
            RichTextArea rta = formulaBox.getRTA();
            rta.requestFocus();
            self.addEventFilter(KeyEvent.KEY_PRESSED, ulineKeyFilter);
        } else {
            self.removeEventFilter(KeyEvent.KEY_PRESSED, ulineKeyFilter);
            self.getChildren().removeAll(ulineMarkers[0], ulineMarkers[1]);
            linesPane.getChildren().clear();
            baseline.clear();
        }
    }

    public VBox getMiddleBox() {
        return middleBox;
    }
}