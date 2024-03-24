package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;
import java.util.ArrayList;

public class BranchModel implements Serializable {

    private boolean annotation;
    private boolean formulaBranch;
    private boolean indefiniteNumBranch;
    private boolean dotDivider;
    private boolean rootBranch;
    private String annotationText;
    private Document formulaDoc;
    private double formulaPrefWidth;
    private Document connectorDoc;
    private double connectorPrefWidth;
    private ArrayList<BranchModel> dependents = new ArrayList<>();



    public ArrayList<BranchModel> getDependents() {   return dependents;  }

    public void setAnnotation(boolean annotation) {    this.annotation = annotation;   }

    public boolean isAnnotation() {     return annotation;  }

    public void setFormulaBranch(boolean formulaBranch) {    this.formulaBranch = formulaBranch;  }

    public boolean isFormulaBranch() {     return formulaBranch;  }

    public void setIndefiniteNumBranch(boolean indefiniteNumBranch) {   this.indefiniteNumBranch = indefiniteNumBranch;   }

    public boolean isIndefiniteNumBranch() {   return indefiniteNumBranch;  }

    public void setDotDivider(boolean dotDivider) {    this.dotDivider = dotDivider;  }

    public boolean isDotDivider() { return dotDivider; }

    public void setRootBranch(boolean rootBranch) {   this.rootBranch = rootBranch;   }

    public boolean isRootBranch() {  return rootBranch;  }

    public void setAnnotationText(String annotationText) {     this.annotationText = annotationText;   }

    public String getAnnotationText() {    return annotationText;  }

    public void setFormulaDoc(Document formulaDoc) {     this.formulaDoc = formulaDoc;   }

    public Document getFormulaDoc() {    return formulaDoc;   }

    public void setFormulaPrefWidth(double formulaPrefWidth) {     this.formulaPrefWidth = formulaPrefWidth;   }

    public double getFormulaPrefWidth() {     return formulaPrefWidth; }

    public void setConnectorDoc(Document connectorDoc) {    this.connectorDoc = connectorDoc;   }

    public Document getConnectorDoc() {    return connectorDoc;  }

    public double getConnectorPrefWidth() {    return connectorPrefWidth;  }

    public void setConnectorPrefWidth(double connectorPrefWidth) {     this.connectorPrefWidth = connectorPrefWidth;  }

}
