package slapp.editor.derivation;

public enum LineType {
    MAIN_CONTENT_LINE,
    PREMISE_LINE,
    CONCLUSION_LINE,
    SETUP_SHELF_LINE,
    SETUP_GAP_LINE,

    SHELF_LINE,
    GAP_LINE;


    public static boolean isContentLine(LineType type) {
        return (type == MAIN_CONTENT_LINE || type == PREMISE_LINE || type == CONCLUSION_LINE);
    }

    public static boolean isSetupLine(LineType type) {
        return (type == PREMISE_LINE || type == CONCLUSION_LINE || type == SETUP_SHELF_LINE || type == SETUP_GAP_LINE);
    }

    public static boolean isShelfLine(LineType type) {
        return (type == SHELF_LINE || type == SETUP_SHELF_LINE);
    }

    public static boolean isGapLine(LineType type) {
        return (type == GAP_LINE || type == SETUP_GAP_LINE);
    }

}
