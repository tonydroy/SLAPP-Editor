package slapp.editor.main_window.assignment;

import java.util.ArrayList;

public class Header {
    private int creationID;
    private int workingID;
    private String assignmentName;
    private String studentName;
    private ArrayList<HeaderItem> headerItems = new ArrayList<>();




    public int getCreationID() {
        return creationID;
    }

    public void setCreationID(int creationID) {
        this.creationID = creationID;
    }

    public int getWorkingID() {
        return workingID;
    }

    public void setWorkingID(int workingID) {
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

    public ArrayList<HeaderItem> getHeaderItems() {
        return headerItems;
    }

    public void setHeaderItems(ArrayList<HeaderItem> headerItems) {
        this.headerItems = headerItems;
    }
}
