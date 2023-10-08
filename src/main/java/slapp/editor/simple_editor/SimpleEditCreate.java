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
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseType;
import slapp.editor.main_window.MainWindowController;
import javafx.scene.shape.Path;

import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class SimpleEditCreate {
    private MainWindowController mainController;
    private RichTextArea statementEditor;
    private boolean nameModified = false;

    private TextField nameField;
    private ChangeListener nameListener;
    private double scale =1.0;
    private Scene scene;
    private Stage stage;
    private SimpleDoubleProperty centerHeightProperty;
    private DecoratedRTA statementRTA;

    public SimpleEditCreate(MainWindowController mainController) {
        this.mainController = mainController;
        setupWindow();
    }

    private void setupWindow() {
        BorderPane borderPane = new BorderPane();

        Menu helpMenu = new Menu("Help");
        MenuBar menuBar = new MenuBar(helpMenu);

        statementRTA = new DecoratedRTA();
        statementEditor = statementRTA.getEditor();
        statementEditor.setPromptText("Exercise Statement:");
        statementEditor.setMaxWidth(PrintUtilities.getPageWidth());
        statementEditor.setPrefWidth(PrintUtilities.getPageWidth());
        statementEditor.setPrefHeight(200);


        Label typeLabel = new Label("Exercise type: " + ExerciseType.SIMPLE_EDITOR);
        Label nameLabel = new Label("Exercise Name: ");
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


        HBox nameBox = new HBox(nameLabel, nameField);
        nameBox.setPadding(new Insets(20,50,20,82));
        nameBox.setAlignment(Pos.BASELINE_LEFT);

        Group centerGroup = new Group(statementEditor);
        borderPane.setCenter(centerGroup);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> closeWindow());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearExercise());

        Button viewButton = new Button("View");
        viewButton.setOnAction(e -> viewExercise());

        Button saveButton = new Button ("Save");
        saveButton.setOnAction(e -> saveExercise());

        HBox buttonBox = new HBox(saveButton, viewButton, clearButton, closeButton);
        buttonBox.setSpacing(40);
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

            double scalePercent = nv;

            scale = scalePercent/100.0;
            statementEditor.setScaleX(scale);
            statementEditor.setScaleY(scale);
            scene.getWindow().setWidth(Math.max(860, PrintUtilities.getPageWidth() * scale + 55));
            setCenterVgrow();

        });
        ToolBar fontsToolBar = statementRTA.getFontsToolbar();
        fontsToolBar.getItems().addAll(zoomLabel, zoomSpinner);

        VBox topBox = new VBox(menuBar, statementRTA.getEditToolbar(), fontsToolBar, statementRTA.getParagraphToolbar(), nameBox );
        borderPane.setTop(topBox);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Create Simple Edit Exercise:");
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(e-> {
            e.consume();
            closeWindow();
        });

        stage.show();
        statementEditor.getActionFactory().save().execute(new ActionEvent());

        setCenterVgrow();
        Platform.runLater(() -> nameField.requestFocus());
    }

    private void updateZoom(int zoom) {
        scale = (double)zoom/100.0;
        statementEditor.setScaleX(scale);
        statementEditor.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(860, PrintUtilities.getPageWidth() * scale + 55));
        setCenterVgrow();
    }

    private void setCenterVgrow() {
        double fixedHeight = 320;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        statementEditor.prefHeightProperty().bind(centerHeightProperty);
    }

    private void closeWindow() {
        if (checkContinue("Confirm Close", "This exercise appears to have been changed.\nContinue to close window?")) stage.close();
        else return;
    }

    private void clearExercise() {
        if (checkContinue("Confirm Clear", "This exercise appears to have been changed.\nContinue to clear exercise?")) {
            nameField.clear();
            statementEditor.getActionFactory().newDocument().execute(new ActionEvent());
            statementEditor.getActionFactory().save().execute(new ActionEvent());

        }
        else return;

    }
    private void viewExercise() {
       SimpleEditExercise exercise = new SimpleEditExercise(extractModelFromWindow(), mainController);
       mainController.getNewExercise(exercise);
    }
    private void saveExercise() {
        nameModified = false;
        nameField.textProperty().addListener(nameListener);
        statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
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
        statementEditor.getActionFactory().saveNow().execute(new ActionEvent());
        int length = statementEditor.textLengthProperty().get();

        //for my attempt to get height
        RichTextAreaSkin richTextAreaSkin = (RichTextAreaSkin) statementEditor.getSkin();
        double posY = richTextAreaSkin.getEndCursorPositionY(length);
        System.out.println("posY: " + posY);

        Document statementDocument = statementEditor.getDocument();
        SimpleEditModel model = new SimpleEditModel(name, false, 0, statementDocument, new Document(), new Document());
        return model;
    }

}
