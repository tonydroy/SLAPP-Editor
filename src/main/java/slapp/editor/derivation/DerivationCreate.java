package slapp.editor.derivation;

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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.main_window.MainWindow;
import slapp.editor.simple_editor.SimpleEditExercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class DerivationCreate {
    private MainWindow mainWindow;
    private RichTextArea statementRTA;
    private DecoratedRTA statementDRTA;
    private TextField nameField;
    private boolean fieldModified = false;
    private ChangeListener nameListener;
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
    private List<SetupLine> setupLines;
    private GridPane setupLinesPane;


    public DerivationCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public DerivationCreate(MainWindow mainWindow, DerivationModel originalModel) {
        this(mainWindow);

        statementRTA.setDocument(originalModel.getExerciseStatement());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        nameField.setText(originalModel.getExerciseName());
        scopeLineCheck.setSelected(originalModel.isLeftmostScopeLine());
        defaultShelfCheck.setSelected(originalModel.isDefaultShelf());

        updateSetupLinesFromModel(originalModel);
        updateGridFromSetupLines();

        fieldModified = false;
    }



    private void setupWindow() {
        BorderPane borderPane = new BorderPane();

        //empty bar for consistent look
        Menu helpMenu = new Menu("");
        MenuBar menuBar = new MenuBar(helpMenu);

        //statement editor
        statementDRTA = new DecoratedRTA();
        statementRTA = statementDRTA.getEditor();
        statementRTA.setPromptText("Exercise Statement:");
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        statementRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
  //      statementRTA.setMinHeight(50);
        statementRTA.setPrefHeight(200);


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
            setupLines.add(new SetupLine());
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

        HBox nameBox = new HBox(10, nameLabel, nameField);
        HBox topFields = new HBox(30, nameBox, scopeLineCheck, defaultShelfCheck, setupLinesLabel, addSetupLineButton, removeSetupLineButton);
        topFields.setAlignment(Pos.CENTER_LEFT);
        topFields.setMargin(setupLinesLabel, new Insets(0,-20, 0, 0));

        //setup lines pane
        setupLines = new ArrayList<>();
        SetupLine firstLine = new SetupLine();
        firstLine.getFormulaDRTA().getEditor().getActionFactory().saveNow().execute(new ActionEvent());
        firstLine.getJustificationDRTA().getEditor().getActionFactory().saveNow().execute(new ActionEvent());
        setupLines.add(firstLine);

        setupLinesPane = new GridPane();
        setupLinesPane.setPadding(new Insets(5,15,10,0));
        setupLinesPane.setHgap(15);
        setupLinesPane.setVgap(15);

        updateGridFromSetupLines();

        VBox upperFieldsBox = new VBox(10, topFields, setupLinesPane);
        upperFieldsBox.setPadding(new Insets(20,0,20,20));


        String helpText = "Derivation Exercise is appropriate for any exercise that calls for a derivation as response.\n\n" +
                "For the derivation exercise, provide the exercise statement, exercise name, and select whether there is to be a leftmost scope line, and/or a \"shelf\" beneath the top line of automatically an generated subderivation. "  +
                "A typical natural derivation system (as chapter 6 of Symbolic Logic) selects both.\n\n" +
                "After that, insert setup derivation lines as appropriate.  In the ordinary case, there will be some premise lines with justification 'P' (the last sitting on a shelf), a couple of blank lines, and a conclusion line (without justification), all at scope depth 1. " +
                "A line identified as a premise cannot have either its formula or justification modified; one identified as a conclusion cannot have its formula modified.  Different arrangements (as, e.g. \"fill in the justification\" exercises) are possible.";
        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(235);
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
            setCenterVgrow();
        });

        FontIcon heightIcon = new FontIcon(LineAwesomeSolid.TEXT_HEIGHT);
        heightIcon.setIconSize(20);
        Button updateHeightButton = new Button();
        updateHeightButton.setGraphic(heightIcon);
        updateHeightButton.setDisable(true);

        saveButton = new Button();
        FontIcon saveIcon = new FontIcon(LineAwesomeSolid.SAVE);
        saveIcon.setIconSize(20);
        saveButton.setGraphic(saveIcon);
        saveButton.setDisable(true);


        ToolBar editToolbar = statementDRTA.getEditToolbar();
        ToolBar fontsToolbar = statementDRTA.getFontsToolbar();
        ToolBar insertToolbar = statementDRTA.getInsertToolbar();
        ToolBar paragraphToolbar = statementDRTA.getParagraphToolbar();
        ToolBar kbdDiaToolBar = statementDRTA.getKbdDiaToolbar();


        if (!kbdDiaToolBar.getItems().contains(zoomSpinner)) {
            kbdDiaToolBar.getItems().add(0, updateHeightButton);
            kbdDiaToolBar.getItems().add(0, zoomSpinner);
            kbdDiaToolBar.getItems().add(0, zoomLabel);
            kbdDiaToolBar.getItems().add(saveButton);
        }

        HBox insertAndFontsBox = new HBox(insertToolbar, fontsToolbar);
        HBox editAndKbdBox = new HBox(editToolbar, kbdDiaToolBar);



        VBox topBox = new VBox(menuBar, paragraphToolbar, insertAndFontsBox, editAndKbdBox, upperFieldsBox );
        borderPane.setTop(topBox);

        stage = new Stage();
        stage.initOwner(EditorMain.mainStage);
        stage.setScene(scene);
        stage.setTitle("Create Derivation Exercise:");
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
        stage.setWidth(1030);
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

    private void updateSetupLinesFromModel(DerivationModel originalModel) {
        List<ModelLine> modelLines = originalModel.getExerciseContent();
        setupLines.clear();
        int i = 0;
        while (i < modelLines.size()) {
            ModelLine modelLine = modelLines.get(i);
            if (LineType.isContentLine(modelLine.getLineType())) {
                SetupLine setupLine = new SetupLine();

                DecoratedRTA formulaDRTA = setupLine.getFormulaDRTA();
                RichTextArea formulaRTA = formulaDRTA.getEditor();
                formulaRTA.setDocument(modelLine.getLineContentDoc());
                formulaRTA.getActionFactory().saveNow().execute(new ActionEvent());

                DecoratedRTA justificationDRTA = setupLine.getJustificationDRTA();
                RichTextArea justificationRTA = justificationDRTA.getEditor();
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
            SetupLine setupLine = setupLines.get(i);
            RichTextArea formulaRTA = setupLine.getFormulaDRTA().getEditor();
//            formulaRTA.getActionFactory().saveNow().execute(new ActionEvent());
            RichTextArea justificationRTA = setupLine.getJustificationDRTA().getEditor();
//            justificationRTA.getActionFactory().saveNow().execute(new ActionEvent());

            setupLinesPane.addRow(i,
                    formulaRTA,
                    justificationRTA,
                    setupLine.getSpinnerBox(),
                    setupLine.getPremiseBox(),
                    setupLine.getConclusionBox(),
                    setupLine.getAddShelfBox(),
                    setupLine.getAddGapBox());
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
            nameField.textProperty().addListener(nameListener);

            scopeLineCheck.setSelected(true);
            defaultShelfCheck.setSelected(true);


            setupLines.clear();
            SetupLine firstLine = new SetupLine();
            firstLine.getFormulaDRTA().getEditor().getActionFactory().saveNow().execute(new ActionEvent());
            firstLine.getJustificationDRTA().getEditor().getActionFactory().saveNow().execute(new ActionEvent());
            setupLines.add(firstLine);
            updateGridFromSetupLines();

            statementRTA.setDocument(new Document());
            statementRTA.getActionFactory().newDocument().execute(new ActionEvent());
            statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
            fieldModified = false;
            viewExercise();
        }
    }

    private boolean checkContinue(String title, String content) {
        boolean okcontinue = true;
        for (SetupLine line : setupLines) {
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
        DerivationExercise exercise = new DerivationExercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 25.0);
        mainWindow.setUpExercise(exercise);
    }

    private void saveExercise(boolean saveAs) {

        nameField.textProperty().addListener(nameListener);
        scopeLineCheck.selectedProperty().addListener(leftmostScopeListner);
        defaultShelfCheck.selectedProperty().addListener(defaultShelfListener);


        DerivationExercise exercise = new DerivationExercise(extractModelFromWindow(), mainWindow);


        for (SetupLine line : setupLines) { line.setModified(false); }

        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 25.0);
        exercise.getExerciseModel().setStatementPrefHeight(height + 25.0);
        exercise.saveExercise(saveAs);

        fieldModified = false;
    }
    private DerivationModel extractModelFromWindow() {
        String name = nameField.getText();
        double width = PrintUtilities.getPageWidth();

        boolean leftmostScope = scopeLineCheck.isSelected();
        boolean defaultShelf = defaultShelfCheck.isSelected();
        if (statementRTA.isModified()) fieldModified = true;
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document statementDocument = statementRTA.getDocument();

        List<ModelLine> modelLines = new ArrayList<>();
        for (int i = 0; i < setupLines.size(); i++) {
            SetupLine setupLine = setupLines.get(i);
            if (setupLine.isModified()) fieldModified = true;

            int depth = (Integer) setupLine.getDepthSpinner().getValue();

            RichTextArea formulaRTA = setupLine.getFormulaDRTA().getEditor();
            formulaRTA.getActionFactory().saveNow().execute(new ActionEvent());
            Document formulaDocument = formulaRTA.getDocument();

            RichTextArea justificationRTA = setupLine.getJustificationDRTA().getEditor();
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

        DerivationModel model = new DerivationModel(name, false, 70.0, width, leftmostScope, defaultShelf, statementDocument, new Document(), modelLines);
        return model;
    }

}
