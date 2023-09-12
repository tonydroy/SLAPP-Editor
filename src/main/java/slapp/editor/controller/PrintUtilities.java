package slapp.editor.controller;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.print.*;
import javafx.scene.Node;
import javafx.stage.Stage;

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
                System.out.println("print dialog: true");
                System.out.println("node height: " + ((RichTextArea) node).getHeight());
                System.out.println("node width: " + ((RichTextArea) node).getWidth());
                System.out.println("PageLayout: " + job.getJobSettings().getPageLayout().toString());
                    boolean printed = job.printPage(pageLayout, node);
                    if (printed) {
                        job.endJob();
                        System.out.println("job complete");
                    }
                    else {
                        System.out.println("job failed");
                    }
                }
            else {
                System.out.println("print dialog: false");
            }
        } else {
            System.out.println("failed to create printer job");
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
                System.out.println("page setup dialog: false");            }
        }
        else {
            System.out.println("failed to create printer job");
        }
    }
    public static PageLayout getPageLayout() {
        return pageLayout;
    }

    //there is a timing issue with saving the document and then getting document from rta.  By opening
    //printer window after the save command, the problem goes away.
    public static void printRTA(RichTextArea rta, Stage owner) {
 //       rta.getActionFactory().save().getActionCmd().apply(((RichTextAreaSkin)rta.getSkin()).getViewModel());
        rta.getActionFactory().save().execute(new ActionEvent());
 //       ((RichTextAreaSkin) rta.getSkin()).getViewModel().save();  //this is the command I need executed.  do w/o runlater?

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(owner);
            if (proceed) {
                Document document = rta.getDocument();
                RichTextArea printable = new RichTextArea(owner);
                printable.setDocument(document);
                printable.setContentAreaWidth(pageLayout.getPrintableWidth());
                printable.setPrefWidth(pageLayout.getPrintableWidth());
                printable.setPrefHeight(pageLayout.getPrintableHeight() * 2);   //this is a kludge - if rta goes over its prefHeight, it generates a scrollbar (how?).  Print cuts page off, so the scroll bar makes no sense.  don't save to disk, just doc

                boolean printed = job.printPage(pageLayout, printable);
                if (printed) {
                    job.endJob();
                    System.out.println("job complete");
                } else {
                    System.out.println("job failed");
                }
            } else {
                System.out.println("print dialog: false");
            }
        } else {
            System.out.println("failed to create printer job");
        }
    }



}
