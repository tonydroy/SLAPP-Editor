package slapp.editor.main_window.assignment;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UpdateAssignmentHeader {

    AssignmentHeader header;
    Stage stage;
    TextField studentNameField;
    List<TextField> labelFields = new ArrayList<>();
    List<TextField> valueFields = new ArrayList<>();
    int optItemIndex = 0;

    public UpdateAssignmentHeader(AssignmentHeader header) {
        this.header = header;
        updateWindow();
    }

    private void updateWindow() {
        Random rand = new Random();
        int idNumber = rand.nextInt(1000000000);
        header.setWorkingID(Integer.toString(idNumber));
        BorderPane borderPane = new BorderPane();

        Menu helpMenu = new Menu("");
        MenuBar menuBar = new MenuBar(helpMenu);
        menuBar.setStyle("-fx-background-color: aliceblue; -fx-border-color: white;");

        Label assignmentNameLabel = new Label("Assignment Name: " + header.getAssignmentName());
        Label creationIDLabel = new Label("Creation ID: " + header.getCreationID() + "-" + header.getWorkingID());
        Region spacer1 = new Region();

        HBox idBox = new HBox(assignmentNameLabel, spacer1, creationIDLabel);
        idBox.setHgrow(spacer1, Priority.ALWAYS);
        idBox.setPadding(new Insets(10,20,10,20));

        GridPane insItemsPane = new GridPane();
        insItemsPane.setPadding(new Insets(5,20,10,20));
        insItemsPane.setHgap(35);
        insItemsPane.setVgap(10);

        List<AssignmentHeaderItem> instructorItems = header.getInstructorItems();

        for (int i = 0; i < instructorItems.size(); i++) {
            Label labelItem = new Label(instructorItems.get(i).getLabel() + ":");
            Label valueItem = new Label(instructorItems.get(i).getValue());
            insItemsPane.add(labelItem, 0, i);
            insItemsPane.add(valueItem, 1, i);
            insItemsPane.setHalignment(labelItem, HPos.RIGHT);
        }

        VBox topBox = new VBox(menuBar, idBox, insItemsPane, new Separator(Orientation.HORIZONTAL));
        borderPane.setTop(topBox);




        Label studentNameLabel = new Label("Student Name: ");
        studentNameField = new TextField();
        studentNameField.setPromptText("Name");
        HBox studentNameBox = new HBox(5,studentNameLabel, studentNameField);
        studentNameBox.setPadding(new Insets(10,20,10,20) );
        studentNameBox.setAlignment(Pos.CENTER_LEFT);

        Label optionalFieldsLabel = new Label("Optional Fields: ");
        Button addOptionalItemsButton = new Button("+");
        Button removeOptionalItemsButton = new Button("-");
        addOptionalItemsButton.setFont(new Font(16));
        addOptionalItemsButton.setPadding(new Insets(0,5,0,5));
        removeOptionalItemsButton.setFont(new Font(16));
        removeOptionalItemsButton.setPadding(new Insets(1,8,1,8));
        HBox optionalItemBox = new HBox(optionalFieldsLabel, addOptionalItemsButton, removeOptionalItemsButton);
        optionalItemBox.setPadding(new Insets(10,20,5,20));
        optionalItemBox.setAlignment(Pos.CENTER_LEFT);
        optionalItemBox.setMargin(addOptionalItemsButton, new Insets(0,45,0,20));

        GridPane optionalItemsPane = new GridPane();
        optionalItemsPane.setPadding(new Insets(5,20,10,20));
        optionalItemsPane.setHgap(10);
        optionalItemsPane.setVgap(10);
        optionalItemsPane.setMargin(optionalFieldsLabel, new Insets(4,0,0,0));

        addOptionalItemsButton.setOnAction(e -> {
            labelFields.add(optItemIndex, new TextField());
            labelFields.get(optItemIndex).setPromptText("Label");
            optionalItemsPane.add(labelFields.get(optItemIndex), 0, optItemIndex);
            valueFields.add(optItemIndex, new TextField());
            valueFields.get(optItemIndex).setPromptText("Value");
            optionalItemsPane.add(valueFields.get(optItemIndex), 2, optItemIndex);
            labelFields.get(optItemIndex).requestFocus();
            optItemIndex++;
        });
        removeOptionalItemsButton.setOnAction(e -> {
            if (optItemIndex > 0) {
                optItemIndex--;
                optionalItemsPane.getChildren().remove(labelFields.get(optItemIndex));
                optionalItemsPane.getChildren().remove(valueFields.get(optItemIndex));
            }
        });

        VBox centerBox = new VBox(10, studentNameBox, optionalItemBox, optionalItemsPane );
        borderPane.setCenter(centerBox);


        String helpText = "Fill in the name field.  Additional information may be included by optional fields.\n\n" +
                "NOTE: Once you click 'Update', you will not be able to return to this window in order to update the header again - without starting the entire assignment over.  Be sure that header fields are filled out completely and correctly!";


        TextArea helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(110);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setPadding(new Insets(5,5,5,5));
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

        borderPane.setBottom(helpArea);

        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(55);
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(55);
        updateButton.setOnAction(e -> {
            String name = studentNameField.getText();
            boolean containsAlphanumeric = name.matches(".*[a-zA-Z0-9].*");
            if (containsAlphanumeric) {
                updateHeaderFromWindow();
                closeWindow();
            } else {
                EditorAlerts.showSimpleAlert("Complete Name Field", "'Name' is a required field.\n\nPlease complete this field in order to update header.");
            }
        });

        cancelButton.setOnAction(e -> closeWindow());


        VBox buttonBox = new VBox(90, updateButton, cancelButton);
        buttonBox.setAlignment(Pos.BASELINE_LEFT);
        buttonBox.setPadding(new Insets(20));
        borderPane.setRight(buttonBox);

        Scene scene = new Scene(borderPane);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Update Assignment Header");
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(600);
        stage.setMaxWidth(600);
        stage.setMinHeight(500);
        stage.setOnCloseRequest(e-> {
            e.consume();
            closeWindow();
        });
    }

    public AssignmentHeader updateHeader() {
        stage.showAndWait();
        return header;
    }

    private void updateHeaderFromWindow() {
        header.setStudentName(studentNameField.getText());
        for (int i = 0; i < labelFields.size(); i++) {
            header.getStudentItems().add(new AssignmentHeaderItem(labelFields.get(i).getText(), valueFields.get(i).getText()));
        }
    }

    private void closeWindow() {
        stage.close();
    }



}