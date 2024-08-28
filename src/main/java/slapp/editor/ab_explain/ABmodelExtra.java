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

package slapp.editor.ab_explain;

import java.io.Serializable;

public class ABmodelExtra implements Serializable {
    private static final long serialVersionUID = 100L;
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
