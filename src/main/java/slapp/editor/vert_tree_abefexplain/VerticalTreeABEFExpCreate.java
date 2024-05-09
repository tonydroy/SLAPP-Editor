package slapp.editor.vert_tree_abefexplain;

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
import slapp.editor.vert_tree_abefexplain.VerticalTreeABEFExpExercise;
import slapp.editor.vert_tree_abefexplain.VerticalTreeABEFExpModel;
import slapp.editor.vertical_tree.drag_drop.DragIconType;
import slapp.editor.vertical_tree.object_models.ObjectControlType;

import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;
import static slapp.editor.vertical_tree.drag_drop.DragIconType.*;
import static slapp.editor.vertical_tree.object_models.ObjectControlType.*;

public class VerticalTreeABEFExpCreate {
    private MainWindow mainWindow;
    private TextField nameField;
    private DecoratedRTA statementDRTA;
    private RichTextArea statementRTA;
    private TextArea helpArea;
    private TextField abChoiceLeadField;
    private TextField aPromptField;
    private TextField bPromptField;
    private TextField efChoiceLeadField;
    private TextField ePromptField;
    private TextField fPromptField;
    private ChangeListener nameListener;
    ChangeListener abChoiceLeadListener;
    ChangeListener aPromptListener;
    ChangeListener bPromptListener;
    ChangeListener efChoiceLeadListener;
    ChangeListener ePromptListener;
    ChangeListener fPromptListener;
    private boolean modified = false;
    private CheckBox treeFormulaBoxCheck;
    private CheckBox verticalBracketCheck;
    private CheckBox dashedLineCheck;
    private CheckBox mapFormulaBoxCheck;
    private CheckBox boxingFormulaCheck;
    private CheckBox circleCheck;
    private CheckBox starCheck;
    private CheckBox annotationCheck;
    private CheckBox underlineCheck;
    private CheckBox mappingCheck;
    private double scale = 1.0;
    private Stage stage;
    private Scene scene;
    private VBox centerBox;
    private SimpleDoubleProperty centerHeightProperty;
    private Button saveButton;
    private Button saveAsButton;



    public VerticalTreeABEFExpCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public VerticalTreeABEFExpCreate(MainWindow mainWindow, VerticalTreeABEFExpModel originalModel) {
        this(mainWindow);

        statementRTA.setDocument(originalModel.getExerciseStatement());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        nameField.setText(originalModel.getExerciseName());
        abChoiceLeadField.setText(originalModel.getABChoiceLead());
        efChoiceLeadField.setText(originalModel.getEFChoiceLead());
        aPromptField.setText(originalModel.getaPrompt());
        bPromptField.setText(originalModel.getbPrompt());
        ePromptField.setText(originalModel.getePrompt());
        fPromptField.setText(originalModel.getfPrompt());

        List<DragIconType> dragIconList = originalModel.getDragIconList();
        treeFormulaBoxCheck.setSelected(dragIconList.contains(tree_field));
        verticalBracketCheck.setSelected(dragIconList.contains(bracket));
        dashedLineCheck.setSelected(dragIconList.contains(dashed_line));
        mapFormulaBoxCheck.setSelected(dragIconList.contains(map_field));
        List<ObjectControlType> objectControlList = originalModel.getObjectControlList();
        boxingFormulaCheck.setSelected(objectControlList.contains(FORMULA_BOX));
        circleCheck.setSelected(objectControlList.contains(OPERATOR_CIRCLE));
        starCheck.setSelected(objectControlList.contains(STAR));
        annotationCheck.setSelected(objectControlList.contains(ANNOTATION));
        underlineCheck.setSelected(objectControlList.contains(UNDERLINE));
        mappingCheck.setSelected(objectControlList.contains(MAPPING));
        modified = false;
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

        //name
        Label nameLabel = new Label("Exercise Name: ");
        nameLabel.setPrefWidth(120);
        nameField  = new TextField();
        nameField.setPromptText("(plain text)");
        nameListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                modified = true;
                nameField.textProperty().removeListener(nameListener);
            }
        };
        nameField.textProperty().addListener(nameListener);
        HBox nameBox = new HBox(nameLabel, nameField);
        nameBox.setAlignment(Pos.BASELINE_LEFT);

        //choice fields
        Label abChoiceLeadLabel = new Label("AB checkbox lead: ");
        abChoiceLeadLabel.setPrefWidth(120);
        abChoiceLeadField  = new TextField();
        abChoiceLeadField.setPromptText("(plain text)");
        abChoiceLeadListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                modified = true;
                abChoiceLeadField.textProperty().removeListener(abChoiceLeadListener);
            }
        };
        abChoiceLeadField.textProperty().addListener(abChoiceLeadListener);

        Label aPromptLabel = new Label("A prompt: ");
        aPromptField  = new TextField();
        aPromptField.setPromptText("(plain text)");
        aPromptListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                modified = true;
                aPromptField.textProperty().removeListener(aPromptListener);
            }
        };
        aPromptField.textProperty().addListener(aPromptListener);

        Label bPromptLabel = new Label("B prompt: ");
        bPromptField  = new TextField();
        bPromptField.setPromptText("(plain text)");
        bPromptListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                modified = true;
                bPromptField.textProperty().removeListener(bPromptListener);
            }
        };
        bPromptField.textProperty().addListener(bPromptListener);

        Label efChoiceLeadLabel = new Label("EF checkbox lead: ");
        efChoiceLeadLabel.setPrefWidth(120);
        efChoiceLeadField  = new TextField();
        efChoiceLeadField.setPromptText("(plain text)");
        efChoiceLeadListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                modified = true;
                efChoiceLeadField.textProperty().removeListener(efChoiceLeadListener);
            }
        };
        efChoiceLeadField.textProperty().addListener(efChoiceLeadListener);

        Label ePromptLabel = new Label("E prompt: ");
        ePromptField  = new TextField();
        ePromptField.setPromptText("(plain text)");
        ePromptListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                modified = true;
                ePromptField.textProperty().removeListener(ePromptListener);
            }
        };
        ePromptField.textProperty().addListener(ePromptListener);

        Label fPromptLabel = new Label("F prompt: ");
        fPromptField  = new TextField();
        fPromptField.setPromptText("(plain text)");
        fPromptListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                modified = true;
                fPromptField.textProperty().removeListener(fPromptListener);
            }
        };
        fPromptField.textProperty().addListener(fPromptListener);



        HBox choicesBox1 = new HBox(10, abChoiceLeadLabel, abChoiceLeadField, aPromptLabel, aPromptField, bPromptLabel, bPromptField);
        choicesBox1.setAlignment(Pos.CENTER_LEFT);
        HBox choicesBox2 = new HBox(10, efChoiceLeadLabel, efChoiceLeadField, ePromptLabel, ePromptField, fPromptLabel, fPromptField);
        choicesBox2.setAlignment(Pos.CENTER_LEFT);


        //drag bar
        Label dragLabel = new Label("Drag Bar: ");
        dragLabel.setPrefWidth(100);

        treeFormulaBoxCheck = new CheckBox("Tree Formula");
        treeFormulaBoxCheck.setSelected(false);
        treeFormulaBoxCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        verticalBracketCheck = new CheckBox("Vertical Bracket");
        verticalBracketCheck.setSelected(false);
        verticalBracketCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        dashedLineCheck = new CheckBox("Dashed Line");
        dashedLineCheck.setSelected(false);
        dashedLineCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        mapFormulaBoxCheck = new CheckBox("Maping Formula");
        mapFormulaBoxCheck.setSelected(false);
        mapFormulaBoxCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        HBox dragBox = new HBox(20, dragLabel, treeFormulaBoxCheck, verticalBracketCheck, dashedLineCheck, mapFormulaBoxCheck);
        dragBox.setAlignment(Pos.BASELINE_LEFT);

        //control pane
        Label controlLabel = new Label("Controls Pane: ");
        controlLabel.setPrefWidth(100);

        boxingFormulaCheck = new CheckBox("Box Button");
        boxingFormulaCheck.setSelected(false);
        boxingFormulaCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        circleCheck = new CheckBox("Circle Button");
        circleCheck.setSelected(false);
        circleCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        starCheck  = new CheckBox("Star Button");
        starCheck.setSelected(false);
        starCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        annotationCheck  = new CheckBox("Annotation Button");
        annotationCheck.setSelected(false);
        annotationCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        underlineCheck  = new CheckBox("Underline Button");
        underlineCheck.setSelected(false);
        underlineCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        mappingCheck  = new CheckBox("Mapping Button");
        mappingCheck.setSelected(false);
        mappingCheck.selectedProperty().addListener((ob, ov, nv) -> { modified = true; });
        HBox controlBox = new HBox(20,controlLabel, boxingFormulaCheck, circleCheck, starCheck, annotationCheck, underlineCheck, mappingCheck);
        controlBox.setAlignment(Pos.BASELINE_LEFT);

        VBox fieldsBox = new VBox(15, nameBox, choicesBox1, choicesBox2, dragBox, controlBox);
        fieldsBox.setPadding(new Insets(20,0, 0, 20));

        //center
        String helpText = "Vertical Tree AB/EF Explain Exercise is like Vertical Tree AB Explain Exercise except that it requires a pair of binary choices.  It is appropriate for any exercise that builds tree or map diagrams and has two choices together with an explanation.  As in the simple cases, it is unlikely that any one exercise will include all the drag and control options -- but the different options make it possible to accommodate a wide variety of exercises.\n\n"+
                "For this exercise, you supply the exercise name and statement.  The checkbox leads introduce the choices, and the A/B E/F prompts label the choices.  Use checkboxes to select items that may be dragged into the work area, and then buttons for functions applied to the formula boxes."
                ;


        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(180);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

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


        ToolBar editToolbar = statementDRTA.getEditToolbar();
        ToolBar fontsToolbar = statementDRTA.getFontsToolbar();
        ToolBar paragraphToolbar = statementDRTA.getParagraphToolbar();
        ToolBar kbdDiaToolBar = statementDRTA.getKbdDiaToolbar();
        kbdDiaToolBar.setPrefHeight(38);

        if (kbdDiaToolBar.getItems().isEmpty()) {
            kbdDiaToolBar.getItems().addAll(zoomLabel, zoomSpinner,  new Label("    "), statementDRTA.getKeyboardDiagramButton());
        }

        HBox editAndKbdBox = new HBox(editToolbar, kbdDiaToolBar);
        editAndKbdBox.setHgrow(kbdDiaToolBar, Priority.ALWAYS);

        VBox topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox, fieldsBox);
        borderPane.topProperty().setValue(topBox);

        //generate view
        scene = new Scene(borderPane);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());

        stage = new Stage();
        stage.initOwner(EditorMain.mainStage);
        stage.setScene(scene);
        stage.setTitle("Create Vertical Tree AB/EF Explain Exercise:");
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
            nameField.textProperty().addListener(nameListener);
            abChoiceLeadField.textProperty().addListener(abChoiceLeadListener);
            aPromptField.textProperty().addListener(aPromptListener);
            bPromptField.textProperty().addListener(bPromptListener);
            efChoiceLeadField.textProperty().addListener(efChoiceLeadListener);
            ePromptField.textProperty().addListener(ePromptListener);
            fPromptField.textProperty().addListener(fPromptListener);

            statementRTA.getActionFactory().newDocument().execute(new ActionEvent());
            statementRTA.setDocument(new Document());
            statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
            viewExercise();
            modified = false;
        }
    }
    private void viewExercise() {
        VerticalTreeABEFExpExercise exercise = new VerticalTreeABEFExpExercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 25.0);
        mainWindow.setUpExercise(exercise);

    }
    private void saveExercise(boolean saveAs) {
        saveButton.setDisable(true);
        saveAsButton.setDisable(true);
        nameField.textProperty().addListener(nameListener);

        abChoiceLeadField.textProperty().addListener(abChoiceLeadListener);
        aPromptField.textProperty().addListener(aPromptListener);
        bPromptField.textProperty().addListener(bPromptListener);
        efChoiceLeadField.textProperty().addListener(efChoiceLeadListener);
        ePromptField.textProperty().addListener(ePromptListener);
        fPromptField.textProperty().addListener(fPromptListener);


        VerticalTreeABEFExpExercise exercise = new VerticalTreeABEFExpExercise(extractModelFromWindow(), mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 25.0);
        exercise.getExerciseModel().setStatementPrefHeight(height + 25.0);
        exercise.saveExercise(saveAs);
        saveButton.setDisable(false);
        saveAsButton.setDisable(false);
        modified = false;
    }

    private VerticalTreeABEFExpModel extractModelFromWindow() {
        VerticalTreeABEFExpModel model = new VerticalTreeABEFExpModel();
        model.setExerciseName(nameField.getText());

        model.setABChoiceLead(abChoiceLeadField.getText());
        model.setaPrompt(aPromptField.getText());
        model.setbPrompt(bPromptField.getText());
        model.setEFChoiceLead(efChoiceLeadField.getText());
        model.setePrompt(ePromptField.getText());
        model.setfPrompt(fPromptField.getText());

        List<DragIconType> dragList = model.getDragIconList();
        if (treeFormulaBoxCheck.isSelected()) dragList.add(tree_field);
        if (verticalBracketCheck.isSelected()) dragList.add(bracket);
        if (dashedLineCheck.isSelected()) dragList.add(dashed_line);
        if (mapFormulaBoxCheck.isSelected()) dragList.add(map_field);

        List<ObjectControlType> controlList = model.getObjectControlList();
        if (boxingFormulaCheck.isSelected()) controlList.add(FORMULA_BOX);
        if (circleCheck.isSelected()) controlList.add(OPERATOR_CIRCLE);
        if (starCheck.isSelected()) controlList.add(STAR);
        if (annotationCheck.isSelected()) controlList.add(ANNOTATION);
        if (underlineCheck.isSelected()) controlList.add(UNDERLINE);
        if (mappingCheck.isSelected()) controlList.add(MAPPING);

        if (statementRTA.isModified()) modified = true;
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseStatement(statementRTA.getDocument());

        return model;
    }

}
