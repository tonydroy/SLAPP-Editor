package slapp.editor.vertical_tree.drag_drop;


import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;

public class RootLayout extends AnchorPane {
    SplitPane base_pane;
    ScrollPane scroll_pane;
    AnchorPane right_pane;
    HBox left_pane;


    EventHandler boxClickFilter;
    EventHandler starClickFilter;
    EventHandler annotationClickFilter;
    EventHandler circleClickFilter;
    EventHandler underlineClickFilter;
    ToggleButton boxToggle;
    ToggleButton starToggle;
    ToggleButton annotationToggle;
    Button annotationPlus;
    Button annotationMinus;
    HBox annotationBox;
    ToggleButton circleToggle;
    ToggleButton underlineToggle;

    private DragIcon mDragOverIcon = null;
    private EventHandler<DragEvent> mIconDragOverRoot = null;
    private EventHandler<DragEvent> mIconDragDropped = null;
    private EventHandler<DragEvent> mIconDragOverRightPane = null;

    public RootLayout() {
        scroll_pane = new ScrollPane();
        left_pane = new HBox();
        scroll_pane.setContent(left_pane);
        right_pane = new AnchorPane();
        base_pane = new SplitPane();
        base_pane.getItems().addAll(left_pane, right_pane);
        base_pane.setOrientation(Orientation.VERTICAL);


        this.getChildren().add(base_pane);






        setupWindow();
        initialize();

    }

    private void setupWindow() {
        RightDragResizer.makeResizable(base_pane);
     //   BottomDragResizer.makeResizable(base_pane);
        left_pane.setStyle("-fx-background-color: #FCFCFC;");

        boxToggle = new ToggleButton();
        boxToggle.setPrefWidth(64);
        boxToggle.setPrefHeight(28);
        Rectangle rectangle = new Rectangle(20,15);
        rectangle.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        HBox boxToggleGraphic = new HBox(rectangle);
        boxToggleGraphic.setAlignment(Pos.CENTER);
   //     FontIcon boxToggleIcon = new FontIcon(LineAwesomeSolid.STOP);
   //     boxToggleIcon.setIconSize(20);
        boxToggle.setGraphic(boxToggleGraphic);
        boxToggle.setTooltip(new Tooltip("Add (left click) or remove (right click) box"));


        starToggle = new ToggleButton();
        starToggle.setPrefWidth(64);
        starToggle.setPrefHeight(28);
        FontIcon starToggleIcon = new FontIcon(LineAwesomeSolid.STAR);
        starToggleIcon.setIconSize(15);
        starToggle.setGraphic(starToggleIcon);
        starToggle.setTooltip(new Tooltip("Add (left click) or remove (right click) star"));

        annotationToggle = new ToggleButton();
        annotationToggle.setPrefWidth(44);
        annotationToggle.setPrefHeight(28);
        AnchorPane boxesPane = new AnchorPane();
        Rectangle bigBox = new Rectangle(15,10);
        bigBox.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        Rectangle littleBox = new Rectangle(7,7);
        littleBox.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        boxesPane.getChildren().addAll(bigBox, littleBox);
        boxesPane.setTopAnchor(bigBox, 10.0);
        boxesPane.setLeftAnchor(littleBox, 15.0);
        boxesPane.setTopAnchor(littleBox, 5.0);
        HBox buttonBox = new HBox(boxesPane);
        buttonBox.setAlignment(Pos.CENTER);
        annotationToggle.setGraphic(buttonBox);
        annotationToggle.setTooltip(new Tooltip("Add (left click) or remove (right click) annotation box"));

        annotationPlus = new Button("+");
        annotationPlus.setFont(new Font(10));
        annotationPlus.setPadding(new Insets(0));
        annotationPlus.setPrefWidth(20); annotationPlus.setPrefHeight(14);
        annotationMinus = new Button("-");
        annotationMinus.setFont(new Font(10));
        annotationMinus.setPadding(new Insets(0));
        annotationMinus.setPrefWidth(20); annotationMinus.setPrefHeight(14);
        VBox annotationButtons = new VBox(annotationPlus, annotationMinus);
        annotationBox = new HBox(annotationToggle, annotationButtons);


        circleToggle = new ToggleButton();
        circleToggle.setPrefWidth(64);
        circleToggle.setPrefHeight(28);
        HBox circlePane = new HBox();
        Circle circleIcon = new Circle(7);
        circleIcon.setStyle("-fx-stroke: black; -fx-stroke-width: 1.5; -fx-fill: transparent;");
        circlePane.getChildren().add(circleIcon);
        circlePane.setAlignment(Pos.CENTER);
        circleToggle.setGraphic(circlePane);
        circleToggle.setTooltip(new Tooltip("Add (left click) or remove (right click) circle"));

        underlineToggle = new ToggleButton();
        underlineToggle.setPrefWidth(64);
        underlineToggle.setPrefHeight(28);
        FontIcon underlineToggleIcon = new FontIcon(LineAwesomeSolid.GRIP_LINES);
        underlineToggleIcon.setIconSize(20);
        underlineToggle.setGraphic(underlineToggleIcon);
        underlineToggle.setTooltip(new Tooltip("Add (left click) or remove (right click) underline"));

        ToggleGroup buttonGroup = new ToggleGroup();
        boxToggle.setToggleGroup(buttonGroup);
        starToggle.setToggleGroup(buttonGroup);
        annotationToggle.setToggleGroup(buttonGroup);
        circleToggle.setToggleGroup(buttonGroup);
        underlineToggle.setToggleGroup(buttonGroup);


        this.getStylesheets().add("/drag_drop.css");
        right_pane.setStyle("-fx-background-color: white," +
                "linear-gradient(from 0.5px 0.0px to 24.5px  0.0px, repeat, #f5f5f5 1%, transparent 5%)," +
                "linear-gradient(from 0.0px 0.5px to  0.0px 24.5px, repeat, #f5f5f5 1%, transparent 5%);");




        this.setMinWidth(200); this.setMinHeight(200);
        this.setBottomAnchor(base_pane, 0.0); this.setTopAnchor(base_pane, 0.0); this.setLeftAnchor(base_pane, 0.0); this.setRightAnchor(base_pane, 0.0);
        scroll_pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); scroll_pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll_pane.setPrefWidth(100); scroll_pane.setMinWidth(100); scroll_pane.setMaxWidth(100);
        scroll_pane.setPadding(new Insets(6,0,0,8));

        left_pane.setSpacing(20);
        left_pane.setAlignment(Pos.CENTER_LEFT);
        left_pane.setMinHeight(32);
        left_pane.setMaxHeight(32);

        base_pane.setResizableWithParent(right_pane, true);


        boxClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<Node> nodesList = right_pane.getChildren();
                for (Node node : nodesList) {
                    if (inHierarchy(event.getPickResult().getIntersectedNode(), node)) {
                        if (node instanceof FormulaBox) {
                            ((FormulaBox) node).processBoxRequest(event.getButton() == MouseButton.PRIMARY);
                            break;
                        }
                    }
                }

            }
        };
        boxToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv)  right_pane.addEventFilter(MouseEvent.MOUSE_PRESSED, boxClickFilter );
            else right_pane.removeEventFilter(MouseEvent.MOUSE_PRESSED, boxClickFilter);
        });

        starClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<Node> nodesList = right_pane.getChildren();
                for (Node node : nodesList) {
                    if (inHierarchy(event.getPickResult().getIntersectedNode(), node)) {
                        if (node instanceof FormulaBox) {
                            ((FormulaBox) node).processStarRequest(event.getButton() == MouseButton.PRIMARY);
                            break;
                        }
                    }
                }

            }
        };
        starToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv)  right_pane.addEventFilter(MouseEvent.MOUSE_PRESSED, starClickFilter );
            else right_pane.removeEventFilter(MouseEvent.MOUSE_PRESSED, starClickFilter);
        });

        annotationClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<Node> nodesList = right_pane.getChildren();
                for (Node node : nodesList) {
                    if (inHierarchy(event.getPickResult().getIntersectedNode(), node)) {
                        if (node instanceof FormulaBox) {
                            ((FormulaBox) node).processAnnotationRequest(event.getButton() == MouseButton.PRIMARY);
                            break;
                        }
                    }
                }

            }
        };
        annotationToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv)  right_pane.addEventFilter(MouseEvent.MOUSE_PRESSED, annotationClickFilter );
            else right_pane.removeEventFilter(MouseEvent.MOUSE_PRESSED, annotationClickFilter);
        });

        annotationPlus.setOnAction(e -> {
            ObservableList<Node> nodesList = right_pane.getChildren();
            for (Node node : nodesList) {
                if (node instanceof FormulaBox) {
                    ((FormulaBox) node).processAnnotationRequest(true);
                }
            }
            annotationToggle.setSelected(false);
        });

        annotationMinus.setOnAction(e -> {
           ObservableList<Node> nodesList = right_pane.getChildren();
           for (Node node : nodesList) {
               if (node instanceof FormulaBox) {
                   ((FormulaBox) node).processAnnotationRequest(false);
               }
           }
            annotationToggle.setSelected(false);
        });

        circleClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<Node> nodesList = right_pane.getChildren();
                for (Node node : nodesList) {
                    if (inHierarchy(event.getPickResult().getIntersectedNode(), node)) {
                        if (node instanceof FormulaBox) {
                            ((FormulaBox) node).processCircleRequest(event.getButton() == MouseButton.PRIMARY);
                            break;
                        }
                    }
                }

            }
        };
        circleToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv)  right_pane.addEventFilter(MouseEvent.MOUSE_PRESSED, circleClickFilter );
            else right_pane.removeEventFilter(MouseEvent.MOUSE_PRESSED, circleClickFilter);
        });

        underlineClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<Node> nodesList = right_pane.getChildren();
                for (Node node : nodesList) {
                    if (inHierarchy(event.getPickResult().getIntersectedNode(), node)) {
                        if (node instanceof FormulaBox) {
                            ((FormulaBox) node).processUnderlineRequest(event.getButton() == MouseButton.PRIMARY);
                            break;
                        }
                    }
                }

            }
        };
        underlineToggle.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv)  right_pane.addEventFilter(MouseEvent.MOUSE_PRESSED, underlineClickFilter );
            else right_pane.removeEventFilter(MouseEvent.MOUSE_PRESSED, underlineClickFilter);
        });







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
        for (int i = 0; i < 4; i++) {

            DragIcon icn = new DragIcon();

            addDragDetection(icn);

            icn.setType(DragIconType.values()[i]);
            left_pane.getChildren().add(icn);
        }
//        controlsBox.getChildren().addAll(boxToggle, starToggle, annotationBox, circleToggle, underlineToggle);
 //       left_pane.setMargin(boxToggle, new Insets(0, 0, 0, 10));
 //       left_pane.setMargin(starToggle, new Insets(5, 0, 0, 10));
 //       left_pane.setMargin(annotationBox, new Insets(5, 0, 0, 10));
 //       left_pane.setMargin(circleToggle, new Insets(5, 0, 0, 10));
 //       left_pane.setMargin(underlineToggle, new Insets(5, 0, 0, 10));


        buildDragHandlers();
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

                        else if (container.getValue("type").equals(DragIconType.bracket.toString())) {
                            VerticalBracket bracket = new VerticalBracket();
                            right_pane.getChildren().add(bracket);
                            Point2D cursorPoint = container.getValue("scene_coords");
                            bracket.relocateToGridPoint(new Point2D(cursorPoint.getX(), cursorPoint.getY()));
                        }

                        else if (container.getValue("type").equals(DragIconType.text_field.toString())) {
                            FormulaBox formulaBox = new FormulaBox();
                            right_pane.getChildren().add(formulaBox);
                            Point2D cursorPoint = container.getValue("scene_coords");
                            formulaBox.relocateToGridPoint(new Point2D(cursorPoint.getX(), cursorPoint.getY()));
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

     //                   //	System.out.println(container.getData());
     //                  NodeLink link = new NodeLink();
                        ClickableNodeLink link = new ClickableNodeLink();


                        //add our link at the top of the rendering order so it's rendered first
                        right_pane.getChildren().add(0,link);

                        FormulaBox source = null;
                        FormulaBox target = null;

                        for (Node n: right_pane.getChildren()) {

                            if (n.getId() == null)
                                continue;

                            if (n.getId().equals(sourceId))
                                source = (FormulaBox) n;

                            if (n.getId().equals(targetId))
                                target = (FormulaBox) n;

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

    public HBox getLeft_pane() {
        return left_pane;
    }



    public ToggleButton getBoxToggle() {
        return boxToggle;
    }

    public ToggleButton getStarToggle() {
        return starToggle;
    }

    public HBox getAnnotationBox() {
        return annotationBox;
    }

    public ToggleButton getCircleToggle() {
        return circleToggle;
    }

    public ToggleButton getUnderlineToggle() {
        return underlineToggle;
    }
}