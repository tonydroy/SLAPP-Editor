package slapp.editor.vertical_tree.object_models;

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;
import java.util.List;

public class MapFormulaBoxMod implements Serializable {

    private String idString;
    private double layoutX;
    private double layoutY;
    private double width;
    private Document text;
    private List<String> linkIdStrings;



    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public double getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(double layoutX) {
        this.layoutX = layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(double layoutY) {
        this.layoutY = layoutY;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public Document getText() {
        return text;
    }

    public void setText(Document text) {
        this.text = text;
    }

    public List<String> getLinkIdStrings() {
        return linkIdStrings;
    }

    public void setLinkIdStrings(List<String> linkIdStrings) {
        this.linkIdStrings = linkIdStrings;
    }
}
