package slapp.editor.main_window.assignment;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;
import slapp.editor.DiskUtilities;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.MainWindow;
import slapp.editor.main_window.TypeSelectorFactories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static javafx.scene.control.ButtonType.OK;

public class CreateAssignment {

    private MainWindow mainWindow;
    private Stage stage;
    private Assignment assignment;
    private int idNumber;
    private int insItemIndex = 0;
    private File exerciseFolder;
    private File assignemtFile;
    private String creationID;
    private TextField assignmentNameField;
    ListView<File> exerciseList;
    ListView<ExerciseModel> assignmentList;
    private boolean isModified = false;
    TextArea helpArea;
    GridPane optionalItemsPane;

    DoubleProperty centerHeightProperty;
    HBox workingBox;


    List<TextField> labelFields = new ArrayList<>();
    List<TextField> valueFields = new ArrayList<>();

    public CreateAssignment(Assignment assignment, MainWindow mainWindow) {
        this.assignment = assignment;
        this.mainWindow = mainWindow;

        setUpWindow();
    }

    private void setUpWindow() {
        Random rand = new Random();
        idNumber = rand.nextInt(1000000000);
        creationID = Integer.toString(idNumber);

        BorderPane borderPane = new BorderPane();

        Label assignmentNameLabel = new Label("Assignment Name: ");
        assignmentNameField = new TextField(assignment.getHeader().getAssignmentName());
        Label creationIDLabel = new Label("Creation ID: ");

        ChangeListener<String> nameListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ob, String ov, String nv) {
                isModified = true;
                assignmentNameField.textProperty().removeListener(this);
            }
        };
        assignmentNameField.textProperty().addListener(nameListener);

        Label creationIDNum = new Label(creationID);
        Region spacer1 = new Region();


        Menu helpMenu = new Menu("");
        MenuBar menuBar = new MenuBar(helpMenu);
        menuBar.setStyle("-fx-background-color: aliceblue; -fx-border-color: white;");
        HBox idBox = new HBox(assignmentNameLabel, assignmentNameField, spacer1, creationIDLabel, creationIDNum);
        idBox.setPadding(new Insets(10,20,10,20));
        idBox.setMargin(assignmentNameLabel, new Insets(4,2,0,0));
        idBox.setMargin(creationIDLabel, new Insets(4,2,0,0));
        idBox.setMargin(creationIDNum, new Insets(4,0,0,0));
        idBox.setHgrow(spacer1, Priority.ALWAYS);

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


        optionalItemsPane = new GridPane();
        optionalItemsPane.setPadding(new Insets(5,20,10,20));
        optionalItemsPane.setHgap(10);
        optionalItemsPane.setVgap(10);


        optionalItemsPane.setMargin(optionalFieldsLabel, new Insets(4,0,0,0));

        for (int i = 0; i < assignment.getHeader().getInstructorItems().size(); i++) {
            labelFields.add(insItemIndex, new TextField());
            labelFields.get(insItemIndex).setText(assignment.getHeader().getInstructorItems().get(insItemIndex).getLabel());
            optionalItemsPane.add(labelFields.get(insItemIndex),0, insItemIndex + 1);
            valueFields.add(insItemIndex, new TextField());
            valueFields.get(insItemIndex).setText(assignment.getHeader().getInstructorItems().get(insItemIndex).getValue());
            optionalItemsPane.add(valueFields.get(insItemIndex), 2, insItemIndex + 1);
            insItemIndex++;
        }
        addOptionalItemsButton.setOnAction(e -> {
            labelFields.add(insItemIndex, new TextField());
            labelFields.get(insItemIndex).setPromptText("Label");
            optionalItemsPane.add(labelFields.get(insItemIndex), 0, insItemIndex + 1);
            valueFields.add(insItemIndex, new TextField());
            valueFields.get(insItemIndex).setPromptText("Value");
            optionalItemsPane.add(valueFields.get(insItemIndex), 2, insItemIndex + 1);
            labelFields.get(insItemIndex).requestFocus();
            insItemIndex++;
        });
        removeOptionalItemsButton.setOnAction(e -> {
            if (insItemIndex > 0) {
                insItemIndex--;
                optionalItemsPane.getChildren().remove(labelFields.get(insItemIndex));
                optionalItemsPane.getChildren().remove(valueFields.get(insItemIndex));
            }
        });

        Label exerciseFolderLabel = new Label("Exercise Folder: ");
        Label exerciseFolderName = new Label("None");
        Button exerciseFolderButton = new Button("Open Folder");
        Region spacer2 = new Region();
        HBox folderBox = new HBox(exerciseFolderLabel, exerciseFolderName, spacer2, exerciseFolderButton);
        folderBox.setPadding(new Insets(10,20,10,20));
        folderBox.setAlignment(Pos.CENTER_LEFT);
        folderBox.setMargin(exerciseFolderName, new Insets(0,20,0,2));
        folderBox.setHgrow(spacer2, Priority.ALWAYS);


        VBox topBox = new VBox(menuBar, idBox, optionalItemBox, optionalItemsPane, new Separator(Orientation.HORIZONTAL), folderBox);

        borderPane.setTop(topBox);

        Label exerciseLabel = new Label("Exercise");
        Label assignmtLabel = new Label("Assignment");
        exerciseList = new ListView<>();
        assignmentList = new ListView<>();
        assignmentList.getItems().addAll(assignment.getExerciseModels());

        exerciseList.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
            public ListCell<File> call(ListView<File> param) {
                return new ListCell<File>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item == null || empty ? null : item.getName().substring(0, item.getName().length() - 4));
                    }
                };
            }
        });

        VBox exerciseVBox = new VBox(10, exerciseLabel, exerciseList);
        exerciseVBox.setVgrow(exerciseList, Priority.ALWAYS);
        VBox assignmentVBox = new VBox(10, assignmtLabel, assignmentList );
        assignmentVBox.setVgrow(assignmentList, Priority.ALWAYS);
        workingBox = new HBox(50, exerciseVBox, assignmentVBox);
        workingBox.setPadding(new Insets(10,20,20,20));

        exerciseFolderButton.setOnAction(e -> {
           exerciseFolder = DiskUtilities.getDirectory();
           if (exerciseFolder != null) {
               exerciseFolderName.setText(exerciseFolder.getAbsolutePath());
               exerciseList.getItems().setAll(DiskUtilities.getFileListFromDir(exerciseFolder, ".sle"));
           }
        });



        borderPane.setCenter(workingBox);

        Button addUpButton = new Button("Add \u2191");
        addUpButton.setPrefWidth(60);
        Button addDownButton = new Button("Add \u2193");
        addDownButton.setPrefWidth(60);
        Button removeButton = new Button("Remove");
        removeButton.setPrefWidth(60);
        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(60);
        Button closeButton = new Button("Close");
        closeButton.setPrefWidth(60);
        VBox buttonBox = new VBox(20, addUpButton, addDownButton, removeButton, saveButton, closeButton);
        buttonBox.setPadding(new Insets(0,20,40,0));
        buttonBox.setMargin(saveButton, new Insets(50,0,0,0));
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        SelectionModel exerciseSelectionModel = exerciseList.getSelectionModel();
        SelectionModel assignmentSelectionModel = assignmentList.getSelectionModel();


        addDownButton.setOnAction(e -> {
            File exerciseFile = (File) exerciseSelectionModel.getSelectedItem();
            if (exerciseFile != null) {
                ExerciseModel exerciseModel = DiskUtilities.getExerciseModelFromFile(exerciseFile);
                if (!exerciseModel.isStarted()) {
                    int newIndex = assignmentSelectionModel.getSelectedIndex() + 1;
                    assignmentList.getItems().add(newIndex, exerciseModel);
                    isModified = true;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            assignmentSelectionModel.select(newIndex);
                            assignmentList.getFocusModel().focus(newIndex);
                        }
                    });

                } else {
                    EditorAlerts.showSimpleAlert("Cannot Add", "The content section of this exercise appears to have been modified.\n\n" +
                            "Please select an empty exercise.");
                }
            } else {
                EditorAlerts.fleetingPopup("No exercise selected.  Please make selection.");
            }
        });

        addUpButton.setOnAction(e -> {
            File exerciseFile = (File) exerciseSelectionModel.getSelectedItem();
            if (exerciseFile != null) {
                ExerciseModel exerciseModel = DiskUtilities.getExerciseModelFromFile(exerciseFile);
                if (!exerciseModel.isStarted()) {
                    int newIndex = Math.max(0, assignmentSelectionModel.getSelectedIndex());
                    assignmentList.getItems().add(newIndex, exerciseModel);
                    isModified = true;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            assignmentSelectionModel.select(newIndex);
                            assignmentList.getFocusModel().focus(newIndex);
                        }
                    });

                } else {
                    EditorAlerts.showSimpleAlert("Cannot Add", "The content section of this exercise appears to have been modified.\n\n" +
                            "Please select an empty exercise.");
                }
            } else {
                EditorAlerts.fleetingPopup("No exercise selected.  Please make selection.");
            }
        });

        assignmentSelectionModel.selectedItemProperty().addListener(new ChangeListener<ExerciseModel>() {
            @Override
            public void changed(ObservableValue ob, ExerciseModel ov, ExerciseModel nv) {
                if (nv != null) {
                    TypeSelectorFactories factory = new TypeSelectorFactories(mainWindow);
                    Exercise exercise = factory.getExerciseFromModelObject(nv);
                    mainWindow.setUpExercise(exercise);
                }
            }
        });

        removeButton.setOnAction(e -> {
           assignmentList.getItems().remove(assignmentSelectionModel.getSelectedIndex());
           isModified = true;
        });

        saveButton.setOnAction(e -> {
           Assignment assignment = getAssignmentFromWindow();
           boolean saved = false;
            if (!assignment.getHeader().getAssignmentName().isEmpty()) {
                saved = DiskUtilities.saveAssignment(false, assignment);
                if (saved) isModified = false;
            }
            else EditorAlerts.showSimpleAlert("Cannot Save", "No named assignment to save.");
        });

        closeButton.setOnAction(e -> {
            closeWindow();
        });

        borderPane.setRight(buttonBox);

        String helpText = "Name the assignment.  Optional identifying fields (as course, instructor) may be included as appropriate.\n\n" +
                "Build an assignment by opening a folder with SlAPP exercise files (*.sle).  A selected exercise is added to the assignment above or below a selected assignment item by the add buttons.  " +
                "And similarly a selected assignment item is removed by the 'remove' button.";


        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(110);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setPadding(new Insets(5,5,5,5));
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

        borderPane.setBottom(helpArea);




        Scene scene = new Scene(borderPane);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Edit Assignment:");
        stage.getIcons().addAll(EditorMain.icons);
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(600);
        stage.setMinHeight(600);
        stage.setOnCloseRequest(e-> {
            e.consume();
            closeWindow();
        });
        stage.show();
        setCenterVgrow();
    }

    private Assignment getAssignmentFromWindow() {
        Assignment assignment = new Assignment();
        AssignmentHeader assignmentHeader = assignment.getHeader();
        assignmentHeader.setCreationID(creationID);
        assignmentHeader.setAssignmentName(assignmentNameField.getText());
        List<AssignmentHeaderItem> instructorItems = assignmentHeader.getInstructorItems();
        instructorItems.clear();
        for (int i = 0; i < labelFields.size(); i++) {
            instructorItems.add(new AssignmentHeaderItem(labelFields.get(i).getText(), valueFields.get(i).getText()));
        }
        assignment.setExerciseModels(new ArrayList<ExerciseModel>(assignmentList.getItems()));

        return assignment;
    }

    private void closeWindow() {
        boolean okContinue = true;
        if (isModified) {
            Alert confirm = EditorAlerts.confirmationAlert("Confirm Close", "This assignment appears to have been changed.\n\nContinue to close window?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okContinue = false;
        }
        if (okContinue) {
            mainWindow.closeExercise();
            stage.close();
        }
    }

    private void setCenterVgrow() {
        double fixedHeight = helpArea.getHeight() + 400;

        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);

        DoubleProperty externalHeightProperty = new SimpleDoubleProperty();
        externalHeightProperty.bind(fixedValueProperty.add(optionalItemsPane.heightProperty()));

        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );

        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(externalHeightProperty))));
        workingBox.prefHeightProperty().bind(centerHeightProperty);
    }




}
