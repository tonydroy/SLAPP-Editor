package slapp.editor.vertical_tree.object_models;

import java.io.Serializable;

public class ClickableNodeLinkMod implements Serializable {

    private String idString;
    private String sourceId;
    private String targetId;


    public ClickableNodeLinkMod(String idString, String sourceId, String targetId) {
        this.idString = idString;
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public String getIdString() {
        return idString;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }
}
