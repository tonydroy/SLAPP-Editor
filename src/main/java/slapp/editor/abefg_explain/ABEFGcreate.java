package slapp.editor.abefg_explain;


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
import javafx.scene.layout.GridPane;
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

public class ABEFGcreate {
    private MainWindow mainWindow;
    private RichTextArea statementEditor;
    private DecoratedRTA statementDRTA;
    private TextField nameField;
    private TextField leaderABfield;
    private TextField promptFieldA;
    private TextField promptFieldB;
    private TextField leaderEFGfield;
    private TextField promptFieldE;
    private TextField promptFieldF;
    private TextField promptFieldG;

    private TextField explainPromptField;

    private boolean fieldsModified = false;
    private ChangeListener nameListener;
    private ChangeListener leaderListenerAB;
    private ChangeListener leaderListenerEFG;
    private ChangeListener fieldListenerA;
    private ChangeListener fieldListenerB;
    private ChangeListener fieldListenerE;
    private ChangeListener fieldListenerF;
    private ChangeListener fieldListenerG;
    private double scale =1.0;
    private Scene scene;
    private Stage stage;
    private SimpleDoubleProperty centerHeightProperty;
    private TextArea helpArea;
    private VBox centerBox;



    public ABEFGcreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public ABEFGcreate(MainWindow mainWindow, ABEFGexercise originalExercise) {
        this(mainWindow);
        RichTextArea originalRTA = originalExercise.getExerciseView().getExerciseStatement().getEditor();
        statementEditor.setDocument(originalRTA.getDocument());
        statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
        ABEFGmodel originalModel = originalExercise.getExerciseModel();

        nameField.setText(originalModel.getExerciseName());
        explainPromptField.setText(originalModel.getContentPrompt());
        ABEFGmodelExtra fields = originalModel.getModelFields();
        leaderABfield.setText(fields.getLeaderAB());
        promptFieldA.setText(fields.getPromptA());
        promptFieldB.setText(fields.getPromptB());
        leaderEFGfield.setText(fields.getLeaderEFG());
        promptFieldE.setText(fields.getPromptE());
        promptFieldF.setText(fields.getPromptF());
        promptFieldG.setText(fields.getPromptG());
        fieldsModified = false;
        nameField.textProperty().addListener(nameListener);
        leaderABfield.textProperty().addListener(leaderListenerAB);
        promptFieldA.textProperty().addListener(fieldListenerA);
        promptFieldB.textProperty().addListener(fieldListenerB);
        leaderEFGfield.textProperty().addListener(leaderListenerEFG);
        promptFieldE.textProperty().addListener(fieldListenerE);
        promptFieldF.textProperty().addListener(fieldListenerF);
        promptFieldG.textProperty().addListener(fieldListenerG);
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

        Label leaderLabelAB = new Label("AB Checkbox Lead: ");
        leaderABfield = new TextField();
        leaderABfield.setPromptText("(plain text)");
        leaderListenerAB = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                leaderABfield.textProperty().removeListener(leaderListenerAB);
            }
        };
        leaderABfield.textProperty().addListener(leaderListenerAB);

        Label promptLabelA = new Label("A Prompt: ");
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

        Label promptLabelB = new Label("B Prompt: ");
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

        Label leaderLabelEFG = new Label("EFG Checkbox Lead: ");
        leaderEFGfield = new TextField();
        leaderEFGfield.setPromptText("(plain text)");
        leaderListenerEFG = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                leaderEFGfield.textProperty().removeListener(leaderListenerEFG);
            }
        };
        leaderEFGfield.textProperty().addListener(leaderListenerEFG);

        Label promptLabelE = new Label("E Prompt: ");
        promptFieldE = new TextField();
        promptFieldE.setPromptText("(plain text)");
        fieldListenerE = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                promptFieldE.textProperty().removeListener(fieldListenerE);
            }
        };
        promptFieldE.textProperty().addListener(fieldListenerE);

        Label promptLabelF = new Label("F Prompt: ");
        promptFieldF = new TextField();
        promptFieldF.setPromptText("(plain text)");
        fieldListenerF = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                promptFieldF.textProperty().removeListener(fieldListenerF);
            }
        };
        promptFieldF.textProperty().addListener(fieldListenerF);

        Label promptLabelG = new Label("G Prompt: ");
        promptFieldG = new TextField();
        promptFieldG.setPromptText("(plain text)");
        fieldListenerG = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                promptFieldG.textProperty().removeListener(fieldListenerG);
            }
        };
        promptFieldG.textProperty().addListener(fieldListenerG);


        Label explainPromptLabel = new Label("Content prompt: ");
        explainPromptLabel.setPrefWidth(100);
        explainPromptField = new TextField();
        explainPromptField.setPromptText("(plain text)");

        GridPane checkboxPane = new GridPane();
        checkboxPane.addColumn(0, nameLabel, leaderLabelAB, promptLabelA, promptLabelB);
        checkboxPane.addColumn(1, nameField, leaderABfield, promptFieldA, promptFieldB);
        checkboxPane.addColumn(5, explainPromptLabel, leaderLabelEFG, promptLabelE, promptLabelF, promptLabelG);
        checkboxPane.addColumn(6, explainPromptField, leaderEFGfield, promptFieldE, promptFieldF, promptFieldG);
        checkboxPane.setPadding(new Insets(20));
        checkboxPane.setHgap(10);
        checkboxPane.setVgap(10);

        HBox gridBox = new HBox(checkboxPane);
        gridBox.setAlignment(Pos.CENTER);





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

        VBox topBox = new VBox(menuBar, statementDRTA.getEditToolbar(), fontsToolBar, statementDRTA.getParagraphToolbar(), gridBox );
        borderPane.setTop(topBox);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Create AB/EFG Explain Exercise:");
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
            statementEditor.getActionFactory().newDocument().execute(new ActionEvent());
            statementEditor.setDocument(new Document());
            statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
            nameField.clear();
            fieldsModified = false;
            nameField.textProperty().addListener(nameListener);
            explainPromptField.clear();
            leaderABfield.clear();
            promptFieldA.clear();
            promptFieldB.clear();
            leaderEFGfield.clear();
            promptFieldE.clear();
            promptFieldF.clear();
            promptFieldG.clear();
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
        ABEFGexercise exercise = new ABEFGexercise(extractModelFromWindow(), mainWindow);
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
        ABEFGexercise exercise = new ABEFGexercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 35.0);
        exercise.getExerciseModel().setStatementPrefHeight(height + 35.0);
        exercise.saveExercise(saveAs);
    }
    private ABEFGmodel extractModelFromWindow() {
        String name = nameField.getText();
        String leaderAB = leaderABfield.getText();
        String promptA = promptFieldA.getText();
        String promptB = promptFieldB.getText();
        String leaderEFG = leaderEFGfield.getText();
        String promptE = promptFieldE.getText();
        String promptF = promptFieldF.getText();
        String promptG = promptFieldG.getText();

        ABEFGmodelExtra fields = new ABEFGmodelExtra(leaderAB, promptA, false, promptB, false, leaderEFG, promptE, false, promptF, false, promptG, false);
        String prompt = explainPromptField.getText();
        statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
        Document statementDocument = statementEditor.getDocument();
        ABEFGmodel model = new ABEFGmodel(name, fields,  false, prompt, 70.0, statementDocument, new Document(), new ArrayList<Document>());
        return model;
    }

}

