package slapp.editor.tests;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class ScrollPaneTest {


    double scale = 1.0;


    /*
    The natural way to have scrolling (together with the ability to scale) is to have content including the RTA wrapped
    in a group and then the group in a scroll pane.  This causes exceptions in the RTA.  So:

    Take a group containing a 'dummy' pane whose height and width are bound to those the content including the RTA as a
    member; put both the group and the content onto an AnchorPane, and the anchor pane into the scroll pane.  Scaling
    applies equally to the dummy pane and to the content. Then the scroll pane can go into the center of a border pane
    or whatever.  This seems to work!
     */
    public void testScrollPane(Stage primaryStage) {

        RichTextArea rta = new RichTextArea(primaryStage);
        rta.getActionFactory().open(new Document("test")).execute(new ActionEvent());
        rta.setPrefHeight(400);
        rta.setPrefWidth(400);

        VBox contentBox = new VBox(10, new Label("upper vbox"), rta, new Label("lower vbox"));

        Pane pane = new Pane();
        pane.prefHeightProperty().bind(contentBox.heightProperty());
        pane.prefWidthProperty().bind(contentBox.widthProperty());
        pane.setStyle("-fx-fill: transparent; -fx-background-color: transparent; -fx-border-color: red; -fx-border-width: 3");

        Group group = new Group(pane);
        AnchorPane anchorPane = new AnchorPane(group, contentBox);
        ScrollPane scrollPane = new ScrollPane(anchorPane);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(scrollPane);
        borderPane.getCenter().setStyle("-fx-border-color: blue");
        borderPane.setLeft(new Label("left"));
        borderPane.setRight(new Label("right"));


        Label scaleLabel = new Label();
        Button scaleUpButton = new Button("scale up");
        scaleUpButton.setOnAction(e -> {
            scale = scale + .02;
            pane.getTransforms().clear();
            pane.getTransforms().add(new Scale(scale, scale));
            contentBox.getTransforms().clear();
            contentBox.getTransforms().add(new Scale(scale, scale));
            scaleLabel.setText("scale: " + scale);
        });

        Button scaleDownButton = new Button("scale down");
        scaleDownButton.setOnAction(e -> {
            scale = scale - .02;
            pane.getTransforms().clear();
            pane.getTransforms().add(new Scale(scale, scale));
            contentBox.getTransforms().clear();
            contentBox.getTransforms().add(new Scale(scale, scale));
            scaleLabel.setText("scale: " + scale);
        });

        Button rtaHeightUpButton = new Button("RTA height up");
        rtaHeightUpButton.setOnAction(e -> rta.setPrefHeight(rta.getPrefHeight() + 5));

        Button rtaHeightDownButton = new Button("RTA width up");
        rtaHeightDownButton.setOnAction(e -> rta.setPrefWidth(rta.getPrefWidth() + 5));

        HBox buttonBox = new HBox(10, scaleUpButton, scaleDownButton, rtaHeightUpButton, rtaHeightDownButton, scaleLabel);
        buttonBox.setPadding(new Insets(10));

        VBox bigBox = new VBox(10, buttonBox, borderPane);
        Scene scene = new Scene(bigBox);

        primaryStage.setWidth(500);
        primaryStage.setHeight(500);

        primaryStage.setScene(scene);
        primaryStage.setTitle("BoxedDRTA Test");
        primaryStage.show();
    }
}
