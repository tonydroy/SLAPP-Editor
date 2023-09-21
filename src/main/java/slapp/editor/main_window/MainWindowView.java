package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.RTAKeyboardDiagram;

public class MainWindowView {
    private MainWindowController mainWindowController;
    private double mainWindowWidth = 960.0;
    private double mainWindowHeight = 800.0;
    private RichTextArea currentEditor;
    private Exercise currentExercise;

    public MainWindowView(Stage stage, MainWindowController controller) {
        mainWindowController = controller;
        DecoratedRTA decoratedRTA = new DecoratedRTA();
        RichTextArea editor = decoratedRTA.getEditor();

        Menu assignmentMenu = new Menu("Assignment");
        Menu exerciseMenu = new Menu("Exercise");
        Menu nextExerciseMenu = new Menu("Next");
        Menu previousExerciseMenu = new Menu("Previous");
        Menu goToExerciseMenu = new Menu("Jump");
        Menu printMenu = new Menu("Print");
        Menu helpMenu = new Menu("Help");
        MenuBar menuBar = new MenuBar(assignmentMenu, exerciseMenu, nextExerciseMenu, previousExerciseMenu, goToExerciseMenu, printMenu, helpMenu);

        HBox statusBar = new HBox(10);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setAlignment(Pos.TOP_LEFT);
        statusBar.setPadding(new Insets(10,20,10,20));

        statusBar.getChildren().setAll(new Label("Assignment/Exercise info:"));

        BorderPane root = new BorderPane(editor);
        root.setMargin(editor, new Insets(20,20,0,20));
        root.setTop(new VBox(menuBar, decoratedRTA.getEditToolbar(), decoratedRTA.getFontsToolbar(), decoratedRTA.getParagraphToolbar()));
/*
        RichTextArea bottom = new RichTextArea(stage);
        bottom.setDocument(mainWindowController.getAssignmentInfo());
        bottom.setEditable(false);
        VBox bottomBox = new VBox(bottom);
        bottomBox.setMaxHeight(95);
 */
        root.setBottom(statusBar);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("fullFeaturedDemo.css").toExternalForm());
        stage.setTitle("SLAPP Editor");
//        stage.titleProperty().bind(Bindings.createStringBinding(() -> "SLAPP Editor" + (editor.isModified() ? " *" : ""), editor.modifiedProperty()));
        stage.setScene(scene);
        //
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double mainWindowX = Math.max(0.0, ((bounds.getMaxX() - bounds.getMinX()) - mainWindowWidth)/3);
        double mainWindowY = Math.max(0.0, ((bounds.getMaxY() - bounds.getMinY()) - mainWindowHeight)/3);

        stage.setX(mainWindowX);
        stage.setY(mainWindowY);
        stage.setWidth(mainWindowWidth);
        stage.setHeight(mainWindowHeight);
        stage.setOnCloseRequest(e -> {
            e.consume();
            RTAKeyboardDiagram keyboardDiagram = decoratedRTA.getRtaKeyboardDiagram();
            if (keyboardDiagram != null) keyboardDiagram.closeKeyboardDiagram();
            stage.close();
        });
        //
        stage.show();
        editor.requestFocus();

        decoratedRTA.setRtaListeners();




    }

    void editorInFocus(RichTextArea editor, ToolBar... bar){
        //update toolbar, if non-null, update keyboard diagram - a first click on keyboard diagram button has to use the editor gained on focus
    }

    void keyMapChanged(RichTextArea editor){
        //if non-null, update keyboard diagram
    }






    public double getMainWindowWidth() {
        return mainWindowWidth;
    }

    public double getMainWindowHeight() {
        return mainWindowHeight;
    }
}
