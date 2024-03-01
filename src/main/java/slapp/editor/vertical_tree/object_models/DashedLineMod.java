package slapp.editor.vertical_tree.object_models;

import java.io.Serializable;

public class DashedLineMod implements Serializable {

    private double layoutX;
    private double layoutY;
    private double width;

    public DashedLineMod(double layoutX, double layoutY, double width) {
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.width = width;
    }

    public double getLayoutX() {  return layoutX;  }

    public double getLayoutY() { return layoutY;   }

    public double getWidth() { return width;  }
}
