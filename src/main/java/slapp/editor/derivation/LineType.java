/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

package slapp.editor.derivation;

/**
 * Enum for types of derivation line
 */
public enum LineType {
    /**
     * A content line that is not a premise or conclusion line
     */
    MAIN_CONTENT_LINE,

    /**
     * Premise content line
     */
    PREMISE_LINE,

    /**
     * Conclusion content line
     */
    CONCLUSION_LINE,

    /**
     * Thin line for shelf (usually after premise or assumption)
     */
    SHELF_LINE,

    /**
     * Thin gap usually between subderivations (as for vE)
     */
    GAP_LINE,

    /**
     * Shelf line that is part of the (fixed) setup
     */
    SETUP_SHELF_LINE,

    /**
     * Gap line that is part of the (fixed) setup)
     */
    SETUP_GAP_LINE;


    /**
     * A content line holds a formula field
     * @param type the line type
     * @return true if content line and otherwise false
     */
    public static boolean isContentLine(LineType type) {
        return (type == MAIN_CONTENT_LINE || type == PREMISE_LINE || type == CONCLUSION_LINE);
    }

    /**
     * A setup line restricts modification
     * @param type the line type
     * @return true if setup line and otherwise false
     */
    public static boolean isSetupLine(LineType type) {
        return (type == PREMISE_LINE || type == CONCLUSION_LINE || type == SETUP_SHELF_LINE || type == SETUP_GAP_LINE);
    }

    /**
     * A shelf line is the (thin) line with the small shelf that may appear under premises or assumption
     * @param type the line type
     * @return true if shelf line and otherwise false
     */
    public static boolean isShelfLine(LineType type) {
        return (type == SHELF_LINE || type == SETUP_SHELF_LINE);
    }

    /**
     * A gap line is the (thin) line with small gap to separate subderivations
     * @param type the line type
     * @return true if gap line and otherwise false
     */
    public static boolean isGapLine(LineType type) {
        return (type == GAP_LINE || type == SETUP_GAP_LINE);
    }

}
