package slapp.editor.decorated_rta;


import com.gluonhq.emoji.Emoji;
import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.action.Action;
import com.gluonhq.richtextarea.action.DecorateAction;
import com.gluonhq.richtextarea.action.ParagraphDecorateAction;
import com.gluonhq.richtextarea.action.TextDecorateAction;
import com.gluonhq.richtextarea.model.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.lineawesome.LineAwesomeSolid;
import slapp.editor.EditorMain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gluonhq.richtextarea.model.ParagraphDecoration.GraphicType.*;
import static javafx.scene.text.FontPosture.ITALIC;
import static javafx.scene.text.FontPosture.REGULAR;
import static javafx.scene.text.FontWeight.BOLD;
import static javafx.scene.text.FontWeight.NORMAL;

public class DecoratedRTA {

    static {
        try (InputStream resourceAsStream = DecoratedRTA.class.getResourceAsStream("/logging.properties")) {
            if (resourceAsStream != null) {
                LogManager.getLogManager().readConfiguration(resourceAsStream);
            }
        } catch (IOException ex) {
            Logger.getLogger(DecoratedRTA.class.getName()).log(Level.SEVERE, "Error opening logging.properties file", ex);
        }
    }

    private static Stage mainStage;
    private final RichTextArea editor;
    private final DecoratedRTA decoratedRTA;
    private double primaryFontSize = 11.0;  //see corresponding value in TextDecoration.java
    private ToolBar editToolbar;
    private ToolBar fontsToolbar;
    private ToolBar paragraphToolbar;
    private ToggleButton overlineButton;

    private ToggleButton keyboardDiagramButton;
    ChoiceBox<RichTextAreaSkin.KeyMapValue> keyboardSelector;
    public DecoratedRTA() {
        decoratedRTA = this;
        this.mainStage = EditorMain.mainStage;
        this.editor = new RichTextArea(mainStage);
        setup();
    }
    public void setup() {
        //presets combo box
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

        //font families combo box
        ComboBox<String> fontFamilies = new ComboBox<>();
        fontFamilies.getItems().setAll(Font.getFamilies());
        fontFamilies.setPrefWidth(130);
        fontFamilies.setTooltip(new Tooltip("Select Font"));
        new TextDecorateAction<>(editor, fontFamilies.valueProperty(), TextDecoration::getFontFamily, (builder, a) -> builder.fontFamily(a).build());
        fontFamilies.setValue("Noto Sans");

        //font size box
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
        fontSize.setValue(primaryFontSize);
        //this is to make the dropdown open to the selected item's scroll position
        fontSize.setOnShowing(e -> {
            ListView list = (ListView) ((ComboBoxListViewSkin<Double>) fontSize.getSkin()).getPopupContent();
            int listIndex = fontSize.getSelectionModel().getSelectedIndex()- 3;
            list.scrollTo(Math.max(0, listIndex));
        });
        fontSize.setOnAction(e -> primaryFontSize = fontSize.getValue());

        //color pickers
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

        //overline button
        overlineButton = new ToggleButton();
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
        keyboardDiagramButton = new ToggleButton();
        keyboardDiagramButton.setTooltip(new Tooltip("Show Keyboard Diagram"));
        FontIcon icon = new FontIcon(LineAwesomeSolid.KEYBOARD);
        icon.setIconSize(20);
        keyboardDiagramButton.setGraphic(icon);
        keyboardDiagramButton.setOnAction(e -> {
            if (keyboardDiagramButton.isSelected()) {
                KeyboardDiagram.getInstance().updateAndShow();
            }
            else KeyboardDiagram.getInstance().hide();
        });

        //unicode field
        final TextField unicodeField = new TextField();
        unicodeField.setTooltip(new Tooltip("Unicode (x/hex, #/decimal)"));
        unicodeField.setPrefColumnCount(6);
        unicodeField.setPromptText("unicode val");
        unicodeField.setOnAction(e -> {
            String text = unicodeField.getText();
            editor.getActionFactory().insertUnicode(text).execute(e);
        });
        //  unicodeField.setOnAction(editor.getActionFactory().insertUnicode(unicodeField.getText())::execute);  for reasons I do not understand, this does not respond to text typed in the box (but does with setText()).

        //keyboard selector
        keyboardSelector = new ChoiceBox<>();
        keyboardSelector.getItems().setAll(RichTextAreaSkin.KeyMapValue.values());
        keyboardSelector.setValue(RichTextAreaSkin.KeyMapValue.BASE);
        keyboardSelector.setTooltip(new Tooltip("Select Keyboard"));

        //toolbars
        editToolbar = new ToolBar();
        editToolbar.getItems().setAll(
                actionButton(LineAwesomeSolid.SAVE, "Save",  editor.getActionFactory().save()),
                wideSeparator(5),

                actionButton(LineAwesomeSolid.CUT, "Cut",   editor.getActionFactory().cut()),
                actionButton(LineAwesomeSolid.COPY, "Copy",  editor.getActionFactory().copy()),
                actionButton(LineAwesomeSolid.PASTE, "Paste", editor.getActionFactory().paste()),
                actionButton(LineAwesomeSolid.UNDO, "Undo",  editor.getActionFactory().undo()),
                actionButton(LineAwesomeSolid.REDO, "Redo",  editor.getActionFactory().redo()),
                wideSeparator(8),

                actionImage(LineAwesomeSolid.IMAGE, "Insert Image"),
                actionEmoji("Insert Emoji"),
                actionHyperlink(LineAwesomeSolid.LINK, "Insert Hyperlink"),
                actionTable(LineAwesomeSolid.TABLE, "Insert Table", td -> editor.getActionFactory().insertTable(td)),
                wideSeparator(8),

                fontFamilies,
                fontSize,
                presets,
                keyboardDiagramButton
            );
//       editToolbar.setPadding(new Insets(3,0,3,20));

        fontsToolbar = new ToolBar();
        fontsToolbar.getItems().setAll(
                keyboardSelector,
                unicodeField,
                wideSeparator(0.9),

                createToggleButton(LineAwesomeSolid.BOLD, "Bold (not for symbol fonts)", property -> new TextDecorateAction<>(editor, property, d -> d.getFontWeight() == BOLD, (builder, a) -> builder.fontWeight(a ? BOLD : NORMAL).build())),
                createToggleButton(LineAwesomeSolid.ITALIC, "Italic (not for symbol fonts)", property -> new TextDecorateAction<>(editor, property, d -> d.getFontPosture() == ITALIC, (builder, a) -> builder.fontPosture(a ? ITALIC : REGULAR).build())),
                createToggleButton(LineAwesomeSolid.STRIKETHROUGH, "Strikethrough", property -> new TextDecorateAction<>(editor, property, TextDecoration::isStrikethrough, (builder, a) -> builder.strikethrough(a).build())),
                createToggleButton(LineAwesomeSolid.UNDERLINE, "Underline", property -> new TextDecorateAction<>(editor, property, TextDecoration::isUnderline, (builder, a) -> builder.underline(a).build())),
                overlineButton,
 //               wideSeparator(0),

                createToggleButton(LineAwesomeSolid.SUPERSCRIPT, "Superscript", property -> new TextDecorateAction<>(editor, property, TextDecoration::isSuperscript, (builder, a) -> builder.superscript(a).subscript(false).transSuperscript(false).transSubscript(false).build())),
                createColoredToggleButton(LineAwesomeSolid.SUPERSCRIPT, "Superscript (translated back)", property -> new TextDecorateAction<>(editor, property, TextDecoration::isTransSuperscript, (builder, a) -> builder.transSuperscript(a).transSubscript(false).subscript(false).superscript(false).build())),
                createToggleButton(LineAwesomeSolid.SUBSCRIPT, "Subscript", property -> new TextDecorateAction<>(editor, property, TextDecoration::isSubscript, (builder, a) -> builder.subscript(a).superscript(false).transSuperscript(false).transSubscript(false).build())),
                createColoredToggleButton(LineAwesomeSolid.SUBSCRIPT, "Subscript (translated back)", property -> new TextDecorateAction<>(editor, property, TextDecoration::isTransSubscript, (builder, a) -> builder.transSubscript(a).transSuperscript(false).superscript(false).subscript(false).build())),
                wideSeparator(0.9),

                textForeground,
                textBackground
            );

        paragraphToolbar = new ToolBar();
        paragraphToolbar.getItems().setAll(
                createToggleButton(LineAwesomeSolid.ALIGN_LEFT, "Align Left", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getAlignment() == TextAlignment.LEFT, (builder, a) -> builder.alignment(TextAlignment.LEFT).build())),
                createToggleButton(LineAwesomeSolid.ALIGN_CENTER, "Align Center", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getAlignment() == TextAlignment.CENTER, (builder, a) -> builder.alignment(a ? TextAlignment.CENTER : TextAlignment.LEFT).build())),
                createToggleButton(LineAwesomeSolid.ALIGN_RIGHT, "Align Right", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getAlignment() == TextAlignment.RIGHT, (builder, a) -> builder.alignment(a ? TextAlignment.RIGHT : TextAlignment.LEFT).build())),
                createToggleButton(LineAwesomeSolid.ALIGN_JUSTIFY, "Justify", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getAlignment() == TextAlignment.JUSTIFY, (builder, a) -> builder.alignment(a ? TextAlignment.JUSTIFY : TextAlignment.LEFT).build())),

                createJumpSpinner("Spacing", "Space for wrapped lines (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getSpacing(), (builder, a) -> builder.spacing(a).build())),
                createJumpSpinner("Top", "Top Margin (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getTopInset(), (builder, a) -> builder.topInset(a).build())),
                createJumpSpinner("Bottom", "Bottom Margin (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getBottomInset(), (builder, a) -> builder.bottomInset(a).build())),
                createJumpSpinner("Left", "LeftMargin (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getLeftInset(), (builder, a) -> builder.leftInset(a).build())),
                createJumpSpinner("Right", "Right Margin (point value)", p -> new ParagraphDecorateAction<>(editor, p, v -> (int) v.getRightInset(), (builder, a) -> builder.rightInset(a).build())),

                createToggleButton(LineAwesomeSolid.LIST_OL, "Numbered List", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getGraphicType() == NUMBERED_LIST, (builder, a) -> builder.graphicType(a ? NUMBERED_LIST : NONE).build())),
                createToggleButton(LineAwesomeSolid.LIST_UL, "Unordered List/Outline", property -> new ParagraphDecorateAction<>(editor, property, d -> d.getGraphicType() == BULLETED_LIST, (builder, a) -> builder.graphicType(a ? BULLETED_LIST : NONE).build())),
                createSpinner("Indent", "Indent Level", p -> new ParagraphDecorateAction<>(editor, p, ParagraphDecoration::getIndentationLevel, (builder, a) -> builder.indentationLevel(a).build()))
            );
        setRtaListeners();
    }

    public void setRtaListeners() {
        BorderPane tempPane = new BorderPane(editor);
        Scene tempScene = new Scene(tempPane);
        editor.applyCss();
        editor.layout();
        overlineButton.selectedProperty().bindBidirectional(((RichTextAreaSkin) editor.getSkin()).overlineOnProperty());
        overlineButton.selectedProperty().addListener((v, ov, nv) -> editor.requestFocus());
        keyboardSelector.valueProperty().bindBidirectional(((RichTextAreaSkin) editor.getSkin()).keyMapStateProperty());
        keyboardSelector.getSelectionModel().selectedItemProperty().addListener((v, ov, nv) -> {
            ((RichTextAreaSkin) editor.getSkin()).setMaps(nv);
            if (KeyboardDiagram.getInstance().isShowing()) KeyboardDiagram.getInstance().updateAndShow();
            editor.requestFocus();   //have not been able to find way to stop keyboard window from stealing focus see https://stackoverflow.com/questions/33151460/javafx-stop-new-window-stealing-focus
        });
    }

    private Separator wideSeparator(double inset) {
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPadding(new Insets(0, inset,0, inset));
        return separator;
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

    private ToggleButton createColoredToggleButton(Ikon ikon, String tooltip, Function<ObjectProperty<Boolean>, DecorateAction<Boolean>> function) {
        final ToggleButton toggleButton = new ToggleButton();
        toggleButton.setTooltip(new Tooltip(tooltip));
        toggleButton.setStyle("-fx-border-color:LIGHTGREEN;-fx-border-radius:3;-fx-border-width:1;");
        FontIcon icon = new FontIcon(ikon);
        icon.setIconSize(20);
        toggleButton.setGraphic(icon);
        function.apply(toggleButton.selectedProperty().asObject());
        return toggleButton;
    }



    private HBox createSpinner(String text, String tooltip, Function<ObjectProperty<Integer>, DecorateAction<Integer>> function) {
        Spinner<Integer> spinner = new Spinner<>();
        //
        //this is a kludge because the spinners do not stop on mouse release.  This behavior only appeared after I started
        //switching headers on focus.  It doesn't happen to a spinner by itself, but only in combination with some application.
        //this "fix" prevents mouse down spinning, but that will be ok for me. I absolutly do not understand how this happens!
        //see https://stackoverflow.com/questions/55933513/javafx-spinner-keeps-going-after-removed-from-scene
        spinner.valueProperty().addListener((obs, ov, nv) -> {
           Node increment = spinner.lookup(".increment-arrow-button");
           if (increment != null) increment.getOnMouseReleased().handle(null);
           Node decrement = spinner.lookup(".decrement-arrow-button");
           if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });
        //
        spinner.setTooltip(new Tooltip(tooltip));
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20);
        spinner.setValueFactory(valueFactory);
        spinner.setPrefWidth(55);
        spinner.setEditable(false);
        function.apply(valueFactory.valueProperty());
        HBox spinnerBox = new HBox(5, new Label(text), spinner);
        spinnerBox.setAlignment(Pos.CENTER);
        return spinnerBox;
    }

    //
    private HBox createJumpSpinner(String text, String tooltip, Function<ObjectProperty<Integer>, DecorateAction<Integer>> function) {
        Spinner<Integer> spinner = new Spinner<>();
        //
        //this is a kludge because the spinners do not stop on mouse release.  This behavior only appeared after I started
        //switching headers on focus.  It doesn't happen to a spinner by itself, but only in combination with RTA logic.
        //this "fix" prevents mouse down spinning, but that will be ok for me. I absolutly do not understand how this happens!
        spinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = spinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = spinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });
        //
        spinner.setTooltip(new Tooltip(tooltip));
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20,0,2);
        spinner.setValueFactory(valueFactory);
        spinner.setPrefWidth(55);
        spinner.setEditable(false);
        function.apply(valueFactory.valueProperty());
        HBox spinnerBox = new HBox(5, new Label(text), spinner);
        spinnerBox.setAlignment(Pos.CENTER);
        return spinnerBox;
    }
    //

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

        DEFAULT("Default",13, NORMAL, TextAlignment.LEFT),
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

    public RichTextArea getEditor() {
        return editor;
    }

    public double getPrimaryFontSize() {
        return primaryFontSize;
    }

    public ToolBar getEditToolbar() {
        return editToolbar;
    }

    public ToolBar getFontsToolbar() {
        return fontsToolbar;
    }

    public ToolBar getParagraphToolbar() {
        return paragraphToolbar;
    }

    public ToggleButton getKeyboardDiagramButton () {
        return keyboardDiagramButton;
    }


}
