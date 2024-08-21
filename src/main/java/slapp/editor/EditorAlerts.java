/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

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

    public static void showFleetingAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(EditorMain.mainStage);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> alert.hide());
        delay.play();
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
