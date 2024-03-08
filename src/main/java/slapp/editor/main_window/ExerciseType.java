package slapp.editor.main_window;

public enum ExerciseType {

    AB_EXPLAIN("AB explain"),
    ABEFG_EXPLAIN("AB/EFG explain"),
    DERIVATION("Derivation"),
    DRVTN_EXP("Derivation explain"),
    SIMPLE_EDITOR("Simple editor"),
    TRUTH_TABLE("Truth table"),
    TRUTH_TABLE_EXPLAIN("Truth table explain"),
    VERTICAL_TREE("Vertical Tree"),
    VERTICAL_TREE_EXP ("Vertical Tree Explain"),
    VERTICAL_TREE_ABEXP ("Vertical Tree AB/Explain");


    public final String label;

    private ExerciseType(String label) {
        this.label = label;
    }
    public String toString() { return label; }

}
