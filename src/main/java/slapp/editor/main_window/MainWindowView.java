package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.simple_editor.SimpleEditExercise;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainWindowView {
    private Stage stage = EditorMain.mainStage;
    private MainWindowController mainWindowController;
    private RichTextArea currentEditor;
//    private Exercise currentExercise;
    private ToolBar editToolbar = new ToolBar();
    private ToolBar fontsToolbar = new ToolBar();
    private ToolBar paragraphToolbar = new ToolBar();
    private DoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);
    private double scale = 1.0;
    MenuBar menuBar;
    MenuItem newExerciseItem = new MenuItem("New");
    private VBox topBox = new VBox();
    private VBox centerBox;
    private Spinner<Integer> zoomSpinner;
    private Label zoomLabel;
    int minStageWidth = 860;
    private BorderPane borderPane = new BorderPane();
    private Scene scene;
    private ExerciseView currentExerciseView;
    private Node statementNode;
    private Node contentNode;
    private DecoratedRTA commentDecoratedRTA;
    private Node commentNode;
    private Node controlNode;


    public MainWindowView(MainWindowController controller) {
        this.mainWindowController = controller;
        this.currentExerciseView = new SlappLogoView(this);
        setupWindow();
        setupExercise();
    }

    private void setupWindow() {




        Menu assignmentMenu = new Menu("Assignment");
        Menu exerciseMenu = new Menu("Exercise");
        Menu nextExerciseMenu = new Menu("Next");
        Menu previousExerciseMenu = new Menu("Previous");
        Menu goToExerciseMenu = new Menu("Jump");
        Menu printMenu = new Menu("Print");
        Menu helpMenu = new Menu("Help");
        menuBar = new MenuBar(assignmentMenu, exerciseMenu, nextExerciseMenu, previousExerciseMenu, goToExerciseMenu, printMenu, helpMenu);


        exerciseMenu.getItems().add(newExerciseItem);



        zoomLabel = new Label(" Zoom ");
        zoomSpinner = new Spinner(25, 500, 100, 5);
        zoomSpinner.setPrefSize(60,25);
        zoomSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = zoomSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = zoomSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
            updateZoom(nv);
        });

        centerBox = new VBox(statementNode, contentNode, commentNode);

        centerBox.setSpacing(3);
        Group centerGroup = new Group(centerBox);
        borderPane.setCenter(centerGroup);
        borderPane.setMargin(centerGroup, new Insets(20,20,0,20));

        VBox statusBar = new VBox(5);
        HBox upperStatusBox = new HBox(10);
        HBox lowerStatusBox = new HBox(10);
        statusBar.setPadding(new Insets(10,20,20,20));
        upperStatusBox.getChildren().add(new Label("Exercise: " + currentExerciseView.getExerciseName()));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        lowerStatusBox.getChildren().add(new Label(dtf.format(LocalDateTime.now())));
        statusBar.getChildren().addAll(upperStatusBox, lowerStatusBox);
     //   lowerStatusBox.getChildren().add()

     //   HBox statusBar = new HBox(10);
     //   statusBar.getStyleClass().add("status-bar");
     //   statusBar.setAlignment(Pos.TOP_LEFT);

     //   statusBar.getChildren().setAll(new Label("Assignment/Exercise info:"));
        borderPane.setBottom(statusBar);

        borderPane.setLeft(controlNode);

        scene = new Scene(borderPane);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("SLAPP Editor");
        stage.setMinWidth(minStageWidth);
        // suffices for the toolbars to appear in window
        // when topBox gets replaced, it appears that the window "looses" box dimensions both vertically and horizontally
        // this replaces the horizontal value

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double mainWindowX = Math.max(0.0, (bounds.getMaxX() - bounds.getMinX())/8);
        double mainWindowY = 40;
        stage.setX(mainWindowX);
        stage.setY(mainWindowY);

        stage.setHeight(800); //this added to keep SlappLogoView on screen (adjust with actual logo) ok in general?


        stage.setOnCloseRequest(e -> {
            e.consume();
            KeyboardDiagram.getInstance().close();
            stage.close();
        });

        //this seems odd: print utilities gives its value in pt.  RTA documentation says it is measured in px.
        //so I expect to set width at 16/12 * px.  But this gives a page too wide.  Is RTA measuring in pt?
        //similarly for height.
        commentDecoratedRTA.getEditor().setPrefWidth(PrintUtilities.getPageWidth());

        stage.show();

        // with content box enclosed in Group, the content pane does not size with window.
        // this restores sizing (inserting height from top box manually)
        double fixedHeight = (statementNode.getLayoutBounds().getHeight() + commentDecoratedRTA.getEditor().getLayoutBounds().getHeight())  * scale + statusBar.getHeight() + 250;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        DoubleProperty centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        currentExerciseView.getContentHeightProperty().bind(centerHeightProperty);

        Platform.runLater(() -> contentNode.requestFocus());
    }

    private void setupExercise() {
        this.statementNode = currentExerciseView.getExerciseStatementNode();
        this.contentNode = currentExerciseView.getExerciseContentNode();
        this.commentDecoratedRTA = currentExerciseView.getExerciseComment();
        this.commentNode = commentDecoratedRTA.getEditor();
        this.controlNode = currentExerciseView.getExerciseControl();

    }

    private void updateZoom(int zoom) {
        scale = (double)zoom/100.0;
        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(minStageWidth, PrintUtilities.getPageWidth() * scale + 55));
    }

    public void editorInFocus(DecoratedRTA decoratedRTA){
        editToolbar = decoratedRTA.getEditToolbar();
        fontsToolbar = decoratedRTA.getFontsToolbar();
        paragraphToolbar = decoratedRTA.getParagraphToolbar();

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.initialize(decoratedRTA, scale);
        if (keyboardDiagram.isShowing()) {
            keyboardDiagram.updateAndShow();
        }
        decoratedRTA.getKeyboardDiagramButton().selectedProperty().setValue(KeyboardDiagram.getInstance().isShowing());

        if (!fontsToolbar.getItems().contains(zoomSpinner)) {
            fontsToolbar.getItems().addAll(zoomLabel, zoomSpinner);
        }
        this.currentEditor = decoratedRTA.getEditor();
        VBox topBox = new VBox(menuBar, editToolbar, fontsToolbar, paragraphToolbar);
        topBox.layout();
        borderPane.topProperty().setValue(topBox);
    }

    MenuItem getNewExerciseItem() { return newExerciseItem; }

    public void setCurrentExerciseView(ExerciseView currentExerciseView) {
        this.currentExerciseView = currentExerciseView;
    }




}
