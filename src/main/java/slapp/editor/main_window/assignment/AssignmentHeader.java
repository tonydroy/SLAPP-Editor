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

package slapp.editor.main_window.assignment;

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssignmentHeader implements Serializable {
    private String creationID = "";
    private String workingID = "";
    private String assignmentName = "";
    private String studentName = "";
    private Document comment = new Document();
    private double commentTextHeight = 0;


    private List<AssignmentHeaderItem> instructorItems = new ArrayList<>();
    private List<AssignmentHeaderItem> studentItems = new ArrayList<>();

    public AssignmentHeader(){ }


    public String getCreationID() {
        return creationID;
    }

    public void setCreationID(String creationID) {
        this.creationID = creationID;
    }

    public String getWorkingID() {
        return workingID;
    }

    public void setWorkingID(String workingID) {
        this.workingID = workingID;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<AssignmentHeaderItem> getInstructorItems() {
        return instructorItems;
    }

    public void setInstructorItems(List<AssignmentHeaderItem> instructorItems) {
        this.instructorItems = instructorItems;
    }

    public List<AssignmentHeaderItem> getStudentItems() {
        return studentItems;
    }

    public void setStudentItems(List<AssignmentHeaderItem> studentItems) {
        this.studentItems = studentItems;
    }

    public Document getComment() {
        return comment;
    }

    public void setComment(Document comment) {
        this.comment = comment;
    }

    public double getCommentTextHeight() {     return commentTextHeight;  }

    public void setCommentTextHeight(double commentTextHeight) {     this.commentTextHeight = commentTextHeight;  }
}
