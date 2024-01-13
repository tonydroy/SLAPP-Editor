package slapp.editor.vertical_tree.drag_drop;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

public class ClickableLine {
    private DoubleProperty startX;
    private DoubleProperty startY;
    private DoubleProperty endX;
    private DoubleProperty endY;
    private final double clickableWidth;

    public ClickableLine(double startX, double startY, double endX, double endY, double clickableWidth) {
        this.startX = new SimpleDoubleProperty(startX);
        this.startY = new SimpleDoubleProperty(startY);
        this.endX = new SimpleDoubleProperty(endX);
        this.endY = new SimpleDoubleProperty(endY);
        this.clickableWidth = clickableWidth;
    }

    //To use:
    //set mouse listeners on lines[0]
    //put both lines[0] and lines[1] on pane
    //other actions (as remove) also on both -- except that endpoint properties are unified through the ClickableLine object

    public Line[] getLinesArray() {
        Line upper = new Line();
        upper.startXProperty().bind(startX);
        upper.startYProperty().bind(startY);
        upper.endXProperty().bind(endX);
        upper.endYProperty().bind(endY);
        upper.setStrokeWidth(clickableWidth);
        upper.setStroke(Color.TRANSPARENT);
        upper.setStrokeLineCap(StrokeLineCap.ROUND);
        upper.setStrokeType(StrokeType.INSIDE);

        Line lower = new Line();
        lower.startXProperty().bind(startX);
        lower.startYProperty().bind(startY);
        lower.endXProperty().bind(endX);
        lower.endYProperty().bind(endY);
        lower.setStrokeType(StrokeType.INSIDE);

        Line[] lines = {upper, lower};
        return lines;
    }



    public double getStartX() {
        return startX.get();
    }

    public DoubleProperty startXProperty() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX.set(startX);
    }

    public double getStartY() {
        return startY.get();
    }

    public DoubleProperty startYProperty() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY.set(startY);
    }

    public double getEndX() {
        return endX.get();
    }

    public DoubleProperty endXProperty() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX.set(endX);
    }

    public double getEndY() {
        return endY.get();
    }

    public DoubleProperty endYProperty() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY.set(endY);
    }
}
