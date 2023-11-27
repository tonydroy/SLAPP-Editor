package slapp.editor.main_window;

public enum ControlType {

    NONE("no control"),
    STATEMENT("statement"),
    FIELD("field"),
    AREA("area");


    public final String label;
    private ControlType(String label) {
        this.label = label;
    }
    public String toString() { return label; }

    }
