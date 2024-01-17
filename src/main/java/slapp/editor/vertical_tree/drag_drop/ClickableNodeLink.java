package slapp.editor.vertical_tree.drag_drop;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.UUID;

import static javafx.beans.binding.Bindings.add;

public class ClickableNodeLink extends Pane {


    Line node_link;
    Line node_link1;


    public ClickableNodeLink() {
        this.getStylesheets().add("/drag_drop.css");

        node_link = new Line(); node_link1 = new Line();
        this.getChildren().addAll(node_link, node_link1);

        //provide a universally unique identifier for this object
        setId(UUID.randomUUID().toString());

        node_link1.setStrokeWidth(10);
        node_link1.setStroke(Color.GREEN);

        node_link1.setOnMouseEntered(e -> setCursor(Cursor.HAND));
        node_link1.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        node_link1.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                AnchorPane parent = (AnchorPane) this.getParent();
                parent.getChildren().remove(this);
            }
        });


    }



    public void bindEnds (FormulaBox source, FormulaBox target) {

        DoubleProperty constantProp = new SimpleDoubleProperty(2.0);

        node_link.startXProperty().bind(add(source.layoutXProperty(), (source.widthProperty().divide(constantProp))));
        node_link.endXProperty().bind(add(target.layoutXProperty(), (target.widthProperty().divide(constantProp))));
        node_link1.startXProperty().bind(add(source.layoutXProperty(), (source.widthProperty().divide(constantProp))));
        node_link1.endXProperty().bind(add(target.layoutXProperty(), (target.widthProperty().divide(constantProp))));

        if (source.getLayoutY() < target.getLayoutY()) {
            node_link.startYProperty().bind(add(source.layoutYProperty(), (source.getHeight() - 9.0)));
            node_link.endYProperty().bind(add(target.layoutYProperty(), 9.0));
            node_link1.startYProperty().bind(add(source.layoutYProperty(), (source.getHeight() - 9.0)));
            node_link1.endYProperty().bind(add(target.layoutYProperty(), 9.0));
        } else {
            node_link.startYProperty().bind(add(source.layoutYProperty(), 9.0));
            node_link.endYProperty().bind(add(target.layoutYProperty(), (target.getHeight() - 9.0)));
            node_link1.startYProperty().bind(add(source.layoutYProperty(), 9.0));
            node_link1.endYProperty().bind(add(target.layoutYProperty(), (target.getHeight() - 9.0)));
        }


        source.registerLink (getId());
        target.registerLink (getId());
    }

}
