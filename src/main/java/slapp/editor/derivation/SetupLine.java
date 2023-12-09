package slapp.editor.derivation;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import slapp.editor.decorated_rta.DecoratedRTA;

public class SetupLine {
    private DecoratedRTA formulaDRTA;
    private DecoratedRTA justificationDRTA;
    private Spinner<Integer> depthSpinner;
    private CheckBox premiseBox;
    private CheckBox conclusionBox;
    private CheckBox addShelfBox;
    private CheckBox addGapBox;
    private HBox spinnerBox;
    private boolean modified = false;

    public SetupLine() {
        formulaDRTA = new DecoratedRTA();
        formulaDRTA.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);
        RichTextArea formulaRTA = formulaDRTA.getEditor();

        formulaRTA.setMaxHeight(27);
        formulaRTA.setMinHeight(27);
        formulaRTA.setPrefWidth(400);
        formulaRTA.setContentAreaWidth(500);
        formulaRTA.getStylesheets().add("RichTextField.css");
        formulaRTA.setPromptText("Formula");


        //        formulaRTA.getActionFactory().saveNow().execute(new ActionEvent());

        justificationDRTA = new DecoratedRTA();
        justificationDRTA.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.BASE);
        RichTextArea justificationRTA = justificationDRTA.getEditor();


        justificationRTA.setDocument(new Document(""));


        justificationRTA.setMaxHeight(27);
        justificationRTA.setMinHeight(27);
        justificationRTA.setPrefWidth(100);
        justificationRTA.setContentAreaWidth(200);
        justificationRTA.getStylesheets().add("RichTextField.css");
        justificationRTA.setPromptText("Justification");
//               justificationRTA.getActionFactory().saveNow().execute(new ActionEvent());

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
        } );

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

    public DecoratedRTA getFormulaDRTA() {
        return formulaDRTA;
    }

    public void setFormulaDRTA(DecoratedRTA formulaDRTA) {
        this.formulaDRTA = formulaDRTA;
    }

    public DecoratedRTA getJustificationDRTA() {
        return justificationDRTA;
    }

    public void setJustificationDRTA(DecoratedRTA justificationDRTA) {
        this.justificationDRTA = justificationDRTA;
    }

    public Spinner getDepthSpinner() {
        return depthSpinner;
    }

    public void setDepthSpinner(Spinner depthSpinner) {
        this.depthSpinner = depthSpinner;
    }

    public CheckBox getPremiseBox() {
        return premiseBox;
    }

    public void setPremiseBox(CheckBox premiseBox) {
        this.premiseBox = premiseBox;
    }

    public CheckBox getConclusionBox() {
        return conclusionBox;
    }

    public void setConclusionBox(CheckBox conclusionBox) {
        this.conclusionBox = conclusionBox;
    }

    public CheckBox getAddShelfBox() {
        return addShelfBox;
    }

    public void setAddShelfBox(CheckBox addShelfBox) {
        this.addShelfBox = addShelfBox;
    }

    public CheckBox getAddGapBox() {
        return addGapBox;
    }

    public void setAddGapBox(CheckBox addGapBox) {
        this.addGapBox = addGapBox;
    }

    public HBox getSpinnerBox() {
        return spinnerBox;
    }

    public void setSpinnerBox(HBox spinnerBox) {
        this.spinnerBox = spinnerBox;
    }

    public boolean isModified() {

        return (modified || formulaDRTA.getEditor().isModified() || justificationDRTA.getEditor().isModified());
    }

    public void setModified(boolean modified) { this.modified = modified; }
}
