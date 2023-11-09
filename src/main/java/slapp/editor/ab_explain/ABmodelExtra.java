package slapp.editor.ab_explain;

import java.io.Serializable;

public class ABmodelExtra implements Serializable {

    private String leader = "";
    private String promptA = "";
    private boolean valueA = false;
    private String promptB = "";
    private boolean valueB = false;



   public ABmodelExtra() {}
    public ABmodelExtra(String leader, String promptA, boolean valueA, String promptB, boolean valueB) {
        this();
        this.leader = leader;
        this.promptA = promptA;
        this.valueA = valueA;
        this.promptB = promptB;
        this.valueB = valueB;
    }

    public String getLeader() {
        return leader;
    }

    public String getPromptA() {
        return promptA;
    }

    public boolean getValueA() {
        return valueA;
    }

    public void setValueA(boolean valueA) {
        this.valueA = valueA;
    }

    public String getPromptB() {
        return promptB;
    }

    public boolean getValueB() {
        return valueB;
    }

    public void setValueB(boolean valueB) {
        this.valueB = valueB;
    }
}
