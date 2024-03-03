package slapp.editor.vertical_tree.object_models;

import java.io.Serializable;

public class ClickableMapLinkMod implements Serializable {
    private String idString;
    private String sourceId;
    private String targetId;

    private int sourceMapStage;
    private int targetMapStage;
    private Double[] sourceXAnchors;
    private Double[] targetXAnchors;

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getSourceMapStage() {
        return sourceMapStage;
    }

    public void setSourceMapStage(int sourceMapStage) {
        this.sourceMapStage = sourceMapStage;
    }

    public int getTargetMapStage() {
        return targetMapStage;
    }

    public void setTargetMapStage(int targetMapStage) {
        this.targetMapStage = targetMapStage;
    }

    public Double[] getSourceXAnchors() {
        return sourceXAnchors;
    }

    public void setSourceXAnchors(Double[] sourceXAnchors) {
        this.sourceXAnchors = sourceXAnchors;
    }

    public Double[] getTargetXAnchors() {
        return targetXAnchors;
    }

    public void setTargetXAnchors(Double[] targetXAnchors) {
        this.targetXAnchors = targetXAnchors;
    }
}
