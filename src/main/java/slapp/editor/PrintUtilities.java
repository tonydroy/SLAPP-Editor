package slapp.editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.print.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;

public class PrintUtilities {

    private static PageLayout pageLayout;

    static {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            pageLayout = job.getJobSettings().getPageLayout();
        }
        else {
            System.out.println("problem initializing page layout");
        }
    }

    public static void printNode(Node node) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(EditorMain.mainStage);
            if (proceed) {
//                job.getJobSettings().setOutputFile("C:/Users/tonyd/OneDrive/Desktop/test.pdf");
//              by this means we should be able to print multi-page pdf: print each page, then using pdfBox (or the like)
//              merge the different pdf files into one.  How to make this work on Mac?
                boolean printed = job.printPage(pageLayout, node);
                if (!printed) {
                    EditorAlerts.showSimpleAlert("Print problem", "Print job failed.");
                }
            }
            job.endJob();
        } else {
            EditorAlerts.showSimpleAlert("Print problem", "Failed to create printer job");
        }
    }

    public static void updatePageLayout() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            job.getJobSettings().setPageLayout(pageLayout);
            boolean proceed = job.showPageSetupDialog(EditorMain.mainStage);
            if (proceed) {
                pageLayout = job.getJobSettings().getPageLayout();
            }
            job.endJob();
        }
        else {
            EditorAlerts.showSimpleAlert("Layout problem", "Failed to create layout job");
        }
    }

    public static void printRTA(RichTextArea rta) {
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        Document document = rta.getDocument();

        RichTextArea printable = new RichTextArea(EditorMain.mainStage);
        printable.setDocument(document);
        printable.setContentAreaWidth(pageLayout.getPrintableWidth());
        printable.setPrefWidth(pageLayout.getPrintableWidth());
        printable.setPrefHeight(pageLayout.getPrintableHeight() * 2);
        //this height is is a kludge - if rta goes over its prefHeight, it generates a scrollbar (how?).
        // Print cuts page off, so the scroll bar makes no sense.

        printNode(printable);
    }

    public static double getPageHeight() {
        return pageLayout.getPrintableHeight();
    }

    public static double getPageWidth() {
        return pageLayout.getPrintableWidth();
    }

}
