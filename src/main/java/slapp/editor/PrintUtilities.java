package slapp.editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;

import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class PrintUtilities {

    private static PageLayout pageLayout;

    static {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            pageLayout = job.getJobSettings().getPageLayout();
        }
        else {
            EditorAlerts.showSimpleAlert("Print Problem", "Failed to create printer job.");
        }
    }

    public static void printNode(Node node, PrinterJob job) {

//      job.getJobSettings().setOutputFile("C:/Users/tonyd/OneDrive/Desktop/test.pdf");

//      by this means we should be able to print multi-page pdf: print each page, then using pdfBox (or the like)
//      merge the different pdf files into one.  How to make this work on Mac?

        boolean printed = job.printPage(pageLayout, node);
        if (!printed) {
            EditorAlerts.showSimpleAlert("Print problem", "Print job failed.");
        }
    }




    public static void updatePageLayout() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            job.getJobSettings().setPageLayout(pageLayout);
            boolean proceed = job.showPageSetupDialog(EditorMain.mainStage);
            if (proceed) {
                pageLayout = job.getJobSettings().getPageLayout();
                System.out.println(pageLayout.toString());
            }
            job.endJob();
        }
        else {
            EditorAlerts.showSimpleAlert("Layout problem", "Failed to create layout job");
        }
    }

    public static void printExercise(ArrayList<Node> nodeList) {
        if (checkExerciseNodeHeights(nodeList)) {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean proceed = job.showPrintDialog(EditorMain.mainStage);
                if (proceed) {
                    VBox pageBox = new VBox();
                    double netHeight = 0;
                    int i = 0;

                    while (i < nodeList.size()) {
                        Node node = nodeList.get(i);
                        double newHeight = getNodeHeight(node) + netHeight;
                        //if the node fits on the page add to page
                        if (newHeight <= PrintUtilities.getPageHeight()) {
                            pageBox.getChildren().add(node);
                            netHeight = newHeight;
                            i++;
                            //if all the nodes have been added print page
                            if (i == nodeList.size()) {
                                printNode(pageBox, job);
                            }
                        //if the node does not fit on this page, print page and start new
                        } else if (!pageBox.getChildren().isEmpty()) {
                            printNode(pageBox, job);
                            netHeight = 0;
                            pageBox.getChildren().clear();
                        //node is too big for a page, truncate and print anyway (human check says ok)
                        } else {
                            printNode(node, job);
                            i++;

                        }
                    }
                    EditorAlerts.fleetingPopup("Job sent to printer.");
                }
                job.endJob();
            } else {
                EditorAlerts.showSimpleAlert("Print Problem", "Failed to create printer job");
            }
        }
    }

    private static double getNodeHeight(Node node) {
        Group root = new Group();
        Scene scene = new Scene(root);
        root.getChildren().add(node);
        root.applyCss();
        root.layout();
        return node.getLayoutBounds().getHeight();
    }

    private static boolean checkExerciseNodeHeights(ArrayList<Node> printNodes) {
        boolean heightGood = true;
        for (Node node : printNodes) {
            if (getNodeHeight(node) > PrintUtilities.getPageHeight()) {
                heightGood = false;
                break;
            }
        }
        if (!heightGood) {
            String message = "This exercise includes at least one block that takes up more than a page.  Content exceeding page bounds will be cropped.  \n\n Continue to print exercise?";
            Alert confirm = EditorAlerts.confirmationAlert("Page Problem:", message);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == OK) heightGood = true;
        }
        return heightGood;
    }




//this is a stub to let the extended demo compile
    public static void printRTA(RichTextArea rta) {}
        /*
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        Document document = rta.getDocument();

        RichTextArea printable = new RichTextArea(EditorMain.mainStage);
        printable.setDocument(document);
        printable.setContentAreaWidth(PrintUtilities.getPageWidth());
        printable.setPrefWidth(pageLayout.getPrintableWidth());
        printable.setPrefHeight(pageLayout.getPrintableHeight() * 2);
        //this height is is a kludge - if rta goes over its prefHeight, it generates a scrollbar (how?).
        // Print cuts page off, so the scroll bar makes no sense.

        printNode(printable);
    }

 */

    public static double getPageHeight() {
        return pageLayout.getPrintableHeight();
    }

    public static double getPageWidth() {
        return pageLayout.getPrintableWidth();
    }

}
