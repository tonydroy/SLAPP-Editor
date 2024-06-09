package slapp.editor;

import javafx.scene.Node;

public class PrintBufferItem {
    Node node;
    double height;
    double width;
    double scale = 1.0;

    PrintBufferItem(Node node, double height, double width) {
        this.node = node;
        this.height = height;
        this.width = width;
    }
    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
