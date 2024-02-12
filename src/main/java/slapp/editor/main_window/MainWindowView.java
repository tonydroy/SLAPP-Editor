package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.main_window.assignment.AssignmentHeader;
import slapp.editor.main_window.assignment.AssignmentHeaderItem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Math.rint;

public class MainWindowView {
    private Stage stage = EditorMain.mainStage;
    private MainWindow mainWindow;
    private ToolBar editToolbar = new ToolBar();
    private ToolBar fontsToolbar = new ToolBar();
    private ToolBar paragraphToolbar = new ToolBar();
    private ToolBar insertToolbar = new ToolBar();
    private ToolBar kbdDiaToolBar = new ToolBar();
    private double scale = 1.0;
    MenuBar menuBar;
    private VBox topBox = new VBox();
    private ScrollPane centerPane;
    private StackPane centerStackPane;
    private VBox centerBox;
    private Spinner<Integer> zoomSpinner;
    private Label zoomLabel;
    double minStageWidth = 860.0;
    private BorderPane borderPane = new BorderPane();
    private Scene mainScene;
    private ExerciseView currentExerciseView;
    private Node statementNode;
    private Node contentNode;
    private DecoratedRTA commentDecoratedRTA;
    private DecoratedRTA lastFocussedDRTA;
    private Node commentNode;
    private Node controlNode;
    private VBox statusBar;
    private HBox upperStatusBox;
    private FlowPane lowerStatusPane;
    private DoubleProperty contentHeightProperty;
    private DoubleProperty contentWidthProperty;

    HBox centerHBox;
    private Button saveButton;
    private CheckBox hWindowCheck;
    private Spinner<Double> hCustomSpinner;
    private CheckBox vWindowCheck;
    private Spinner<Double> vCustomSpinner;

    private ChangeListener verticalListener;
    private ChangeListener horizontalListener;

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
    private CheckMenuItem fitToPageItem = new CheckMenuItem("Fit to Page");
    Menu previousExerciseMenu = new Menu();
    Menu nextExerciseMenu = new Menu();
    Menu goToExerciseMenu = new Menu();
    Menu assignmentCommentMenu = new Menu();
    HBox menuBox;
    Group testGroup;



    DoubleProperty fixedValueProperty;
    double windowFraction;



    public MainWindowView(MainWindow controller) {
        this.mainWindow = controller;
        setupWindow();
    }

    private void setupWindow() {



        Menu assignmentMenu = new Menu("Assignment");
        Menu exerciseMenu = new Menu("Exercise");
        Menu printMenu = new Menu("Print");
        Menu helpMenu = new Menu("Help");
        fitToPageItem.setStyle("-fx-text-fill: green");

        menuBar = new MenuBar(assignmentMenu, exerciseMenu, previousExerciseMenu, nextExerciseMenu, goToExerciseMenu, assignmentCommentMenu, printMenu, helpMenu);
        exerciseMenu.getItems().addAll(saveExerciseItem, saveAsExerciseItem, openExerciseItem, clearExerciseItem, closeExerciseItem, printExerciseItem, exportToPDFExerciseItem, createRevisedExerciseItem, createNewExerciseItem);
        assignmentMenu.getItems().addAll(saveAssignmentItem, saveAsAssignmentItem, openAssignmentItem, closeAssignmentItem, printAssignmentItem, exportAssignmentToPDFItem, createRevisedAssignmentItem, createNewAssignmentItem);
        printMenu.getItems().addAll(printExerciseItemPM, exportExerciseToPDFItemPM, printAssignmentItemPM, exportAssignmentToPDFItemPM, pageSetupItem, exportSetupItem, fitToPageItem);

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

        saveButton = new Button();
        FontIcon saveIcon = new FontIcon(LineAwesomeSolid.SAVE);
        saveIcon.setIconSize(20);
        saveButton.setGraphic(saveIcon);
        saveButton.setTooltip(new Tooltip("Save assignment if open and otherwise exercise"));





        hWindowCheck = new CheckBox("Win");
        hWindowCheck.setTooltip(new Tooltip("Fix width by window"));
        hCustomSpinner = new Spinner<>(5.0, 999.0, 100.0, 5.0);
        hCustomSpinner.setPrefWidth(60);
        hCustomSpinner.setTooltip(new Tooltip("Width as % of selected paper"));


        hCustomSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = hCustomSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = hCustomSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });






        vWindowCheck = new CheckBox("Win");
        vWindowCheck.setTooltip(new Tooltip("Fix height by window"));
        vCustomSpinner = new Spinner<>(5.0, 999.0, 100.0, 5.0);
        vCustomSpinner.setPrefWidth(60);
        vCustomSpinner.setTooltip(new Tooltip("Height as % of selected paper"));

        vCustomSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = vCustomSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = vCustomSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        centerBox = new VBox();
        centerBox.setSpacing(3);

        //centering interacts wierdly with zoom
 //       centerHBox = new HBox(centerBox);
 //       centerHBox.setAlignment(Pos.CENTER);


    /*
        When the centerPane has the new Group(centerHBox) as member, zoom works as one would expect - the scroll pane
        responds to the layout bounds of the scaled group.  But, in this case, RTA crashes two ways: Typing any char
        results in ConcurrentModificationException (from ParagraphTile 352). And if the window is dragged with
        HSize Win checked there is a NullPointerException (from ParagraphTile 202).  There are no exceptions when
        the scroll pane has just center box -- but then zoom doesn't generate properly scroll bars.  Revisit this issue
        if and whenRTA gets an update.
     */
//        centerPane = new ScrollPane(new Group(centerHBox));
        centerPane = new ScrollPane(centerBox);   //or centerHBox

//        centerHBox.minWidthProperty().bind(Bindings.createDoubleBinding(() -> centerPane.getViewportBounds().getWidth(), centerPane.viewportBoundsProperty()));

        centerPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        centerPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        centerPane.setStyle("-fx-background-color: transparent");

        borderPane.setCenter(centerPane);
        borderPane.setMargin(centerPane, new Insets(10,0,0,0));


        hWindowCheck.setOnAction(e -> {

            updateContentWidthProperty();
 //           if (hWindowCheck.isSelected()) updateWindowH();
 //           else updateCustomH();
        });
        hWindowCheck.setSelected(false);
        hCustomSpinner.setDisable(false);

        vWindowCheck.setOnAction(e -> {

            updateContentHeightProperty();
//            if (vWindowCheck.isSelected()) updateupdateWindowV();
  //          else updateCustomV();
        });
        vWindowCheck.setSelected(true);
        vCustomSpinner.setDisable(true);


        statusBar = new VBox(5);
        upperStatusBox = new HBox(40);
        lowerStatusPane = new FlowPane();
        lowerStatusPane.setHgap(40);
        statusBar.setPadding(new Insets(0,20,20,20));

        statusBar.getChildren().addAll(upperStatusBox, lowerStatusPane);
        borderPane.setBottom(statusBar);

        borderPane.setLeft(controlNode);

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

        stage.setHeight(800); //this added to keep SlappLogoView on screen (adjust with actual logo) ok in general?
        stage.setOnCloseRequest(e -> {
            e.consume();
            closeWindow();
        });
        stage.show();
    }

    public void setupExercise() {


        this.currentExerciseView = (ExerciseView) mainWindow.currentExercise.getExerciseView();
        this.statementNode = currentExerciseView.getExerciseStatementNode();
        this.contentNode = currentExerciseView.getExerciseContentNode();
        this.commentDecoratedRTA = currentExerciseView.getExerciseComment();
        this.commentNode = commentDecoratedRTA.getEditor();
        this.controlNode = currentExerciseView.getExerciseControl();
        this.contentHeightProperty = currentExerciseView.getContentHeightProperty();
        this.contentWidthProperty = currentExerciseView.getContentWidthProperty();

        statementNode.minHeight(currentExerciseView.getStatementHeight());
        commentNode.minHeight(currentExerciseView.getCommentHeight());

        centerBox.getChildren().clear();
        centerBox.getChildren().addAll(commentNode, statementNode, contentNode);



        centerBox.setVgrow(contentNode, Priority.ALWAYS);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        upperStatusBox.getChildren().clear();
        lowerStatusPane.getChildren().clear();
        upperStatusBox.getChildren().add(new Label("Exercise: " + currentExerciseView.getExerciseName()));
        lowerStatusPane.getChildren().add(new Label("Date: " + dtf.format(LocalDateTime.now())));

        borderPane.setLeft(controlNode);



        verticalListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
        //        vCustomSpinner.getValueFactory().setValue(Math.rint((Double) nv / PrintUtilities.getPageHeight() * 100));
                vCustomSpinner.getValueFactory().setValue((double) (Math.round((Double) nv / PrintUtilities.getPageHeight() * 20 ) * 5));
            }
        };


        horizontalListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {

                hCustomSpinner.getValueFactory().setValue((double) (Math.round((Double) nv / PrintUtilities.getPageWidth() * 20 ) * 5));
            }
        };



        centerBox.layout();


        contentHeightProperty.removeListener(verticalListener);
        contentHeightProperty.unbind();
        double fixedHeight = (currentExerciseView.getStatementHeight() + currentExerciseView.getCommentHeight() + currentExerciseView.getContentFixedHeight()) * scale + statusBar.getHeight() + 270;
        contentHeightProperty.setValue((stage.getHeight() - fixedHeight)/scale );
        vCustomSpinner.getValueFactory().setValue((double) Math.round(contentHeightProperty.getValue()/PrintUtilities.getPageHeight() * 20) * 5);



        updateExerciseHeight();



        contentHeightProperty.removeListener(verticalListener);
        contentHeightProperty.unbind();
        updateContentHeightProperty();

        contentWidthProperty.removeListener(horizontalListener);
        contentWidthProperty.unbind();
        updateContentWidthProperty();

        Platform.runLater(() -> contentNode.requestFocus());
    }


    public void updateContentHeightProperty() {
        contentHeightProperty = currentExerciseView.getContentHeightProperty();
        double contentHeight = currentExerciseView.getContentHeight();
        double spinnerMin = Math.round(contentHeight/PrintUtilities.getPageHeight() * 20) * 5;
        double spinnerVal = vCustomSpinner.getValue();
        vCustomSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(spinnerMin, 999.0, Math.max(spinnerMin, spinnerVal), 5.0));
        contentHeightProperty().unbind();
        contentHeightProperty.setValue(Math.max(contentHeight, PrintUtilities.getPageHeight() * vCustomSpinner.getValue()/100.0));
        updateExerciseHeight();
    }

    public void updateContentWidthProperty() {
        contentWidthProperty = currentExerciseView.getContentWidthProperty();
        double contentWidth = currentExerciseView.getContentWidth();
        double spinnerMin = Math.max(100, Math.round(contentWidth/PrintUtilities.getPageWidth() * 20.0) * 5);
        double spinnerVal = hCustomSpinner.getValue();
//        hCustomSpinner.getValueFactory().setValue(spinnerMin);
        hCustomSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(spinnerMin, 999.0, Math.max(spinnerMin, spinnerVal), 5.0));
        contentWidthProperty.unbind();
        contentWidthProperty.setValue(Math.max(contentWidth, PrintUtilities.getPageWidth() * hCustomSpinner.getValue()/100.0));
        updateExerciseWidth();
    }

    public void updateExerciseHeight() {
        if (vWindowCheck.isSelected()) updateWindowV();
        else  updateCustomV();
    }

    public void updateWindowV() {
        vCustomSpinner.setDisable(true);
        centerPane.setFitToHeight(true);
        contentHeightProperty.unbind();
        setCenterVgrow();
        vCustomSpinner.getValueFactory().setValue(Math.max(vCustomSpinner.getValue(), (double) (Math.round((Double) contentHeightProperty().getValue() / PrintUtilities.getPageHeight() * 20 ) * 5)));
        contentHeightProperty.addListener(verticalListener);
    }
    public void updateCustomV() {
        contentHeightProperty.removeListener(verticalListener);
        vCustomSpinner.setDisable(false);
        contentHeightProperty.unbind();
        centerPane.setFitToHeight(false);
  //      vCustomSpinner.getValueFactory().setValue(rint((contentHeightProperty.getValue() / PrintUtilities.getPageHeight() * 100.0)));
        vCustomSpinner.getValueFactory().setValue((double) (Math.round((Double) contentHeightProperty.getValue() / PrintUtilities.getPageHeight() * 20 ) * 5));

        contentHeightProperty.bind(Bindings.multiply(PrintUtilities.getPageHeight(), DoubleProperty.doubleProperty(vCustomSpinner.getValueFactory().valueProperty()).divide(100.0)));
    }

    public void updateExerciseWidth() {
        if (hWindowCheck.isSelected()) updateWindowH();
        else updateCustomH();
    }
    public void updateWindowH(){
        hCustomSpinner.setDisable(true);
        centerPane.setFitToWidth(true);
        contentWidthProperty.unbind();
        setCenterHgrow();
        hCustomSpinner.getValueFactory().setValue(Math.max(hCustomSpinner.getValue(), (double) (Math.round((Double) contentWidthProperty().getValue() / PrintUtilities.getPageWidth() * 20 ) * 5)));
        contentWidthProperty.addListener(horizontalListener);
    }
    public void updateCustomH(){
        contentWidthProperty.removeListener(horizontalListener);
        hCustomSpinner.setDisable(false);
        contentWidthProperty.unbind();
        centerPane.setFitToWidth(false);
        hCustomSpinner.getValueFactory().setValue((double) (Math.round((Double) contentWidthProperty.getValue() / PrintUtilities.getPageWidth() * 20 ) * 5));
//        hCustomSpinner.getValueFactory().setValue(rint((contentWidthProperty.getValue() / PrintUtilities.getPageWidth() * 100.0)));
        contentWidthProperty.bind(Bindings.multiply(PrintUtilities.getPageWidth(), DoubleProperty.doubleProperty(hCustomSpinner.getValueFactory().valueProperty()).divide(100.0)));


    }

    public void setCenterVgrow() {
        if (vWindowCheck.isSelected()) {


            double fixedHeight = (currentExerciseView.getStatementHeight() + currentExerciseView.getCommentHeight() + currentExerciseView.getContentFixedHeight()) * scale + statusBar.getHeight() + 270;
//            DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
//            DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
 //           contentHeightProperty.bind(Bindings.divide(stage.heightProperty().subtract(fixedValueProperty), scaleProperty));
            /*
            DoubleProperty centerHeightProperty = new SimpleDoubleProperty();
            centerHeightProperty.bind(Bindings.divide(stage.heightProperty().subtract(fixedHeight), scale));
            contentHeightProperty.bind(centerHeightProperty);

             */

            contentHeightProperty.bind(Bindings.divide(stage.heightProperty().subtract(fixedHeight), scale));

        }
    }

    public void setCenterHgrow() {
        if (hWindowCheck.isSelected()) {
            contentWidthProperty.bind(Bindings.divide(stage.widthProperty().subtract(controlNode.getLayoutBounds().getWidth() + 30.0), scale ));
        }
    }


    public void setUpLowerAssignmentBar() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        upperStatusBox.getChildren().clear();
        lowerStatusPane.getChildren().clear();

        AssignmentHeader header = mainWindow.getCurrentAssignment().getHeader();
        int exerciseNum = mainWindow.getAssignmentIndex() + 1;

        upperStatusBox.getChildren().addAll(new Label("Student Name: " + header.getStudentName()),
                new Label("Exercise: " + currentExerciseView.getExerciseName() + " (" + exerciseNum + "/" + mainWindow.getCurrentAssignment().getExerciseModels().size() + ")"),
                new Label("Assignment: " + header.getAssignmentName()),
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
        keyboardDiagram.initialize(lastFocussedDRTA);
        keyboardDiagram.update();

        centerBox.getTransforms().clear();
        centerBox.getTransforms().add(new Scale(scale, scale));

        setCenterVgrow();
    }


    public void editorInFocus(DecoratedRTA decoratedRTA, ControlType control) {
        lastFocussedDRTA = decoratedRTA;
        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.initialize(decoratedRTA);
        if (keyboardDiagram.isShowing()) {
            keyboardDiagram.updateAndShow();
        }

        editToolbar = decoratedRTA.getEditToolbar();
        fontsToolbar = decoratedRTA.getFontsToolbar();
        paragraphToolbar = decoratedRTA.getParagraphToolbar();
        kbdDiaToolBar = decoratedRTA.getKbdDiaToolbar();

        if (kbdDiaToolBar.getItems().isEmpty()) {

            kbdDiaToolBar.getItems().addAll(zoomLabel, zoomSpinner, new Label("    V Size:"), vCustomSpinner, new Label("/"), vWindowCheck,
                    new Label("    H Size:"), hCustomSpinner, new Label("/"), hWindowCheck, new Label("    "), decoratedRTA.getKeyboardDiagramButton(), new Label("  "), saveButton);

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
        }

        HBox editAndKbdBox = new HBox(editToolbar, kbdDiaToolBar);
        editAndKbdBox.setHgrow(kbdDiaToolBar, Priority.ALWAYS);

        topBox = new VBox(menuBar, paragraphToolbar, fontsToolbar, editAndKbdBox);
        topBox.layout();
        borderPane.topProperty().setValue(topBox);
    }

    private void closeWindow() {
        if (mainWindow.checkCloseWindow()) {
            KeyboardDiagram.getInstance().close();
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

        Scene tempScene = new Scene(commentArea);
        Stage tempStage = new Stage();
        tempStage.initStyle(StageStyle.TRANSPARENT);
        tempStage.toBack();
        tempStage.setScene(tempScene);
        tempStage.show();
        RichTextAreaSkin commentRTASkin = ((RichTextAreaSkin) commentArea.getSkin());
        commentArea.setDocument(header.getComment());
        commentArea.setContentAreaWidth(PrintUtilities.getPageWidth());
        commentArea.setPrefWidth(PrintUtilities.getPageWidth());
        double commentHeight = commentRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
        commentArea.setPrefHeight(Math.max(70, commentHeight + 35.0));
        commentArea.requestFocus();
        tempStage.close();

        headerBox.getChildren().addAll(nameBox,itemsBox, separator, commentArea );
        headerBox.setPadding(new Insets(0,0,20,0));
        return headerBox;
    }

    public void setCurrentExerciseView(ExerciseView currentExerciseView) {
        this.currentExerciseView = currentExerciseView;
    }

    public void setContentHeightProperty(DoubleProperty contentHeight) {
        this.contentHeightProperty = contentHeight;
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

    public DoubleProperty contentHeightProperty() {
        return contentHeightProperty;
    }

    public ChangeListener getVerticalListener() {
        return verticalListener;
    }

    public DoubleProperty contentWidthProperty() { return contentWidthProperty; }
    public ChangeListener getHorizontalListener() {return horizontalListener; }

    public double getMinStageWidth() {
        return minStageWidth;
    }

    public boolean isFitToPageSelected() {return fitToPageItem.isSelected(); }
}
