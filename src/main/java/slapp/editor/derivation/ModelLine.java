package slapp.editor.derivation;

import com.gluonhq.richtextarea.model.Document;

public class ModelLine {

    private int gridLineIndex;
    private int depth;
    private Document lineContentDoc;
    private String justification = "";
    private LineType lineType = LineType.CONTENT_LINE;

    private boolean setupLine;

    public ModelLine(int depth, Document lineContent, String justification, LineType type, boolean setupLine) {
        this.depth = depth;
        this.lineContentDoc = lineContent;
        this.justification = justification;
        this.lineType = type;
        this.setupLine = setupLine;
    }


    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Document getLineContentDoc() {
        return lineContentDoc;
    }

    public void setLineContentDoc(Document lineContentDoc) {
        this.lineContentDoc = lineContentDoc;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public int getGridVerticalIndex() {
        return gridLineIndex;
    }

    public void setGridVerticalIndex(int gridVerticalIndex) {
        this.gridLineIndex = gridVerticalIndex;
    }

    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public boolean isSetupLine() {
        return setupLine;
    }

    public void setSetupLine(boolean setupLine) {
        this.setupLine = setupLine;
    }
}
