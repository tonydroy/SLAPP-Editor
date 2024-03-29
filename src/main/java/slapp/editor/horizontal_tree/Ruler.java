package slapp.editor.horizontal_tree;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public class Ruler extends Pane {
    double tickGap = 20.0;

    public Ruler() {
        this.setPadding(new Insets(5, 0, 0, 10));
    }


    public void updateRuler(double width) {
        this.getChildren().clear();


        int totalTicks = (int)(width/tickGap);


        Line axis = new Line(0,0, width,0);
        axis.setStyle("-fx-stroke-width: 1.2; -fx-stroke: grey");
        this.getChildren().add(axis);

        for (int tickCount = 0; tickCount <= totalTicks; tickCount++) {
            double xPos = tickCount * tickGap;
            if (tickCount % 5 == 0) {
                Line majorTick = new Line(xPos, 1, xPos, 5);
                majorTick.setStyle("-fx-stroke-width: 1.2; -fx-stroke: grey");
                this.getChildren().add(majorTick);
                Label number = new Label(String.valueOf(tickCount));
                number.setFont(new Font(8));
                this.getChildren().add(number);
                number.setLayoutX(xPos - 3);
                number.setLayoutY(7);
                number.setStyle("-fx-text-fill: grey");
            }
            else {
                Line minorTick = new Line(xPos, 1, xPos, 3);
                minorTick.setStyle("-fx-stroke-width: 1.2; -fx-stroke: grey");
                this.getChildren().add(minorTick);
            }
        }
    }

}
