package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainWindowView {
    private Stage stage = EditorMain.mainStage;
    private MainWindow mainWindow;
    private ToolBar editToolbar = new ToolBar();
    private ToolBar fontsToolbar = new ToolBar();
    private ToolBar paragraphToolbar = new ToolBar();
    private double scale = 1.0;
    MenuBar menuBar;
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
    private VBox statusBar;
    private HBox upperStatusBox;
    private HBox lowerStatusBox;
    private DoubleProperty centerHeightProperty;
    private DoubleProperty contentHeightProperty;
    private Button saveButton;
    private MenuItem createNewExerciseItem = new MenuItem("Create New");
    private MenuItem createRevisedExerciseItem = new MenuItem("Create Revised");
    private MenuItem saveExerciseItem = new MenuItem("Save");
    private MenuItem saveAsExerciseItem = new MenuItem("Save As");
    private MenuItem openExerciseItem = new MenuItem("Open");
    private MenuItem printExerciseItem = new MenuItem("Print");
    private MenuItem exportToPDFExerciseItem = new MenuItem("Export to PDF");
    private MenuItem clearExerciseItem = new MenuItem("Clear");
    private MenuItem closeExerciseItem = new MenuItem("Close");
    private MenuItem newAssignmentItem = new MenuItem("New");
    private MenuItem saveAssignmentItem = new MenuItem("Save");
    private MenuItem saveAsAssignmentItem = new MenuItem("Save As");
    private MenuItem openAssignmentItem = new MenuItem("Open");



    public MainWindowView(MainWindow controller) {
        this.mainWindow = controller;
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

        exerciseMenu.getItems().addAll(saveExerciseItem, saveAsExerciseItem, openExerciseItem, clearExerciseItem, closeExerciseItem, printExerciseItem, exportToPDFExerciseItem, createRevisedExerciseItem, createNewExerciseItem);
        assignmentMenu.getItems().addAll(saveAssignmentItem, saveAsAssignmentItem, openAssignmentItem, newAssignmentItem);


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

        saveButton = new Button();
        FontIcon icon = new FontIcon(LineAwesomeSolid.SAVE);
        icon.setIconSize(20);
        saveButton.setGraphic(icon);
        saveButton.setTooltip(new Tooltip("Save assignment if open and otherwise exercise"));


        centerBox = new VBox();
        centerBox.setSpacing(3);

        Group centerGroup = new Group(centerBox);  //this lets scene width scale with nodes https://stackoverflow.com/questions/67724906/javafx-scaling-does-not-resize-the-component-in-parent-container

        borderPane.setCenter(centerGroup);
        borderPane.setMargin(centerGroup, new Insets(10,0,0,0));





        statusBar = new VBox(5);
        upperStatusBox = new HBox(10);
        lowerStatusBox = new HBox(10);
        statusBar.setPadding(new Insets(0,20,20,20));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        lowerStatusBox.getChildren().add(new Label(dtf.format(LocalDateTime.now())));
        statusBar.getChildren().addAll(upperStatusBox, lowerStatusBox);
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
            closeWindow();
        });
        stage.show();
    }

    public void setupExercise() {
        this.statementNode = currentExerciseView.getExerciseStatementNode();
        this.contentNode = currentExerciseView.getExerciseContentNode();
        this.commentDecoratedRTA = currentExerciseView.getExerciseComment();
        this.commentNode = commentDecoratedRTA.getEditor();
        this.controlNode = currentExerciseView.getExerciseControl();

        this.contentHeightProperty = currentExerciseView.getContentHeightProperty();

        statementNode.setFocusTraversable(false);
        statementNode.setMouseTransparent(true);


        //this seems odd: print utilities gives its value in pt.  RTA documentation says it is measured in px.
        //so I expect to set width at 16/12 * px.  But this gives a page too wide.  Is RTA measuring in pt?
        //similarly for height.
        commentDecoratedRTA.getEditor().setContentAreaWidth(PrintUtilities.getPageWidth());
        commentDecoratedRTA.getEditor().setPrefWidth(PrintUtilities.getPageWidth() +20);
        centerBox.getChildren().clear();
        centerBox.getChildren().addAll(commentNode, statementNode, contentNode);

        upperStatusBox.getChildren().clear();
        upperStatusBox.getChildren().add(new Label("Exercise: " + currentExerciseView.getExerciseName()));

        borderPane.setLeft(controlNode);

        centerBox.layout();

        setCenterVgrow();
        Platform.runLater(() -> contentNode.requestFocus());
    }

    public void setCenterVgrow() {
        // with content box enclosed in Group, the content pane does not size with window.
        // this restores sizing (inserting height from top box manually)

        double fixedHeight = (currentExerciseView.getStatementHeight() + currentExerciseView.getCommentHeight())  * scale + statusBar.getHeight() + 250;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        contentHeightProperty.bind(centerHeightProperty);

    }

    private void updateZoom(int zoom) {
        scale = (double)zoom/100.0;
        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(minStageWidth, controlNode.getLayoutBounds().getWidth() + PrintUtilities.getPageWidth() * scale + 55));
        setCenterVgrow();
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

        if (!editToolbar.getItems().contains(saveButton)) {
            editToolbar.getItems().add(0, saveButton);
        }

        if (!fontsToolbar.getItems().contains(zoomSpinner)) {
            fontsToolbar.getItems().addAll(zoomLabel, zoomSpinner);
        }
        VBox topBox = new VBox(menuBar, editToolbar, fontsToolbar, paragraphToolbar);
        topBox.layout();
        borderPane.topProperty().setValue(topBox);
    }

    private void closeWindow() {
        KeyboardDiagram.getInstance().close();
        stage.close();
    }



    public void setCurrentExerciseView(ExerciseView currentExerciseView) {
        this.currentExerciseView = currentExerciseView;
    }

    public void setContentHeightProperty(DoubleProperty contentHeight) {
        this.contentHeightProperty = contentHeight;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public MenuItem getSaveExerciseItem() {
        return saveExerciseItem;
    }

    public MenuItem getSaveAsExerciseItem() {
        return saveAsExerciseItem;
    }

    public MenuItem getSaveAssignmentItem() {
        return saveAssignmentItem;
    }

    public MenuItem getSaveAsAssignmentItem() {
        return saveAsAssignmentItem;
    }

    public MenuItem getOpenExerciseItem() {
        return openExerciseItem;
    }

    public MenuItem getNewAssignmentItem() {
        return newAssignmentItem;
    }

    public MenuItem getOpenAssignmentItem() {
        return openAssignmentItem;
    }

    public MenuItem getClearExerciseItem() {
        return clearExerciseItem;
    }

    public MenuItem getCreateNewExerciseItem() {
        return createNewExerciseItem;
    }

    public MenuItem getCreateRevisedExerciseItem() {
        return createRevisedExerciseItem;
    }

    public MenuItem getPrintExerciseItem() {
        return printExerciseItem;
    }

    public MenuItem getExportToPDFExerciseItem() {
        return exportToPDFExerciseItem;
    }

    public MenuItem getCloseExerciseItem() {
        return closeExerciseItem;
    }
}
