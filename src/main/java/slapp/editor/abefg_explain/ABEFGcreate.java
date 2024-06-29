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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.main_window.MainWindow;
import slapp.editor.simple_editor.PageContent;

import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class ABEFGcreate {
    private MainWindow mainWindow;
    private RichTextArea statementRTA;
    private DecoratedRTA statementDRTA;
    private double statementTextHeight;
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
    private ChangeListener explainPromptListener;
    private double scale =1.0;
    private Scene scene;
    private Stage stage;
    private SimpleDoubleProperty centerHeightProperty;
    private TextArea helpArea;
    private VBox centerBox;
    private Button saveButton;
    private Button saveAsButton;
    private ToolBar sizeToolBar;


    public ABEFGcreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public ABEFGcreate(MainWindow mainWindow, ABEFGmodel originalModel) {
        this(mainWindow);

        statementRTA.getActionFactory().open(originalModel.getExerciseStatement()).execute(new ActionEvent());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());

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
        nameField.textProperty().addListener(nameListener);
        explainPromptField.textProperty().addListener(explainPromptListener);
        leaderABfield.textProperty().addListener(leaderListenerAB);
        promptFieldA.textProperty().addListener(fieldListenerA);
        promptFieldB.textProperty().addListener(fieldListenerB);
        leaderEFGfield.textProperty().addListener(leaderListenerEFG);
        promptFieldE.textProperty().addListener(fieldListenerE);
        promptFieldF.textProperty().addListener(fieldListenerF);
        promptFieldG.textProperty().addListener(fieldListenerG);
        fieldsModified = false;
    }

    private void setupWindow() {
        BorderPane borderPane = new BorderPane();

        //empty bar for consistent look
        Menu helpMenu = new Menu("");
        MenuBar menuBar = new MenuBar(helpMenu);

        statementDRTA = new DecoratedRTA();
        statementRTA = statementDRTA.getEditor();
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setPromptText("Exercise Statement:");
        statementRTA.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        statementRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementRTA.setPrefHeight(200);

        statementRTA.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            fieldsModified = true;
            statementTextHeight = mainWindow.getMainView().getRTATextHeight(statementRTA);
        });

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


        Label explainPromptLabel = new Label("Explain prompt: ");
        explainPromptLabel.setPrefWidth(100);
        explainPromptField = new TextField();
        explainPromptField.setPromptText("(plain text)");
        explainPromptListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldsModified = true;
                explainPromptField.textProperty().removeListener(explainPromptListener);
            }
        };
        explainPromptField.textProperty().addListener(explainPromptListener);




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





        String helpText = "AB/EFG Explain is appropriate for exercises that require choices from among two groups of mutually exclusive items: first, between some A and B, then between E, F and G, together with an explanation or justification.\n\n" +
                "For the AB/EFG Explain exercise you supply the exercise name and, if desired, a prompt to appear in the explanation field.  Then each set of options has a Lead that appears prior to the check boxes, and labels to appear with the check boxes.";


        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(130);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

        centerBox = new VBox(10, statementRTA, helpArea);

        Group centerGroup = new Group(centerBox);
        borderPane.setCenter(centerGroup);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> closeWindow());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearExercise());

        Button viewButton = new Button("View");
        viewButton.setOnAction(e -> viewExercise());

        saveButton = new Button ("Save");
        saveButton.setOnAction(e -> saveExercise(false));

        saveAsButton = new Button("Save As");
        saveAsButton.setOnAction(e -> saveExercise(true));

        HBox buttonBox = new HBox(saveAsButton, saveButton, viewButton, clearButton, closeButton);
        buttonBox.setSpacing(30);
        buttonBox.setPadding(new Insets(20,50,20,50));
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        borderPane.setBottom(buttonBox);

        if (EditorMain.secondaryCopy) {
            saveButton.setDisable(true);
            saveAsButton.setDisable(true);
        }

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

        sizeToolBar = new ToolBar();
        sizeToolBar.setPrefHeight(38);
        sizeToolBar.getItems().addAll(zoomLabel, zoomSpinner, new Label("     "));


        ToolBar editToolbar = statementDRTA.getEditToolbar();
        ToolBar fontsToolbar = statementDRTA.getFontsToolbar();
        ToolBar paragraphToolbar = statementDRTA.getParagraphToolbar();
        ToolBar kbdDiaToolBar = statementDRTA.getKbdDiaToolbar();
        kbdDiaToolBar.setPrefHeight(38);

        if (kbdDiaToolBar.getItems().isEmpty()) {
            kbdDiaToolBar.getItems().addAll(statementDRTA.getKeyboardDiagramButton());
        }

        HBox editAndKbdBox = new HBox(editToolbar, sizeToolBar, kbdDiaToolBar);
        editAndKbdBox.setHgrow(kbdDiaToolBar, Priority.ALWAYS);

        VBox topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox, gridBox);
//        topBox.layout();
        borderPane.topProperty().setValue(topBox);


        borderPane.setTop(topBox);

        stage = new Stage();
        stage.initOwner(EditorMain.mainStage);
        stage.setScene(scene);
        stage.setTitle("Create AB/EFG Explain Exercise:");
        stage.getIcons().addAll(EditorMain.icons);
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
        stage.setWidth(860);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(e-> {
            e.consume();
            closeWindow();
        });

        stage.show();
        statementRTA.getActionFactory().save().execute(new ActionEvent());
        centerBox.layout();
        setCenterVgrow();
        Platform.runLater(() -> nameField.requestFocus());
    }

    private void updateZoom() {
        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(860, PrintUtilities.getPageWidth() * scale + 55));
        setCenterVgrow();

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.updateFontSize(scale);
        keyboardDiagram.initialize(statementDRTA);
        keyboardDiagram.update();
    }

    private void setCenterVgrow() {
        double fixedHeight = helpArea.getHeight() * scale + 500;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        statementRTA.prefHeightProperty().bind(centerHeightProperty);
    }

    private void closeWindow() {
        if (checkContinue("Confirm Close", "This exercise appears to have been changed.\nContinue to close window?")) {
            mainWindow.closeExercise();
            stage.close();
        }
    }

    private void clearExercise() {
        if (checkContinue("Confirm Clear", "This exercise appears to have been changed.\nContinue to clear exercise?")) {
 //           statementRTA.getActionFactory().newDocument().execute(new ActionEvent());
            statementRTA.getActionFactory().open(new Document()).execute(new ActionEvent());
            statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
            nameField.clear();
            nameField.textProperty().addListener(nameListener);
            viewExercise();
            fieldsModified = false;
        }
    }

    private boolean checkContinue(String title, String content) {
        boolean okcontinue = true;
        if (fieldsModified || statementRTA.isModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okcontinue = false;
        }
        return okcontinue;
    }

    private void viewExercise() {
        ABEFGmodel model = extractModelFromWindow();
        ABEFGexercise exercise = new ABEFGexercise(model, mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.prefHeightProperty().unbind();
        rta.setEditable(false);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        exercise.getExerciseView().setStatementPrefHeight(Math.min(PrintUtilities.getPageHeight(), model.getStatementPrefHeight()));
        mainWindow.setUpExercise(exercise);
    }

    private void saveExercise(boolean saveAs) {
        saveButton.setDisable(true);
        saveAsButton.setDisable(true);
        nameField.textProperty().addListener(nameListener);

        ABEFGmodel model = extractModelFromWindow();
        ABEFGexercise exercise = new ABEFGexercise(model, mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(false);
        rta.prefHeightProperty().unbind();
        exercise.getExerciseView().setStatementPrefHeight(Math.min(PrintUtilities.getPageHeight(), model.getStatementPrefHeight()));
        exercise.saveExercise(saveAs);

        saveButton.setDisable(false);
        saveAsButton.setDisable(false);
        fieldsModified = false;
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
        if (statementRTA.isModified()) fieldsModified = true;
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document statementDocument = statementRTA.getDocument();
        Document commentDoc = new Document();
        double statementPrefHeight = statementTextHeight + 25;

        ABEFGmodel model = new ABEFGmodel(name, fields,  false, prompt, statementPrefHeight, statementDocument, commentDoc, new ArrayList<PageContent>());
        model.setStatementTextHeight(statementTextHeight);
        return model;
    }

}

