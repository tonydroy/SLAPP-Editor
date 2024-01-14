package slapp.editor.vertical_tree.drag_drop;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

public class VerticalBracket extends AnchorPane {
    private Label topDragLabel;
    private Label closeLabel;

    private EventHandler<DragEvent> mContextDragOver;
    private EventHandler <DragEvent> mContextDragDropped;

    private DragIconType mType = DragIconType.bracket;

    private Point2D mDragOffset = new Point2D (0.0, 0.0);

    private final VerticalBracket self;



    VerticalBracket() {
        self = this;

        topDragLabel = new Label("");
        topDragLabel.setMaxHeight(10);
        topDragLabel.setMinHeight(10);
        topDragLabel.setMaxWidth(12);
        topDragLabel.setPadding(new Insets(0));
        //       bottomDragLabel.setStyle("-fx-background-color: red");

        closeLabel = new Label();
        closeLabel.setMaxHeight(10);
        closeLabel.setMinHeight(10);
        closeLabel.setMaxWidth(12);
        closeLabel.setPadding(new Insets(0));

        GridPane labelPane = new GridPane();
        RowConstraints rowConstraints = new RowConstraints(10);
        rowConstraints.setVgrow(Priority.NEVER);
        labelPane.getRowConstraints().add(rowConstraints);
        ColumnConstraints closeColumnConstraints = new ColumnConstraints(12);
        closeColumnConstraints.setHgrow(Priority.NEVER);
        ColumnConstraints moveColumnConstraints = new ColumnConstraints(12);
        moveColumnConstraints.setHgrow(Priority.SOMETIMES);
        labelPane.getColumnConstraints().addAll(moveColumnConstraints, closeColumnConstraints);
        labelPane.add(topDragLabel, 0, 0); labelPane.add(closeLabel, 1, 0);



        topDragLabel.setOnMouseEntered(e -> {
            topDragLabel.setStyle("-fx-background-color: grey; -fx-background-radius: 8 0 0 8;");
            closeLabel.setStyle("-fx-background-color: black; -fx-background-radius: 0 8 8 0;");
//            leftDragLabel.setCursor(Cursor.MOVE);
        });
        topDragLabel.setOnMouseExited(e -> {
            topDragLabel.setStyle("-fx-background-color: transparent");
            closeLabel.setStyle("-fx-background-color:transparent");
            //           leftDragLabel.setCursor(Cursor.DEFAULT);
        });

        closeLabel.setOnMouseEntered(e -> {
            topDragLabel.setStyle("-fx-background-color: grey; -fx-background-radius: 8 0 0 8");
            closeLabel.setStyle("-fx-background-color: black; -fx-background-radius: 0 8 8 0");
//            leftDragLabel.setCursor(Cursor.MOVE);
        });
        closeLabel.setOnMouseExited(e -> {
            topDragLabel.setStyle("-fx-background-color: transparent");
            closeLabel.setStyle("-fx-background-color:transparent");
            //           leftDragLabel.setCursor(Cursor.DEFAULT);
        });


        Pane brackPane = new Pane();
        brackPane.setMinWidth(8.0); brackPane.setMaxWidth(8.0);
        brackPane.setMinHeight(64); brackPane.setMaxHeight(64);
        brackPane.setStyle("-fx-border-width: 1.5 0.0 1.5 1.5; -fx-border-color: black; -fx-border-radius: 5 0 0 5");

        AnchorPane mainPane = new AnchorPane();
        mainPane.setMinWidth(24.0);
        mainPane.setPrefWidth(24.0);
        mainPane.setPrefHeight(48);

        //       mainPane.setStyle("-fx-border-color: red; fx-border-width: 1 1 1 1");

        mainPane.getChildren().add(brackPane);
        //       linesBox.setStyle("-fx-border-color: blue; -fx-border-width: 2 2 2 2;");
        mainPane.setLeftAnchor(brackPane, 8.0);


        BottomDragResizer.makeResizable(mainPane);
        brackPane.minHeightProperty().bind(mainPane.heightProperty());

        VBox mainBox = new VBox(labelPane, mainPane);
        self.getChildren().addAll(mainBox);
        mainBox.setVgrow(mainPane, Priority.ALWAYS);
        self.setBottomAnchor(mainBox, 0.0); self.setLeftAnchor(mainBox, 0.0); self.setTopAnchor(mainBox, 0.0); self.setRightAnchor(mainBox, 0.0);

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
            }

        });



        topDragLabel.setOnDragDetected (new EventHandler <MouseEvent> () {

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


        double localY = Math.round((localCoords.getY() - 20)  / 24.0) * 24.0;

        relocate (
                (int) localCoords.getX() - 12,
                //         (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),

                (int) localY - 20
 //               (int) (localY - (getBoundsInLocal().getHeight() / 2 ))


                //             (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2 ))
        );
    }


    // I don't understand why both this and the following method are required to drop on line - should be same w/o offset localY??
    public void relocateToGridPoint2 (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);


        double localY = Math.round((localCoords.getY() + 24) / 24.0) * 24.0 ;

        relocate (
                (int) localCoords.getX() - 12,
                //        (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),
                (int) localY - 20


                //             (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2 ))
        );
    }




    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);


        //       double localY = Math.round(localCoords.getY() / 24.0) * 24.0;

        relocate (
  //              (int) localCoords.getX(),
                           (int) ((localCoords.getX() - (getBoundsInLocal().getWidth()) / 2)),

                (int) localCoords.getY()
  //              (int) ((localCoords.getY() - (getBoundsInLocal().getHeight()) / 2 ))
        );
    }



}
