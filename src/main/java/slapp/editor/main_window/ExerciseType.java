package slapp.editor.main_window;

public enum ExerciseType {

    SIMPLE_EDITOR("simple editor"),
    AB_EXPLAIN("AB explain"),
    ABEFG_EXPLAIN("AB/EFG explain");



    public final String label;

    private ExerciseType(String label) {
        this.label = label;
    }
    public String toString() { return label; }

}
