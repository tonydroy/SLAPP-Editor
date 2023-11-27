package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.front_page.FrontPageView;
import slapp.editor.main_window.assignment.AssignmentHeader;
import slapp.editor.main_window.assignment.AssignmentHeaderItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;

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
    private VBox centerBox;
    private Spinner<Integer> zoomSpinner;
    private Label zoomLabel;
    int minStageWidth = 950;
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
    private DoubleProperty centerHeightProperty;
    private DoubleProperty contentHeightProperty;
    private Button saveButton;
    private Button updateHeightButton = new Button();
    private Label nodeHeightLabel = new Label("0");
    private Label pageHeightLabel = new Label("100");
    private Label nodePercentageLabel = new Label("0");

    private MenuItem createNewExerciseItem = new MenuItem("Create New");
    private MenuItem createRevisedExerciseItem = new MenuItem("Create Revised");
    private MenuItem saveExerciseItem = new MenuItem("Save");
    private MenuItem saveAsExerciseItem = new MenuItem("Save As");
    private MenuItem openExerciseItem = new MenuItem("Open");
    private MenuItem printExerciseItem = new MenuItem("Print");
    private MenuItem exportToPDFExerciseItem = new MenuItem("Export to PDF");
    private MenuItem clearExerciseItem = new MenuItem("Clear");
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
    Menu previousExerciseMenu = new Menu("Previous");
    Menu nextExerciseMenu = new Menu("Next");
    Menu goToExerciseMenu = new Menu("Jump");
    Menu assignmentCommentMenu = new Menu("Comment");
    HBox menuBox;









    public MainWindowView(MainWindow controller) {
        this.mainWindow = controller;
        setupWindow();
    }

    private void setupWindow() {

        //dummy items
        previousExerciseMenu.getItems().add(new MenuItem());
        nextExerciseMenu.getItems().add(new MenuItem());
        goToExerciseMenu.getItems().add(new MenuItem());
        assignmentCommentMenu.getItems().add(new MenuItem());




        Menu assignmentMenu = new Menu("Assignment");
        Menu exerciseMenu = new Menu("Exercise");
        Menu printMenu = new Menu("Print");
        Menu helpMenu = new Menu("Help");
        menuBar = new MenuBar(assignmentMenu, exerciseMenu, previousExerciseMenu, nextExerciseMenu, goToExerciseMenu, assignmentCommentMenu, printMenu, helpMenu);

        exerciseMenu.getItems().addAll(saveExerciseItem, saveAsExerciseItem, openExerciseItem, clearExerciseItem, closeExerciseItem, printExerciseItem, exportToPDFExerciseItem, createRevisedExerciseItem, createNewExerciseItem);
        assignmentMenu.getItems().addAll(saveAssignmentItem, saveAsAssignmentItem, openAssignmentItem, closeAssignmentItem, printAssignmentItem, exportAssignmentToPDFItem, createRevisedAssignmentItem, createNewAssignmentItem);
        printMenu.getItems().addAll(printExerciseItemPM, exportExerciseToPDFItemPM, printAssignmentItemPM, exportAssignmentToPDFItemPM, pageSetupItem, exportSetupItem);

        Region spacer = new Region();
        Label slashLabel = new Label("/");
        Label percentSignLabel = new Label("%");
        Label heightLabel = new Label("Block Height: ");
        menuBox = new HBox(menuBar, spacer, heightLabel, nodeHeightLabel, slashLabel, pageHeightLabel, nodePercentageLabel, percentSignLabel);
        menuBox.setStyle("-fx-background-color: aliceblue; fx-border-color: white;");
        menuBox.setPadding(new Insets(0,15,0,0));
        menuBox.setMaxWidth(minStageWidth - 10);
        menuBox.setMargin(heightLabel, new Insets(8,2,0,0));
        menuBox.setMargin(nodeHeightLabel, new Insets(8,0,0,0));
        menuBox.setMargin(slashLabel, new Insets(8,0,0,0));
        menuBox.setMargin(pageHeightLabel, new Insets(8,0,0,0));
        menuBox.setMargin(nodePercentageLabel, new Insets(8,0,0,5));
        menuBox.setMargin(percentSignLabel, new Insets(8,0,0,0));

//        menuBox.setMargin(updateHeightButton, new Insets(6,0,0,10));
        menuBox.setHgrow(spacer, Priority.ALWAYS);
//        updateHeightButton.setPadding(new Insets(1,4,1,4));

        FontIcon heightIcon = new FontIcon(LineAwesomeSolid.TEXT_HEIGHT);
        heightIcon.setIconSize(20);
        updateHeightButton.setGraphic(heightIcon);
        updateHeightButton.setTooltip(new Tooltip("Update text height"));



        updatePageHeightLabel(PrintUtilities.getPageHeight());

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
        FontIcon saveIcon = new FontIcon(LineAwesomeSolid.SAVE);
        saveIcon.setIconSize(20);
        saveButton.setGraphic(saveIcon);
        saveButton.setTooltip(new Tooltip("Save assignment if open and otherwise exercise"));





        centerBox = new VBox();
        centerBox.setSpacing(3);
        Group centerGroup = new Group(centerBox);  //this lets scene width scale with nodes https://stackoverflow.com/questions/67724906/javafx-scaling-does-not-resize-the-component-in-parent-container

        borderPane.setCenter(centerGroup);
        borderPane.setMargin(centerGroup, new Insets(10,0,0,0));

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

        this.currentExerciseView = (ExerciseView) mainWindow.currentExercise.getExerciseView();
        this.statementNode = currentExerciseView.getExerciseStatementNode();
        this.contentNode = currentExerciseView.getExerciseContentNode();
        this.commentDecoratedRTA = currentExerciseView.getExerciseComment();
        this.commentNode = commentDecoratedRTA.getEditor();
        this.controlNode = currentExerciseView.getExerciseControl();
        this.contentHeightProperty = currentExerciseView.getContentHeightProperty();

        //this seems odd: print utilities gives its value in pt.  RTA documentation says it is measured in px.
        //so I expect to set width at 16/12 * px.  But this gives a page too wide.  Is RTA measuring in pt?
        //similarly for height.
        commentDecoratedRTA.getEditor().setContentAreaWidth(PrintUtilities.getPageWidth());
        commentDecoratedRTA.getEditor().setPrefWidth(PrintUtilities.getPageWidth() +40);
        centerBox.getChildren().clear();
        centerBox.getChildren().addAll(commentNode, statementNode, contentNode);




        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        upperStatusBox.getChildren().clear();
        lowerStatusPane.getChildren().clear();
        upperStatusBox.getChildren().add(new Label("Exercise: " + currentExerciseView.getExerciseName()));
        lowerStatusPane.getChildren().add(new Label("Date: " + dtf.format(LocalDateTime.now())));

        borderPane.setLeft(controlNode);

        centerBox.layout();

        setCenterVgrow();
        Platform.runLater(() -> contentNode.requestFocus());
    }

    public void setUpLowerAssignmentBar() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        upperStatusBox.getChildren().clear();
        lowerStatusPane.getChildren().clear();

        AssignmentHeader header = mainWindow.getCurrentAssignment().getHeader();
        int exerciseNum = mainWindow.getAssignmentIndex() + 1;
        upperStatusBox.getChildren().addAll(new Label("Student Name: " + header.getStudentName()), new Label("Assignment: " + header.getAssignmentName()),
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

    public void updateNodeHeightLabel(double nodeHeight) {
        double pageHeight = Double.parseDouble(pageHeightLabel.getText());
        double percentValue = (nodeHeight / pageHeight) * 100;
        nodeHeightLabel.setText(Integer.toString((int) Math.round(nodeHeight)));
        nodePercentageLabel.setText(Integer.toString((int) Math.round(percentValue)));
    }

    public void updatePageHeightLabel(double pageHeight) {
        double nodeHeight = Double.parseDouble(nodeHeightLabel.getText());
        double percentValue = (nodeHeight / pageHeight) * 100;
        pageHeightLabel.setText(Integer.toString((int) Math.round(pageHeight)));
        nodePercentageLabel.setText(Integer.toString((int) Math.round(percentValue)));
    }

    public void setCenterVgrow() {
        // with content box enclosed in Group, the content pane does not size with window.
        // this restores sizing (inserting height from top box manually)

        double fixedHeight = (currentExerciseView.getStatementHeight() + currentExerciseView.getCommentHeight() + currentExerciseView.getContentFixedHeight())  * scale + statusBar.getHeight() + 270;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(fixedValueProperty)).divide(scaleProperty)));
        contentHeightProperty.bind(centerHeightProperty);
    }

    public void updateZoom(int zoom) {
        scale = (double)zoom/100.0;

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.updateFontSize(scale);
        keyboardDiagram.initialize(lastFocussedDRTA);
        keyboardDiagram.update();

        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        mainScene.getWindow().setWidth(Math.max(minStageWidth, controlNode.getLayoutBounds().getWidth() + PrintUtilities.getPageWidth() * scale + 85));
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
        insertToolbar = decoratedRTA.getInsertToolbar();
        paragraphToolbar = decoratedRTA.getParagraphToolbar();
        kbdDiaToolBar = decoratedRTA.getKbdDiaToolbar();

        if (!kbdDiaToolBar.getItems().contains(saveButton)) {
            kbdDiaToolBar.getItems().add(0, updateHeightButton);
            kbdDiaToolBar.getItems().add(0, zoomSpinner);
            kbdDiaToolBar.getItems().add(0, zoomLabel);
            kbdDiaToolBar.getItems().add(saveButton);

            switch(control) {
                case NONE: {
                    kbdDiaToolBar.setDisable(true);
                }
                case STATEMENT: {
                    editToolbar.setDisable(true);
                    fontsToolbar.setDisable(true);
                      }
                case FIELD: {
                    paragraphToolbar.setDisable(true);
                    insertToolbar.setDisable(true);
                      }
                case AREA: {}
            }
        }
        HBox insertAndFontsBox = new HBox(insertToolbar, fontsToolbar);
        HBox editAndKbdBox = new HBox(editToolbar, kbdDiaToolBar);

        VBox topBox = new VBox(menuBox, paragraphToolbar, insertAndFontsBox, editAndKbdBox);
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

    public Button getUpdateHeightButton() {
        return updateHeightButton;
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
}
