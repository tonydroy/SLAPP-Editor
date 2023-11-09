package slapp.editor.ab_explain;


import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.MainWindow;

import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class ABcreate {
    private MainWindow mainWindow;
    private RichTextArea statementEditor;
    private DecoratedRTA statementDRTA;
    private TextField nameField;
    private TextField leaderField;
    private TextField promptFieldA;
    private TextField promptFieldB;
    private TextField explainPromptField;

    private boolean fieldsModified = false;
    private ChangeListener nameListener;
    private ChangeListener leaderListener;
    private ChangeListener fieldListenerA;
    private ChangeListener fieldListenerB;
    private double scale =1.0;
    private Scene scene;
    private Stage stage;
    private SimpleDoubleProperty centerHeightProperty;
    private TextArea helpArea;
    private VBox centerBox;



    public ABcreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public ABcreate(MainWindow mainWindow, ABexercise originalExercise) {
        this(mainWindow);
        RichTextArea originalRTA = originalExercise.getExerciseView().getExerciseStatement().getEditor();
        statementEditor.setDocument(originalRTA.getDocument());
        statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
        ABmodel originalModel = originalExercise.getExerciseModel();

        nameField.setText(originalModel.getExerciseName());
        explainPromptField.setText(originalModel.getContentPrompt());
        ABmodelExtra fields = originalModel.getModelFields();
        leaderField.setText(fields.getLeader());
        promptFieldA.setText(fields.getPromptA());
        promptFieldB.setText(fields.getPromptB());

        fieldsModified = false;
        nameField.textProperty().addListener(nameListener);
        leaderField.textProperty().addListener(leaderListener);
        promptFieldA.textProperty().addListener(fieldListenerA);

    }

    private void setupWindow() {
        BorderPane borderPane = new BorderPane();

        //empty bar for consistent look
        Menu helpMenu = new Menu("");
        MenuBar menuBar = new MenuBar(helpMenu);

        statementDRTA = new DecoratedRTA();
        statementEditor = statementDRTA.getEditor();
        statementEditor.setPromptText("Exercise Statement:");
        statementEditor.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        statementEditor.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementEditor.setPrefHeight(200);

        Label nameLabel = new Label("Exercise Name: ");
        nameLabel.setPrefWidth(100);
        nameField  = new TextField();
        nameField.setPromptText("(plain text)");
        nameListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                nameField.textProperty().removeListener(nameListener);
            }
        };
        nameField.textProperty().addListener(nameListener);

        Label leaderLabel = new Label("Checkbox Lead: ");
        leaderLabel.setPrefWidth(100);
        leaderField = new TextField();
        leaderField.setPromptText("(plain text)");
        leaderListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                leaderField.textProperty().removeListener(leaderListener);
            }
        };
        leaderField.textProperty().addListener(leaderListener);


        Label aPromptLabel = new Label("A Prompt: ");
        aPromptLabel.setPrefWidth(100);
        promptFieldA = new TextField();
        promptFieldA.setPromptText("(plain text)");
        fieldListenerA = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                promptFieldA.textProperty().removeListener(fieldListenerA);
            }
        };
        promptFieldA.textProperty().addListener(fieldListenerA);


        Label bPromptLabel = new Label("B Prompt: ");
        bPromptLabel.setPrefWidth(100);
        promptFieldB = new TextField();
        promptFieldB.setPromptText("(plain text)");
        fieldListenerB = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                promptFieldB.textProperty().removeListener(fieldListenerB);
            }
        };
        promptFieldB.textProperty().addListener(fieldListenerB);



        Label promptLabel = new Label("Content prompt: ");
        promptLabel.setPrefWidth(100);
        explainPromptField = new TextField();
        explainPromptField.setPromptText("(plain text)");





        HBox nameBox = new HBox(nameLabel, nameField);
        nameBox.setAlignment(Pos.BASELINE_LEFT);
        HBox promptBox = new HBox(promptLabel, explainPromptField);
        promptBox.setAlignment(Pos.BASELINE_LEFT);
        HBox leaderBox = new HBox(leaderLabel, leaderField);
        leaderBox.setAlignment(Pos.BASELINE_LEFT);
        HBox aBbox = new HBox(aPromptLabel, promptFieldA);
        promptBox.setAlignment(Pos.BASELINE_LEFT);
        HBox bBox = new HBox(bPromptLabel, promptFieldB);
        bBox.setAlignment(Pos.BASELINE_LEFT);
        VBox textFieldsPromptBox = new VBox(10,nameBox, promptBox, leaderBox, aBbox, bBox);
        textFieldsPromptBox.setPadding(new Insets(20,0,20,70));

        String helpText = "AB Explain is appropriate for any exercise that requires a choice between mutually exclusive options (as true/false, consistent/inconsistent) together with an explanation or justification.\n\n" +
                "For the AB Explain Exercise, you supply the exercise name and, if desired, a prompt to appear in the explanation field.  Then the Checkbox Lead appears prior to the check boxes, the A Prompt with the first box, and the B Prompt with the second.";


        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(120);
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
        saveButton.setOnAction(e -> saveExercise(false));

        Button saveAsButton = new Button("Save As");
        saveAsButton.setOnAction(e -> saveExercise(true));

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

        VBox topBox = new VBox(menuBar, statementDRTA.getEditToolbar(), fontsToolBar, statementDRTA.getParagraphToolbar(), textFieldsPromptBox );
        borderPane.setTop(topBox);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Create AB Explain Exercise:");
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
        double fixedHeight = helpArea.getHeight() * scale + 500;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        statementEditor.prefHeightProperty().bind(centerHeightProperty);
    }

    private void closeWindow() {
        if (checkContinue("Confirm Close", "This exercise appears to have been changed.\nContinue to close window?")) {
            mainWindow.closeExercise();
            stage.close();
        }
    }

    private void clearExercise() {
        if (checkContinue("Confirm Clear", "This exercise appears to have been changed.\nContinue to clear exercise?")) {

            nameField.clear();
            nameField.textProperty().addListener(nameListener);
            explainPromptField.clear();
            leaderField.clear();
            promptFieldA.clear();
            promptFieldB.clear();
            statementEditor.getActionFactory().newDocument().execute(new ActionEvent());
            statementEditor.setDocument(new Document());
            statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
            fieldsModified = false;
            viewExercise();
        }
    }

    private boolean checkContinue(String title, String content) {
        boolean okcontinue = true;
        if (fieldsModified || statementEditor.isModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okcontinue = false;
        }
        return okcontinue;
    }

    private void viewExercise() {
        ABexercise exercise = new ABexercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 35.0);
        mainWindow.setUpExercise(exercise);
    }

    private void saveExercise(boolean saveAs) {
        fieldsModified = false;
        nameField.textProperty().addListener(nameListener);
        ABexercise exercise = new ABexercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 35.0);
        exercise.getExerciseModel().setStatementPrefHeight(height + 35.0);
        exercise.saveExercise(saveAs);
    }
    private ABmodel extractModelFromWindow() {
        String name = nameField.getText();
        String leader = leaderField.getText();
        String Aprompt = promptFieldA.getText();
        String Bprompt = promptFieldB.getText();
        ABmodelExtra fields = new ABmodelExtra(leader, Aprompt, false, Bprompt, false);
        String prompt = explainPromptField.getText();
        statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
        Document statementDocument = statementEditor.getDocument();
        ABmodel model = new ABmodel(name, fields,  false, prompt, 70.0, statementDocument, new Document(), new ArrayList<Document>());
        return model;
    }

}

