package slapp.editor.main_window.assignment;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;

public class AssignmentCommentWindow {

    private AssignmentHeader header;
    private DecoratedRTA commentDRTA;
    private RichTextArea commentEditor;
    private double scale = 1.0;
    private Scene scene;
    private Stage stage;
    private TextArea helpArea;
    private VBox centerBox;
    private SimpleDoubleProperty centerHeightProperty;

    public AssignmentCommentWindow(AssignmentHeader header) {
        this.header = header;
        setUpWindow();
    }
    public AssignmentHeader getHeaderComment() {
        stage.showAndWait();
        return header;
    }

    private void setUpWindow() {
        BorderPane borderPane = new BorderPane();

        Menu helpMenu = new Menu("");
        MenuBar menuBar = new MenuBar(helpMenu);
        menuBar.setStyle("-fx-background-color: aliceblue; -fx-border-color: white;");

        commentDRTA = new DecoratedRTA();
        commentEditor = commentDRTA.getEditor();
        commentEditor.getActionFactory().open(header.getComment()).execute(new ActionEvent());
        commentEditor.setPromptText("Assignment Comment:");
        commentEditor.getStylesheets().add("slappTextArea.css");
        commentEditor.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        commentEditor.setContentAreaWidth(PrintUtilities.getPageWidth());
        commentEditor.setPrefHeight(200);

        String helpText = "You may comment on the assignment as a whole.  Comment will not show on main screen, but does appear at top of your printed assignment.";

        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(45);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

        centerBox = new VBox(10, commentEditor, helpArea);
        centerBox.setPadding(new Insets(20,0,0,0));
        Group centerGroup = new Group(centerBox);
        borderPane.setCenter(centerGroup);

        Button updateButton = new Button("Update");
        updateButton.setPrefWidth(55);
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(55);
        updateButton.setOnAction(e -> {
            updateHeaderFromWindow();
            closeWindow();
        });
        cancelButton.setOnAction(e ->  closeWindow() );
        HBox buttonBox = new HBox(updateButton, cancelButton);
        buttonBox.setSpacing(80);
        buttonBox.setPadding(new Insets(20,50,20,50));
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        borderPane.setBottom(buttonBox);
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

        ToolBar editToolbar = commentDRTA.getEditToolbar();
        ToolBar fontsToolbar = commentDRTA.getFontsToolbar();
        ToolBar paragraphToolbar = commentDRTA.getParagraphToolbar();
        ToolBar kbdDiaToolBar = commentDRTA.getKbdDiaToolbar();

        if (kbdDiaToolBar.getItems().isEmpty()) {
            kbdDiaToolBar.getItems().addAll(zoomLabel, zoomSpinner,  new Label("    "), commentDRTA.getKeyboardDiagramButton());
        }

        HBox editAndKbdBox = new HBox(editToolbar, kbdDiaToolBar);
        editAndKbdBox.setHgrow(kbdDiaToolBar, Priority.ALWAYS);

        VBox topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox);
//        topBox.layout();
        borderPane.topProperty().setValue(topBox);

        /*
        ToolBar fontsToolBar = commentDRTA.getFontsToolbar();
        fontsToolBar.getItems().addAll(zoomLabel, zoomSpinner);

        VBox topBox = new VBox(menuBar, commentDRTA.getEditToolbar(), fontsToolBar, commentDRTA.getParagraphToolbar() );
        borderPane.setTop(topBox);

         */

        scene = new Scene(borderPane);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Update Assignment Comment:");
        stage.getIcons().addAll(EditorMain.icons);
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(e-> {
            e.consume();
            closeWindow();
        });


    }

    private void updateZoom() {
        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(860, PrintUtilities.getPageWidth() * scale + 55));
        setCenterVgrow();
    }

    private void setCenterVgrow() {
        double fixedHeight = helpArea.getHeight() * scale + 280;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        commentEditor.prefHeightProperty().bind(centerHeightProperty);
    }


    private void updateHeaderFromWindow() {
        commentEditor.getActionFactory().saveNow().execute(new ActionEvent());
        Document statementDocument = commentEditor.getDocument();
        header.setComment(statementDocument);

    }

    private void closeWindow() { stage.close(); }


}
