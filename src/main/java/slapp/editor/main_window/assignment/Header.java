package slapp.editor.main_window.assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Header implements Serializable {
    private String creationID = "";
    private String workingID = "";
    private String assignmentName = "";
    private String studentName = "";

    private List<HeaderItem> instructorItems = new ArrayList<>();
    private List<HeaderItem> studentItems = new ArrayList<>();

    public Header(){ }


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

    public List<HeaderItem> getInstructorItems() {
        return instructorItems;
    }

    public void setInstructorItems(List<HeaderItem> instructorItems) {
        this.instructorItems = instructorItems;
    }

    public List<HeaderItem> getStudentItems() {
        return studentItems;
    }

    public void setStudentItems(List<HeaderItem> studentItems) {
        this.studentItems = studentItems;
    }
}
