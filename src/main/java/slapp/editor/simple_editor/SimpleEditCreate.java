package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import slapp.editor.DiskUtilities;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseType;
import slapp.editor.main_window.MainWindow;
import slapp.editor.main_window.TypeSelectorFactories;

import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class SimpleEditCreate {
    private RichTextArea statementEditor;
    private DecoratedRTA statementDRTA;
    private TextField nameField;
    private TextField promptField;
    private boolean nameModified = false;
    private MainWindow mainWindow;
    private ChangeListener nameListener;
    private double scale =1.0;
    private Scene scene;
    private Stage stage;
    private SimpleDoubleProperty centerHeightProperty;

    private TextArea helpArea;
    private VBox centerBox;


    public SimpleEditCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public SimpleEditCreate(MainWindow mainWindow, SimpleEditExercise originalExercise) {
        this(mainWindow);
        RichTextArea originalRTA = originalExercise.getExerciseView().getExerciseStatement().getEditor();
        statementEditor.setDocument(originalRTA.getDocument());
        statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
        nameField.setText(originalExercise.getExerciseModel().getExerciseName());
        nameModified = false;
        nameField.textProperty().addListener(nameListener);
        promptField.setText(originalExercise.getExerciseModel().getContentPrompt());
    }

    private void setupWindow() {
        BorderPane borderPane = new BorderPane();

        Menu helpMenu = new Menu("");
        MenuBar menuBar = new MenuBar(helpMenu);

        statementDRTA = new DecoratedRTA();
        statementEditor = statementDRTA.getEditor();
        statementEditor.setPromptText("Exercise Statement:");
       statementEditor.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        statementEditor.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementEditor.setPrefHeight(200);


        Label typeLabel = new Label("Exercise type: " + ExerciseType.SIMPLE_EDITOR);
        Label nameLabel = new Label("Exercise Name: ");
        nameLabel.setPrefWidth(95);
        nameField  = new TextField();
        nameField.setPromptText("(plain text)");
        nameListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                nameModified = true;
                nameField.textProperty().removeListener(nameListener);
            }
        };
        nameField.textProperty().addListener(nameListener);

        Label promptLabel = new Label("Content prompt: ");
        promptLabel.setPrefWidth(95);
        promptField = new TextField();
        promptField.setPromptText("(plain text)");



        HBox nameBox = new HBox(nameLabel, nameField);
        nameBox.setAlignment(Pos.BASELINE_LEFT);
        HBox promptBox = new HBox(promptLabel, promptField);
        promptBox.setAlignment(Pos.BASELINE_LEFT);
        VBox nameNpromptBox = new VBox(10,nameBox,promptBox);
        nameNpromptBox.setPadding(new Insets(20,0,20,70));

        String helpText = "Simple Edit Exercise is appropriate for any exercise that calls for a text response (which may include special symbols).  The response may range from short answer to multiple pages. All the usual edit commands apply.\n\n" +
                "For the Simple Edit Exercise, you need only provide the exercise name, exercise statement and, if desired, a prompt that will appear in an empty content area (you may not see the prompt until the content area gains focus).";
        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(130);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

        centerBox = new VBox(10, statementEditor, helpArea);

        Group centerGroup = new Group(centerBox);
        borderPane.setCenter(centerGroup);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> closeWindow());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearExercise());

        Button viewButton = new Button("View");
        viewButton.setOnAction(e -> viewExercise());

        Button saveButton = new Button ("Save");
        saveButton.setOnAction(e -> saveExercise());

        Button saveAsButton = new Button("Save As");
        saveAsButton.setOnAction(e -> saveAsExercise());

        HBox buttonBox = new HBox(saveAsButton, saveButton, viewButton, clearButton, closeButton);
        buttonBox.setSpacing(30);
        buttonBox.setPadding(new Insets(20,50,20,50));
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        borderPane.setBottom(buttonBox);

        scene = new Scene(borderPane);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());

        Label zoomLabel = new Label(" Zoom ");
        Spinner<Integer> zoomSpinner = new Spinner(25, 500, 100, 5);
        zoomSpinner.setPrefSize(60,25);
        zoomSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = zoomSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = zoomSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);

            scale = (double) nv/100;
            updateZoom();
            scene.getWindow().setWidth(Math.max(860, PrintUtilities.getPageWidth() * scale + 55));
            setCenterVgrow();

        });
        ToolBar fontsToolBar = statementDRTA.getFontsToolbar();
        fontsToolBar.getItems().addAll(zoomLabel, zoomSpinner);

        VBox topBox = new VBox(menuBar, statementDRTA.getEditToolbar(), fontsToolBar, statementDRTA.getParagraphToolbar(), nameNpromptBox );
        borderPane.setTop(topBox);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Create Simple Edit Exercise:");
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(e-> {
            e.consume();
            closeWindow();
        });

        stage.show();
        statementEditor.getActionFactory().save().execute(new ActionEvent());

        centerBox.layout();
        setCenterVgrow();
        Platform.runLater(() -> nameField.requestFocus());
    }

    private void updateZoom() {
        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(860, PrintUtilities.getPageWidth() * scale + 55));
        setCenterVgrow();
    }

    private void setCenterVgrow() {
        double fixedHeight = helpArea.getHeight() * scale + 400;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        statementEditor.prefHeightProperty().bind(centerHeightProperty);
    }

    private void closeWindow() {
        if (checkContinue("Confirm Close", "This exercise appears to have been changed.\nContinue to close window?")) {
            SimpleEditExercise emptyExercise = new SimpleEditExercise(new SimpleEditModel("", false, "", 80.0, new Document(), new Document(), new ArrayList<Document>()), mainWindow);
            mainWindow.setUpExercise(emptyExercise);
            stage.close();
        }
    }

    private void clearExercise() {
        if (checkContinue("Confirm Clear", "This exercise appears to have been changed.\nContinue to clear exercise?")) {
            nameField.clear();
            nameModified = false;
            nameField.textProperty().addListener(nameListener);
            statementEditor.getActionFactory().newDocument().execute(new ActionEvent());
            statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
            viewExercise();
        }
    }

    private void viewExercise() {
        SimpleEditExercise exercise = new SimpleEditExercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 35.0);
        mainWindow.setUpExercise(exercise);
    }
    private void saveExercise() {
        nameModified = false;
        nameField.textProperty().addListener(nameListener);
        SimpleEditExercise exercise = new SimpleEditExercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 35.0);
        exercise.getExerciseModel().setStatementPrefHeight(height + 35.0);
        exercise.saveExercise(false);
    }

    private void saveAsExercise() {
        nameModified = false;
        nameField.textProperty().addListener(nameListener);
        SimpleEditExercise exercise = new SimpleEditExercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 35.0);
        exercise.getExerciseModel().setStatementPrefHeight(height + 35.0);
        exercise.saveExercise(true);
    }

    private boolean checkContinue(String title, String content) {
        boolean okcontinue = true;
         if (nameModified || statementEditor.isModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okcontinue = false;
        }
        return okcontinue;
    }

    private SimpleEditModel extractModelFromWindow() {
        String name = nameField.getText();
        String prompt = promptField.getText();
        statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
        Document statementDocument = statementEditor.getDocument();
        SimpleEditModel model = new SimpleEditModel(name, false, prompt, 70.0, statementDocument, new Document(), new ArrayList<Document>());
        return model;
    }


}
