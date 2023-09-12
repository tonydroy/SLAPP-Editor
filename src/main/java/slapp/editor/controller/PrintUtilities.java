package slapp.editor.controller;

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

    public static void printNode(Node node, Stage owner) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(owner);
            if (proceed) {
                    boolean printed = job.printPage(pageLayout, node);
                    if (printed) {
                        job.endJob();
                    }
                    else {
                        EditorAlerts.showSimpleAlert(owner, "Print problem", "Print job failed.");
                    }                }
            else {
                EditorAlerts.showSimpleAlert(owner,"Print problem", "Failed to open printer dialog");
            }
        } else {
            EditorAlerts.showSimpleAlert(owner, "Print problem", "Failed to create printer job");
        }
    }

    public static void updatePageLayout(Stage owner) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            job.getJobSettings().setPageLayout(pageLayout);
            boolean proceed = job.showPageSetupDialog(owner);
            if (proceed) {
                pageLayout = job.getJobSettings().getPageLayout();
                job.endJob();
            }
            else {
                EditorAlerts.showSimpleAlert(owner, "Layout problem", "Failed to open page layout dialog");
            }
        }
        else {
            EditorAlerts.showSimpleAlert(owner, "Layout problem", "Failed to create layout job");
        }
    }
    public static PageLayout getPageLayout() {
        return pageLayout;
    }

    public static void printRTA(RichTextArea rta, Stage owner) {
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        Document document = rta.getDocument();

        RichTextArea printable = new RichTextArea(owner);
        printable.setDocument(document);
        printable.setContentAreaWidth(pageLayout.getPrintableWidth());
        printable.setPrefWidth(pageLayout.getPrintableWidth());
        printable.setPrefHeight(pageLayout.getPrintableHeight() * 2);
        //this height is is a kludge - if rta goes over its prefHeight, it generates a scrollbar (how?).
        // Print cuts page off, so the scroll bar makes no sense.

        printNode(printable, owner);
    }

}
