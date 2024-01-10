package slapp.editor.vertical_tree.drag_drop;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

public class CubicCurveDemo extends AnchorPane {

    /**
     * FXML initialization requirement
     */

    private Circle curveEnd;
    private Circle curveStart;
    private Circle curveC1;
    private Circle curveC2;
    private Line mLt_start_c1;
    private Line mLt_c2_end;
    private CubicCurve mCurve;


    private Point2D mDragOffset = new Point2D (0.0, 0.0);

    public CubicCurveDemo() {
        this.setPrefHeight(96.0);
        this.setPrefWidth(80.0);
        this.getStylesheets().add("/drag_drop.css");

        mLt_c2_end = new Line();
        mLt_c2_end.setEndX(100.0); mLt_c2_end.setFill(Paint.valueOf("#eb5656")); mLt_c2_end.setStartX(-100.0); mLt_c2_end.setStroke(Paint.valueOf("#d03333"));

        mLt_start_c1 = new Line();
        mLt_start_c1.setEndX(100.0); mLt_start_c1.setFill(Paint.valueOf("#296ec9")); mLt_start_c1.setStartX(-100.0); mLt_start_c1.setStroke(Paint.valueOf("#2967c3"));

        mCurve = new CubicCurve();
        mCurve.setControlX1(-50.0); mCurve.setControlX2(50.0); mCurve.setControlY1(-100.0); mCurve.setControlY2(100.0); mCurve.setEndX(150.0); mCurve.setFill(Paint.valueOf("#1f93ff")); mCurve.setStartX(-150.0); mCurve.setStroke(Color.BLACK);

        curveStart = new Circle();
        curveStart.setFill(Paint.valueOf("#b5d1eb")); curveStart.setRadius(8.0); curveStart.setStroke(Color.BLACK); curveStart.setStrokeType(StrokeType.INSIDE);
        curveStart.setOnMouseDragged(e -> updateCurveStart(e));

        curveC2 = new Circle();
        curveC2.setFill(Paint.valueOf("#ff2121")); curveC2.setRadius(8.0); curveC2.setStroke(Color.BLACK); curveC2.setStrokeType(StrokeType.INSIDE);
        curveC2.setOnMouseDragged(e -> updateCurveC2(e));

        curveEnd = new Circle();
        curveEnd.setFill(Paint.valueOf("#f29f9f")); curveEnd.setRadius(8.0); curveEnd.setStroke(Color.BLACK); curveEnd.setStrokeType(StrokeType.INSIDE);
        curveEnd.setOnMouseDragged(e -> updateCurveEnd(e));

         curveC1 = new Circle();
         curveC1.setFill(Paint.valueOf("#2197ff")); curveC1.setRadius(8.0); curveC1.setStroke(Color.BLACK); curveC1.setStrokeType(StrokeType.INSIDE);
         curveC1.setOnMouseDragged(e -> updateCurveC1(e));

         Group group = new Group(mLt_c2_end, mLt_start_c1, mCurve, curveStart, curveC2, curveEnd, curveC1 );
         group.setLayoutX(80.0); group.setLayoutY(130.0);

         Pane pane = new Pane(group);
         pane.setPrefHeight(200); pane.setPrefWidth(200);

         this.getChildren().add(pane);



        initialize();
    }



    private void initialize() {

        initCurves();

    }


    private void labelMouseOver() {
        Background b = new Background(new BackgroundFill(null, null, null));

    }

    public void relocateToPoint (Point2D p) {

        //relocates the object to a point that has been converted to
        //scene coordinates
        Point2D localCoords = getParent().sceneToLocal(p);

        relocate (
                (int) (localCoords.getX() - mDragOffset.getX()),
                (int) (localCoords.getY() - mDragOffset.getY())
        );
    }

    private void initCurves() {

        // bind control lines to circle centers
        mLt_start_c1.startXProperty().bind(mCurve.startXProperty());
        mLt_start_c1.startYProperty().bind(mCurve.startYProperty());

        mLt_start_c1.endXProperty().bind(mCurve.controlX1Property());
        mLt_start_c1.endYProperty().bind(mCurve.controlY1Property());

        mLt_c2_end.startXProperty().bind(mCurve.controlX2Property());
        mLt_c2_end.startYProperty().bind(mCurve.controlY2Property());

        mLt_c2_end.endXProperty().bind(mCurve.endXProperty());
        mLt_c2_end.endYProperty().bind(mCurve.endYProperty());

        // bind curve to circle centers
        mCurve.startXProperty().bind(curveStart.centerXProperty());
        mCurve.startYProperty().bind(curveStart.centerYProperty());

        mCurve.controlX1Property().bind(curveC1.centerXProperty());
        mCurve.controlY1Property().bind(curveC1.centerYProperty());

        mCurve.controlX2Property().bind(curveC2.centerXProperty());
        mCurve.controlY2Property().bind(curveC2.centerYProperty());

        mCurve.endXProperty().bind(curveEnd.centerXProperty());
        mCurve.endYProperty().bind(curveEnd.centerYProperty());

        curveStart.setCenterX(10.0f);
        curveStart.setCenterY(10.0f);

        curveC1.setCenterX(20.0f);
        //curveC1.centerXProperty().bind(Bindings.add(150.0f, curveStart.centerXProperty()));
        //curveC1.centerYProperty().bind(curveStart.centerYProperty());

        curveC2.setCenterX(50.0f);
        //curveC2.centerXProperty().bind(Bindings.add(-150.0f, curveEnd.centerXProperty()));
        //curveC2.centerYProperty().bind(curveEnd.centerYProperty());

        curveEnd.setCenterX(40.0f);
        curveEnd.setCenterY(40.0f);
    }


    private void updateCurveStart(MouseEvent event) {

        curveStart.setCenterX(event.getX());
        curveStart.setCenterY(event.getY());
    }


    private void updateCurveC1(MouseEvent event) {

        curveC1.setCenterX(event.getX());
        curveC1.setCenterY(event.getY());
    }


    private void updateCurveC2(MouseEvent event) {

        curveC2.setCenterX(event.getX());
        curveC2.setCenterY(event.getY());

    }


    private void updateCurveEnd(MouseEvent event) {

        curveEnd.setCenterX(event.getX());
        curveEnd.setCenterY(event.getY());

    }
}
