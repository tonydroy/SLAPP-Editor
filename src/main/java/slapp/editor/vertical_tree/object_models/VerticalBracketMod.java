package slapp.editor.vertical_tree.object_models;

import java.io.Serializable;

public class VerticalBracketMod implements Serializable {

    private double layoutX;
    private double layoutY;
    private double height;

    public VerticalBracketMod(double layoutX, double layoutY, double height) {
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.height = height;
    }

    public double getLayoutX() { return layoutX;  }

    public double getLayoutY() { return layoutY;  }

    public double getHeight() { return height; }
}
