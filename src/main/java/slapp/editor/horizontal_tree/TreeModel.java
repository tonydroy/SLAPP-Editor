package slapp.editor.horizontal_tree;

import java.io.Serializable;

public class TreeModel implements Serializable {

    private double paneXlayout;
    private double paneYlayout;
    private double rootXlayout;
    private double rootYlayout;
    private BranchModel root;



    public void setPaneXlayout(double paneXlayout) {    this.paneXlayout = paneXlayout;   }

    public void setPaneYlayout(double paneYlayout) {   this.paneYlayout = paneYlayout;    }

    public double getPaneXlayout() {  return paneXlayout;  }

    public double getPaneYlayout() { return paneYlayout;  }

    public void setRoot(BranchModel root) {     this.root = root;  }

    public BranchModel getRoot() {   return root;  }

    public double getRootXlayout() {
        return rootXlayout;
    }

    public void setRootXlayout(double rootXlayout) {
        this.rootXlayout = rootXlayout;
    }

    public double getRootYlayout() {
        return rootYlayout;
    }

    public void setRootYlayout(double rootYlayout) {
        this.rootYlayout = rootYlayout;
    }
}