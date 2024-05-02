package slapp.editor.derivation_explain;

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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.derivation.LineType;
import slapp.editor.derivation.ModelLine;

import slapp.editor.main_window.ControlType;
import slapp.editor.main_window.MainWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class DrvtnExpCreate {
    private MainWindow mainWindow;
    private RichTextArea statementRTA;
    private DecoratedRTA statementDRTA;
    private TextField nameField;
    private TextField promptField;
    private boolean fieldModified = false;
    private ChangeListener nameListener;
    private ChangeListener promptFieldListener;
    private ChangeListener leftmostScopeListner;
    private ChangeListener defaultShelfListener;
    private double scale = 1.0;
    private Scene scene;
    private Stage stage;
    private SimpleDoubleProperty centerHeightProperty;
    private TextArea helpArea;
    private VBox centerBox;
    private CheckBox scopeLineCheck;
    private CheckBox defaultShelfCheck;
    private List<DrvtnExpSetupLine> setupLines;
    private GridPane setupLinesPane;
    private Spinner<Double> widthSpinner;
    private ChangeListener defaultWidthListener;
    private Label zoomLabel;
    private Spinner<Integer> zoomSpinner;
    private Button updateHeightButton;
    private Button saveButton;
    private MenuBar menuBar;
    private BorderPane borderPane;
    private VBox upperFieldsBox;
    private ToolBar editToolbar;
    private ToolBar fontsToolbar;
    private ToolBar insertToolbar;
    private ToolBar paragraphToolbar;;
    private ToolBar kbdDiaToolBar;
    private Button lowerSaveButton;
    private Button saveAsButton;

    public DrvtnExpCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public DrvtnExpCreate(MainWindow mainWindow, DrvtnExpModel originalModel) {
        this(mainWindow);

        statementRTA.setDocument(originalModel.getExerciseStatement());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        nameField.setText(originalModel.getExerciseName());
        promptField.setText(originalModel.getContentPrompt());
        scopeLineCheck.setSelected(originalModel.isLeftmostScopeLine());
        defaultShelfCheck.setSelected(originalModel.isDefaultShelf());
        widthSpinner.getValueFactory().setValue(((double) Math.round(originalModel.getGridWidth() * 100/2)) * 2);

        updateSetupLinesFromModel(originalModel);
        updateGridFromSetupLines();
        fieldModified = false;
    }

    private void setupWindow() {
        borderPane = new BorderPane();

        //empty bar for consistent look
        Menu helpMenu = new Menu("");
        menuBar = new MenuBar(helpMenu);

        //statement editor
        statementDRTA = new DecoratedRTA();
        statementRTA = statementDRTA.getEditor();
        statementRTA.setPromptText("Exercise Statement:");
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        statementRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementRTA.setPrefHeight(100);
        statementRTA.setMinHeight(50);
        statementRTA.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                editorInFocus(statementDRTA, ControlType.AREA);
            }
        });

        //top fields row

        //name field
        Label nameLabel = new Label("Exercise Name: ");
        nameLabel.setPrefWidth(95);
        nameField  = new TextField();
        nameField.setPromptText("(plain text)");
        nameListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldModified = true;
                nameField.textProperty().removeListener(nameListener);
            }
        };
        nameField.textProperty().addListener(nameListener);

        Label promptLabel = new Label("Explain prompt");
        promptLabel.setPrefWidth(95);
        promptField = new TextField();
        promptField.setPromptText("(plain text)");
        promptFieldListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldModified = true;
                promptField.textProperty().removeListener(promptFieldListener);
            }
        };
        promptField.textProperty().addListener(promptFieldListener);


        HBox nameBox = new HBox(10, nameLabel, nameField, promptLabel, promptField);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameBox.setMargin(promptLabel, new Insets(0,0,0,20));

        //check boxes
        scopeLineCheck = new CheckBox("Leftmost scope line");
        leftmostScopeListner = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldModified = true;
                scopeLineCheck.selectedProperty().removeListener(leftmostScopeListner);
            }
        };
        scopeLineCheck.setSelected(true);
        scopeLineCheck.selectedProperty().addListener(leftmostScopeListner);

        defaultShelfCheck = new CheckBox("Default shelf");
        defaultShelfListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldModified = true;
                defaultShelfCheck.selectedProperty().removeListener(defaultShelfListener);
            }
        };
        defaultShelfCheck.setSelected(true);
        defaultShelfCheck.selectedProperty().addListener(defaultShelfListener);

        widthSpinner = new Spinner<>(64.0, 100, 0, 2 );
        defaultWidthListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldModified = true;
                widthSpinner.valueProperty().removeListener(defaultWidthListener);
            }
        };
        widthSpinner.valueProperty().addListener(defaultWidthListener);
        widthSpinner.setPrefWidth(65);

        //setup lines control
        Label setupLinesLabel = new Label("Setup Lines: ");
        setupLinesLabel.setPrefWidth(75);
        Button addSetupLineButton = new Button("+");
        Button removeSetupLineButton = new Button("-");
        addSetupLineButton.setFont(new Font(16));
        addSetupLineButton.setPadding(new Insets(0,5,0,5));
        removeSetupLineButton.setFont(new Font(16));
        removeSetupLineButton.setPadding(new Insets(1,8,1,8));

        addSetupLineButton.setOnAction(e -> {
            DrvtnExpSetupLine newLine = new DrvtnExpSetupLine(this);

            setupLines.add(new DrvtnExpSetupLine(this));
            fieldModified = true;
            updateGridFromSetupLines();
        });
        removeSetupLineButton.setOnAction(e -> {
            int index = setupLines.size();
            index--;
            if (index > 0) {
                setupLines.remove(index);
                fieldModified = true;
                updateGridFromSetupLines();
            } else {
                EditorAlerts.showSimpleAlert("Cannot Remove", "A derivation must include at least one setup line.");
            }
        });



        Label widthLabel = new Label("Width: ");
        HBox topFields = new HBox(30, scopeLineCheck, defaultShelfCheck, widthLabel, widthSpinner, setupLinesLabel, addSetupLineButton, removeSetupLineButton);
        topFields.setAlignment(Pos.CENTER_LEFT);
        topFields.setMargin(widthLabel, new Insets(0, -20, 0, 0));
        topFields.setMargin(setupLinesLabel, new Insets(0,-20, 0, 0));

        //setup lines pane
        setupLines = new ArrayList<>();

        DrvtnExpSetupLine firstLine = new DrvtnExpSetupLine(this);
        RichTextArea firstLineFormulaRTA = firstLine.getFormulaBoxedDRTA().getRTA();
        firstLineFormulaRTA.getActionFactory().saveNow().execute(new ActionEvent());


        RichTextArea firstLineJustificationRTA = firstLine.getJustificationBoxedDRTA().getRTA();
        firstLineJustificationRTA.getActionFactory().saveNow().execute(new ActionEvent());

        setupLines.add(firstLine);
        setupLinesPane = new GridPane();
        setupLinesPane.setPadding(new Insets(5,15,10,0));
        setupLinesPane.setHgap(15);
        setupLinesPane.setVgap(15);
        updateGridFromSetupLines();

        upperFieldsBox = new VBox(10, nameBox, topFields, setupLinesPane);
        upperFieldsBox.setPadding(new Insets(20,0,20,20));

        String helpText = "Derivation Explain is appropriate for any exercise that calls for a derivation together with an explanation.\n\n" +
                "Setup is the same as Derivation Exercise except that you may add a prompt to appear in the explanation area.  For the derivation exercise, provide the exercise name, and explanation prompt.  Then and select whether there is to be " +
                "a leftmost scope line, and/or a \"shelf\" beneath the top line of automatically an generated subderivation; width is the (default) percentage of the window's width allocated to this derivation.\n\n" +
                "After that, give the exercise statement, and insert setup derivation lines as appropriate.";
        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(200);
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

        lowerSaveButton = new Button ("Save");
        lowerSaveButton.setOnAction(e -> saveExercise(false));

        saveAsButton = new Button("Save As");
        saveAsButton.setOnAction(e -> saveExercise(true));

        HBox buttonBox = new HBox(saveAsButton, lowerSaveButton, viewButton, clearButton, closeButton);
        buttonBox.setSpacing(30);
        buttonBox.setPadding(new Insets(20,50,20,50));
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        borderPane.setBottom(buttonBox);

        scene = new Scene(borderPane);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());

        zoomLabel = new Label(" Zoom ");
        zoomSpinner = new Spinner(25, 500, 100, 5);
        zoomSpinner.setPrefSize(60,25);
        zoomSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = zoomSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = zoomSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);

            scale = (double) nv/100;
            updateZoom();
            setCenterVgrow();
        });



        stage = new Stage();
        stage.initOwner(EditorMain.mainStage);
        stage.setScene(scene);

        stage.setTitle("Create Derivation Explain Exercise:");
        stage.getIcons().addAll(EditorMain.icons);
        stage.setWidth(1030);
        stage.setHeight(850);
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
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

    private void updateSetupLinesFromModel(DrvtnExpModel originalModel) {

        List<ModelLine> modelLines = originalModel.getDerivationLines();
        setupLines.clear();
        int i = 0;
        while (i < modelLines.size()) {
            ModelLine modelLine = modelLines.get(i);
            if (LineType.isContentLine(modelLine.getLineType())) {
                DrvtnExpSetupLine setupLine = new DrvtnExpSetupLine(this);

                BoxedDRTA formulaBoxedDRTA = setupLine.getFormulaBoxedDRTA();
                RichTextArea formulaRTA = formulaBoxedDRTA.getRTA();
                formulaRTA.setDocument(modelLine.getLineContentDoc());
                formulaRTA.getActionFactory().saveNow().execute(new ActionEvent());

                BoxedDRTA justificationBoxedDRTA = setupLine.getJustificationBoxedDRTA();
                RichTextArea justificationRTA = justificationBoxedDRTA.getRTA();
                justificationRTA.setDocument(new Document(modelLine.getJustification()));
                justificationRTA.getActionFactory().saveNow().execute(new ActionEvent());

                setupLine.getDepthSpinner().getValueFactory().setValue(modelLine.getDepth());
                setupLine.getPremiseBox().setSelected(modelLine.getLineType() == LineType.PREMISE_LINE);
                setupLine.getConclusionBox().setSelected(modelLine.getLineType() == LineType.CONCLUSION_LINE);

                i++;
                if (i < modelLines.size()) {
                    ModelLine nextLine = modelLines.get(i);
                    if (LineType.isShelfLine(nextLine.getLineType())) {
                        setupLine.getAddShelfBox().setSelected(true);
                        i++;
                    } else if (LineType.isGapLine(nextLine.getLineType())) {
                        setupLine.getAddGapBox().setSelected(true);
                        i++;
                    }
                }
                setupLine.setModified(false);
                setupLines.add(setupLine);
            }
        }
    }

    private void updateGridFromSetupLines() {
        setupLinesPane.getChildren().clear();
        for (int i = 0; i < setupLines.size(); i++) {
            DrvtnExpSetupLine setupLine = setupLines.get(i);
            BoxedDRTA formulaBoxedDRTA = setupLine.getFormulaBoxedDRTA();
            BoxedDRTA justificationBoxedDRTA = setupLine.getJustificationBoxedDRTA();
            setupLinesPane.addRow(i,
                    formulaBoxedDRTA.getBoxedRTA(),
                    justificationBoxedDRTA.getBoxedRTA(),
                    setupLine.getSpinnerBox(),
                    setupLine.getPremiseBox(),
                    setupLine.getConclusionBox(),
                    setupLine.getAddShelfBox(),
                    setupLine.getAddGapBox()
            );
        }
    }

    private void updateZoom() {
        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(1030, PrintUtilities.getPageWidth() * scale + 55));
        setCenterVgrow();

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.updateFontSize(scale);
        keyboardDiagram.initialize(statementDRTA);
        keyboardDiagram.update();
    }

    private void setCenterVgrow() {
        double fixedHeight = helpArea.getHeight() * scale + 400;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty externalHeightProperty = new SimpleDoubleProperty();
        externalHeightProperty.bind(fixedValueProperty.add(setupLinesPane.heightProperty()));
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(externalHeightProperty)).divide(scaleProperty)));
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
            nameField.clear();
//            promptField.clear();
            nameField.textProperty().addListener(nameListener);
            promptField.textProperty().addListener(promptFieldListener);

//            scopeLineCheck.setSelected(true);
//            defaultShelfCheck.setSelected(true);
            widthSpinner.getValueFactory().setValue(0.0);

            setupLines.clear();
            DrvtnExpSetupLine firstLine = new DrvtnExpSetupLine(this);
            firstLine.getFormulaBoxedDRTA().getRTA().getActionFactory().saveNow().execute(new ActionEvent());
            firstLine.getJustificationBoxedDRTA().getRTA().getActionFactory().saveNow().execute(new ActionEvent());
            setupLines.add(firstLine);
            updateGridFromSetupLines();

            statementRTA.setDocument(new Document());
            statementRTA.getActionFactory().newDocument().execute(new ActionEvent());
            statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
            viewExercise();
            fieldModified = false;
        }
    }

    private boolean checkContinue(String title, String content) {
        boolean okcontinue = true;
        for (DrvtnExpSetupLine line : setupLines) {
            if (line.isModified()) fieldModified = true;
        }
        if (fieldModified || statementRTA.isModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okcontinue = false;
        }
        return okcontinue;
    }

    private void viewExercise() {
        DrvtnExpExercise exercise = new DrvtnExpExercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 25.0);
        mainWindow.setUpExercise(exercise);
    }

    private void saveExercise(boolean saveAs) {
        lowerSaveButton.setDisable(true);
        saveAsButton.setDisable(true);

        nameField.textProperty().addListener(nameListener);
        promptField.textProperty().addListener(promptFieldListener);
        scopeLineCheck.selectedProperty().addListener(leftmostScopeListner);
        defaultShelfCheck.selectedProperty().addListener(defaultShelfListener);

        DrvtnExpExercise exercise = new DrvtnExpExercise(extractModelFromWindow(), mainWindow);

        for (DrvtnExpSetupLine line : setupLines) { line.setModified(false); }

        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 25.0);
        exercise.getExerciseModel().setStatementPrefHeight(height + 25.0);
        exercise.saveExercise(saveAs);
        lowerSaveButton.setDisable(false);
        saveAsButton.setDisable(false);

        fieldModified = false;
    }
    private DrvtnExpModel extractModelFromWindow() {
        String name = nameField.getText();
        String prompt = promptField.getText();

        boolean leftmostScope = scopeLineCheck.isSelected();
        boolean defaultShelf = defaultShelfCheck.isSelected();
        double gridWidth = widthSpinner.getValue()/100.0;

        if (statementRTA.isModified()) fieldModified = true;
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document statementDocument = statementRTA.getDocument();

        List<ModelLine> modelLines = new ArrayList<>();
        for (int i = 0; i < setupLines.size(); i++) {
            DrvtnExpSetupLine setupLine = setupLines.get(i);
            if (setupLine.isModified()) fieldModified = true;

            int depth = (Integer) setupLine.getDepthSpinner().getValue();

            RichTextArea formulaRTA = setupLine.getFormulaBoxedDRTA().getRTA();
            formulaRTA.getActionFactory().saveNow().execute(new ActionEvent());
            Document formulaDocument = formulaRTA.getDocument();

            RichTextArea justificationRTA = setupLine.getJustificationBoxedDRTA().getRTA();
            justificationRTA.getActionFactory().saveNow().execute(new ActionEvent());
            String justificationString = justificationRTA.getDocument().getText();

            LineType lineType = LineType.MAIN_CONTENT_LINE;
            if (setupLine.getPremiseBox().isSelected()) lineType = LineType.PREMISE_LINE;
            else if (setupLine.getConclusionBox().isSelected()) lineType = LineType.CONCLUSION_LINE;

            ModelLine modelLine = new ModelLine(depth, formulaDocument, justificationString, lineType );
            modelLines.add(modelLine);

           if (modelLine.getLineType() == LineType.PREMISE_LINE && setupLine.getAddShelfBox().isSelected()) {
                modelLines.add(new ModelLine(depth, null, "", LineType.SETUP_SHELF_LINE ));
            } else if (setupLine.getAddShelfBox().isSelected()) {
               modelLines.add(new ModelLine(depth, null, "", LineType.SHELF_LINE));
           } else if (setupLine.getAddGapBox().isSelected()) {
               modelLines.add(new ModelLine(depth, null, "", LineType.GAP_LINE));
           }
        }


        DrvtnExpModel model = new DrvtnExpModel(name, false, 70.0, gridWidth, prompt, leftmostScope, defaultShelf, statementDocument, new Document(), new Document(), modelLines);
        return model;
    }

    void editorInFocus(DecoratedRTA decoratedRTA, ControlType control) {

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.initialize(decoratedRTA);
        if (keyboardDiagram.isShowing()) {
            keyboardDiagram.updateAndShow();
        }

        editToolbar = decoratedRTA.getEditToolbar();
        fontsToolbar = decoratedRTA.getFontsToolbar();
        paragraphToolbar = decoratedRTA.getParagraphToolbar();
        kbdDiaToolBar = decoratedRTA.getKbdDiaToolbar();
        kbdDiaToolBar.setPrefHeight(38);

        if (kbdDiaToolBar.getItems().isEmpty()) {

            kbdDiaToolBar.getItems().addAll(zoomLabel, zoomSpinner,  new Label("    "), decoratedRTA.getKeyboardDiagramButton());

            switch (control) {
                case NONE: {
                    kbdDiaToolBar.setDisable(true);
                }
                case STATEMENT: {
                    editToolbar.setDisable(true);
                    fontsToolbar.setDisable(true);
                }
                case FIELD: {
                    paragraphToolbar.setDisable(true);
                }
                case AREA: { }
            }
        }

        HBox editAndKbdBox = new HBox(editToolbar, kbdDiaToolBar);
        editAndKbdBox.setHgrow(kbdDiaToolBar, Priority.ALWAYS);
        editAndKbdBox.layout();

        VBox topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox, upperFieldsBox);
//        topBox.layout();
        borderPane.topProperty().setValue(topBox);


    }

}
