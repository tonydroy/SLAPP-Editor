package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.simple_editor.SimpleEditExercise;

public class MainWindowView {
    private Stage stage;
    private MainWindowController mainWindowController;
    private RichTextArea currentEditor;
    private Exercise currentExercise;
    private ToolBar editToolbar;
    private ToolBar fontsToolbar;
    private ToolBar paragraphToolbar;
    private DoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);
    private double scale = 1.0;
    MenuBar menuBar;
    private VBox topBox;
    private VBox centerBox;
    private Spinner<Integer> zoomSpinner;
    private Label zoomLabel;
    ObjectProperty<VBox> topPane = new SimpleObjectProperty<>();
    int minStageWidth = 858;

    private Scene scene;

    public MainWindowView(Stage stage, MainWindowController controller) {
        this.stage = stage;
        this.mainWindowController = controller;
        currentExercise = new SimpleEditExercise(this);
        setup();
    }

    private void setup() {
        BorderPane root = new BorderPane();

        Node statementNode = currentExercise.getExerciseView().getExerciseStatementNode();
        Node contentNode = currentExercise.getExerciseView().getExerciseContentNode();
        DecoratedRTA commentDecoratedRTA = currentExercise.getExerciseView().getExerciseComment();
        Node commentNode = commentDecoratedRTA.getEditor();

        this.currentEditor = commentDecoratedRTA.getEditor();
        this.editToolbar = commentDecoratedRTA.getEditToolbar();
        this.fontsToolbar = commentDecoratedRTA.getFontsToolbar();
        this.paragraphToolbar = commentDecoratedRTA.getParagraphToolbar();

        Menu assignmentMenu = new Menu("Assignment");
        Menu exerciseMenu = new Menu("Exercise");
        Menu nextExerciseMenu = new Menu("Next");
        Menu previousExerciseMenu = new Menu("Previous");
        Menu goToExerciseMenu = new Menu("Jump");
        Menu printMenu = new Menu("Print");
        Menu helpMenu = new Menu("Help");
        menuBar = new MenuBar(assignmentMenu, exerciseMenu, nextExerciseMenu, previousExerciseMenu, goToExerciseMenu, printMenu, helpMenu);

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

        topBox = new VBox(menuBar, editToolbar, fontsToolbar, paragraphToolbar);
        root.setTop(topBox);
        root.topProperty().bind(topPane);

        centerBox = new VBox(statementNode, contentNode, commentNode);

        centerBox.setSpacing(3);
        Group centerGroup = new Group(centerBox);
        root.setCenter(centerGroup);
        root.setMargin(centerGroup, new Insets(20,20,0,20));

        HBox statusBar = new HBox(10);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setAlignment(Pos.TOP_LEFT);
        statusBar.setPadding(new Insets(10,20,20,20));
        statusBar.getChildren().setAll(new Label("Assignment/Exercise info:"));
        root.setBottom(statusBar);

        scene = new Scene(root);
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

        stage.setOnCloseRequest(e -> {
            e.consume();
            //if not null, close keyborad diagram
            stage.close();
        });

        stage.show();

        //this seems odd: print utilities gives its value in pt.  RTA documentation says it is measured in px.
        //so I expect to set width at 16/12 * px.  But this gives a page too wide.  Is RTA measuring in pt?
        //similarly for height.
        commentDecoratedRTA.getEditor().setPrefWidth(PrintUtilities.getPageWidth() );


        // with content box enclosed in Group, the content pane does not size with window.
        // this restores sizing (inserting height from top box manually)
        double fixedHeight = (statementNode.getLayoutBounds().getHeight() + commentNode.getLayoutBounds().getHeight())  * scale + statusBar.getHeight() + 250;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        DoubleProperty centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        currentExercise.getExerciseView().getContentHeightProperty().bind(centerHeightProperty);

        Platform.runLater(() -> contentNode.requestFocus());
    }

    private void updateZoom(int zoom) {
        scale = (double)zoom/100.0;
        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(minStageWidth, PrintUtilities.getPageWidth() * scale + 55));
    }

    public void editorInFocus(RichTextArea editor, ToolBar editToolbar, ToolBar fontsToolbar, ToolBar paragraphToolbar){
        if (!fontsToolbar.getItems().contains(zoomSpinner)) {
            fontsToolbar.getItems().add(zoomLabel);
            fontsToolbar.getItems().add(zoomSpinner);
        }

        VBox topBox = new VBox(menuBar, editToolbar, fontsToolbar, paragraphToolbar);
        setTopPane(topBox);
        this.currentEditor = editor;

        //if it is non-null, update keyboard diagram - a first click on keyboard diagram button uses the current editor
    }

    void keyMapChanged(RichTextArea editor){
        //if non-null, update keyboard diagram
    }

    public final VBox getTopPane() {
        return topPane.get();
    }
    public final void setTopPane(VBox box) {
        topPane.set(box);
    }
    public final ObjectProperty<VBox> topPanePoperty() {
        return topPane;
    }


}
