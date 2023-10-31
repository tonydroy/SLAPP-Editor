package slapp.editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import slapp.editor.EditorAlerts;

import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

public class PrintUtilities {


    private static PageLayout pageLayout;
    //this is the layout visible to the rest of the program for the printable area
    private static PageLayout internalPageLayout;
    // this is the layout with space for footer, for internal use
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

    public static void printExercise(ArrayList<Node> nodeList) {
        if (checkExerciseNodeHeights(nodeList)) {
            PrinterJob job = PrinterJob.createPrinterJob();

            //                job.getJobSettings().setOutputFile("C:/Users/tonyd/OneDrive/Desktop/test.pdf");

            if (job != null) {
                boolean proceed = job.showPrintDialog(EditorMain.mainStage);
                if (proceed) {
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
                                pageBox.getChildren().addAll(spacer, new Label(Integer.toString(++pageNum)));
                                success = (job.printPage(internalPageLayout, pageBox) && success);
                            }
                        //if the node does not fit on this page, print page and start new
                        } else if (!pageBox.getChildren().isEmpty()) {
                            spacer.setPrefHeight(internalPageLayout.getPrintableHeight() - (netHeight + 16.0));
                            pageBox.getChildren().addAll(spacer, new Label(Integer.toString(++pageNum)));
                            success = (job.printPage(internalPageLayout, pageBox) && success);
                            netHeight = 0;
                            pageBox.getChildren().clear();
                        //node is too big for a page, truncate and print anyway (human check says ok)
                        } else {
                            //the pageBox is too tall with this node, so we need another approach
                            Rectangle nodeBox = new Rectangle(pageLayout.getPrintableWidth(), pageLayout.getPrintableHeight());
                            node.setClip(nodeBox);
                            Label pageLabel = new Label(Integer.toString(++pageNum));

                            StackPane pane = new StackPane(node, pageLabel);
                            pane.setAlignment(pageLabel, Pos.TOP_LEFT);
                            pageLabel.setTranslateY(internalPageLayout.getPrintableHeight() - 16.0);
                            pageLabel.setTranslateX(0.0);
                            success = (job.printPage(internalPageLayout, pane) && success);
                            i++;
                        }
                    }
                    if (success) EditorAlerts.fleetingPopup("Print job complete.");
                    else EditorAlerts.fleetingPopup("Print job failed.");
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

    public static double getPageHeight() {
        return pageLayout.getPrintableHeight();
    }

    public static double getPageWidth() {
        return pageLayout.getPrintableWidth();
    }





    //this is a stub to let the extended demo compile
        public static void printRTA(RichTextArea rta) {}
}
