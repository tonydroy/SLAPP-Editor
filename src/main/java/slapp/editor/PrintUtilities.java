package slapp.editor;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class PrintUtilities {


    private static PageLayout pageLayout;
    //this is the layout visible to the rest of the program for the printable area
    private static PageLayout internalPageLayout;
    // this is the layout with space for footer, for internal use
    private static Printer pdfPrinter = null;
    private static Region spacer = new Region();

    static {
        spacer.setVisible(false);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            PageLayout baseLayout = job.getJobSettings().getPageLayout();
            Printer printer = job.getPrinter();
            double bottomMargin = Math.max(baseLayout.getBottomMargin(), 48);
            pageLayout = printer.createPageLayout(baseLayout.getPaper(), baseLayout.getPageOrientation(), baseLayout.getLeftMargin(), baseLayout.getRightMargin(), baseLayout.getTopMargin(), bottomMargin );
            internalPageLayout = printer.createPageLayout(baseLayout.getPaper(), baseLayout.getPageOrientation(), baseLayout.getLeftMargin(), baseLayout.getRightMargin(), baseLayout.getTopMargin(), 18.0);
        }
        else {
            EditorAlerts.showSimpleAlert("Print Problem", "Failed to set print and page defaults.");
        }
    }


    public static void updatePageLayout() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            job.getJobSettings().setPageLayout(internalPageLayout);
            boolean proceed = job.showPageSetupDialog(EditorMain.mainStage);
            if (proceed) {
                PageLayout baseLayout = job.getJobSettings().getPageLayout();
                Printer printer = job.getPrinter();
                double bottomMargin = Math.max(baseLayout.getBottomMargin(), 48);
                pageLayout = printer.createPageLayout(baseLayout.getPaper(), baseLayout.getPageOrientation(), baseLayout.getLeftMargin(), baseLayout.getRightMargin(), baseLayout.getTopMargin(), bottomMargin );
                internalPageLayout = printer.createPageLayout(baseLayout.getPaper(), baseLayout.getPageOrientation(), baseLayout.getLeftMargin(), baseLayout.getRightMargin(), baseLayout.getTopMargin(), 18.0);
            }
            job.endJob();
        }
        else {
            EditorAlerts.showSimpleAlert("Layout problem", "Failed to create layout job");
        }
    }

    public static void exportSetup() {
        if (!runExportSetup())  EditorAlerts.showSimpleAlert("Print Problem", "Export setup failed");
    }
    private static boolean runExportSetup() {
        boolean isExportSetup = false;
        String os = System.getProperty("os.name");
        if (os.startsWith("Mac")) {
            EditorAlerts.showSimpleAlert("Use Print Option", "On Macintosh there is no independent PDF export option.  To create a PDF, select 'Print' and from the dropdown at the bottom of the print dialog, 'Save to PDF'.\n\n" +
                    "Export works by a \"PDF printer\".  Recent versions of the MAC OS exclude such printers from the printer list -- preferring to require the internal MAC option.");
        }
        else {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                String current = "None";
                if (pdfPrinter != null) current = pdfPrinter.toString();
                EditorAlerts.showSimpleAlert("Select Printer", "Please select a PDF printer from the following window.\n\n" +
                        "There are a variety of such printers, each with slightly different characteristics.  On PC, 'Microsoft Print to PDF' works fine.\n\n" +
                        "Current: " + current);
                boolean proceed = job.showPrintDialog(EditorMain.mainStage);
                if (proceed) {
                    pdfPrinter = job.getPrinter();
                    isExportSetup = true;
                }
                job.endJob();
            }
        }
        return isExportSetup;
    }

    public static void printNodes(List<Node> nodeList, String footerInfo, PrinterJob job) {
        boolean success = true;
        int pageNum = 0;
        VBox pageBox = new VBox();
        double netHeight = 0;
        int i = 0;

        while (i < nodeList.size()) {
            Node node = nodeList.get(i);
            double newHeight = getNodeHeight(node) + netHeight;
            //if the node fits on the page add to page
            if (newHeight <= pageLayout.getPrintableHeight()) {
                pageBox.getChildren().add(node);
                netHeight = newHeight;
                i++;
                //if all the nodes have been added print page
                if (i == nodeList.size()) {
                    spacer.setPrefHeight(internalPageLayout.getPrintableHeight() - (netHeight + 16.0));
                    pageBox.getChildren().addAll(spacer, getFooterBox(++pageNum, footerInfo));
 //                   pageBox.getChildren().addAll(spacer, new Label(Integer.toString(++pageNum)));
                    success = (job.printPage(internalPageLayout, pageBox) && success);
                }
                //if the node does not fit on this page, print page and start new
            } else if (!pageBox.getChildren().isEmpty()) {
                spacer.setPrefHeight(internalPageLayout.getPrintableHeight() - (netHeight + 16.0));
                pageBox.getChildren().addAll(spacer, getFooterBox(++pageNum, footerInfo));
 //               pageBox.getChildren().addAll(spacer, new Label(Integer.toString(++pageNum)));
                success = (job.printPage(internalPageLayout, pageBox) && success);
                netHeight = 0;
                pageBox.getChildren().clear();
                //node is too big for a page, truncate and print anyway (human check says ok)
            } else {
                //the pageBox is too tall with this node, so we need another approach
                Rectangle nodeBox = new Rectangle(pageLayout.getPrintableWidth(), pageLayout.getPrintableHeight());
                node.setClip(nodeBox);
 //               Label pageLabel = new Label(Integer.toString(++pageNum));

//                StackPane pane = new StackPane(node, pageLabel);
                HBox footerBox = getFooterBox(++pageNum, footerInfo);
                StackPane pane = new StackPane(node, footerBox);
                pane.setAlignment(footerBox, Pos.TOP_LEFT);
                footerBox.setTranslateY(internalPageLayout.getPrintableHeight() - 16.0);
                footerBox.setTranslateX(0.0);
                success = (job.printPage(internalPageLayout, pane) && success);
                i++;
            }
        }
        if (success) EditorAlerts.fleetingPopup("Print job complete.");
        else EditorAlerts.fleetingPopup("Print job did not complete.");
    }

    private static HBox getFooterBox(int pageNum, String infoString) {
        Region spacer = new Region();
        HBox footerBox = new HBox(new Label(Integer.toString(pageNum)), spacer, new Label(infoString));
        footerBox.setHgrow(spacer, Priority.ALWAYS);
        return footerBox;
    }

    public static void printExercise(List<Node> nodeList, String exerciseName) {
        if (checkExerciseNodeHeights(nodeList, exerciseName)) {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean proceed = job.showPrintDialog(EditorMain.mainStage);
                if (proceed) printNodes(nodeList, null, job);
                job.endJob();
            }
            else  EditorAlerts.showSimpleAlert("Print Problem", "Failed to create printer job");
        }
    }

    public static void printAssignment(List<Node> nodeList, String footerInfo) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(EditorMain.mainStage);
            if (proceed) printNodes(nodeList, footerInfo, job);
            job.endJob();
        }
        else  EditorAlerts.showSimpleAlert("Print Problem", "Failed to create printer job");
    }



    public static void exportExerciseToPDF(List<Node> nodeList, String exerciseName) {
        boolean success = false;
        if (pdfPrinter != null || runExportSetup()) {
            if (checkExerciseNodeHeights(nodeList, exerciseName)) {
                PrinterJob job = PrinterJob.createPrinterJob(pdfPrinter);
                if (job != null) {
                    printNodes(nodeList, null, job);
                    success = true;
                    job.endJob();
                }
            }
        }
        if (!success) EditorAlerts.showSimpleAlert("Print Problem", "Failed to create export job");
    }

    public static void exportAssignmentToPDF(List<Node> nodeList, String footerInfo) {
        boolean success = false;
        if (pdfPrinter !=null || runExportSetup()) {
            PrinterJob job  = PrinterJob.createPrinterJob(pdfPrinter);
            if (job != null) {
                printNodes(nodeList, footerInfo, job);
                job.endJob();
            }
            else  EditorAlerts.showSimpleAlert("Print Problem", "Failed to create export job");
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

    public static boolean checkExerciseNodeHeights(List<Node> printNodes, String exerciseName) {
        boolean heightGood = true;
        for (Node node : printNodes) {
            if (getNodeHeight(node) > PrintUtilities.getPageHeight()) {
                heightGood = false;
                break;
            }
        }
        if (!heightGood) {
            String message = "Exercise " + exerciseName + " includes at least one block that takes up more than a page.  Content exceeding page bounds will be cropped.  \n\n Continue to print?";
            Alert confirm = EditorAlerts.confirmationAlert("Page Problem:", message);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == OK) heightGood = true;
        }
        return heightGood;
    }

    public static double getPageHeight() {
        return pageLayout.getPrintableHeight();
    }

    public static double getPageWidth() {
        return pageLayout.getPrintableWidth();
    }





    //this is a stub to let the extended demo compile
        public static void printRTA(RichTextArea rta) {}
}
