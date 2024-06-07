package slapp.editor;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;
import slapp.editor.main_window.MainWindowView;

public class PrintUtilities {


    private static PageLayout pageLayout;
    //this is the layout visible to the rest of the program for the printable area
    private static DoubleProperty pageWidth = new SimpleDoubleProperty();
    private static DoubleProperty pageHeight = new SimpleDoubleProperty();


    private static PageLayout internalPageLayout;
    // this is the layout with space for footer, for internal use
    private static Printer pdfPrinter = null;
    private static Region spacer = new Region();

    private static double scale = 1.0;
    private static List<Pair<Node, Double>> printBuffer = new ArrayList<>();

    private static VBox topBox;


    static {
        spacer.setVisible(false);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            PageLayout baseLayout = job.getJobSettings().getPageLayout();
            pageHeight.set(baseLayout.getPrintableHeight());
            pageWidth.set(baseLayout.getPrintableWidth());
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
                pageHeight.set(pageLayout.getPrintableHeight());
                pageWidth.set(pageLayout.getPrintableWidth());

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

                boolean proceed = true;

                String message = "Please select a PDF printer from the following window.\n\n" +
                        "There are a variety of such printers, each with slightly different characteristics.  On PC, 'Microsoft Print to PDF' works fine.\nYou will only have to do this once per session.\n\n" +
                        "Current: " + current;

                Alert confirm = EditorAlerts.confirmationAlert("Select Printer", message);
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() != OK) proceed = false;

                if (proceed) {
                    proceed = job.showPrintDialog(EditorMain.mainStage);

                    if (proceed) {
                        pdfPrinter = job.getPrinter();
                        isExportSetup = true;
                    }
                }
                job.endJob();
            }
        }
        return isExportSetup;
    }

    public static void printNodes(String footerInfo, PrinterJob job) {

        MainWindowView.activateProgressIndicator("printing");

        boolean success = true;
        int pageNum = 0;
        VBox pageBox = new VBox();
        pageBox.getTransforms().add(new Scale(scale, scale));
        double netHeight = 0;
        int i = 0;

        if (topBox != null) {
            topBox.setMaxWidth(getPageWidth() / scale);
            topBox.setMinWidth(getPageWidth() / scale);
            Pair<Double, Double> size = getNodeSize(topBox);
            double height = size.getValue();
            printBuffer.add(0, new Pair(topBox, height));
        }

        while (i < printBuffer.size()) {
            Pair<Node, Double> bufferItem = printBuffer.get(i);
            Node node = bufferItem.getKey();
            double nodeHeight = bufferItem.getValue() * scale;
            double newHeight = nodeHeight + netHeight;
            //if the node fits on the page add to page
            if (newHeight <= pageLayout.getPrintableHeight()) {
                pageBox.getChildren().add(node);
                netHeight = newHeight;
                i++;
                //if all the nodes have been added print page
                if (i == printBuffer.size()) {
                    spacer.setPrefHeight((internalPageLayout.getPrintableHeight() - (netHeight + 16.0)) / scale);
                    pageBox.getChildren().addAll(spacer, getFooterBox(++pageNum, footerInfo));
                    //                   pageBox.getChildren().addAll(spacer, new Label(Integer.toString(++pageNum)));
                    success = (job.printPage(internalPageLayout, pageBox) && success);
                }
                //if the node does not fit on this page, print page and start new
            } else if (!pageBox.getChildren().isEmpty()) {
                spacer.setPrefHeight((internalPageLayout.getPrintableHeight() - (netHeight + 16.0)) / scale);
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
                footerBox.getTransforms().add(new Scale(scale, scale));
                StackPane pane = new StackPane(node, footerBox);
                pane.setAlignment(footerBox, Pos.TOP_LEFT);
                footerBox.setTranslateY(internalPageLayout.getPrintableHeight() - 16.0);
                footerBox.setTranslateX(0.0);
                success = (job.printPage(internalPageLayout, pane) && success);
                i++;
            }
        }
        MainWindowView.deactivateProgressIndicator();
        if (success) EditorAlerts.fleetingPopup("Print job complete.");
        else EditorAlerts.fleetingPopup("Print job did not complete.");
    }

    private static HBox getFooterBox(int pageNum, String infoString) {
        Region spacer = new Region();
        HBox footerBox = new HBox(new Label(Integer.toString(pageNum)), spacer, new Label(infoString));
        footerBox.setHgrow(spacer, Priority.ALWAYS);
        footerBox.setMaxWidth(getPageWidth()/scale);
        footerBox.setMinWidth(getPageWidth()/scale);
        return footerBox;
    }

    public static void sendBufferToPrint(String footerInfo) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(EditorMain.mainStage);
            if (proceed) printNodes(footerInfo, job);
            job.endJob();
        }
        else  EditorAlerts.showSimpleAlert("Print Problem", "Failed to create printer job");

    }

    public static void sendBufferToPDF(String footerInfo) {
        boolean success = false;
        if (pdfPrinter != null || runExportSetup()) {
            PrinterJob job = PrinterJob.createPrinterJob(pdfPrinter);
            if (job != null) {
                printNodes(footerInfo, job);
                success = true;
                job.endJob();
            }
        }
        if (!success) EditorAlerts.showSimpleAlert("Print Problem", "Failed to create export job");
    }


    private static Pair<Double, Double> getNodeSize(Node node) {
        Group root = new Group();
        Scene scene = new Scene(root);
        root.getChildren().add(node);
        root.applyCss();
        root.layout();
        Bounds bounds = node.getLayoutBounds();
        return new Pair(bounds.getWidth(), bounds.getHeight());
    }

    public static boolean processPrintNode(Node node) {
        boolean nodeFit = true;
        double wScale = 1.0;
        double hScale = 1.0;
        Pair<Double, Double> size = getNodeSize(node);
        double width = size.getKey();     //increase height and width to stop right/bottom cutoff on scaled images
        double height = size.getValue();

        if (width > getPageWidth()) {
            nodeFit = false;
            wScale = getPageWidth()/width;
        }
        if (height > getPageHeight()) {
            nodeFit = false;
            hScale = getPageHeight()/height;
        }
        scale = Math.min(Math.min(wScale, hScale), scale);
        printBuffer.add(new Pair(node, height));

        return nodeFit;
    }




    public static double getPageWidth() {
        return pageWidth.get();
    }

    public static DoubleProperty pageWidthProperty() {
        return pageWidth;
    }

    public static double getPageHeight() {
        return pageHeight.get();
    }

    public static DoubleProperty pageHeightProperty() {
        return pageHeight;
    }

  /*
    public static double getPageHeight() {
        return pageLayout.getPrintableHeight();
    }

    public static double getPageWidth() {
        return pageLayout.getPrintableWidth();
    }

 */

    public static void resetScale() { scale = 1.0; }
    public static void resetPrintBuffer() {
        printBuffer.clear();
        scale = 1.0;
        topBox = null;
    }

    //this is a stub to let the extended demo compile
        public static void printRTA(RichTextArea rta) {}

    public static void setTopBox(VBox box) { topBox = box; }
}
