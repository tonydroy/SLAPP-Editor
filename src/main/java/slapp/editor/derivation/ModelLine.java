package slapp.editor.derivation;

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;

public class ModelLine implements Serializable {

    private int gridLineIndex;
    private int depth;
    private Document lineContentDoc;
    private String justification = "";
    private LineType lineType = LineType.MAIN_CONTENT_LINE;

    public ModelLine(int depth, Document lineContent, String justification, LineType type) {
        this.depth = depth;
        this.lineContentDoc = lineContent;
        this.justification = justification;
        this.lineType = type;
    }


    public int getDepth() {
        return depth;
    }

    public Document getLineContentDoc() {
        return lineContentDoc;
    }

    public String getJustification() {
        return justification;
    }

    public LineType getLineType() {
        return lineType;
    }


}
