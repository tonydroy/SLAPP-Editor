/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

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
