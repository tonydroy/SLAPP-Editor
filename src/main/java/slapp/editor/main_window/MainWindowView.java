package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.*;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.main_window.assignment.AssignmentHeader;
import slapp.editor.main_window.assignment.AssignmentHeaderItem;

import javax.xml.stream.EventFilter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainWindowView {
    private Stage stage = EditorMain.mainStage;
    private MainWindow mainWindow;
    private ToolBar editToolbar = new ToolBar();
    private ToolBar fontsToolbar = new ToolBar();
    private ToolBar paragraphToolbar = new ToolBar();
    private ToolBar insertToolbar = new ToolBar();
    private ToolBar kbdDiaToolBar = new ToolBar();
    private ToolBar sizeToolBar = new ToolBar();
    private double scale = 1.0;
    MenuBar menuBar;
    private VBox topBox = new VBox();

    private Pane spacerPane;
    private ScrollPane centerPane;
    private StackPane centerStackPane;
    private VBox centerBox;
    private Spinner<Integer> zoomSpinner;
    private Label zoomLabel;
    double minStageWidth = 870.0;
    private BorderPane borderPane = new BorderPane();
    private Scene mainScene;
    private ExerciseView currentExerciseView;
    private Node statementNode;
    private Node contentNode;
    private DecoratedRTA commentDecoratedRTA;
    private DecoratedRTA lastFocusedDRTA;
    private DecoratedRTA dummyDRTA = new DecoratedRTA();
    private Node commentNode;
    private Node leftControlNode;
    private Node rightControlNode;
    private VBox statusBar;
    private HBox upperStatusBox;
    private FlowPane lowerStatusPane;
    HBox centerHBox;
    private Button saveButton;
    private CheckBox hWindowCheck;
    private Spinner<Double> horizontalSizeSpinner;
    private CheckBox vWindowCheck;
    private Spinner<Double> verticalSizeSpinner;

    private ChangeListener verticalListener;
    private ChangeListener horizontalListener;

    private RichTextArea dummyRTA;
    private Group dummyRoot;
    private Scene dummyScene;
    private Stage dummyStage;
    private RichTextAreaSkin dummyRTASkin;



    private MenuItem createNewExerciseItem = new MenuItem("Create New");
    private MenuItem createRevisedExerciseItem = new MenuItem("Create Revised");
    private MenuItem saveExerciseItem = new MenuItem("Save");
    private MenuItem saveAsExerciseItem = new MenuItem("Save As");
    private MenuItem openExerciseItem = new MenuItem("Open");
    private MenuItem printExerciseItem = new MenuItem("Print");
    private MenuItem exportToPDFExerciseItem = new MenuItem("Export to PDF");
    private MenuItem clearExerciseItem = new MenuItem("Reset");
    private MenuItem closeExerciseItem = new MenuItem("Close");
    private MenuItem saveAssignmentItem = new MenuItem("Save");
    private MenuItem saveAsAssignmentItem = new MenuItem("Save As");
    private MenuItem openAssignmentItem = new MenuItem("Open");
    private MenuItem closeAssignmentItem = new MenuItem("Close");
    private MenuItem printAssignmentItem = new MenuItem("Print");
    private MenuItem exportAssignmentToPDFItem = new MenuItem("Export to PDF");
    private MenuItem createNewAssignmentItem = new MenuItem("Create New");
    private MenuItem createRevisedAssignmentItem = new MenuItem("Create Revised");
    private MenuItem exportExerciseToPDFItemPM = new MenuItem("Export Exercise");
    private MenuItem printExerciseItemPM = new MenuItem("Print Exercise");
    private MenuItem printAssignmentItemPM = new MenuItem("Print Assignment");
    private MenuItem exportAssignmentToPDFItemPM = new MenuItem("Export Assignment");
    private MenuItem pageSetupItem = new MenuItem("Page Setup");
    private MenuItem exportSetupItem = new MenuItem("Export Setup");
    private MenuItem scaleSetupItem = new MenuItem("Scale Setup");



    private MenuItem commonElementsTextItem;
    private MenuItem contextualTextItem;
    private MenuItem aboutItem;

    private MenuItem quickStartItem;
    private MenuItem slappEditorItem;
    private MenuItem verticalTreeItem;
    private MenuItem horizontalTreeItem;
    private MenuItem truthTableItem;
    private MenuItem derivationItem;
    private MenuItem instructorInfoItem;
    private MenuItem reportItem;
    Menu previousExerciseMenu = new Menu();
    Menu nextExerciseMenu = new Menu();
    Menu goToExerciseMenu = new Menu();
    Menu assignmentCommentMenu = new Menu();
    private static Label progressLabel;
    public static ProgressIndicator progressIndicator;
//    public static Text progressIndicator;

    public static TextField txtHeightIndicator;
    DoubleProperty scalePageHeight = new SimpleDoubleProperty();
    DoubleProperty scalePageWidth = new SimpleDoubleProperty();












    public MainWindowView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    private void setupWindow() {

        setUpDummyWindow();




        Menu assignmentMenu = new Menu("Assignment");
        Menu exerciseMenu = new Menu("Exercise");
        Menu printMenu = new Menu("Print");
        Menu helpMenu = new Menu("Help");

        Text filmItemGraphic0 = new Text("\uf008");
        filmItemGraphic0.setStyle("-fx-font-family: la-solid-900");
        quickStartItem = new MenuItem("Intro/Quick Start", filmItemGraphic0);
        Text filmItemGraphic1 = new Text("\uf008");
        filmItemGraphic1.setStyle("-fx-font-family: la-solid-900");
        slappEditorItem = new MenuItem("SLAPP editor", filmItemGraphic1);
        Text filmItemGraphic2 = new Text("\uf008");
        filmItemGraphic2.setStyle("-fx-font-family: la-solid-900");
        verticalTreeItem = new MenuItem("Vertical Trees", filmItemGraphic2);
        Text filmItemGraphic3 = new Text("\uf008");
        filmItemGraphic3.setStyle("-fx-font-family: la-solid-900");
        horizontalTreeItem = new MenuItem("Horizontal Trees", filmItemGraphic3);
        Text filmItemGraphic4 = new Text("\uf008");
        filmItemGraphic4.setStyle("-fx-font-family: la-solid-900");
        truthTableItem = new MenuItem("Truth Tables", filmItemGraphic4);
        Text filmItemGraphic5 = new Text("\uf008");
        filmItemGraphic5.setStyle("-fx-font-family: la-solid-900");
        derivationItem = new MenuItem("Derivations", filmItemGraphic5);
        Text filmItemGraphic6 = new Text("\uf008");
        filmItemGraphic6.setStyle("-fx-font-family: la-solid-900");
        instructorInfoItem = new MenuItem("Instructor Info", filmItemGraphic6);


        Text textItemGraphic1 = new Text("\uf15c");
        textItemGraphic1.setStyle("-fx-font-family: la-solid-900");
        commonElementsTextItem = new MenuItem("General Info", textItemGraphic1);
        Text textItemGraphic2 = new Text("\uf15c");
        textItemGraphic2.setStyle("-fx-font-family: la-solid-900");
        contextualTextItem = new MenuItem("Contextual", textItemGraphic2);
        Text textItemGraphic3 = new Text("\uf15c");
        textItemGraphic3.setStyle("-fx-font-family: la-solid-900");
        aboutItem = new MenuItem("About", textItemGraphic3);

        Text reportItemGraphic = new Text("\uf4ad");
        reportItemGraphic.setStyle("-fx-font-family: la-solid-900");
        reportItem = new MenuItem("Comment/Report", reportItemGraphic);



        menuBar = new MenuBar(exerciseMenu, assignmentMenu, previousExerciseMenu, nextExerciseMenu, goToExerciseMenu, assignmentCommentMenu, printMenu, helpMenu);
        exerciseMenu.getItems().addAll(saveExerciseItem, saveAsExerciseItem, openExerciseItem, clearExerciseItem, closeExerciseItem, printExerciseItem, exportToPDFExerciseItem, createRevisedExerciseItem, createNewExerciseItem);
        assignmentMenu.getItems().addAll(saveAssignmentItem, saveAsAssignmentItem, openAssignmentItem, closeAssignmentItem, printAssignmentItem, exportAssignmentToPDFItem, createRevisedAssignmentItem, createNewAssignmentItem);
        printMenu.getItems().addAll(printExerciseItemPM, exportExerciseToPDFItemPM, printAssignmentItemPM, exportAssignmentToPDFItemPM, exportSetupItem, pageSetupItem, scaleSetupItem);
        helpMenu.getItems().addAll(quickStartItem, slappEditorItem, verticalTreeItem, truthTableItem, horizontalTreeItem, derivationItem, instructorInfoItem, commonElementsTextItem, contextualTextItem, aboutItem, reportItem);
/*
        if (EditorMain.os.startsWith("Mac")) {
            menuBar.setUseSystemMenuBar(true);
        }
        
 */

        zoomLabel = new Label(" Zoom ");
        zoomSpinner = new Spinner(25, 500, 100, 5);
        zoomSpinner.setPrefSize(60,25);
        zoomSpinner.setTooltip(new Tooltip("Window zoom as percentage of normal"));
        zoomSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = zoomSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = zoomSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
            updateZoom(nv);
        });

        saveButton = new Button("\uf0c7");  //LineAwesome.SAVE
        saveButton.getStyleClass().add("lasolid-icon");
        saveButton.setTooltip(new Tooltip("Save assignment if open and otherwise exercise"));

        if (EditorMain.secondaryCopy) {
            saveButton.setDisable(true);
            saveExerciseItem.setDisable(true);
            saveAsExerciseItem.setDisable(true);
            saveAssignmentItem.setDisable(true);
            saveAsAssignmentItem.setDisable(true);
        }

        hWindowCheck = new CheckBox("Win");
        hWindowCheck.setTooltip(new Tooltip("Fix width by window"));


        horizontalSizeSpinner = new Spinner<>(0.0, 999.0, 0.0, 5.0);
        horizontalSizeSpinner.setPrefWidth(60);
        horizontalSizeSpinner.setTooltip(new Tooltip("Width as % of selected paper"));
        horizontalSizeSpinner.setDisable(true);



        vWindowCheck = new CheckBox("Win");
        vWindowCheck.setTooltip(new Tooltip("Fix height by window"));

        verticalSizeSpinner = new Spinner<>(0.0, 999.0, 0.0, 5.0);
        verticalSizeSpinner.setPrefWidth(60);
        verticalSizeSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        verticalSizeSpinner.setDisable(true);





        progressLabel = new Label("");
//        progressLabel.setTextFill(Color.RED);
//        progressLabel.setVisible(false);

/*
        progressIndicator = new Text("\uf110");
        progressIndicator.setFill(Color.DEEPSKYBLUE);
        progressIndicator.setStyle("-fx-font:  28 la-solid-900");
        progressIndicator.setVisible(false);

 */
        txtHeightIndicator = new TextField("100");
        txtHeightIndicator.setPrefWidth(40);
        txtHeightIndicator.setDisable(true);






        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefWidth(25);
        progressIndicator.setPrefHeight(25);
        progressIndicator.setVisible(false);





        sizeToolBar.setStyle("-fx-spacing: 10");
        sizeToolBar.getItems().addAll(zoomLabel, zoomSpinner, new Label(" T Ht:"), txtHeightIndicator,  new Label("V Sz:"), verticalSizeSpinner,
                new Label("H Sz:"), horizontalSizeSpinner, new Label(" "), saveButton, progressIndicator);

        sizeToolBar.setPrefHeight(38);






        centerBox = new VBox();
        centerBox.setSpacing(3);
//        centerBox.setStyle("-fx-border-color: red");


        /*
        When the centerPane has the new Group(centerHBox) as member, zoom works as one would expect - the scroll pane
        responds to the layout bounds of the scaled group.  But, in this case, RTA crashes two ways: Typing any char
        results in ConcurrentModificationException (from ParagraphTile 352). And if the window is dragged with
        HSize Win checked there is a NullPointerException (from ParagraphTile 202).  There are no exceptions when
        the scroll pane has just center box -- but then zoom doesn't generate properly scroll bars.

        For the current solution, see comment in ScrollPaneTest
     */


        spacerPane = new Pane();
        spacerPane.prefHeightProperty().bind(centerBox.heightProperty());
        spacerPane.prefWidthProperty().bind(centerBox.widthProperty());
        Group group = new Group(spacerPane);
        AnchorPane comboPane = new AnchorPane(group, centerBox);

        centerPane = new ScrollPane(comboPane);

//        centerBox.setStyle("-fx-border-color: red; -fx-background-color: red; -fx-border-width: 3");






//        centerPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        centerPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        centerPane.setStyle("-fx-background-color: transparent");

        borderPane.setCenter(centerPane);
        borderPane.setMargin(centerPane, new Insets(10,0,0,0));
        borderPane.getCenter().setStyle("-fx-background-color: transparent");











        statusBar = new VBox(5);
        upperStatusBox = new HBox(40);
        lowerStatusPane = new FlowPane();
        lowerStatusPane.setHgap(40);
        statusBar.setPadding(new Insets(0,20,20,20));

        statusBar.getChildren().addAll(upperStatusBox, lowerStatusPane);
        borderPane.setBottom(statusBar);

        borderPane.setLeft(leftControlNode);
        borderPane.setRight(rightControlNode);

  //      borderPane.setTop(menuBar);

        mainScene = new Scene(borderPane);
        mainScene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());

        stage.setScene(mainScene);
        stage.setTitle("SLAPP Editor");
        stage.setMinWidth(minStageWidth);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double mainWindowX = Math.max(0.0, (bounds.getMaxX() - bounds.getMinX())/8);
        double mainWindowY = 40;
        stage.setX(mainWindowX);
        stage.setY(mainWindowY);

        stage.setHeight(860); //this added to keep SlappLogoView on screen (adjust with actual logo) ok in general? 800
        stage.setOnCloseRequest(e -> {
            e.consume();
            closeWindow();
        });

//        stage.setIconified(false);
        stage.show();




    }




    public void setupDummyWindowRTASize() {
        dummyRTA.prefHeightProperty().bind(Bindings.multiply(scalePageHeight, 5.0));
        dummyRTA.prefWidthProperty().bind(scalePageWidth);
    }
    private void setUpDummyWindow() {
        dummyRTA = new RichTextArea(EditorMain.mainStage);
        dummyRTA.getStylesheets().add("slappTextArea.css");


        dummyRoot = new Group();
        dummyRoot.getChildren().add(dummyRTA);
        dummyScene = new Scene(dummyRoot);
        dummyStage = new Stage();
        dummyStage.setScene(dummyScene);

        dummyStage.initOwner(EditorMain.mainStage);
        dummyStage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon32x32.png")));
        dummyStage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));

        dummyStage.toBack();
        dummyStage.setOnCloseRequest(e -> {e.consume();});
        dummyStage.setOpacity(0);
        dummyStage.show();
        dummyRTASkin = ((RichTextAreaSkin) dummyRTA.getSkin());
    }

    public double getRTATextHeight(RichTextArea rta) {
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        Document doc = rta.getDocument();
        dummyRTA.getActionFactory().open(doc).execute(new ActionEvent());
        dummyRoot.applyCss();
        dummyRoot.layout();
        double height = dummyRTASkin.getComputedHeight();
        double scaledHeight = 5.0 +  100 * (height / scalePageHeight.get());
        String strHeight = String.valueOf(Math.round(scaledHeight));
        txtHeightIndicator.setText(strHeight);
        return height;
    }

    public void setUpLeftControl(Node leftControl) {
        borderPane.setLeft(leftControl);
    }

    public void setupExercise() {


        this.currentExerciseView = (ExerciseView) mainWindow.getCurrentExercise().getExerciseView();
        this.statementNode = currentExerciseView.getExerciseStatementNode();
        this.contentNode = currentExerciseView.getExerciseContentNode();
        this.commentDecoratedRTA = currentExerciseView.getExerciseComment();
        this.commentNode = commentDecoratedRTA.getEditor();
        this.leftControlNode = currentExerciseView.getExerciseControl();
        this.rightControlNode = currentExerciseView.getRightControl();

//        this.contentWidthProperty = currentExerciseView.getContentWidthProperty();

        statementNode.minHeight(currentExerciseView.getStatementHeight());
        commentNode.minHeight(currentExerciseView.getCommentHeight());


        //this prevents scrollpane from jumping to top (esp in derivations)
        commentNode.setFocusTraversable(false);

        centerBox.getChildren().clear();
        centerBox.getChildren().addAll(commentNode, statementNode, contentNode);







 //       centerBox.setVgrow(contentNode, Priority.ALWAYS);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        upperStatusBox.getChildren().clear();
        lowerStatusPane.getChildren().clear();
        upperStatusBox.getChildren().add(new Label("Exercise: " + currentExerciseView.getExerciseName()));
        lowerStatusPane.getChildren().add(new Label("Date: " + dtf.format(LocalDateTime.now())));

        borderPane.setLeft(leftControlNode);
        borderPane.setRight(rightControlNode);



//        borderPane.setTop(menuBar);



        centerBox.layout();





        Platform.runLater(() -> contentNode.requestFocus());
    }

    public void updateSizeSpinners(Spinner<Double> height, Spinner<Double> width) {
        sizeToolBar.getItems().remove(5);
        sizeToolBar.getItems().add(5, height);
        sizeToolBar.getItems().remove(7);
        sizeToolBar.getItems().add(7, width);
    }





    public static void deactivateProgressIndicator() {
        progressLabel.setVisible(false);
        progressIndicator.setVisible(false);
    }
    public static void activateProgressIndicator(String text) {
        progressLabel.setText(text);
        progressIndicator.setVisible(true);
        progressLabel.setVisible(true);
    }



    public void updateContentHeightProperty() {

    }

    public void updateContentWidthProperty() {

    }

    public void updateExerciseHeight() {

    }

    public void updateWindowV() {

    }
    public void updateCustomV() {

    }



    public void updateExerciseWidth() {

    }
    public void updateWindowH(){

    }
    public void updateCustomH(){

    }



    public void setCenterHgrow() {

    }


    public void setUpLowerAssignmentBar() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        upperStatusBox.getChildren().clear();
        lowerStatusPane.getChildren().clear();

        AssignmentHeader header = mainWindow.getCurrentAssignment().getHeader();
        int exerciseNum = mainWindow.getAssignmentIndex() + 1;

        upperStatusBox.getChildren().addAll(new Label("Student Name: " + header.getStudentName()),
                new Label("Assignment: " + header.getAssignmentName()),
                new Label("Exercise: " + currentExerciseView.getExerciseName() + " (" + exerciseNum + "/" + mainWindow.getCurrentAssignment().getExerciseModels().size() + ")"),
                new Label("ID: " + header.getCreationID() + "-" + header.getWorkingID()) );
        lowerStatusPane.getChildren().add(new Label("Date: " + dtf.format(LocalDateTime.now())));
        for (int i = 0; i < header.getInstructorItems().size(); i++) {
            AssignmentHeaderItem headerItem = header.getInstructorItems().get(i);
            lowerStatusPane.getChildren().add(new Label(headerItem.getLabel() + ": " + headerItem.getValue()));
        }
        for (int i = 0; i < header.getStudentItems().size(); i++) {
            AssignmentHeaderItem headerItem = header.getStudentItems().get(i);
            lowerStatusPane.getChildren().add(new Label( headerItem.getLabel() + ": " + headerItem.getValue()));
        }

    }



    public void updateZoom(int zoom) {
        scale = (double)zoom/100.0;

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.updateFontSize(scale);
        keyboardDiagram.initialize(lastFocusedDRTA);
        keyboardDiagram.update();

        centerBox.getTransforms().clear();
        centerBox.getTransforms().add(new Scale(scale, scale));
        spacerPane.getTransforms().clear();
        spacerPane.getTransforms().add(new Scale(scale, scale));

    }


    public void editorInFocus(DecoratedRTA decoratedRTA, ControlType control) {



        lastFocusedDRTA = decoratedRTA;
        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.initialize(decoratedRTA);
        if (keyboardDiagram.isShowing()) {
            keyboardDiagram.updateAndShow();
        }

        editToolbar = decoratedRTA.getEditToolbar();
        fontsToolbar = decoratedRTA.getFontsToolbar();
        paragraphToolbar = decoratedRTA.getParagraphToolbar();
        kbdDiaToolBar = decoratedRTA.getKbdDiaToolbar();
        editToolbar.setPrefHeight(38);

//        if (kbdDiaToolBar.getItems().isEmpty()) {
//            kbdDiaToolBar.getItems().add(decoratedRTA.getKeyboardDiagramButton());
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
//        }

        HBox editAndKbdBox = new HBox(editToolbar, kbdDiaToolBar, sizeToolBar);

        editAndKbdBox.setHgrow(sizeToolBar, Priority.ALWAYS);




        topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox);
        topBox.layout();

        borderPane.topProperty().setValue(topBox);





    }

    public void textFieldInFocus() {
            editorInFocus(dummyDRTA, ControlType.STATEMENT);
    }

    private void closeWindow() {
        if (mainWindow.checkCloseWindow()) {
            KeyboardDiagram.getInstance().close();
            dummyStage.close();
            stage.close();
        }
    }

    public VBox getAssignmentHeader() {
        VBox headerBox = new VBox(10);
        AssignmentHeader header = mainWindow.getCurrentAssignment().getHeader();

        Label studentNameLabel = new Label(header.getStudentName());
        studentNameLabel.setStyle("-fx-font-weight: bold;");
        HBox nameBox = new HBox(studentNameLabel);
        nameBox.setAlignment(Pos.CENTER);

        VBox leftBox = new VBox(0);
        leftBox.getChildren().add(new Label("Assignment: " + header.getAssignmentName()));
        for (AssignmentHeaderItem item : header.getStudentItems()) {
            leftBox.getChildren().add(new Label(item.getLabel() + ": " + item.getValue()));
        }
        leftBox.setAlignment(Pos.TOP_LEFT);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        VBox rightBox = new VBox(0);
        rightBox.getChildren().add(new Label("Date: " + dtf.format(LocalDateTime.now())));
        for (AssignmentHeaderItem item : header.getInstructorItems()) {
            rightBox.getChildren().add(new Label(item.getLabel() + ": " + item.getValue()));
        }
        rightBox.setAlignment(Pos.TOP_LEFT);

        Region spacer = new Region();
        HBox itemsBox = new HBox(leftBox, spacer, rightBox);
        itemsBox.setHgrow(spacer, Priority.ALWAYS);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);

        RichTextArea commentArea = new RichTextArea(EditorMain.mainStage);
        commentArea.getActionFactory().open(header.getComment()).execute(new ActionEvent());
        commentArea.setContentAreaWidth(PrintUtilities.getPageWidth());
        commentArea.setPrefWidth(PrintUtilities.getPageWidth());
        commentArea.setPrefHeight(header.getCommentTextHeight() + 35);

        headerBox.getChildren().addAll(nameBox,itemsBox, separator, commentArea );
        headerBox.setPadding(new Insets(0,0,20,0));
        return headerBox;
    }

    public void setCurrentExerciseView(ExerciseView currentExerciseView) {
        this.currentExerciseView = currentExerciseView;
    }


    public Spinner<Integer> getZoomSpinner() {
        return zoomSpinner;
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

    public MenuItem getCreateNewAssignmentItem() {
        return createNewAssignmentItem;
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

    public MenuItem getPageSetupItem() {
        return pageSetupItem;
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public void setHorizontalSizeSpinner(Spinner<Double> horizontalSizeSpinner) {   this.horizontalSizeSpinner = horizontalSizeSpinner;  }

    public void setVerticalSizeSpinner(Spinner<Double> verticalSizeSpinner) {
        this.verticalSizeSpinner = verticalSizeSpinner;
    }



    public Node getStatementNode() {
        return statementNode;
    }

    public Node getContentNode() {
        return contentNode;
    }

    public Node getCommentNode() {
        return commentNode;
    }

    public MenuItem getPrintExerciseItemPM() {
        return printExerciseItemPM;
    }

    public MenuItem getExportExerciseToPDFItemPM() {
        return exportExerciseToPDFItemPM;
    }

    public MenuItem getExportSetupItem() {
        return exportSetupItem;
    }
    public MenuItem getScaleSetupItem() {
        return scaleSetupItem;
    }

    public MenuItem getCloseAssignmentItem() {
        return closeAssignmentItem;
    }

    public MenuItem getPrintAssignmentItem() {
        return printAssignmentItem;
    }

    public MenuItem getCreateRevisedAssignmentItem() {
        return createRevisedAssignmentItem;
    }

    public MenuItem getExportAssignmentToPDFItem() {
        return exportAssignmentToPDFItem;
    }

    public MenuItem getPrintAssignmentItemPM() {
        return printAssignmentItemPM;
    }

    public MenuItem getExportAssignmentToPDFItemPM() {
        return exportAssignmentToPDFItemPM;
    }

    public MenuItem getCommonElementsTextItem() {
        return commonElementsTextItem;
    }

    public MenuItem getContextualTextItem() {
        return contextualTextItem;
    }

    public MenuItem getAboutItem() {
        return aboutItem;
    }

    public MenuItem getQuickStartItem() {return quickStartItem; }
    public MenuItem getSlappEditorItem() {
        return slappEditorItem;
    }

    public MenuItem getVerticalTreeItem() {
        return verticalTreeItem;
    }

    public MenuItem getHorizontalTreeItem() {
        return horizontalTreeItem;
    }

    public MenuItem getTruthTableItem() {
        return truthTableItem;
    }

    public MenuItem getDerivationItem() {
        return derivationItem;
    }
    public MenuItem getInstructorInfoItem() { return instructorInfoItem; }

    public MenuItem getReportItem() {
        return reportItem;
    }

    public Menu getPreviousExerciseMenu() {
        return previousExerciseMenu;
    }

    public Menu getNextExerciseMenu() {
        return nextExerciseMenu;
    }

    public Menu getGoToExerciseMenu() {
        return goToExerciseMenu;
    }

    public Menu getAssignmentCommentMenu() {
        return assignmentCommentMenu;
    }

    public CheckBox getvWindowCheck() {
        return vWindowCheck;
    }



    public ChangeListener getVerticalListener() {
        return verticalListener;
    }


    public ChangeListener getHorizontalListener() {return horizontalListener; }

    public double getMinStageWidth() {
        return minStageWidth;
    }

    public double getScalePageHeight() {   return scalePageHeight.get(); }

    public DoubleProperty scalePageHeightProperty() {   return scalePageHeight;  }

    public void setScalePageHeight(double scalePageHeight) {   this.scalePageHeight.set(scalePageHeight);
    }

    public double getScalePageWidth() {   return scalePageWidth.get();   }

    public DoubleProperty scalePageWidthProperty() {    return scalePageWidth;  }

    public void setScalePageWidth(double scalePageWidth) {    this.scalePageWidth.set(scalePageWidth);   }
}
