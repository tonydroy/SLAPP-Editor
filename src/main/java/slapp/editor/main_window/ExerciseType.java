package slapp.editor.main_window;

public enum ExerciseType {

    AB_EXPLAIN("AB explain"),
    ABEFG_EXPLAIN("AB/EFG explain"),
    DERIVATION("Derivation"),
    DRVTN_EXP("Derivation explain"),
    SIMPLE_EDITOR("Simple editor"),
    TRUTH_TABLE("Truth table");


    public final String label;

    private ExerciseType(String label) {
        this.label = label;
    }
    public String toString() { return label; }

}
