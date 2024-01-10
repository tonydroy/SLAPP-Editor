package slapp.editor.vertical_tree.drag_drop;


import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurve;

import java.util.UUID;

public class NodeLink extends Pane {

    CubicCurve node_link;

    private final DoubleProperty mControlOffsetX = new SimpleDoubleProperty();
    private final DoubleProperty mControlOffsetY = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY1 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionX2 = new SimpleDoubleProperty();
    private final DoubleProperty mControlDirectionY2 = new SimpleDoubleProperty();

    public NodeLink() {
        this.getStylesheets().add("/drag_drop.css");

        node_link = new CubicCurve();
        node_link.setControlX2(50.0); node_link.setControlY1(10.0); node_link.setControlY2(10.0); node_link.setEndX(10.0); node_link.setFill(Paint.valueOf("#1f93ff00")); node_link.setStroke(Color.BLACK);
        this.getChildren().add(node_link);

        //provide a universally unique identifier for this object
        setId(UUID.randomUUID().toString());


        initialize();
    }


    private void initialize() {

        mControlOffsetX.set(100.0);
        mControlOffsetY.set(50.0);


        mControlDirectionX1.bind(new When (
                node_link.startXProperty().greaterThan(node_link.endXProperty()))
                .then(-1.0).otherwise(1.0));

        mControlDirectionX2.bind(new When (
                node_link.startXProperty().greaterThan(node_link.endXProperty()))
                .then(1.0).otherwise(-1.0));


        node_link.controlX1Property().bind(
                Bindings.add(
                        node_link.startXProperty(), mControlOffsetX.multiply(mControlDirectionX1)
                )
        );

        node_link.controlX2Property().bind(
                Bindings.add(
                        node_link.endXProperty(), mControlOffsetX.multiply(mControlDirectionX2)
                )
        );

        node_link.controlY1Property().bind(
                Bindings.add(
                        node_link.startYProperty(), mControlOffsetY.multiply(mControlDirectionY1)
                )
        );

        node_link.controlY2Property().bind(
                Bindings.add(
                        node_link.endYProperty(), mControlOffsetY.multiply(mControlDirectionY2)
                )
        );


    }


    public void setStart(Point2D startPoint) {

        node_link.setStartX(startPoint.getX());
        node_link.setStartY(startPoint.getY());
    }

    public void setEnd(Point2D endPoint) {

        node_link.setEndX(endPoint.getX());
        node_link.setEndY(endPoint.getY());
    }


    public void bindEnds (DraggableNode source, DraggableNode target) {
        node_link.startXProperty().bind(
                Bindings.add(source.layoutXProperty(), (source.getWidth() / 2.0)));

        node_link.startYProperty().bind(
                Bindings.add(source.layoutYProperty(), (source.getWidth() / 2.0)));

        node_link.endXProperty().bind(
                Bindings.add(target.layoutXProperty(), (target.getWidth() / 2.0)));

        node_link.endYProperty().bind(
                Bindings.add(target.layoutYProperty(), (target.getWidth() / 2.0)));

        source.registerLink (getId());
        target.registerLink (getId());
    }

}