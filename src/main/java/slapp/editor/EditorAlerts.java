package slapp.editor;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EditorAlerts {

    public static void showSimpleAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(EditorMain.mainStage);
        alert.showAndWait();
    }

    public static Alert confirmationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(EditorMain.mainStage);
        return alert;
    }

    public static void fleetingPopup(String message) {
        Popup popup = new Popup();
        Label label = new Label(message);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color: aliceblue; -fx-border-color: blue;");

        Text text = new Text(message);
        new Scene(new Group(text));
        text.applyCss();
        double width = Math.max(200, text.getLayoutBounds().getWidth());


        label.setMinWidth(width + 20);
        label.setMaxWidth(1000);
        label.setMinHeight(100);
        popup.getContent().add(label);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        popup.show(EditorMain.mainStage);
        delay.play();
    }

    public static void fleetingPopup(Label label) {
        Popup popup = new Popup();
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-background-color: aliceblue; -fx-border-color: blue;");


        new Scene(new Group(label));
        label.applyCss();
        double width = Math.max(200, label.getLayoutBounds().getWidth());


        label.setMinWidth(width + 20);
        label.setMaxWidth(1000);
        label.setMinHeight(100);
        popup.getContent().add(label);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        popup.show(EditorMain.mainStage);
        delay.play();
    }




}
