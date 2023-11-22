package slapp.editor.main_window;

public enum ExerciseType {

    AB_EXPLAIN("AB explain"),
    ABEFG_EXPLAIN("AB/EFG explain"),
    DERIVATION("Derivation"),
    SIMPLE_EDITOR("simple editor");


    public final String label;

    private ExerciseType(String label) {
        this.label = label;
    }
    public String toString() { return label; }

}
