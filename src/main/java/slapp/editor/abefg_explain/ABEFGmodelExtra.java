/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.
If not, see <https://www.gnu.org/licenses/>.
 */

package slapp.editor.abefg_explain;

import java.io.Serializable;

public class ABEFGmodelExtra implements Serializable {

    private String leaderAB = "";
    private String promptA = "";
    private boolean valueA = false;
    private String promptB = "";
    private boolean valueB = false;
    private String leaderEFG ="";
    private String promptE = "";
    private boolean valueE = false;
    private String promptF = "";
    private boolean valueF = false;
    private String promptG = "";
    private boolean valueG = false;



   public ABEFGmodelExtra() {}
    public ABEFGmodelExtra(String leaderAB, String promptA, boolean valueA, String promptB, boolean valueB, String leaderEFG, String promptE, boolean valueE, String promptF, boolean valueF, String promptG, boolean valueG) {
        this();
        this.leaderAB = leaderAB;
        this.promptA = promptA;
        this.valueA = valueA;
        this.promptB = promptB;
        this.valueB = valueB;
        this.leaderEFG = leaderEFG;
        this.promptE = promptE;
        this.valueE = valueE;
        this.promptF = promptF;
        this.valueF = valueF;
        this.promptG = promptG;
        this.valueG = valueG;
    }

    public String getLeaderAB() {
        return leaderAB;
    }
    public String getLeaderEFG() { return leaderEFG; }

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

    public String getPromptE() {
        return promptE;
    }

    public boolean getValueE() {
        return valueE;
    }

    public void setValueE(boolean valueE) {
        this.valueE = valueE;
    }
    public String getPromptF() {
        return promptF;
    }

    public boolean getValueF() {
        return valueF;
    }

    public void setValueF(boolean valueF) {
        this.valueF = valueF;
    }
    public String getPromptG() {
        return promptG;
    }

    public boolean getValueG() {
        return valueG;
    }

    public void setValueG(boolean valueG) {
        this.valueG = valueG;
    }
}
