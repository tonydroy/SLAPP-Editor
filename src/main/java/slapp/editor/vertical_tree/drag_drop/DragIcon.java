package slapp.editor.vertical_tree.drag_drop;

import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class DragIcon extends AnchorPane {

//    AnchorPane root_pane;



    private DragIconType mType = null;

    public DragIcon() {
        this.getStylesheets().add("/drag_drop.css");
        this.getStyleClass().add("dragicon");
//        this.setPrefHeight(64); this.setPrefWidth(64);
//        this.setMaxWidth(64); this.setMinWidth(64); this.setMaxHeight(64); this.setMinHeight(64);
    }


    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
                (int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
                (int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
        );
    }

    public DragIconType getType () { return mType; }

    public void setType (DragIconType type) {

        mType = type;

        getStyleClass().clear();
        getStyleClass().add("dragicon");

        //added because the cubic curve will persist into other icons
        if (this.getChildren().size() > 0)
            getChildren().clear();

        switch (mType) {

            case text_field:
                Rectangle rectangle = new Rectangle(60, 24);
                rectangle.setFill(Color.LIGHTBLUE);
                rectangle.setStrokeWidth(1.5);
                rectangle.setStroke(Color.BLACK);
                rectangle.setArcHeight(8); rectangle.setArcWidth(8);

                Line line = new Line(0,0,0,17);
                line.setStrokeWidth(1.0);
                Line topLine = new Line(0, 0, 8, 0);
                Line bottomLine = new Line(0, 17, 8, 17);

                AnchorPane fieldPane = new AnchorPane(rectangle, line, topLine, bottomLine);
                fieldPane.setLeftAnchor(line, 8.0);
                fieldPane.setTopAnchor(line, 3.0);

                fieldPane.setLeftAnchor(topLine, 4.0);
                fieldPane.setTopAnchor(topLine, 3.0);

                fieldPane.setLeftAnchor(bottomLine, 4.0);
                fieldPane.setTopAnchor(bottomLine, 20.0);

                this.setPrefHeight(64); this.setPrefWidth(64);
                this.setMaxWidth(64); this.setMinWidth(64); this.setMaxHeight(64); this.setMinHeight(64);

                getChildren().add(fieldPane);
                this.setLeftAnchor(fieldPane, 2.0);
                this.setTopAnchor(fieldPane, 20.0);
                break;

            case bracket:
                Pane brackPane = new Pane();
                brackPane.setMinWidth(8.0); brackPane.setMaxWidth(8.0);
                brackPane.setMinHeight(54); brackPane.setMaxHeight(54);
                brackPane.setStyle("-fx-border-width: 1.5 0.0 1.5 1.5; -fx-border-color: black; -fx-border-radius: 5 0 0 5");

                Rectangle bracketRec = new Rectangle(24, 64);
                bracketRec.setFill(Color.LIGHTBLUE);
                bracketRec.setStroke(Color.TRANSPARENT);
                bracketRec.setArcHeight(8); bracketRec.setArcWidth(8);

                AnchorPane bracketPane = new AnchorPane(bracketRec, brackPane);
                bracketPane.setLeftAnchor(brackPane, 8.0);
                bracketPane.setTopAnchor(brackPane, 4.0);

                this.setPrefHeight(64); this.setPrefWidth(64);
                this.setMaxWidth(64); this.setMinWidth(64); this.setMaxHeight(64); this.setMinHeight(64);

                getChildren().add(bracketPane);
                this.setLeftAnchor(bracketPane, 20.0);
                break;

            case dashed_line:

                Rectangle dashLineRec = new Rectangle(64, 24);
                dashLineRec.setFill(Color.LIGHTBLUE);
                dashLineRec.setStroke(Color.TRANSPARENT);
                dashLineRec.setArcHeight(8); dashLineRec.setArcWidth(8);

                Line dashedLine = new Line(0,0,60,0);
                dashedLine.setStrokeWidth(1.5);
                dashedLine.getStrokeDashArray().addAll(5.0,5.0);

                AnchorPane dashLinePane = new AnchorPane(dashLineRec, dashedLine);
                dashLinePane.setLeftAnchor(dashedLine, 4.0);
                dashLinePane.setTopAnchor(dashedLine, 12.0);

                this.setPrefHeight(64); this.setPrefWidth(64);
                this.setMaxWidth(64); this.setMinWidth(64); this.setMaxHeight(64); this.setMinHeight(64);
                getChildren().add(dashLinePane);
                this.setTopAnchor(dashLinePane, 20.0);
                break;

            case grey:
                this.setPrefHeight(64); this.setPrefWidth(64);
                this.setMaxWidth(64); this.setMinWidth(64); this.setMaxHeight(64); this.setMinHeight(64);
                getStyleClass().add("icon-grey");
                break;

            case purple:
                this.setPrefHeight(64); this.setPrefWidth(64);
                this.setMaxWidth(64); this.setMinWidth(64); this.setMaxHeight(64); this.setMinHeight(64);
                getStyleClass().add("icon-purple");
                break;

            case yellow:
                this.setPrefHeight(64); this.setPrefWidth(64);
                this.setMaxWidth(64); this.setMinWidth(64); this.setMaxHeight(64); this.setMinHeight(64);
                getStyleClass().add("icon-yellow");
                break;

            case black:
                this.setPrefHeight(64); this.setPrefWidth(64);
                this.setMaxWidth(64); this.setMinWidth(64); this.setMaxHeight(64); this.setMinHeight(64);
                getStyleClass().add("icon-black");
                break;

            default:
                break;
        }
    }
}