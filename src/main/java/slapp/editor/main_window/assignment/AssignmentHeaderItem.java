package slapp.editor.main_window.assignment;

import java.io.Serializable;

public class AssignmentHeaderItem implements Serializable {

    String label = "";
    String value = "";

    public AssignmentHeaderItem(){};
    public AssignmentHeaderItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public void setLabel(String label) { this.label = label; }
    public void setValue(String value) { this.value = value; }
    public String getLabel() {return label; }
    public String getValue() { return value; }

}
