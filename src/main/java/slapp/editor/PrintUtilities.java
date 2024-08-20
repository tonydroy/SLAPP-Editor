package slapp.editor;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;
import static slapp.editor.EditorMain.os;

import slapp.editor.main_window.MainWindowView;

public class PrintUtilities {


    private static PageLayout pageLayout;
    //this is the layout visible to the rest of the program for the printable area
    private static DoubleProperty pageWidth = new SimpleDoubleProperty();
    private static DoubleProperty pageHeight = new SimpleDoubleProperty();

    private static PageLayout internalPageLayout;
    // this is the layout with space for footer, for internal use
    private static Printer pdfPrinter = null;
    private static Printer printer;
    private static Region spacer = new Region();
    private static double baseScale = 1.0;
    private static List<PrintBufferItem> printBuffer = new ArrayList<>();
    private static VBox topBox;



    static {
        spacer.setVisible(false);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            PageLayout baseLayout = job.getJobSettings().getPageLayout();
            pageHeight.set(baseLayout.getPrintableHeight());
            pageWidth.set(baseLayout.getPrintableWidth());
            printer = job.getPrinter();
            double bottomMargin = Math.max(baseLayout.getBottomMargin(), 48);
            pageLayout = printer.createPageLayout(baseLayout.getPaper(), baseLayout.getPageOrientation(), baseLayout.getLeftMargin(), baseLayout.getRightMargin(), baseLayout.getTopMargin(), bottomMargin );

            double top = pageLayout.getTopMargin();
            double bottom = 18.0;
            double left = pageLayout.getLeftMargin();
            double right = pageLayout.getRightMargin();

            if (pageLayout.getPageOrientation() == PageOrientation.LANDSCAPE) {
                if (os.startsWith("Win")) {
                    internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), left, right, bottom, top);
                }
                else if (os.startsWith("Mac")) {
                    double offset = 18.0;
                    internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), left + offset, right - offset, top - offset, bottom + offset);
                }
            } else {
                internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), left, right, top, bottom);
            }



//            internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), pageLayout.getLeftMargin(), pageLayout.getRightMargin(), top, bottom);

        }
        else {
            EditorAlerts.showSimpleAlert("Print Problem", "Failed to set print and page defaults.");
        }
    }


    public static void updatePageLayout() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            job.getJobSettings().setPageLayout(pageLayout);

            boolean proceed = job.showPageSetupDialog(null);
     //       boolean proceed = job.showPageSetupDialog(EditorMain.mainStage);
            if (proceed) {
                PageLayout baseLayout = job.getJobSettings().getPageLayout();
                Printer printer = job.getPrinter();
                double bottomMargin = Math.max(baseLayout.getBottomMargin(), 48);
                pageLayout = printer.createPageLayout(baseLayout.getPaper(), baseLayout.getPageOrientation(), baseLayout.getLeftMargin(), baseLayout.getRightMargin(), baseLayout.getTopMargin(), bottomMargin );
                pageHeight.set(pageLayout.getPrintableHeight());
                pageWidth.set(pageLayout.getPrintableWidth());

                double top = pageLayout.getTopMargin();
                double bottom = 18.0;
                double left = pageLayout.getLeftMargin();
                double right = pageLayout.getRightMargin();

                if (pageLayout.getPageOrientation() == PageOrientation.LANDSCAPE) {
                    if (os.startsWith("Win")) {
                        internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), left, right, bottom, top);
                    }
                    else if (os.startsWith("Mac")) {
                        double offset = 18.0;
                        internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), left + offset, right - offset, top - offset, bottom + offset);
                    }
                } else {
                    internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), left, right, top, bottom);
                }



 //               internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), pageLayout.getLeftMargin(), pageLayout.getRightMargin(), top, bottom);
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
                    proceed = job.showPrintDialog(null);

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
//        pageBox.setPadding(new Insets(25,0,0,0));   // this papers over the fact that top margin is not working (giving about .75 margin); see compensation adding node to page
        double netHeight = 0;
        int i = 0;

        if (topBox != null) {
            topBox.setMaxWidth(getPageWidth() / baseScale);
            topBox.setMinWidth(getPageWidth() / baseScale);
            PrintBufferItem topBoxItem = getNodeSize(topBox);
            topBoxItem.setScale(baseScale);
            printBuffer.add(0, topBoxItem);
        }

        while (i < printBuffer.size()) {
            PrintBufferItem bufferItem = printBuffer.get(i);
            double scale = bufferItem.getScale();
            double nodeHeight = bufferItem.getHeight() * scale;

            Node node = bufferItem.getNode();
            node.getTransforms().clear();
            node.getTransforms().add(new Scale(scale, scale));
            Group nodeGroup = new Group(bufferItem.getNode());

            double newHeight = nodeHeight + netHeight;

            //if the node fits on the page add to page
            if (newHeight <= pageLayout.getPrintableHeight()) {
                pageBox.getChildren().add(nodeGroup);
                netHeight = newHeight;
                i++;

                //if all the nodes have been added print page
                if (i == printBuffer.size()) {

                    spacer.setPrefHeight(internalPageLayout.getPrintableHeight() - (netHeight + 16.0)) ;  //24  temp fix to stop cutoff footer for vtree
                    pageBox.getChildren().addAll(spacer, getFooterBox(++pageNum, footerInfo));
                    success = (job.printPage(internalPageLayout, pageBox) && success);
                }
                //if the node does not fit on this page, print page and start new
            } else if (!pageBox.getChildren().isEmpty()) {

                spacer.setPrefHeight(internalPageLayout.getPrintableHeight() - (netHeight + 16.0));  //16
                pageBox.getChildren().addAll(spacer, getFooterBox(++pageNum, footerInfo));
                success = (job.printPage(internalPageLayout, pageBox) && success);

                netHeight = 0;
                pageBox.getChildren().clear();
                //node is too big for a page, truncate and print anyway (human check says ok)
            } else {
                //the pageBox is too tall with this node, so we need another approach
                Rectangle nodeBox = new Rectangle(pageLayout.getPrintableWidth(), pageLayout.getPrintableHeight());
                nodeGroup.setClip(nodeBox);

                HBox footerBox = getFooterBox(++pageNum, footerInfo);
                footerBox.getTransforms().add(new Scale(scale, scale));
                StackPane pane = new StackPane(nodeGroup, footerBox);
                pane.setAlignment(footerBox, Pos.TOP_LEFT);
                footerBox.setTranslateY(internalPageLayout.getPrintableHeight() - 16.0);  //16
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
        footerBox.getTransforms().add(new Scale(baseScale, baseScale));
        footerBox.setHgrow(spacer, Priority.ALWAYS);
        footerBox.setMaxWidth(getPageWidth() / baseScale);
        footerBox.setMinWidth(getPageWidth() / baseScale);

        return footerBox;
    }

    public static void sendBufferToPrint(String footerInfo) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(null);
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


    private static PrintBufferItem getNodeSize(Node node) {
        Group root = new Group();
        Scene scene = new Scene(root);

        root.getChildren().add(node);
        root.applyCss();
        root.layout();

        Bounds bounds = node.getLayoutBounds();

        return new PrintBufferItem(node, bounds.getHeight(), bounds.getWidth());
    }

    public static boolean processPrintNode(Node node) {
        boolean nodeFit = true;
        double wScale = 1.0;
        double hScale = 1.0;
        PrintBufferItem bufferItem = getNodeSize(node);
        double width = bufferItem.getWidth();
        double height = bufferItem.getHeight();

        if (width > getPageWidth() / baseScale) {
            nodeFit = false;
            wScale = getPageWidth() / width;
        }
        if (height > getPageHeight() / baseScale) {
            nodeFit = false;
            hScale = getPageHeight() / height;
        }
        double scale = Math.min(Math.min(wScale, hScale), baseScale);
        bufferItem.setScale(scale);
        printBuffer.add(bufferItem);

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

    public static PageLayout getPageLayout() {
        return pageLayout;
    }

    public static void setPageLayout(PageLayout pageLayout) {
        if (pageLayout != null) {
            PrintUtilities.pageLayout = pageLayout;
            pageHeight.set(pageLayout.getPrintableHeight());
            pageWidth.set(pageLayout.getPrintableWidth());

            /*
            In landscape printing, the top and bottom margins are reversed!!   Is this a feature of my Windows setup?  Revisit on Mac
             */
            double top = pageLayout.getTopMargin();
            double bottom = 18.0;
            if (pageLayout.getPageOrientation() == PageOrientation.LANDSCAPE) {
                bottom = top;
                top = 18.0;
            }
            internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), pageLayout.getLeftMargin(), pageLayout.getRightMargin(), top, bottom);
//           internalPageLayout = printer.createPageLayout(pageLayout.getPaper(), pageLayout.getPageOrientation(), pageLayout.getLeftMargin(), pageLayout.getRightMargin(), pageLayout.getTopMargin(), 18.0);

        }
    }

    public static Printer getPrinter() {
        return printer;
    }

    public static void resetPrintBufferScale() {
        for (PrintBufferItem item : printBuffer) {
            item.setScale(baseScale);
        }

         }
    public static void resetPrintBuffer(double baseScale) {
        PrintUtilities.baseScale = baseScale;
        printBuffer.clear();
        topBox = null;
    }

    //this is a stub to let the extended demo compile
        public static void printRTA(RichTextArea rta) {}

    public static void setTopBox(VBox box) { topBox = box; }
}
