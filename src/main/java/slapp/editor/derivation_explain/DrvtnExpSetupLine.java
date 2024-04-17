package slapp.editor.derivation_explain;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ControlType;

public class DrvtnExpSetupLine {
    private DrvtnExpCreate drvtnExpCreate;
    private BoxedDRTA formulaBoxedDRTA;
    private BoxedDRTA justificationBoxedDRTA;
    private Spinner<Integer> depthSpinner;
    private CheckBox premiseBox;
    private CheckBox conclusionBox;
    private CheckBox addShelfBox;
    private CheckBox addGapBox;
    private HBox spinnerBox;
    private boolean modified = false;
    private double formulaBoxHeight = 27;

    public DrvtnExpSetupLine(DrvtnExpCreate drvtnExpCreate) {
        this.drvtnExpCreate = drvtnExpCreate;
        formulaBoxedDRTA = new BoxedDRTA();
        formulaBoxedDRTA.getDRTA().getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea formulaRTA = formulaBoxedDRTA.getRTA();

        formulaRTA.setMaxHeight(formulaBoxHeight);
        formulaRTA.setMinHeight(formulaBoxHeight);
        formulaRTA.setPrefWidth(400);
        formulaRTA.setContentAreaWidth(500);
        formulaRTA.getStylesheets().add("RichTextFieldWide.css");
        formulaRTA.setPromptText("Formula");

        formulaRTA.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShortcutDown())) {
                justificationBoxedDRTA.getRTA().requestFocus();
                e.consume();
            }
        });

        formulaRTA.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
               drvtnExpCreate.editorInFocus(formulaBoxedDRTA.getDRTA(), ControlType.FIELD);
            }
        });

        justificationBoxedDRTA = new BoxedDRTA();
        justificationBoxedDRTA.getDRTA().getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.BASE);
        RichTextArea justificationRTA = justificationBoxedDRTA.getRTA();
        justificationRTA.setDocument(new Document(""));
        justificationRTA.setMaxHeight(formulaBoxHeight);
        justificationRTA.setMinHeight(formulaBoxHeight);
        justificationRTA.setPrefWidth(100);
        justificationRTA.setContentAreaWidth(200);
        justificationRTA.getStylesheets().add("RichTextFieldWide.css");
        justificationRTA.setPromptText("Justification");

        justificationRTA.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShortcutDown())) {
                depthSpinner.requestFocus();
                e.consume();
            }
            else if (code == KeyCode.LEFT && e.isShortcutDown()) {
                formulaRTA.requestFocus();
                e.consume();
            }
        });

        justificationRTA.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                drvtnExpCreate.editorInFocus(justificationBoxedDRTA.getDRTA(), ControlType.STATEMENT);
            }
        });

        depthSpinner = new Spinner<>(1,19,1);
        depthSpinner.setPrefWidth(55);
        depthSpinner.valueProperty().addListener((ob, ov, nv) -> modified = true);
        spinnerBox = new HBox(10, new Label("Depth"), depthSpinner);
        spinnerBox.setAlignment(Pos.CENTER_LEFT);

        premiseBox = new CheckBox("Premise");
        premiseBox.setSelected(false);
        premiseBox.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv == true) {
                conclusionBox.setSelected(false);
                addGapBox.setSelected(false);
            }
            modified = true;
        });

        conclusionBox = new CheckBox("Conclusion");
        conclusionBox.setSelected(false);
        conclusionBox.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv == true) {
                premiseBox.setSelected(false);
                addShelfBox.setSelected(false);
                addGapBox.setSelected(false);
            }
            modified = true;
        });

        addShelfBox = new CheckBox("Add shelf");
        addShelfBox.setSelected(false);
        addShelfBox.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv == true) addGapBox.setSelected(false);
            modified = true;
        });

        addGapBox = new CheckBox("Add gap");
        addShelfBox.setSelected(false);
        addGapBox.selectedProperty().addListener((ob, ov, nv) -> {
            if (nv == true) addShelfBox.setSelected(false);
            modified = true;
        });
    }


    public BoxedDRTA getFormulaBoxedDRTA() {
        return formulaBoxedDRTA;
    }
    public BoxedDRTA getJustificationBoxedDRTA() {
        return justificationBoxedDRTA;
    }
    public Spinner getDepthSpinner() {
        return depthSpinner;
    }
    public CheckBox getPremiseBox() {
        return premiseBox;
    }
    public CheckBox getConclusionBox() {
        return conclusionBox;
    }
    public CheckBox getAddShelfBox() {
        return addShelfBox;
    }
    public CheckBox getAddGapBox() {
        return addGapBox;
    }
    public HBox getSpinnerBox() {
        return spinnerBox;
    }

    public boolean isModified() {
        return (modified || formulaBoxedDRTA.getRTA().isModified() || justificationBoxedDRTA.getRTA().isModified());
    }
    public void setModified(boolean modified) { this.modified = modified; }

}
