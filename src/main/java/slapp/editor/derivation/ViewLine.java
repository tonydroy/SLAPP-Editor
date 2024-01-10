package slapp.editor.derivation;

import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;

import java.util.ArrayList;
import java.util.List;

public class ViewLine {

    private Label lineNumberLabel = null;
    private int depth = 0;
    private LineType lineType = null;
    private BoxedDRTA lineContentBoxedDRTA = null;
    private TextFlow justificationFlow = null;
    private List<Label> clientLabels = new ArrayList<Label>();

    public ViewLine(){}
    public ViewLine(Label lineNumberLabel, int depth, LineType lineType, BoxedDRTA bdrta, TextFlow justificationFlow, List<Label> clientLabels) {
        this.lineNumberLabel = lineNumberLabel;
        this.depth = depth;
        this.lineType = lineType;
        this.lineContentBoxedDRTA = bdrta;
        this.justificationFlow = justificationFlow;
        this.clientLabels = clientLabels;
    }

    public void setLineNumberLabel(Label lineNumberLabel) {
        this.lineNumberLabel = lineNumberLabel;
    }

    public void setDepth(int depth) { this.depth = depth; }

    public void setLineType(LineType type) {
        this.lineType = type;
    }

    public void setLineContentBoxedDRTA(BoxedDRTA lineContentBoxedDRTA) {
        this.lineContentBoxedDRTA = lineContentBoxedDRTA;
    }

    public void setJustificationFlow(TextFlow justificationFlow) {this.justificationFlow = justificationFlow; }

    public Label getLineNumberLabel() {
        return lineNumberLabel;
    }

    public int getDepth() {
        return depth;
    }

    public LineType getLineType() {
        return lineType;
    }

    public BoxedDRTA getLineContentBoxedDRTA() {
        return lineContentBoxedDRTA;
    }

    public TextFlow getJustificationFlow() {
        return justificationFlow;
    }

    public List<Label> getClientLabels() {
        return clientLabels;
    }

}
