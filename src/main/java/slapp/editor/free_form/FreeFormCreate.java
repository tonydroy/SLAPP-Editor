package slapp.editor.free_form;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.main_window.MainWindow;
import slapp.editor.vertical_tree.VerticalTreeExercise;
import slapp.editor.vertical_tree.VerticalTreeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class FreeFormCreate {

    private MainWindow mainWindow;
    private TextField nameField;
    private DecoratedRTA statementDRTA;
    private RichTextArea statementRTA;
    private double statementTextHeight;
    private TextArea helpArea;
    private ChangeListener nameListener;
    private boolean modified = false;
    private CheckBox simpleEditCheck;
    private CheckBox truthTableCheck;
    private CheckBox horizTreeCheck;
    private CheckBox vertTreeBaseItalCheck;
    private CheckBox vertTreeItalSansCheck;
    private CheckBox natDrvtnItalSansCheck;
    private CheckBox natDrvtnScriptItalicCheck;
    private CheckBox natDrvtnScriptSansCheck;
    private CheckBox natDrvtnItalBBCheck;
    private CheckBox axDrvtnItalSansCheck;
    private CheckBox axDrvtnScriptItalicCheck;
    private CheckBox axDrvtnScriptSansCheck;
    private CheckBox axDrvtnItalBBCheck;

    private double scale = 1.0;
    private Stage stage;
    private Scene scene;
    private VBox centerBox;
    SimpleDoubleProperty centerHeightProperty;
    private Button saveButton;
    private Button saveAsButton;
    private ToolBar sizeToolBar;
    private List<ElementTypes> elementTypes = new ArrayList<>();

    public FreeFormCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public FreeFormCreate(MainWindow mainWindow, FreeFormModel originalModel) {
        this(mainWindow);

        statementRTA.getActionFactory().open(originalModel.getExerciseStatement()).execute(new ActionEvent());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        statementTextHeight = originalModel.getStatementTextHeight();
        nameField.setText(originalModel.getExerciseName());
        setChecks(originalModel);
        modified = false;

    }

    private void setChecks(FreeFormModel originalModel) {
        simpleEditCheck.setSelected(false); truthTableCheck.setSelected(false); horizTreeCheck.setSelected(false);
        vertTreeBaseItalCheck.setSelected(false); vertTreeItalSansCheck.setSelected(false);
        natDrvtnItalSansCheck.setSelected(false); natDrvtnScriptItalicCheck.setSelected(false); natDrvtnScriptSansCheck.setSelected(false); natDrvtnItalBBCheck.setSelected(false);
        axDrvtnItalSansCheck.setSelected(false); axDrvtnScriptItalicCheck.setSelected(false); axDrvtnScriptSansCheck.setSelected(false); axDrvtnItalBBCheck.setSelected(false);

        List<ElementTypes> elementTypes = originalModel.getElementTypes();
        for (ElementTypes type: elementTypes) {
            switch(type) {
                case SIMPLE_EDIT: {
                    simpleEditCheck.setSelected(true);
                    break;
                }
                case TRUTH_TABLE: {
                    truthTableCheck.setSelected(true);
                    break;
                }
                case HORIZ_TREE: {
                    horizTreeCheck.setSelected(true);
                    break;
                }
                case VERT_TREE_BASE_ITAL: {
                    vertTreeBaseItalCheck.setSelected(true);
                    break;
                }
                case VERT_TREE_ITAL_SANS: {
                    vertTreeItalSansCheck.setSelected(true);
                    break;
                }
                case N_DERIVATION_ITAL_SANS: {
                    natDrvtnItalSansCheck.setSelected(true);
                    break;
                }
                case N_DERIVATION_SCRIPT_ITAL: {
                    natDrvtnScriptItalicCheck.setSelected(true);
                    break;
                }
                case N_DERIVATION_SCIRPT_SANS: {
                    natDrvtnScriptSansCheck.setSelected(true);
                    break;
                }
                case N_DERIVATION_ITAL_BB: {
                    natDrvtnItalBBCheck.setSelected(true);
                    break;
                }
                case A_DERIVATION_ITAL_SANS: {
                    axDrvtnItalSansCheck.setSelected(true);
                    break;
                }
                case A_DERIVATION_SCRIPT_ITAL: {
                    axDrvtnScriptItalicCheck.setSelected(true);
                    break;
                }
                case A_DERIVATION_SCRIPT_SANS: {
                    axDrvtnScriptSansCheck.setSelected(true);
                    break;
                }
                case A_DERIVATION_ITAL_BB: {
                    axDrvtnItalBBCheck.setSelected(true);
                    break;
                }
            }
        }
    }

    private void setupWindow() {
        BorderPane borderPane = new BorderPane();

        //empty bar for consistent look
        Menu helpMenu = new Menu("");
        MenuBar menuBar = new MenuBar(helpMenu);

        //statementDRTA
        statementDRTA = new DecoratedRTA();
        statementRTA = statementDRTA.getEditor();
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setPromptText("Exercise Statement:");
        statementRTA.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        statementRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementRTA.setPrefHeight(200);
        statementRTA.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            modified = true;
            statementTextHeight = mainWindow.getMainView().getRTATextHeight(statementRTA);
        });

        //name
        Label nameLabel = new Label("Exercise Name: ");
        nameLabel.setPrefWidth(100);
        nameField  = new TextField();
        nameField.setPromptText("(plain text)");
        nameField.textProperty().addListener((ob, ov, nv) -> modified = true );

        HBox nameBox = new HBox(nameLabel, nameField);
        nameBox.setAlignment(Pos.BASELINE_LEFT);

        //checks
        simpleEditCheck = new CheckBox("Simple Edit");
        simpleEditCheck.setSelected(true);
        simpleEditCheck.selectedProperty().addListener((ob, ov, nv) -> modified = true );

        truthTableCheck = new CheckBox("Truth Table");
        truthTableCheck.setSelected(false);
        truthTableCheck.selectedProperty().addListener((ob, ov, nv) -> modified = true );

        horizTreeCheck = new CheckBox("Horizontal Tree");
        horizTreeCheck.setSelected(false);
        horizTreeCheck.selectedProperty().addListener((ob, ov, nv) -> modified = true );

        vertTreeBaseItalCheck = new CheckBox("Vertical Tree (base/ital)");
        vertTreeBaseItalCheck.setSelected(false);
        vertTreeBaseItalCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                vertTreeItalSansCheck.setSelected(false);
            }
        });

        vertTreeItalSansCheck = new CheckBox("Vertical Tree (ital/sans)");
        vertTreeItalSansCheck.setSelected(false);
        vertTreeItalSansCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                vertTreeBaseItalCheck.setSelected(false);
            }
        });

        natDrvtnItalSansCheck = new CheckBox("Nat Drvtn (ital/sans)");
        natDrvtnItalSansCheck.setSelected(false);
        natDrvtnItalSansCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                natDrvtnScriptItalicCheck.setSelected(false);
                natDrvtnScriptSansCheck.setSelected(false);
                natDrvtnItalBBCheck.setSelected(false);
            }
        });

        natDrvtnScriptItalicCheck = new CheckBox("Nat Drvtn (script/italic)");
        natDrvtnScriptItalicCheck.setSelected(false);
        natDrvtnScriptItalicCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                natDrvtnItalSansCheck.setSelected(false);
                natDrvtnScriptSansCheck.setSelected(false);
                natDrvtnItalBBCheck.setSelected(false);
            }
        });

        natDrvtnScriptSansCheck = new CheckBox("Nat Drvtn (script/sans)");
        natDrvtnScriptSansCheck.setSelected(false);
        natDrvtnScriptSansCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                natDrvtnItalSansCheck.setSelected(false);
                natDrvtnScriptItalicCheck.setSelected(false);
                natDrvtnItalBBCheck.setSelected(false);
            }
        });

        natDrvtnItalBBCheck = new CheckBox("Nat Drvtn (ital/bb)");
        natDrvtnItalBBCheck.setSelected(false);
        natDrvtnItalBBCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                natDrvtnScriptSansCheck.setSelected(false);
                natDrvtnItalSansCheck.setSelected(false);
                natDrvtnScriptItalicCheck.setSelected(false);
            }
        });

        axDrvtnItalSansCheck = new CheckBox("Ax Drvtn (ital/sans)");
        axDrvtnItalSansCheck.setSelected(false);
        axDrvtnItalSansCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                axDrvtnScriptItalicCheck.setSelected(false);
                axDrvtnScriptSansCheck.setSelected(false);
                axDrvtnItalBBCheck.setSelected(false);
            }
        });

        axDrvtnScriptItalicCheck = new CheckBox("Ax Drvtn (script/italic)");
        axDrvtnScriptItalicCheck.setSelected(false);
        axDrvtnScriptItalicCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                axDrvtnItalSansCheck.setSelected(false);
                axDrvtnScriptSansCheck.setSelected(false);
                axDrvtnItalBBCheck.setSelected(false);
            }
        });

        axDrvtnScriptSansCheck = new CheckBox("Ax Drvtn (script/sans)");
        axDrvtnScriptSansCheck.setSelected(false);
        axDrvtnScriptSansCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                axDrvtnItalSansCheck.setSelected(false);
                axDrvtnScriptItalicCheck.setSelected(false);
                axDrvtnItalBBCheck.setSelected(false);
            }
        });

        axDrvtnItalBBCheck = new CheckBox("Ax Drvtn (ital/bb)");
        axDrvtnItalBBCheck.setSelected(false);
        axDrvtnItalBBCheck.selectedProperty().addListener((ob, ov, nv) -> {
            boolean selected = (boolean) nv;
            if (selected) {
                modified = true;
                axDrvtnScriptSansCheck.setSelected(false);
                axDrvtnItalSansCheck.setSelected(false);
                axDrvtnScriptItalicCheck.setSelected(false);
            }
        });

        HBox checks1 = new HBox(30, simpleEditCheck, truthTableCheck, horizTreeCheck);
        HBox checks2 = new HBox(30, vertTreeBaseItalCheck, vertTreeItalSansCheck);
        HBox checks3 = new HBox(30, natDrvtnItalSansCheck, natDrvtnScriptItalicCheck, natDrvtnScriptSansCheck, natDrvtnItalBBCheck);
        HBox checks4 = new HBox(30, axDrvtnItalSansCheck, axDrvtnScriptItalicCheck, axDrvtnScriptSansCheck, axDrvtnItalBBCheck);

        VBox fieldsBox = new VBox(15, nameBox, checks1, checks2, checks3, checks4);

        fieldsBox.setPadding(new Insets(20, 0, 0, 40));
        nameBox.setPadding(new Insets(0,0,10,0));

        //help
        String helpText = "Free Form Exercise is appropriate for any exercise that combines text (usually) with other elements - tree, truth table, or derivation.  " +
        "Selected elements may be inserted into the exercise in arbitrary combinations.\n\n" +
                "For the free form exercise, provide the exercise statement and exercise name.  Then use check boxes to select the elements that may be inserted.  " +
                "The vertical tree, natural derivation, and axiomatic derivation options allow different default keyboards.  You will be able to select just one keyboard option per item."
                ;

        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(150);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

        //center
        centerBox = new VBox(10, statementRTA, helpArea);
        Group centerGroup = new Group(centerBox);
        borderPane.setCenter(centerGroup);

        //bottom
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

        //finish up with top
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

        VBox topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox, fieldsBox);
        borderPane.topProperty().setValue(topBox);

        //generate view
        scene = new Scene(borderPane);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());

        stage = new Stage();
        stage.initOwner(EditorMain.mainStage);
        stage.setScene(scene);
        stage.setTitle("Create Free Form Exercise:");
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

    private boolean checkContinue(String title, String content) {
        boolean okcontinue = true;
        if (modified || statementRTA.isModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okcontinue = false;
        }
        return okcontinue;
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
            statementRTA.getActionFactory().open(new Document()).execute(new ActionEvent());
            statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
            viewExercise();
            modified = false;
        }

    }
    private void viewExercise() {
        FreeFormModel model = extractModelFromWindow();
        FreeFormExercise exercise = new FreeFormExercise(model, mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(false);
        rta.prefHeightProperty().unbind();
        exercise.getExerciseView().setStatementPrefHeight(Math.min(PrintUtilities.getPageHeight(), model.getStatementPrefHeight()));
        mainWindow.setUpExercise(exercise);
    }
    private void saveExercise(boolean saveAs) {
        saveButton.setDisable(true);
        saveAsButton.setDisable(true);

        FreeFormModel model = extractModelFromWindow();
        FreeFormExercise exercise = new FreeFormExercise(model, mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(false);
        rta.prefHeightProperty().unbind();
        exercise.getExerciseView().setStatementPrefHeight(Math.min(PrintUtilities.getPageHeight(), model.getStatementPrefHeight()));
        exercise.saveExercise(saveAs);
        saveButton.setDisable(false);
        saveAsButton.setDisable(false);
        modified = false;
    }




    private FreeFormModel extractModelFromWindow() {
        populateElementTypesFromWindow();
        FreeFormModel model = new FreeFormModel(nameField.getText(), elementTypes);

        if (statementRTA.isModified()) modified = true;
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseStatement(statementRTA.getDocument());
        model.setStatementPrefHeight(statementTextHeight + 25);
        model.setStatementTextHeight(statementTextHeight);

        return model;
    }

    private void populateElementTypesFromWindow() {
        elementTypes.clear();
        if (simpleEditCheck.isSelected()) elementTypes.add(ElementTypes.SIMPLE_EDIT);
        if (truthTableCheck.isSelected()) elementTypes.add(ElementTypes.TRUTH_TABLE);
        if (horizTreeCheck.isSelected()) elementTypes.add(ElementTypes.HORIZ_TREE);
        if (vertTreeBaseItalCheck.isSelected()) elementTypes.add(ElementTypes.VERT_TREE_BASE_ITAL);
        if (vertTreeItalSansCheck.isSelected()) elementTypes.add(ElementTypes.VERT_TREE_ITAL_SANS);
        if (natDrvtnItalSansCheck.isSelected()) elementTypes.add(ElementTypes.N_DERIVATION_ITAL_SANS);
        if (natDrvtnScriptItalicCheck.isSelected()) elementTypes.add(ElementTypes.N_DERIVATION_SCRIPT_ITAL);
        if (natDrvtnScriptSansCheck.isSelected()) elementTypes.add(ElementTypes.N_DERIVATION_SCIRPT_SANS);
        if (natDrvtnItalBBCheck.isSelected()) elementTypes.add(ElementTypes.N_DERIVATION_ITAL_BB);
        if (axDrvtnItalSansCheck.isSelected()) elementTypes.add(ElementTypes.A_DERIVATION_ITAL_SANS);
        if (axDrvtnScriptItalicCheck.isSelected()) elementTypes.add(ElementTypes.A_DERIVATION_SCRIPT_ITAL);
        if (axDrvtnScriptSansCheck.isSelected()) elementTypes.add(ElementTypes.A_DERIVATION_SCRIPT_SANS);
        if (axDrvtnItalBBCheck.isSelected()) elementTypes.add(ElementTypes.A_DERIVATION_ITAL_BB);
    }



}