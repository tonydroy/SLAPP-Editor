package slapp.editor.main_window;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.main_window.assignment.AssignmentHeader;
import slapp.editor.main_window.assignment.AssignmentHeaderItem;
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
    private Spinner<Double> horizontalSizeSpinner;
    private CheckBox vWindowCheck;
    private Spinner<Double> verticalSizeSpinner;

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

    private MenuItem commonElementsTextItem;
    private MenuItem contextualTextItem;
    private MenuItem aboutItem;
    private MenuItem generalIntroItem;
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

        Text filmItemGraphic1 = new Text("\uf008");
        filmItemGraphic1.setStyle("-fx-font-family: la-solid-900");
        generalIntroItem = new MenuItem("General Intro", filmItemGraphic1);
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
        commonElementsTextItem = new MenuItem("Common", textItemGraphic1);
        Text textItemGraphic2 = new Text("\uf15c");
        textItemGraphic2.setStyle("-fx-font-family: la-solid-900");
        contextualTextItem = new MenuItem("Contextual", textItemGraphic2);
        Text textItemGraphic3 = new Text("\uf15c");
        textItemGraphic3.setStyle("-fx-font-family: la-solid-900");
        aboutItem = new MenuItem("About", textItemGraphic3);

        Text reportItemGraphic = new Text("\uf4ad");
        reportItemGraphic.setStyle("-fx-font-family: la-solid-900");
        reportItem = new MenuItem("Comment/Report", reportItemGraphic);



        menuBar = new MenuBar(exerciseMenu, assignmentMenu, nextExerciseMenu, previousExerciseMenu, goToExerciseMenu, assignmentCommentMenu, printMenu, helpMenu);
        exerciseMenu.getItems().addAll(saveExerciseItem, saveAsExerciseItem, openExerciseItem, clearExerciseItem, closeExerciseItem, printExerciseItem, exportToPDFExerciseItem, createRevisedExerciseItem, createNewExerciseItem);
        assignmentMenu.getItems().addAll(saveAssignmentItem, saveAsAssignmentItem, openAssignmentItem, closeAssignmentItem, printAssignmentItem, exportAssignmentToPDFItem, createRevisedAssignmentItem, createNewAssignmentItem);
        printMenu.getItems().addAll(printExerciseItemPM, exportExerciseToPDFItemPM, printAssignmentItemPM, exportAssignmentToPDFItemPM, pageSetupItem, exportSetupItem, fitToPageItem);
        helpMenu.getItems().addAll(generalIntroItem, verticalTreeItem, truthTableItem, horizontalTreeItem, derivationItem, instructorInfoItem, commonElementsTextItem, contextualTextItem, aboutItem, reportItem);

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



        sizeToolBar.getItems().addAll(zoomLabel, zoomSpinner, new Label("    V Size:"), verticalSizeSpinner,
                new Label("    H Size:"), horizontalSizeSpinner, new Label("      "), saveButton);



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
        borderPane.getCenter().setStyle("-fx-background-color: transparent;");









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

        stage.setHeight(860); //this added to keep SlappLogoView on screen (adjust with actual logo) ok in general? 800
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
//        this.contentWidthProperty = currentExerciseView.getContentWidthProperty();

        statementNode.minHeight(currentExerciseView.getStatementHeight());
        commentNode.minHeight(currentExerciseView.getCommentHeight());

        centerBox.getChildren().clear();
        centerBox.getChildren().addAll(commentNode, statementNode, contentNode);







 //       centerBox.setVgrow(contentNode, Priority.ALWAYS);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        upperStatusBox.getChildren().clear();
        lowerStatusPane.getChildren().clear();
        upperStatusBox.getChildren().add(new Label("Exercise: " + currentExerciseView.getExerciseName()));
        lowerStatusPane.getChildren().add(new Label("Date: " + dtf.format(LocalDateTime.now())));

        borderPane.setLeft(controlNode);







        centerBox.layout();





        Platform.runLater(() -> contentNode.requestFocus());
    }

    public void updateSizeSpinners(Spinner<Double> height, Spinner<Double> width) {
        sizeToolBar.getItems().remove(3);
        sizeToolBar.getItems().add(3, height);
        sizeToolBar.getItems().remove(5);
        sizeToolBar.getItems().add(5, width);
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

    public void setCenterVgrow() {

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
        spacerPane.getTransforms().clear();
        spacerPane.getTransforms().add(new Scale(scale, scale));

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

    public void setHorizontalSizeSpinner(Spinner<Double> horizontalSizeSpinner) {   this.horizontalSizeSpinner = horizontalSizeSpinner;  }

    public void setVerticalSizeSpinner(Spinner<Double> verticalSizeSpinner) {
        System.out.println("before: " + this.verticalSizeSpinner);
        this.verticalSizeSpinner = verticalSizeSpinner;
        System.out.println("after: " + this.verticalSizeSpinner);
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

    public MenuItem getCommonElementsTextItem() {
        return commonElementsTextItem;
    }

    public MenuItem getContextualTextItem() {
        return contextualTextItem;
    }

    public MenuItem getAboutItem() {
        return aboutItem;
    }

    public MenuItem getGeneralIntroItem() {
        return generalIntroItem;
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
