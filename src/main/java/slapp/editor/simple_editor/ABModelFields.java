package slapp.editor.simple_editor;

import java.io.Serializable;

public class ABModelFields implements Serializable {

    private String leader = "";
    private String Aprompt = "";
    private boolean Avalue = false;
    private String Bprompt = "";
    private boolean Bvalue = false;



   public ABModelFields() {}
    public ABModelFields(String leader, String Aprompt, boolean Avalue, String Bprompt, boolean Bvalue) {
        this();
        this.leader = leader;
        this.Aprompt = Aprompt;
        this.Avalue = Avalue;
        this.Bprompt = Bprompt;
        this.Bvalue = Bvalue;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getAprompt() {
        return Aprompt;
    }

    public void setAprompt(String aprompt) {
        Aprompt = aprompt;
    }

    public boolean getAvalue() {
        return Avalue;
    }

    public void setAvalue(boolean avalue) {
        Avalue = avalue;
    }

    public String getBprompt() {
        return Bprompt;
    }

    public void setBprompt(String bprompt) {
        Bprompt = bprompt;
    }

    public boolean getBvalue() {
        return Bvalue;
    }

    public void setBvalue(boolean bvalue) {
        Bvalue = bvalue;
    }
}
