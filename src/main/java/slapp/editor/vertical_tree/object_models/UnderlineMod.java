package slapp.editor.vertical_tree.object_models;

import java.io.Serializable;

public class UnderlineMod implements Serializable {

    double startX;
    double length;
    double yPos;

    public UnderlineMod(double startX, double length, double yPos) {
        this.startX = startX;
        this.length = length;
        this.yPos = yPos;
    }

    public double getStartX() {return startX; }
    public double getLength() { return length;  }
    public double getyPos() { return yPos; }
}
