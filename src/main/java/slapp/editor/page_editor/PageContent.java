package slapp.editor.page_editor;

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;

public class PageContent implements Serializable {

    Document pageDoc;
    double textHeight;


    public PageContent(Document pageDoc, double textHeight) {
        this.pageDoc = pageDoc;
        this.textHeight = textHeight;
    }
    public Document getPageDoc() {
        return pageDoc;
    }

    public double getTextHeight() {
        return textHeight;
    }

    public void setTextHeight(double textHeight) {
        this.textHeight = textHeight;
    }
}
