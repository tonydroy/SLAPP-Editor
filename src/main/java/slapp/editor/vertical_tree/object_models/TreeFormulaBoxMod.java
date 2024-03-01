package slapp.editor.vertical_tree.object_models;

import com.gluonhq.richtextarea.model.Document;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.List;

public class TreeFormulaBoxMod implements Serializable {

    private String idString;
    private double layoutX;
    private double layoutY;
    private double width;
    private Document text;
    private List<String> linkIdStrings;
    boolean box;
    boolean star;
    boolean annotation;
    String annotationText;
    Double[] circleXAnchors;
    List<UnderlineMod> underlineList;
    List<Integer> baseline;


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

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public boolean isAnnotation() {
        return annotation;
    }

    public void setAnnotation(boolean annotation) {
        this.annotation = annotation;
    }

    public String getAnnotationText() {
        return annotationText;
    }

    public void setAnnotationText(String annotationText) {
        this.annotationText = annotationText;
    }

    public Double[] getCircleXAnchors() {
        return circleXAnchors;
    }

    public void setCircleXAnchors(Double[] circleXAnchors) {
        this.circleXAnchors = circleXAnchors;
    }

    public List<UnderlineMod> getUnderlineList() {
        return underlineList;
    }

    public void setUnderlineList(List<UnderlineMod> underlineList) {
        this.underlineList = underlineList;
    }

    public List<Integer> getBaseline() {
        return baseline;
    }

    public void setBaseline(List<Integer> baseline) {
        this.baseline = baseline;
    }
}
