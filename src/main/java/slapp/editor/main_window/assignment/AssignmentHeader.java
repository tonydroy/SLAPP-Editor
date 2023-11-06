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

}
