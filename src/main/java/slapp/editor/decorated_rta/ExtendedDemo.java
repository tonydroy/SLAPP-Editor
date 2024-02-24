package slapp.editor.decorated_rta;


import com.gluonhq.emoji.Emoji;
import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.action.Action;
import com.gluonhq.richtextarea.action.DecorateAction;
import com.gluonhq.richtextarea.action.ParagraphDecorateAction;
import com.gluonhq.richtextarea.action.TextDecorateAction;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.ImageDecoration;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TableDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import slapp.editor.PrintUtilities;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.util.StringConverter;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gluonhq.richtextarea.model.ParagraphDecoration.GraphicType.BULLETED_LIST;
import static com.gluonhq.richtextarea.model.ParagraphDecoration.GraphicType.NONE;
import static com.gluonhq.richtextarea.model.ParagraphDecoration.GraphicType.NUMBERED_LIST;
import static javafx.scene.text.FontPosture.ITALIC;
import static javafx.scene.text.FontPosture.REGULAR;
import static javafx.scene.text.FontWeight.BOLD;
import static javafx.scene.text.FontWeight.NORMAL;

/**
 * This is an advance sample that shows how to create a rich text editor, by using
 * the RichTextArea control and adding actions for the user interaction, via toolbars and
 * menus, and most of the features of the control are showcased in this sample.
 * <p>
 * For more basic test cases with single features, check the rest of the samples.
 */
public class ExtendedDemo {

    static {
        try (InputStream resourceAsStream = ExtendedDemo.class.getResourceAsStream("/logging.properties")) {
            if (resourceAsStream != null) {
                LogManager.getLogManager().readConfiguration(resourceAsStream);
            }
        } catch (IOException ex) {
            Logger.getLogger(ExtendedDemo.class.getName()).log(Level.SEVERE, "Error opening logging.properties file", ex);
        }
    }

    private final List<DecorationModel> decorations;

    {
        TextDecoration bold14 = TextDecoration.builder().presets().fontWeight(BOLD).fontSize(14).build();
        TextDecoration preset = TextDecoration.builder().presets().build();
        ParagraphDecoration center63 = ParagraphDecoration.builder().presets().alignment(TextAlignment.CENTER).topInset(6).bottomInset(3).build();
        ParagraphDecoration justify22 = ParagraphDecoration.builder().presets().alignment(TextAlignment.JUSTIFY).topInset(2).bottomInset(2).graphicType(BULLETED_LIST).build();
        ParagraphDecoration right22 = ParagraphDecoration.builder().presets().alignment(TextAlignment.RIGHT).topInset(2).bottomInset(2).build();
        ParagraphDecoration left535 = ParagraphDecoration.builder().presets().alignment(TextAlignment.LEFT).topInset(5).bottomInset(3).spacing(5).build();
        ParagraphDecoration center42 = ParagraphDecoration.builder().presets().alignment(TextAlignment.CENTER).topInset(4).bottomInset(2).build();
        TableDecoration tdec = new TableDecoration(2, 3, new TextAlignment[][]{{TextAlignment.CENTER, TextAlignment.LEFT, TextAlignment.RIGHT}, {TextAlignment.JUSTIFY, TextAlignment.RIGHT, TextAlignment.CENTER}});
        ParagraphDecoration table = ParagraphDecoration.builder().presets().alignment(TextAlignment.CENTER).tableDecoration(tdec).build();
        decorations = List.of(
                new DecorationModel(0, 21, bold14, center63),
                new DecorationModel(21, 575, preset, justify22),
                new DecorationModel(596, 18, bold14, center63),
                new DecorationModel(614, 614, preset, right22),
                new DecorationModel(1228, 27, bold14, table),
                new DecorationModel(1255, 764, preset, left535),
                new DecorationModel(2019, 295, preset, center42)
        );
    }
    private final Document document = new Document("What is Lorem Ipsum?\n" +
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n" +
            "Why do we use it?\n" +
            "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).\n" +
            "Where does\u200bit\u200bcome\u200bfrom?\u200b\u200b\n" +
            "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
            "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.\n",
            decorations, 2314);





//    private final Document document = new Document("this is\n a test");


    private final Label textLengthLabel = new Label();

    private final Stage stage;
    private final RichTextArea editor;
    private final ExtendedDemo simpleEditorView;
    private ExDemKeybdDiagram keyboardDiagram;
    private double mainWindowX;
    private double  mainWindowY;
    private double mainWindowWidth = 960.0;
    private double mainWindowHeight = 800.0;
    private double keyboardWindowX;
    private double keyboardWindowY;
    private double keyboardWindowWidth = 650.0;
    private double keyboardWindowHeight = 990.0;
    private boolean keyboardPositionInitialized = false;
    private double primaryFontSize = 15.0;

    public ExtendedDemo(Stage stage) {
        this.simpleEditorView = this;
        this.stage = stage;
        editor = new RichTextArea(stage);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        mainWindowX = Math.max(0.0, ((bounds.getMaxX() - bounds.getMinX()) - mainWindowWidth)/3);
        mainWindowY = Math.max(0.0, ((bounds.getMaxY() - bounds.getMinY()) - mainWindowHeight)/3);
    }


    public void start() {

        editor.textLengthProperty().addListener( (o, ov, nv) ->
                textLengthLabel.setText( "Text length: " + nv)
        );

        ComboBox<Presets> presets = new ComboBox<>();
        presets.setTooltip(new Tooltip("Heading Level"));
        presets.getItems().setAll(Presets.values());
        presets.setValue(Presets.DEFAULT);
        presets.setPrefWidth(100);
        presets.setConverter(new StringConverter<>() {
            @Override
            public String toString(Presets presets) {
                return presets.getName();
            }

            @Override
            public Presets fromString(String s) {
                return Presets.valueOf(s.replaceAll(" ", ""));
            }
        });
        presets.getSelectionModel().selectedItemProperty().addListener((observableValue, ov, nv) -> {
            // presets should define all decoration attributes.
            // For now just they set only font size and weight for text, and alignment for paragraphs,
            // so the rest of attributes come for the Builder::presets
            editor.getActionFactory()
                    .decorate(TextDecoration.builder().presets().fontSize(nv.getFontSize()).fontWeight(nv.getWeight()).build(),
                            ParagraphDecoration.builder().presets().alignment(nv.getTextAlignment()).build()).execute(new ActionEvent());
            editor.requestFocus();
        });

        //font families
        ComboBox<String> fontFamilies = new ComboBox<>();
        fontFamilies.getItems().setAll(Font.getFamilies());
        fontFamilies.setPrefWidth(130);
        fontFamilies.setTooltip(new Tooltip("Select Font"));
        new TextDecorateAction<>(editor, fontFamilies.valueProperty(), TextDecoration::getFontFamily, (builder, a) -> builder.fontFamily(a).build());
        fontFamilies.setValue("Noto Sans");

        //font size
        final ComboBox<Double> fontSize = new ComboBox<>();
        fontSize.setEditable(true);
        fontSize.setPrefWidth(60);
        fontSize.setTooltip(new Tooltip("Font Size"));
        fontSize.getItems().addAll(IntStream.range(1, 100)
                .filter(i -> i % 2 == 0 || i < 18)
                .asDoubleStream().boxed().collect(Collectors.toList()));
        new TextDecorateAction<>(editor, fontSize.valueProperty(), TextDecoration::getFontSize, (builder, a) -> builder.fontSize(a).build());
        fontSize.setConverter(new StringConverter<>() {
            @Override
            public String toString(Double aDouble) {
                return Integer.toString(aDouble.intValue());
            }

            @Override
            public Double fromString(String s) {
                return Double.parseDouble(s);
            }
        });
        fontSize.setValue(15.0);

        //this is to make the dropdown open to the selected item's scroll position
        fontSize.setOnShowing(e -> {
            ListView list = (ListView) ((ComboBoxListViewSkin<Double>) fontSize.getSkin()).getPopupContent();
            int listIndex = fontSize.getSelectionModel().getSelectedIndex()- 3;
            list.scrollTo(Math.max(0, listIndex));
        });
        fontSize.setOnAction(e -> primaryFontSize = fontSize.getValue());


        final ColorPicker textForeground = new ColorPicker();
        textForeground.setTooltip(new Tooltip("Foreground Color"));
        textForeground.getStyleClass().add("foreground");
        new TextDecorateAction<>(editor, textForeground.valueProperty(), TextDecoration::getForeground, (builder, a) -> builder.foreground(a).build());
        textForeground.setValue(Color.BLACK);

        final ColorPicker textBackground = new ColorPicker();
        textBackground.setTooltip(new Tooltip("Background Color"));
        textBackground.getStyleClass().add("background");
        new TextDecorateAction<>(editor, textBackground.valueProperty(), TextDecoration::getBackground, (builder, a) -> builder.background(a).build());
        textBackground.setValue(Color.TRANSPARENT);

        CheckBox editableProp = new CheckBox("Editable");
        editableProp.selectedProperty().bindBidirectional(editor.editableProperty());

        //overline button
        ToggleButton overlineButton = new ToggleButton();
        overlineButton.setTooltip(new Tooltip("Overline (best on symbol fonts)"));
        Font overlineButtonFont = Font.font("Noto Sans Math", FontWeight.LIGHT, FontPosture.REGULAR, 18);
        Text overlineButtonText = new Text("\u035e\ud835\uddae");
        overlineButtonText.setFont(overlineButtonFont);
        TextFlow overlineButtonTextFlow = new TextFlow(overlineButtonText);
        overlineButtonTextFlow.setMaxHeight(0.0);
//        overlineButtonTextFlow.setPadding(new Insets(-6,3,-6,3));
        overlineButton.setGraphic(overlineButtonTextFlow);
        overlineButton.setAlignment(Pos.CENTER);
        overlineButton.setPadding(new Insets(-10,0,-10,11));
        overlineButton.setMaxHeight(28);
        overlineButton.setPrefSize(34,28);

        //keyboardDiagramButton
        ToggleButton keyboardDiagramButton = new ToggleButton();
        keyboardDiagramButton.setTooltip(new Tooltip("Show Keyboard Diagram"));
        FontIcon icon = new FontIcon(LineAwesomeSolid.KEYBOARD);
        icon.setIconSize(20);
        keyboardDiagramButton.setGraphic(icon);
        keyboardDiagramButton.setOnAction(e -> {
            if (!keyboardPositionInitialized) {
                keyboardWindowX = stage.getX() + mainWindowWidth;                                                   //(mainWindowWidth * .95);
                keyboardWindowY = stage.getY() + 25;                                                // mainWindowHeight/4;
                keyboardPositionInitialized = true;
            }
            if (keyboardDiagramButton.isSelected()) {
                keyboardDiagram = new ExDemKeybdDiagram(stage, simpleEditorView,  keyboardDiagramButton.selectedProperty());
            }
            else keyboardDiagram.closeKeyboardDiagram();
            editor.requestFocus();
        });

        //unicode field
        final TextField unicodeField = new TextField();
        unicodeField.setTooltip(new Tooltip("Unicode (x/hex, #/decimal)"));
        unicodeField.setPrefColumnCount(7);
        unicodeField.setPromptText("insert unicode");
        unicodeField.setOnAction(e -> {
            String text = unicodeField.getText();
            editor.getActionFactory().insertUnicode(text).execute(e);
        });
        //  unicodeField.setOnAction(editor.getActionFactory().insertUnicode(unicodeField.getText())::execute);  for reasons I do not understand, this does not respond to text typed in the box (but does with setText()).

        //keyboard selector
        final ChoiceBox<RichTextAreaSkin.KeyMapValue> keyboardSelector = new ChoiceBox<>();
        keyboardSelector.getItems().setAll(RichTextAreaSkin.KeyMapValue.values());
        keyboardSelector.setValue(RichTextAreaSkin.KeyMapValue.BASE);
        keyboardSelector.setTooltip(new Tooltip("Select Keyboard"));
        //

//        Button testOpenFolder =
//           actionButton(LineAwesomeSolid.FOLDER_OPEN, "test", editor.getActionFactory()
//           .open(new Document("Emoji: \uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC73\uDB40\uDC63\uDB40\uDC74\uDB40\uDC7F! and \ufeff@name\ufeff!",
//               List.of(new DecorationModel(0, 35,
//                   TextDecoration.builder().presets().build(),
//                   ParagraphDecoration.builder().presets().build())), 35)));


        //print button
        Button printButton = new Button();
        printButton.setTooltip(new Tooltip("Print Exercise"));
        FontIcon printerIcon = new FontIcon(LineAwesomeSolid.PRINT);
        printerIcon.setIconSize(20);
        printButton.setGraphic(printerIcon);
        printButton.setOnAction(e -> {
            editor.getActionFactory().save().execute(e);

           PrintUtilities.printRTA(editor);});
 //       printButton.setOnAction (e -> PrintUtilities.printRTA(editor, stage));




        //page setup button
        Button pageSetupButton = new Button();
        pageSetupButton.setTooltip(new Tooltip("Page Setup"));
        FontIcon pageSetupIcon = new FontIcon(LineAwesomeSolid.FILE_ALT);
        pageSetupIcon.setIconSize(20);
        pageSetupButton.setGraphic(pageSetupIcon);
        pageSetupButton.setOnAction(e -> {
            PrintUtilities.updatePageLayout();
        });

        Button dumpButton = new Button("dump doc");
        dumpButton.setOnAction(e -> {

            Document doc = editor.getDocument();
            System.out.println(doc);
        });


        //toolbars
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().setAll(
                actionButton(LineAwesomeSolid.FILE, "New Assignment",  editor.getActionFactory().newDocument()),
                actionButton(LineAwesomeSolid.FOLDER_OPEN, "Open Assignment File", editor.getActionFactory().open(document)),
//                testOpenFolder,
                actionButton(LineAwesomeSolid.SAVE, "Save Assignment",  editor.getActionFactory().save()),
                printButton,
                pageSetupButton,
                dumpButton,
                new Separator(Orientation.VERTICAL),

                actionButton(LineAwesomeSolid.CUT, "Cut",   editor.getActionFactory().cut()),
                actionButton(LineAwesomeSolid.COPY, "Copy",  editor.getActionFactory().copy()),
                actionButton(LineAwesomeSolid.PASTE, "Paste", editor.getActionFactory().paste()),
                new Separator(Orientation.VERTICAL),

                actionButton(LineAwesomeSolid.UNDO, "Undo",  editor.getActionFactory().undo()),
                actionButton(LineAwesomeSolid.REDO, "Redo",  editor.getActionFactory().redo()),
                new Separator(Orientation.VERTICAL),

                actionImage(LineAwesomeSolid.IMAGE, "Insert Image"),
                actionEmoji("Insert Emoji"),
                actionHyperlink(LineAwesomeSolid.LINK, "Insert Hyperlink"),
                actionTable(LineAwesomeSolid.TABLE, "Insert Table", td -> editor.getActionFactory().insertTable(td)),
                new Separator(Orientation.VERTICAL),
                fontFamilies,
                fontSize,
                presets
                 );

        ToolBar fontsToolbar = new ToolBar();
        fontsToolbar.getItems().setAll(
                keyboardSelector,
                unicodeField,
                new Separator(Orientation.VERTICAL),

                createToggleButton(LineAwesomeSolid.BOLD, "Bold (not for symbol fonts)", property -> new TextDecorateAction<>(editor, property, d -> d.getFontWeight() == BOLD, (builder, a) -> builder.fontWeight(a ? BOLD : NORMAL).build())),
                createToggleButton(LineAwesomeSolid.ITALIC, "Italic (not for symbol fonts)", property -> new TextDecorateAction<>(editor, property, d -> d.getFontPosture() == ITALIC, (builder, a) -> builder.fontPosture(a ? ITALIC : REGULAR).build())),
                createToggleButton(LineAwesomeSolid.STRIKETHROUGH, "Strikethrough", property -> new TextDecorateAction<>(editor, property, TextDecoration::isStrikethrough, (builder, a) -> builder.strikethrough(a).build())),
                createToggleButton(LineAwesomeSolid.UNDERLINE, "Underline", property -> new TextDecorateAction<>(editor, property, TextDecoration::isUnderline, (builder, a) -> builder.underline(a).build())),
                overlineButton,
                createToggleButton(LineAwesomeSolid.SUPERSCRIPT, "Superscript", property -> new TextDecorateAction<>(editor, property, TextDecoration::isSuperscript, (builder, a) -> builder.superscript(a).subscript(false).build())),
                createToggleButton(LineAwesomeSolid.SUBSCRIPT, "Subscript", property -> new TextDecorateAction<>(editor, property, TextDecoration::isSubscript, (builder, a) -> builder.subscript(a).superscript(false).build())),

                createToggleButton(LineAwesomeSolid.CARET_SQUARE_UP, "Superscript (translated back)", property -> new TextDecorateAction<>(editor, property, TextDecoration::isTransSuperscript, (builder, a) -> builder.transSuperscript(a).transSubscript(false).subscript(false).superscript(false).build())),
                createToggleButton(LineAwesomeSolid.CARET_SQUARE_DOWN, "Subscript (translated back)", property -> new TextDecorateAction<>(editor, property, TextDecoration::isTransSubscript, (builder, a) -> builder.transSubscript(a).transSuperscript(false).superscript(false).subscript(false).build())),

                textForeground,
                textBackground,
                new Separator(Orientation.VERTICAL),
                keyboardDiagramButton,
                new Separator(Orientation.VERTICAL),
                editableProp);


        ToolBar paragraphToolbar = new ToolBar();
        paragraphToolbar.getItems().setAll(
                createToggleButton(LineAwesomeSolid.ALIGN_LEFT, "Align Left", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getAlignment() == TextAlignment.LEFT, (builder, a) -> builder.alignment(TextAlignment.LEFT).build())),
                createToggleButton(LineAwesomeSolid.ALIGN_CENTER, "Align Center", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getAlignment() == TextAlignment.CENTER, (builder, a) -> builder.alignment(a ? TextAlignment.CENTER : TextAlignment.LEFT).build())),
                createToggleButton(LineAwesomeSolid.ALIGN_RIGHT, "Align Right", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getAlignment() == TextAlignment.RIGHT, (builder, a) -> builder.alignment(a ? TextAlignment.RIGHT : TextAlignment.LEFT).build())),
                createToggleButton(LineAwesomeSolid.ALIGN_JUSTIFY, "Justify", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getAlignment() == TextAlignment.JUSTIFY, (builder, a) -> builder.alignment(a ? TextAlignment.JUSTIFY : TextAlignment.LEFT).build())),
                new Separator(Orientation.VERTICAL),

                createSpinner("Spacing", "Space for wrapped lines (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getSpacing(), (builder, a) -> builder.spacing(a).build())),
                new Separator(Orientation.VERTICAL),
                createSpinner("Top", "Top Margin (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getTopInset(), (builder, a) -> builder.topInset(a).build())),
                createSpinner("Bottom", "Bottom Margin (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getBottomInset(), (builder, a) -> builder.bottomInset(a).build())),
                createSpinner("Left", "LeftMargin (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getLeftInset(), (builder, a) -> builder.leftInset(a).build())),
                createSpinner("Right", "Right Margin (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getRightInset(), (builder, a) -> builder.rightInset(a).build())),
                new Separator(Orientation.VERTICAL),

                createToggleButton(LineAwesomeSolid.LIST_OL, "Numbered List", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getGraphicType() == NUMBERED_LIST, (builder, a) -> builder.graphicType(a ? NUMBERED_LIST : NONE).build())),
                createToggleButton(LineAwesomeSolid.LIST_UL, "Unordered List/Outline", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getGraphicType() == BULLETED_LIST, (builder, a) -> builder.graphicType(a ? BULLETED_LIST : NONE).build())),
                createSpinner("Indent", "Indent Level", p -> new ParagraphDecorateAction<>(editor, p, ParagraphDecoration::getIndentationLevel, (builder, a) -> builder.indentationLevel(a).build())),
                new Separator(Orientation.VERTICAL)
        );

        HBox statusBar = new HBox(10);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setAlignment(Pos.CENTER_RIGHT);
        statusBar.getChildren().setAll(textLengthLabel);



        Menu fileMenu = new Menu("File");
        CheckMenuItem autoSaveMenuItem = new CheckMenuItem("Auto Save");
        editor.autoSaveProperty().bind(autoSaveMenuItem.selectedProperty());
        fileMenu.getItems().addAll(
                actionMenuItem("New Text", LineAwesomeSolid.FILE, editor.getActionFactory().newDocument()),
                actionMenuItem("Open Text", LineAwesomeSolid.FOLDER_OPEN, editor.getActionFactory().open(document)),
                new SeparatorMenuItem(),
                autoSaveMenuItem,
                actionMenuItem("Save Text", LineAwesomeSolid.SAVE, editor.getActionFactory().save()));
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(
                actionMenuItem("Undo", LineAwesomeSolid.UNDO, editor.getActionFactory().undo()),
                actionMenuItem("Redo", LineAwesomeSolid.REDO, editor.getActionFactory().redo()),
                new SeparatorMenuItem(),
                actionMenuItem("Copy", LineAwesomeSolid.COPY, editor.getActionFactory().copy()),
                actionMenuItem("Cut", LineAwesomeSolid.CUT, editor.getActionFactory().cut()),
                actionMenuItem("Paste", LineAwesomeSolid.PASTE, editor.getActionFactory().paste()));
        MenuBar menuBar = new MenuBar(fileMenu, editMenu);
//        menuBar.setUseSystemMenuBar(true);

        editor.setPromptText("Hello!");
        BorderPane root = new BorderPane(editor);

//        root.setMargin(editor, new Insets(20,20,20,20));           //I did this to compensate for taking out padding -- restore to original?
        root.setTop(new VBox(menuBar, toolbar, fontsToolbar, paragraphToolbar));
        root.setBottom(statusBar);

//        Scene scene = new Scene(root, 960, 580);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ExtendedDemo.class.getClassLoader().getResource("slappEditor.css").toExternalForm());
        stage.titleProperty().bind(Bindings.createStringBinding(() -> "SLAPP Editor" + (editor.isModified() ? " *" : ""), editor.modifiedProperty()));
        stage.setScene(scene);
        //
        stage.setX(mainWindowX);
        stage.setY(mainWindowY);
        stage.setWidth(mainWindowWidth);
        stage.setHeight(mainWindowHeight);
        stage.setOnCloseRequest(e -> {
            e.consume();
            closeWindow();
        });
        //
        stage.show();
        editor.requestFocus();

        overlineButton.selectedProperty().bindBidirectional(((RichTextAreaSkin) editor.getSkin()).overlineOnProperty());
        overlineButton.selectedProperty().addListener((v, ov, nv) -> editor.requestFocus());
        keyboardSelector.valueProperty().bindBidirectional(((RichTextAreaSkin) editor.getSkin()).keyMapStateProperty());
        keyboardSelector.getSelectionModel().selectedItemProperty().addListener((v, ov, nv) -> {
            ((RichTextAreaSkin) editor.getSkin()).setMaps(nv);
           if (keyboardDiagram != null) keyboardDiagram.updateTextMaps();
           editor.requestFocus();   //have not been able to find way to stop keyboard window from stealing focus see https://stackoverflow.com/questions/33151460/javafx-stop-new-window-stealing-focus
        });
    }

    private Button actionButton(Ikon ikon, String tooltip, Action action) {
        Button button = new Button();
        FontIcon icon = new FontIcon(ikon);
        icon.setIconSize(20);
        button.setGraphic(icon);
        button.setTooltip(new Tooltip(tooltip));
        button.disableProperty().bind(action.disabledProperty());
        button.setOnAction(action::execute);
        return button;
    }

    private ToggleButton createToggleButton(Ikon ikon, String tooltip, Function<ObjectProperty<Boolean>, DecorateAction<Boolean>> function) {
        final ToggleButton toggleButton = new ToggleButton();
        toggleButton.setTooltip(new Tooltip(tooltip));
        FontIcon icon = new FontIcon(ikon);
        icon.setIconSize(20);
        toggleButton.setGraphic(icon);
        function.apply(toggleButton.selectedProperty().asObject());
        return toggleButton;
    }

    private HBox createSpinner(String text, String tooltip, Function<ObjectProperty<Integer>, DecorateAction<Integer>> function) {
        Spinner<Integer> spinner = new Spinner<>();
        spinner.setTooltip(new Tooltip(tooltip));
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20);
        spinner.setValueFactory(valueFactory);
        spinner.setPrefWidth(60);
        spinner.setEditable(false);
        function.apply(valueFactory.valueProperty());
        HBox spinnerBox = new HBox(5, new Label(text), spinner);
        spinnerBox.setAlignment(Pos.CENTER);
        return spinnerBox;
    }

    private Button actionImage(Ikon ikon, String tooltip) {
        Button button = new Button();
        button.setTooltip(new Tooltip(tooltip));
        FontIcon icon = new FontIcon(ikon);
        icon.setIconSize(20);
        button.setGraphic(icon);
        button.setOnAction(e -> {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.png", ".jpeg", ".gif"));
            File file = fileChooser.showOpenDialog(button.getScene().getWindow());
            if (file != null) {
                String url = file.toURI().toString();
                editor.getActionFactory().decorate(new ImageDecoration(url)).execute(e);
            }
        });
        return button;
    }

    private Button actionEmoji(String tooltip) {
        Region region = new Region();
        region.getStyleClass().addAll("icon", "emoji-outline");
        Button emojiButton = new Button(null, region);
        emojiButton.setTooltip(new Tooltip("Insert Emoji"));
        emojiButton.getStyleClass().add("emoji-button");
        emojiButton.setOnAction(e -> {
            EmojiPopup emojiPopup = new EmojiPopup();
            emojiPopup.setSkinTone(editor.getSkinTone());
            editor.skinToneProperty().bindBidirectional(emojiPopup.skinToneProperty());
            emojiPopup.setOnAction(ev -> {
                Emoji emoji = (Emoji) ev.getSource();
                editor.getActionFactory().insertEmoji(emoji).execute(new ActionEvent());
            });
            emojiPopup.show(emojiButton);
        });
        return emojiButton;
    }

    private Button actionHyperlink(Ikon ikon, String tooltip) {
        Button button = new Button();
        button.setTooltip(new Tooltip(tooltip));
        FontIcon icon = new FontIcon(ikon);
        icon.setIconSize(20);
        button.setGraphic(icon);
        button.setOnAction(e -> {
            final Dialog<String> hyperlinkDialog = createHyperlinkDialog();
            Optional<String> result = hyperlinkDialog.showAndWait();
            result.ifPresent(textURL -> {
                editor.getActionFactory().decorate(TextDecoration.builder().url(textURL).build()).execute(e);
            });
        });
        return button;
    }

    private Dialog<String> createHyperlinkDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Hyperlink");

        // Set the button types
        ButtonType textButtonType = new ButtonType("Create", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(textButtonType);

        // Create the text and url labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField url = new TextField();
        url.setPromptText("URL");

        grid.add(new Label("URL:"), 0, 1);
        grid.add(url, 1, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(textButtonType);
        loginButton.setDisable(true);
        loginButton.disableProperty().bind(url.textProperty().isEmpty());

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == textButtonType) {
                return url.getText();
            }
            return null;
        });

        // Request focus on the username field by default.
        dialog.setOnShown(e -> Platform.runLater(url::requestFocus));

        return dialog;
    }

    private Button actionTable(Ikon ikon, String tooltip, Function<TableDecoration, Action> actionFunction) {
        Button button = new Button();
        button.setTooltip(new Tooltip(tooltip));
        FontIcon icon = new FontIcon(ikon);
        icon.setIconSize(20);
        button.setGraphic(icon);
        button.disableProperty().bind(actionFunction.apply(null).disabledProperty());
        button.setOnAction(e -> {
            final Dialog<TableDecoration> tableDialog = insertTableDialog();
            Optional<TableDecoration> result = tableDialog.showAndWait();
            result.ifPresent(td -> actionFunction.apply(td).execute(e));
        });
        return button;
    }

    private Dialog<TableDecoration> insertTableDialog() {
        Dialog<TableDecoration> dialog = new Dialog<>();
        dialog.setTitle("Insert table");

        // Set the button types
        ButtonType textButtonType = new ButtonType("Create", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(textButtonType);

        // Create the text and rows labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField rows = new TextField();
        rows.setPromptText("Rows ");

        grid.add(new Label("Rows:"), 0, 1);
        grid.add(rows, 1, 1);

        TextField cols = new TextField();
        cols.setPromptText("Columns ");

        grid.add(new Label("Columns:"), 0, 2);
        grid.add(cols, 1, 2);

        Node tableButton = dialog.getDialogPane().lookupButton(textButtonType);
        tableButton.setDisable(true);
        tableButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    if (rows.getText().isEmpty() || cols.getText().isEmpty()) {
                        return true;
                    }
                    try {
                        Integer.parseInt(rows.getText());
                        Integer.parseInt(cols.getText());
                    } catch (NumberFormatException nfe) {
                        return true;
                    }
                    return false;
                },
                rows.textProperty(), cols.textProperty()));

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == textButtonType) {
                int r = Integer.parseInt(rows.getText());
                int c = Integer.parseInt(cols.getText());
                return new TableDecoration(r, c); // TODO: add cell alignment
            }
            return null;
        });

        // Request focus on the username field by default.
        dialog.setOnShown(e -> Platform.runLater(rows::requestFocus));

        return dialog;
    }

    private MenuItem actionMenuItem(String text, Ikon ikon, Action action) {
        FontIcon icon = new FontIcon(ikon);
        icon.setIconSize(16);
        MenuItem menuItem = new MenuItem(text, icon);
        menuItem.disableProperty().bind(action.disabledProperty());
        menuItem.setOnAction(action::execute);
        return menuItem;
    }


    private enum Presets {

        DEFAULT("Default",15, NORMAL, TextAlignment.LEFT),
        HEADER1("Header 1", 32, BOLD, TextAlignment.CENTER),
        HEADER2("Header 2", 24, BOLD, TextAlignment.LEFT),
        HEADER3("Header 3", 19, BOLD, TextAlignment.LEFT);

        private final String name;
        private final int fontSize;
        private final FontWeight weight;
        private final TextAlignment textAlignment;

        Presets(String name, int fontSize, FontWeight weight, TextAlignment textAlignment) {
            this.name = name;
            this.fontSize = fontSize;
            this.weight = weight;
            this.textAlignment = textAlignment;
        }

        public String getName() {
            return name;
        }

        public int getFontSize() {
            return fontSize;
        }

        public FontWeight getWeight() {
            return weight;
        }

        public TextAlignment getTextAlignment() {
            return textAlignment;
        }
    }

    public void setKeyboardWindowX(double windowX) {
        keyboardWindowX = windowX;
    }
    public void setKeyboardWindowY(double windowY) {
        keyboardWindowY = windowY;
    }
    public void setKeyboardWindowWidth(double windowWidth) {
        keyboardWindowWidth = windowWidth;
    }
    public void setKeyboardWindowHeight(double windowHeight) {
        keyboardWindowHeight = windowHeight;
    }
    public double getKeyboardWindowX() {
        return keyboardWindowX;
    }
    public double getKeyboardWindowY() {
        return keyboardWindowY;
    }
    public double getKeyboardWindowWidth() {
        return keyboardWindowWidth;
    }
    public double getKeyboardWindowHeight() {
        return keyboardWindowHeight;
    }

    public RichTextArea getEditor() {
        return editor;
    }

    public double getPrimaryFontSize() {
        return primaryFontSize;
    }

    private void closeWindow() {
        if (keyboardDiagram != null) keyboardDiagram.closeKeyboardDiagram();
        stage.close();
//        System.exit(0);
    }




}
