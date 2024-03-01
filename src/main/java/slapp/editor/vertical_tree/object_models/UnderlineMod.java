package slapp.editor.vertical_tree.object_models;

public class UnderlineMod {
    double startX;
    double endX;
    double yPos;

    UnderlineMod(double startX, double endX, double yPos) {
        this.startX = startX;
        this.endX = endX;
        this.yPos = yPos;
    }

    public double getStartX() { return startX;  }

    public double getEndX() { return endX;  }

    public double getyPos() { return yPos; }
}
