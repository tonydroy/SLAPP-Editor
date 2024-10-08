/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import slapp.editor.main_window.ControlType;
import slapp.editor.main_window.MainWindow;
import slapp.editor.main_window.MainWindowView;

import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class HorizontalTreeCreate {

    private MainWindow mainWindow;
    private RichTextArea statementRTA;
    private DecoratedRTA statementDRTA;
    private double statementTextHeight;
    private TextField nameField;
    private boolean fieldModified = false;
    private ChangeListener nameListener;
    private TextField promptField;
    private ChangeListener promptListener;
    private double scale =1.0;
    private Scene scene;
    private Stage stage;
    private SimpleDoubleProperty centerHeightProperty;
    private TextArea helpArea;
    private VBox centerBox;
    private Button saveButton;
    private Button saveAsButton;
    private ToolBar sizeToolBar;
    private DecoratedRTA dummyDRTA = new DecoratedRTA();
    private MenuBar menuBar;
    private VBox topFieldsBox;
    private BorderPane borderPane;


    public HorizontalTreeCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }
    public HorizontalTreeCreate(MainWindow mainWindow, HorizontalTreeModel originalModel) {
        this(mainWindow);

        statementRTA.getActionFactory().open(originalModel.getExerciseStatement()).execute(new ActionEvent());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        statementTextHeight = originalModel.getStatementTextHeight();
        nameField.setText(originalModel.getExerciseName());
        promptField.setText(originalModel.getExplainPrompt());
        nameField.textProperty().addListener(nameListener);
        fieldModified = false;
    }

    private void setupWindow() {
        borderPane = new BorderPane();

        //empty bar for consistent look
        Menu helpMenu = new Menu("");
        menuBar = new MenuBar(helpMenu);

        //statement field
        statementDRTA = new DecoratedRTA();
        statementRTA = statementDRTA.getEditor();
        statementRTA.setPromptText("Exercise Statement:");
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        statementRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementRTA.setPrefHeight(200);

        statementRTA.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            fieldModified = true;
            statementTextHeight = mainWindow.getMainView().getRTATextHeight(statementRTA);
        });

        statementRTA.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                editorInFocus(statementDRTA, ControlType.AREA);
            }
        });

        //name field
        Label nameLabel = new Label("Exercise Name: ");
        nameLabel.setPrefWidth(100);
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

        nameField.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) textFieldInFocus();
        });

        HBox nameBox = new HBox(nameLabel, nameField);
        nameBox.setAlignment(Pos.BASELINE_LEFT);
//        nameBox.setPadding(new Insets(20, 0, 0, 70));

        //prompt
        Label promptLabel = new Label("Explain Prompt: ");
        promptLabel.setPrefWidth(100);
        promptField = new TextField();
        promptField.setPromptText("(plain text)");
        promptListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldModified = true;
            }
        };
        promptField.textProperty().addListener(promptListener);
        promptField.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) textFieldInFocus();
        });

        HBox promptBox = new HBox(promptLabel, promptField);
        promptBox.setAlignment(Pos.BASELINE_LEFT);

        topFieldsBox = new VBox(15, nameBox, promptBox);
        topFieldsBox.setPadding(new Insets(10, 0,0, 60));

        //help area
        String helpText = "Horizontal Tree Explain Exercise is appropriate for exercises that involve horizontal trees (of the sort that appear in Chapter 4 of Symbolic Logic) along with an explanation of some sort.\n\n" +
                "Setup of these exercises is especially easy: Give the exercise name, a prompt for the explain field, and exercise statement.  That's it!";
        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(115);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

        //center
        centerBox = new VBox(10, statementRTA, helpArea);
        Group centerGroup = new Group(centerBox);
        borderPane.setCenter(centerGroup);

        //buttons
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

        //window setup
        scene = new Scene(borderPane);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());

        Label zoomLabel = new Label(" Zoom ");
        Spinner<Integer> zoomSpinner = new Spinner(25, 500, 100, 5);
        zoomSpinner.setPrefSize(65,25);
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

        /*
        ToolBar editToolbar = statementDRTA.getKbdSelectorToolbar();
        ToolBar fontsToolbar = statementDRTA.getEditToolbar();
        ToolBar paragraphToolbar = statementDRTA.getParagraphToolbar();
        ToolBar kbdDiaToolBar = statementDRTA.getKbdDiaToolbar();
        kbdDiaToolBar.setPrefHeight(38);

        if (kbdDiaToolBar.getItems().isEmpty()) {
            kbdDiaToolBar.getItems().addAll(statementDRTA.getKeyboardDiagramButton());
        }

        HBox editAndKbdBox = new HBox(editToolbar, sizeToolBar, kbdDiaToolBar);
        editAndKbdBox.setHgrow(kbdDiaToolBar, Priority.ALWAYS);

        VBox topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox, topFieldsBox);
//        topBox.layout();
        borderPane.topProperty().setValue(topBox);

        borderPane.setTop(topBox);

         */

        stage = new Stage();
        stage.initOwner(EditorMain.mainStage);
        stage.setScene(scene);
        stage.setTitle("Create Horizontal Tree Explain Exercise:");
        stage.getIcons().addAll(EditorMain.icons);



        stage.setWidth(860);
        stage.setHeight(700);

        Rectangle2D bounds = MainWindowView.getCurrentScreenBounds();
        stage.setX(Math.min(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth(), bounds.getMaxX() - 860));
        stage.setY(Math.min(EditorMain.mainStage.getY() + 20, bounds.getMaxY() - 700));
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
        double fixedHeight = helpArea.getHeight() * scale + 400;
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
            nameField.clear();
            nameField.textProperty().addListener(nameListener);
   //         statementRTA.getActionFactory().open(new Document()).execute(new ActionEvent());
            statementRTA.getActionFactory().newDocumentNow().execute(new ActionEvent());
   //         statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
            viewExercise();
            fieldModified = false;
        }
    }

    private boolean checkContinue(String title, String content) {
        boolean okcontinue = true;
        if (fieldModified || statementRTA.isModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okcontinue = false;
        }
        return okcontinue;
    }

    private void viewExercise() {
        HorizontalTreeModel model = extractModelFromWindow();
        HorizontalTreeExercise exercise = new HorizontalTreeExercise(model, mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(false);
        rta.prefHeightProperty().unbind();
        exercise.getExerciseView().setStatementPrefHeight(Math.min(PrintUtilities.getPageHeight(), model.getStatementPrefHeight()));
        mainWindow.setUpExercise(exercise);
    }

    private void saveExercise(boolean saveAs) {
        saveButton.setDisable(true);
        saveAsButton.setDisable(true);
        nameField.textProperty().addListener(nameListener);

        HorizontalTreeModel model = extractModelFromWindow();
        HorizontalTreeExercise exercise = new HorizontalTreeExercise(model, mainWindow);
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(false);
        rta.prefHeightProperty().unbind();
        exercise.getExerciseView().setStatementPrefHeight(Math.min(PrintUtilities.getPageHeight(), model.getStatementPrefHeight()));
        exercise.saveExercise(saveAs);

        saveButton.setDisable(false);
        saveAsButton.setDisable(false);
        fieldModified = false;
    }

    private HorizontalTreeModel extractModelFromWindow() {
        HorizontalTreeModel model = new HorizontalTreeModel();
        model.setExerciseName(nameField.getText());
        model.setExplainPrompt(promptField.getText());
        if (statementRTA.isModified()) fieldModified = true;
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseStatement(statementRTA.getDocument());
        model.setStatementPrefHeight(statementTextHeight + 25);
        model.setStatementTextHeight(statementTextHeight);
        return model;
    }

    private void editorInFocus(DecoratedRTA decoratedRTA, ControlType control) {

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.initialize(decoratedRTA);
        if (keyboardDiagram.isShowing()) {
            keyboardDiagram.updateAndShow();
        }

        ToolBar editToolbar = decoratedRTA.getKbdSelectorToolbar();
        ToolBar fontsToolbar = decoratedRTA.getEditToolbar();
        ToolBar paragraphToolbar = decoratedRTA.getParagraphToolbar();
        ToolBar kbdDiaToolBar = decoratedRTA.getKbdDiaToolbar();
        kbdDiaToolBar.setPrefHeight(38);

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
        sizeToolBar.setDisable(kbdDiaToolBar.isDisable());


        HBox editAndKbdBox = new HBox(editToolbar, sizeToolBar, kbdDiaToolBar);
        editAndKbdBox.setHgrow(kbdDiaToolBar, Priority.ALWAYS);
        editAndKbdBox.layout();



        VBox topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox, topFieldsBox);
        borderPane.topProperty().setValue(topBox);
    }

    private void textFieldInFocus() {
        editorInFocus(dummyDRTA, ControlType.STATEMENT);
    }


}
