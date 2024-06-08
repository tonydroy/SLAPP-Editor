package slapp.editor.main_window.assignment;

import javafx.print.PageOrientation;

import java.io.Serializable;

public class PageLayoutValues implements Serializable {

    PageOrientation orientation;
    double leftMargin;
    double rightMargin;
    double topMargin;
    double bottomMargin;

    public PageLayoutValues(PageOrientation orientation, double leftMargin, double rightMargin, double topMargin, double bottomMargin) {
        this.orientation = orientation;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    public PageOrientation getOrientation() {
        return orientation;
    }



    public double getLeftMargin() {
        return leftMargin;
    }



    public double getRightMargin() {
        return rightMargin;
    }


    public double getTopMargin() {
        return topMargin;
    }



    public double getBottomMargin() {
        return bottomMargin;
    }


}
