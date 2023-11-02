package slapp.editor.main_window.assignment;

public class HeaderItem {

    String label = "";
    String value = "";

    public void setLabel(String label) { this.label = label; }
    public void setValue(String value) { this.value = value; }

    private String[] getItemPair() {
        String[] item = {label, value};
        return item;
    }

    private void setItemPair(String label, String value) {
        this.label = label;
        this.value = value;
    }


}
