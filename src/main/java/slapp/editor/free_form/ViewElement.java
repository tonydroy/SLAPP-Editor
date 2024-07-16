package slapp.editor.free_form;

import javafx.scene.Node;

public class ViewElement {

    private Node node;
    private int indentLevel = 0;


    public ViewElement(Node node, int indentLevel) {
        this.node = node;
        this.indentLevel = indentLevel;
    }

    public Node getNode() {     return node;  }

    public void setNode(Node node) {     this.node = node;   }

    public int getIndentLevel() {     return indentLevel;   }

    public void setIndentLevel(int indentLevel) {     this.indentLevel = indentLevel;   }

}
