package slapp.editor.derivation;

import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import slapp.editor.decorated_rta.DecoratedRTA;

import java.util.ArrayList;
import java.util.List;

public class ViewLine {

    private Label lineNumberLabel = null;
    private int depth = 0;
    private LineType lineType = null;
    private DecoratedRTA lineContentDRTA = null;
    private TextFlow justificationFlow = null;
    private boolean setupLine = false;
    private List<Label> clientLabels = new ArrayList<Label>();

    public ViewLine(){}
    public ViewLine(Label lineNumberLabel, int depth, LineType lineType, boolean setupLine, DecoratedRTA drta, TextFlow justificationFlow, List<Label> clientLabels) {
        this.lineNumberLabel = lineNumberLabel;
        this.depth = depth;
        this.lineType = lineType;
        this.setupLine = setupLine;
        this.lineContentDRTA = drta;
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

    public void setLineContentDRTA(DecoratedRTA lineContentDRTA) {
        this.lineContentDRTA = lineContentDRTA;
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

    public DecoratedRTA getLineContentDRTA() {
        return lineContentDRTA;
    }

    public TextFlow getJustificationFlow() {
        return justificationFlow;
    }

    public boolean isSetupLine() {
        return setupLine;
    }

    public void setSetupLine(boolean setupLine) {
        this.setupLine = setupLine;
    }

    public List<Label> getClientLabels() {
        return clientLabels;
    }

    public void setClientLabels(List<Label> clientLabels) {
        this.clientLabels = clientLabels;
    }
}
