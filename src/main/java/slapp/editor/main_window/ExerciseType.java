package slapp.editor.main_window;

public enum ExerciseType {

    AB_EXPLAIN("AB explain", "Requires binary choice with an explanation."),
    ABEFG_EXPLAIN("AB/EFG explain", "Requires both binary and tertiary choice with an explanation"),
    DERIVATION("Derivation", "Requires a derivation"),
    DRVTN_EXP("Derivation explain", "Requires a derivation and explanation."),
    HORIZONTAL_TREE("Horizontal Tree", "Exercise requires a horizontal tree (of the type encountered in Ch 4 of SL) and an explanation."),
    SIMPLE_EDITOR("Simple editor", "Exercise requires a text response -- anywhere from short answer, to paragraph, to essay."),
    TRUTH_TABLE("Truth table", "Exercise requires a truth table."),
    TRUTH_TABLE_ABEXP("Truth table AB explain", "Exercise requires a truth table with a binary choice and explanation."),
    VERTICAL_TREE("Vertical Tree", "Exercise requires a vertical tree (or map) of types as encountered in chapters 2, 4, and 5 of SL."),
    VERTICAL_TREE_EXP ("Vertical Tree Explain", "Exercise requires a vertical tree (or map) with an explanation."),
    VERTICAL_TREE_ABEXP ("Vertical Tree AB Explain", "Exercise requires a vertical tree (or map) with a binary choice and explanation."),
    VERTICAL_TREE_ABEFEXP ("Vertical Tree AB/EF Explain", "Exercise requires a vertical tree (or map) with a pair of binary choices and explanation.");


    public final String label;
    public final String description;

    private ExerciseType(String label, String description) {

        this.label = label;
        this.description = description;
    }
    public String toString() { return label; }
    public String getDescription() {return description; }

}
