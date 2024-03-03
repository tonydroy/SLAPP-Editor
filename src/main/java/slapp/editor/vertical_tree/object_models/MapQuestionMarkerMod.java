package slapp.editor.vertical_tree.object_models;

import java.io.Serializable;

public class MapQuestionMarkerMod implements Serializable {

    private String idString;
    private String targetId;
    int targetMapStage;
    private Double[] targetXAnchors;


    public MapQuestionMarkerMod(String idString, String targetId, int targetMapStage, Double[] targetXAnchors) {
        this.idString = idString;
        this.targetId = targetId;
        this.targetMapStage = targetMapStage;
        this.targetXAnchors = targetXAnchors;
    }

    public String getIdString() {
        return idString;
    }

    public String getTargetId() {
        return targetId;
    }

    public int getTargetMapStage() {
        return targetMapStage;
    }

    public Double[] getTargetXAnchors() {
        return targetXAnchors;
    }
}
