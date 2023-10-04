package slapp.editor.main_window;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import javafx.scene.layout.*;

public class ExerciseTypePopup {
    private static ExerciseType selectedItem = null;
    private static ExerciseType lastSelectedItem = null;


    public static ExerciseType getType() {

        ListView<ExerciseType> typeList = new ListView();
        typeList.getItems().setAll(ExerciseType.values());
        typeList.setPrefWidth(200);
        typeList.getSelectionModel().select(lastSelectedItem);


        Button selectButton = new Button("Select");
        Button cancelButton = new Button("Cancel");
        Region spacer = new Region();

        VBox buttonBox = new VBox(20);
        buttonBox.getChildren().addAll(selectButton, spacer, cancelButton);
        buttonBox.setVgrow(spacer, Priority.ALWAYS);
        buttonBox.setMargin(selectButton, new Insets(10));
        buttonBox.setMargin(cancelButton, new Insets(10));

        HBox mainBox = new HBox(10);
        mainBox.setPadding(new Insets(5,0,20,20));
        mainBox.getChildren().addAll(typeList, buttonBox);

        Label title = new Label("Select Exercise Type:");
        title.setFont(new Font(16));

        VBox titledBox = new VBox(5);
        titledBox.setPadding(new Insets(20));
        titledBox.getChildren().addAll(title, mainBox);

        StackPane pane = new StackPane(titledBox);
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Exercise Type");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
        selectButton.setOnAction(e -> {
            ExerciseType selection = typeList.getSelectionModel().getSelectedItem();
            if (selection == null) EditorAlerts.showSimpleAlert("No Selection Made", "Please select an exercise type (or cancel)");
            else {
                selectedItem = selection;
                lastSelectedItem = selection;
                stage.close();
            }
        });
        cancelButton.setOnAction(e -> {
            selectedItem = null;
            stage.close();
        });
        stage.setOnCloseRequest(e -> {
            selectedItem = null;
            stage.close();
        });
        stage.showAndWait();

        return selectedItem;
    }



}
