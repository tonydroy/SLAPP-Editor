package slapp.editor.truth_table;

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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.decorated_rta.KeyboardDiagram;
import slapp.editor.main_window.ControlType;
import slapp.editor.main_window.MainWindow;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class TruthTableCreate {
    private MainWindow mainWindow;
    private MainWindowView mainView;
    private TextField nameField;
    private RichTextArea statementRTA;
    private DecoratedRTA statementDRTA;
    private boolean fieldModified = false;
    private double scale = 1.0;
    private Scene scene;
    private Stage stage;
    private BorderPane borderPane;
    private SimpleDoubleProperty centerHeightProperty;
    private TextArea helpArea;
    private VBox centerBox;
    private CheckBox conclusionDividerCheck;
    private List<DecoratedRTA> unaryOperatorList;
    private List<DecoratedRTA> binaryOperatorList;
    private List<DecoratedRTA> mainFormulaList;
    private GridPane unaryOperatorsPane;
    private GridPane binaryOperatorsPane;
    private GridPane mainFormulasPane;
    private Label zoomLabel;
    private Spinner<Integer> zoomSpinner;
    private Button updateHeightButton;
    private Button saveButton;
    private MenuBar menuBar;
    private VBox upperFieldsBox;
    private ToolBar editToolbar;
    private ToolBar fontsToolbar;
    private ToolBar insertToolbar;
    private ToolBar paragraphToolbar;
    private ToolBar kbdDiaToolbar;
    ChangeListener nameListener;


    public TruthTableCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public TruthTableCreate(MainWindow mainWindow, TruthTableModel originalModel) {
        this(mainWindow);

        nameField.setText(originalModel.getExerciseName());
        statementRTA.setDocument(originalModel.getExerciseStatement());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        conclusionDividerCheck.setSelected(originalModel.isConclusionDivider());
        updateOperatorFieldsFromModel(originalModel);
        updateUnaryOperatorGridFromFields();
        updateBinaryOperatorGridFromFields();
        updateMainFormulaFieldsFromModel(originalModel);
        updateMainFormulaGridFromFields();

        fieldModified = false;
    }

    private void setupWindow(){
        borderPane = new BorderPane();

        //empty bar for consistent look
        Menu helpMenu = new Menu("");
        menuBar = new MenuBar(helpMenu);

        //statement editor
        statementDRTA = new DecoratedRTA();
        statementRTA = statementDRTA.getEditor();
        statementRTA.setPromptText("Exercise Statement:");
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setPrefWidth(PrintUtilities.getPageWidth() + 20);
        statementRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        statementRTA.setPrefHeight(100);
        statementRTA.setMinHeight(50);
        statementRTA.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                editorInFocus(statementDRTA, ControlType.AREA);
            }
        });

        //name field
        Label nameLabel = new Label("Exercise Name: ");
        nameLabel.setPrefWidth(95);
        nameField  = new TextField();
        nameField.setPromptText("(plain text)");
        nameListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldModified = true;
                nameField.textProperty().removeListener(nameListener);
            }
        };
        nameField.textProperty().addListener(nameListener);
        HBox nameBox = new HBox(10, nameLabel, nameField);
        nameBox.setAlignment(Pos.CENTER_LEFT);



        //language presets
        Label operatorPresetLabel = new Label("Preset operators: ");
        Button baseLangPresetButton = new Button();
        Button plusLangPresetButton = new Button();
        baseLangPresetButton.setPrefHeight(25); baseLangPresetButton.setPrefWidth(40);
        plusLangPresetButton.setPrefHeight(25); plusLangPresetButton.setPrefWidth(40);

        Font flowFont = new Font("Noto Sans Combo", 11);
        Text t0 = new Text("\u2112");
        Text t1 = new Text("\u2112");
        Text t2 = new Text("\ud835\udcc8");
        Text t3 = new Text("\ud835\udcc8+");
        t0.setFont(flowFont); t1.setFont(flowFont); t2.setFont(flowFont); t3.setFont(flowFont);

        t2.setTranslateY(t2.getFont().getSize() * 0.2);
        t3.setTranslateY(t3.getFont().getSize() * 0.2);
        TextFlow baseFlow = new TextFlow();
        baseFlow.getChildren().addAll(t0,t2);
        baseFlow.setTextAlignment(TextAlignment.CENTER);
        TextFlow plusFlow = new TextFlow();
        plusFlow.getChildren().addAll(t1,t3);
        plusFlow.setTextAlignment(TextAlignment.CENTER);
        baseLangPresetButton.setGraphic(baseFlow);
        plusLangPresetButton.setGraphic(plusFlow);

        HBox languagePresetsBox = new HBox(10, operatorPresetLabel, baseLangPresetButton, plusLangPresetButton);
        languagePresetsBox.setAlignment(Pos.CENTER_LEFT);

        baseLangPresetButton.setOnAction(e -> {
           unaryOperatorList.clear();
           unaryOperatorList.add(contentOperatorDRTA("\u223c"));
           updateUnaryOperatorGridFromFields();
           binaryOperatorList.clear();
           binaryOperatorList.add(contentOperatorDRTA("\u2192"));
           updateBinaryOperatorGridFromFields();
           fieldModified = true;
        });
        plusLangPresetButton.setOnAction(e -> {
            unaryOperatorList.clear();
            unaryOperatorList.add(contentOperatorDRTA("\u223c"));
            updateUnaryOperatorGridFromFields();
            binaryOperatorList.clear();
            binaryOperatorList.add(contentOperatorDRTA("\u2192"));
            binaryOperatorList.add(contentOperatorDRTA("\u2194"));
            binaryOperatorList.add(contentOperatorDRTA("\u2227"));
            binaryOperatorList.add(contentOperatorDRTA("\u2228"));
            updateBinaryOperatorGridFromFields();
            fieldModified = true;
        });

        //unary operators
        unaryOperatorsPane = new GridPane();
        unaryOperatorsPane.setHgap(10);
        unaryOperatorList = new ArrayList<>();

        Label unaryOperatorLabel = new Label("Unary operators: ");
        unaryOperatorLabel.setPrefWidth(95);
        Button addUnaryOperatorButton = new Button("+");
        Button removeUnaryOperatorButton = new Button("-");
        addUnaryOperatorButton.setFont(new Font(16)); removeUnaryOperatorButton.setFont(new Font(16));
        addUnaryOperatorButton.setPadding(new Insets(0, 5, 0, 5)); removeUnaryOperatorButton.setPadding(new Insets(1, 8, 1, 8));

        addUnaryOperatorButton.setOnAction(e -> {
            DecoratedRTA drta = newOperatorDRTAField();
            unaryOperatorList.add(drta);
            fieldModified = true;
            updateUnaryOperatorGridFromFields();
        });
        removeUnaryOperatorButton.setOnAction(e -> {
            int index = unaryOperatorList.size();
            index--;
            if (index >= 0) {
                unaryOperatorList.remove(index);
                fieldModified = true;
                updateUnaryOperatorGridFromFields();
            }
        });

        HBox unaryOperatorBox = new HBox(10, unaryOperatorLabel, addUnaryOperatorButton, removeUnaryOperatorButton, unaryOperatorsPane);
        unaryOperatorBox.setAlignment(Pos.CENTER_LEFT);

        //binary operators
        binaryOperatorsPane = new GridPane();
        binaryOperatorsPane.setHgap(10);
        binaryOperatorList = new ArrayList<>();

        Label binaryOperatorLabel = new Label("Binary operators: ");
        binaryOperatorLabel.setPrefWidth(95);
        Button addBinaryOperatorButton = new Button("+");
        Button removeBinaryOperatorButton = new Button("-");
        addBinaryOperatorButton.setFont(new Font(16)); removeBinaryOperatorButton.setFont(new Font(16));
        addBinaryOperatorButton.setPadding(new Insets(0, 5, 0, 5)); removeBinaryOperatorButton.setPadding(new Insets(1, 8, 1, 8));

        addBinaryOperatorButton.setOnAction(e -> {
            DecoratedRTA drta = newOperatorDRTAField();
            binaryOperatorList.add(drta);
            fieldModified = true;
            updateBinaryOperatorGridFromFields();
        });
        removeBinaryOperatorButton.setOnAction(e -> {
            int index = binaryOperatorList.size();
            index--;
            if (index >= 0) {
                binaryOperatorList.remove(index);
                fieldModified = true;
                updateBinaryOperatorGridFromFields();
            }
        });

        HBox binaryOperatorBox = new HBox(10, binaryOperatorLabel, addBinaryOperatorButton, removeBinaryOperatorButton, binaryOperatorsPane);
        binaryOperatorBox.setAlignment(Pos.CENTER_LEFT);

        //main formulas top
        mainFormulasPane = new GridPane();
        mainFormulasPane.setPadding(new Insets(10, 0, 20, 105));
        mainFormulasPane.setVgap(10);
        mainFormulaList = new ArrayList<>();
        DecoratedRTA mainFormulaDRTA = newMainFormulaDRTAField();
        mainFormulaList.add(mainFormulaDRTA);
        updateMainFormulaGridFromFields();

        Label mainFormulaLabel = new Label("Main formulas: ");
        mainFormulaLabel.setPrefWidth(95);
        Button addMainFormulaButton = new Button("+");
        Button removeMainFormulaButton = new Button("-");
        addMainFormulaButton.setFont(new Font(16)); removeMainFormulaButton.setFont(new Font(16));
        addMainFormulaButton.setPadding(new Insets(0, 5, 0, 5)); removeMainFormulaButton.setPadding(new Insets(1, 8, 1, 8));

        addMainFormulaButton.setOnAction(e -> {
            DecoratedRTA drta = newMainFormulaDRTAField();
            mainFormulaList.add(drta);
            fieldModified = true;
            updateMainFormulaGridFromFields();
        });
        removeMainFormulaButton.setOnAction(e -> {
            int index = mainFormulaList.size();
            index--;
            if (index > 0) {
                mainFormulaList.remove(index);
                fieldModified = true;
                updateMainFormulaGridFromFields();
            } else {
                EditorAlerts.showSimpleAlert("Cannot Remove", "A truth table must include at least one formula.");
            }
        });

        conclusionDividerCheck = new CheckBox("Include Conclusion Divider");
        ChangeListener conclusionDividerCheckListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue ob, Object ov, Object nv) {
                fieldModified = true;
            }
        };
        conclusionDividerCheck.selectedProperty().addListener(conclusionDividerCheckListener);



        HBox mainFormulasTop = new HBox(10, mainFormulaLabel, addMainFormulaButton, removeMainFormulaButton, conclusionDividerCheck);
        mainFormulasTop.setAlignment(Pos.CENTER_LEFT);
        mainFormulasTop.setMargin(conclusionDividerCheck, new Insets(0, 0, 0, 100));

        upperFieldsBox = new VBox(10, nameBox, languagePresetsBox, unaryOperatorBox, binaryOperatorBox, mainFormulasTop, mainFormulasPane);
        upperFieldsBox.setPadding(new Insets(20, 0, 20, 20));

        //center area
        String helpText = "Truth Table Explain Exercise is like Truth Table Exercise except that it requests a choice between some mutually exclusive options (as valid/invalid) along with a short explanation.\n\n" +
                "For the Truth Table Explain exercise, supply the exercise name and exercise statement.  The Checkbox lead appears prior to the check boxes, the A prompt with the first box, and the B prompt with the second.  " +
                "The preset operator buttons set operators according to the official and abbreviating languages from Symbolic Logic; alternatively, you may edit sentential operator symbols individually. " +
                "Finally supply formulas to appear across the top of the truth table (not including the base column).  The \"conclusion divider\" merely inserts an extra space and slash ('/') prior to the last formula." ;

        helpArea = new TextArea(helpText);
        helpArea.setWrapText(true);
        helpArea.setPrefHeight(180);
        helpArea.setEditable(false);
        helpArea.setFocusTraversable(false);
        helpArea.setMouseTransparent(true);
        helpArea.setStyle("-fx-text-fill: mediumslateblue");

        centerBox = new VBox(10, statementRTA, helpArea);
        centerBox = new VBox(10, statementRTA, helpArea);
        Group centerGroup = new Group(centerBox);
        borderPane.setCenter(centerGroup);

        //bottom buttons
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> closeWindow());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearExercise());

        Button viewButton = new Button("View");
        viewButton.setOnAction(e -> viewExercise());

        Button lowerSaveButton = new Button ("Save");
        lowerSaveButton.setOnAction(e -> saveExercise(false));

        Button saveAsButton = new Button("Save As");
        saveAsButton.setOnAction(e -> saveExercise(true));

        HBox buttonBox = new HBox(saveAsButton, lowerSaveButton, viewButton, clearButton, closeButton);
        buttonBox.setSpacing(30);
        buttonBox.setPadding(new Insets(20,50,20,50));
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        borderPane.setBottom(buttonBox);

        //editor decoration misc
        zoomLabel = new Label(" Zoom ");
        zoomSpinner = new Spinner(25, 500, 100, 5);
        zoomSpinner.setPrefSize(60,25);
        zoomSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = zoomSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = zoomSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);

            scale = (double) nv/100;
            updateZoom();
            setCenterVgrow();
        });

        FontIcon heightIcon = new FontIcon(LineAwesomeSolid.ARROWS_ALT);
        heightIcon.setIconSize(20);
        updateHeightButton = new Button();
        updateHeightButton.setGraphic(heightIcon);
        updateHeightButton.setDisable(true);

        FontIcon saveIcon = new FontIcon(LineAwesomeSolid.SAVE);
        saveIcon.setIconSize(20);
        saveButton = new Button();
        saveButton.setGraphic(saveIcon);
        saveButton.setDisable(true);

        //setup  window
        scene = new Scene(borderPane);
        scene.getStylesheets().add(DecoratedRTA.class.getClassLoader().getResource("slappEditor.css").toExternalForm());

        stage = new Stage();
        stage.initOwner(EditorMain.mainStage);
        stage.setScene(scene);

        stage.setTitle("Create Truth Table Exercise:");
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(e-> {
            e.consume();
            closeWindow();
        });

        stage.show();
        statementRTA.getActionFactory().save().execute(new ActionEvent());
        centerBox.layout();
        setCenterVgrow();
        Platform.runLater(() -> nameField.requestFocus());

    }

    private void updateOperatorFieldsFromModel(TruthTableModel model){
        unaryOperatorList.clear();
        List<String> unaryList = model.getUnaryOperators();
        for (String str : unaryList) {
            DecoratedRTA drta = newOperatorDRTAField();
            RichTextArea rta = drta.getEditor();
            rta.setDocument(new Document(str));
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            unaryOperatorList.add(drta);
        }
        binaryOperatorList.clear();
        List<String> binaryList = model.getBinaryOperators();
       for (String str : binaryList) {
           DecoratedRTA drta = newOperatorDRTAField();
           RichTextArea rta = drta.getEditor();
           rta.setDocument(new Document(str));
           rta.getActionFactory().saveNow().execute(new ActionEvent());
           binaryOperatorList.add(drta);
       }
    }
    private void updateUnaryOperatorGridFromFields(){
        unaryOperatorsPane.getChildren().clear();
        for (int i = 0; i < unaryOperatorList.size(); i++) {
            DecoratedRTA drta = unaryOperatorList.get(i);
            RichTextArea rta = drta.getEditor();
            unaryOperatorsPane.add(rta, i, 0);
        }
    }
    private void updateBinaryOperatorGridFromFields(){
        binaryOperatorsPane.getChildren().clear();
        for (int i = 0; i < binaryOperatorList.size(); i++) {
            DecoratedRTA drta = binaryOperatorList.get(i);
            RichTextArea rta = drta.getEditor();
            binaryOperatorsPane.add(rta, i, 0);
        }
    }

    private void updateMainFormulaFieldsFromModel(TruthTableModel model){
        mainFormulaList.clear();
        List<Document> formulasList = model.getMainFormulas();
        for (Document doc : formulasList) {
            DecoratedRTA drta = newMainFormulaDRTAField();
            RichTextArea rta = drta.getEditor();
            rta.setDocument(doc);
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            mainFormulaList.add(drta);
        }
    }

    private void updateMainFormulaGridFromFields(){
        mainFormulasPane.getChildren().clear();
        for (int i = 0; i < mainFormulaList.size(); i++) {
            DecoratedRTA drta = mainFormulaList.get(i);
            RichTextArea rta = drta.getEditor();
            mainFormulasPane.add(rta, 0, i);
        }
    }

    private DecoratedRTA newOperatorDRTAField() {
        DecoratedRTA drta = new DecoratedRTA();
        drta.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea rta = drta.getEditor();
        rta.setMaxHeight(27);
        rta.setMinHeight(27);
        rta.setPrefWidth(30);
        rta.getStylesheets().add("RichTextField.css");

 // Center text in field?  This doesn't work.
 //       RichTextAreaViewModel rtaViewModel = ((RichTextAreaSkin) rta.getSkin()).getViewModel();
 //       new ActionCmdFactory().decorate(ParagraphDecoration.builder().presets().alignment(TextAlignment.CENTER).build()).apply(rtaViewModel);

        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                editorInFocus(drta, ControlType.STATEMENT);
            }
        });
        return drta;
    }

    private DecoratedRTA contentOperatorDRTA(String operator) {
        DecoratedRTA drta = newOperatorDRTAField();
        RichTextArea rta = drta.getEditor();
        rta.setDocument(new Document(operator));
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        return drta;
    }

    private DecoratedRTA newMainFormulaDRTAField() {
        DecoratedRTA drta = new DecoratedRTA();
        drta.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea rta = drta.getEditor();
        rta.setMaxHeight(27);
        rta.setMinHeight(27);
        rta.setPrefWidth(300);
        rta.getStylesheets().add("RichTextField.css");
        rta.setPromptText("Formula");
        rta.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                editorInFocus(drta, ControlType.FIELD);
            }
        });
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        return drta;
    }

    private void closeWindow() {
        if (checkContinue("Confirm Close", "This exercise appears to have been changed.\nContinue to close window?")) {
            mainWindow.closeExercise();
            stage.close();
        }
    }
    private void viewExercise() {
        TruthTableExercise exercise = new TruthTableExercise(extractModelFromWindow(), mainWindow, true);
        exercise.generateEmptyTableModel();
        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 25.0);
        mainWindow.setUpExercise(exercise);
    }
    private void clearExercise() {
        if (checkContinue("Confirm Clear", "This exercise appears to have been changed.\nContinue to clear exercise?")) {
            nameField.clear();
            nameField.textProperty().addListener(nameListener);

            conclusionDividerCheck.setSelected(false);
            unaryOperatorList.clear();
            updateUnaryOperatorGridFromFields();
            binaryOperatorList.clear();
            updateBinaryOperatorGridFromFields();
            mainFormulaList.clear();
            DecoratedRTA mainFormulaDRTA = newMainFormulaDRTAField();
            mainFormulaList.add(mainFormulaDRTA);
            updateMainFormulaGridFromFields();

            statementRTA.setDocument(new Document());
            statementRTA.getActionFactory().newDocument().execute(new ActionEvent());
            statementRTA.getActionFactory().saveNow().execute(new ActionEvent());

            fieldModified = false;
            viewExercise();
        }
    }
    private void saveExercise(boolean saveAs) {
        nameField.textProperty().addListener(nameListener);

        TruthTableExercise exercise = new TruthTableExercise(extractModelFromWindow(), mainWindow, true);

        RichTextArea rta = exercise.getExerciseView().getExerciseStatement().getEditor();
        rta.setEditable(true);
        RichTextAreaSkin rtaSkin = ((RichTextAreaSkin) rta.getSkin());
        double height = Math.min(PrintUtilities.getPageHeight(), rtaSkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight()));
        rta.setEditable(false);
        exercise.getExerciseView().setStatementPrefHeight(height + 25.0);
        exercise.getExerciseModel().setStatementPrefHeight(height + 25.0);

        exercise.saveExercise(saveAs);
        fieldModified = false;
    }

    private boolean checkContinue(String title, String content) {
        boolean okcontinue = true;

        for (DecoratedRTA drta : unaryOperatorList) {
            if (drta.getEditor().isModified()) {fieldModified = true; }
        }

        for (DecoratedRTA drta : binaryOperatorList) {
            if (drta.getEditor().isModified()) {fieldModified = true; }
        }

        for (DecoratedRTA drta : mainFormulaList) {
            if (drta.getEditor().isModified()) {fieldModified = true; }
        }

        if (statementRTA.isModified()) {fieldModified = true;  }

        if (fieldModified) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okcontinue = false;
        }

        return okcontinue;
    }

    private void updateZoom() {
        centerBox.setScaleX(scale);
        centerBox.setScaleY(scale);
        scene.getWindow().setWidth(Math.max(1030, PrintUtilities.getPageWidth() * scale + 55));
        setCenterVgrow();

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.updateFontSize(scale);
        keyboardDiagram.initialize(statementDRTA);
        keyboardDiagram.update();
    }

    private void setCenterVgrow() {
        double fixedHeight = helpArea.getHeight() * scale + 550;
        DoubleProperty fixedValueProperty = new SimpleDoubleProperty(fixedHeight);
        DoubleProperty externalHeightProperty = new SimpleDoubleProperty();
        externalHeightProperty.bind(fixedValueProperty.add(mainFormulasPane.heightProperty()));
        DoubleProperty maximumHeightProperty = new SimpleDoubleProperty(PrintUtilities.getPageHeight() );
        DoubleProperty scaleProperty = new SimpleDoubleProperty(scale);
        centerHeightProperty = new SimpleDoubleProperty();
        centerHeightProperty.bind(Bindings.min(maximumHeightProperty, (stage.heightProperty().subtract(externalHeightProperty)).divide(scaleProperty)));
        statementRTA.prefHeightProperty().bind(centerHeightProperty);
    }

    //this leaves tableValues, rowComments, columnHighlights to be initialized by the TruthTableExercise (based on the model mainFormulas).
    private TruthTableModel extractModelFromWindow() {
        TruthTableModel model = new TruthTableModel();

        model.setExerciseName(nameField.getText());
        model.setStarted(false);
        model.setStatementPrefHeight(70.0);

        if (statementRTA.isModified()) fieldModified = true;
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseStatement(statementRTA.getDocument());

        List<String> unaryOperatorStrings = new ArrayList<>();
        for (DecoratedRTA drta : unaryOperatorList) {
            RichTextArea rta = drta.getEditor();
            if (rta.isModified()) fieldModified = true;
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            String op = rta.getDocument().getText();
            unaryOperatorStrings.add(op);
        }
        model.setUnaryOperators(unaryOperatorStrings);

        List<String> binaryOperatorStrings = new ArrayList<>();
        for (DecoratedRTA drta : binaryOperatorList) {
            RichTextArea rta = drta.getEditor();
            if (rta.isModified()) fieldModified = true;
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            String op = rta.getDocument().getText();
            binaryOperatorStrings.add(op);
        }
        model.setBinaryOperators(binaryOperatorStrings);

        List<Document> mainFormulaDocs = new ArrayList<>();
        for (DecoratedRTA drta : mainFormulaList) {
            RichTextArea rta = drta.getEditor();
            if (rta.isModified()) fieldModified = true;
            rta.getActionFactory().saveNow().execute(new ActionEvent());
            Document doc = rta.getDocument();
            mainFormulaDocs.add(doc);
        }
        model.setMainFormulas(mainFormulaDocs);

        model.setConclusionDivider(conclusionDividerCheck.isSelected());

        return model;
    }





    void editorInFocus(DecoratedRTA decoratedRTA, ControlType control) {

        KeyboardDiagram keyboardDiagram = KeyboardDiagram.getInstance();
        keyboardDiagram.initialize(decoratedRTA);
        if (keyboardDiagram.isShowing()) {
            keyboardDiagram.updateAndShow();
        }

        editToolbar = decoratedRTA.getEditToolbar();
        fontsToolbar = decoratedRTA.getFontsToolbar();
        insertToolbar = decoratedRTA.getInsertToolbar();
        paragraphToolbar = decoratedRTA.getParagraphToolbar();
        kbdDiaToolbar = decoratedRTA.getKbdDiaToolbar();


        if (!kbdDiaToolbar.getItems().contains(saveButton)) {
            //          kbdDiaToolBar.getItems().add(0, updateHeightButton);
            //          kbdDiaToolBar.getItems().add(0, zoomSpinner);
            //          kbdDiaToolBar.getItems().add(0, zoomLabel);
            //          kbdDiaToolBar.getItems().add(saveButton);


            switch (control) {
                case NONE: {
                    kbdDiaToolbar.setDisable(true);
                }
                case STATEMENT: {
                    editToolbar.setDisable(true);
                    fontsToolbar.setDisable(true);
                }
                case FIELD: {
                    paragraphToolbar.setDisable(true);
                    insertToolbar.setDisable(true);
                }
                case AREA: {
                }
            }
        }

        HBox insertAndFontsBox = new HBox(insertToolbar, fontsToolbar);

        //this is a kludge.  When the kbdDiaToolBar is extended (as in other cases), new elements do not appear in the window.
        //calling .layout() results in an error from the RTA skin.  All I need are the zoom spinner and extra disabled elements
        // -- and this try does not seem to have either the layout problems or the RTA error.

        HBox dudBox = new HBox(zoomLabel, zoomSpinner, updateHeightButton);

        ToolBar dudBar = new ToolBar(zoomLabel, zoomSpinner, updateHeightButton);
        dudBar.setStyle("-fx-spacing: 12");

        Pane spacer1 = new Pane();
        spacer1.setPrefWidth(2);
        ToolBar dudSaveBar = new ToolBar(spacer1, saveButton);
        //

        HBox editAndKbdBox = new HBox(editToolbar, dudBar, kbdDiaToolbar, dudSaveBar);

        VBox topBox = new VBox(menuBar, paragraphToolbar, insertAndFontsBox, editAndKbdBox, upperFieldsBox);
//        topBox.layout();
        borderPane.topProperty().setValue(topBox);
    }

}
