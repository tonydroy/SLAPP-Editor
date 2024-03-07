package slapp.editor.vertical_tree.drag_drop;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import slapp.editor.vertical_tree.VerticalTreeView;

public class BottomDragResizer {

    private VerticalTreeView verticalTreeView;
    private static final int RESIZE_MARGIN = 12;

    private Region region;

    private double y;

    private boolean initMinHeight;

    private boolean dragging;

    public BottomDragResizer(VerticalTreeView verticalTreeView)  {
        this.verticalTreeView = verticalTreeView;
    }

    public void makeResizable(Region region) {
        this.region = region;

        region.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePressed(event);
            }});
        region.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseDragged(event);
            }});
        region.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseOver(event);
            }});
        region.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseReleased(event);
            }});
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        region.setCursor(Cursor.DEFAULT);
        verticalTreeView.setUndoRedoFlag(true);
        verticalTreeView.setUndoRedoFlag(false);
    }

    protected void mouseOver(MouseEvent event) {
        if(isInDraggableZone(event) || dragging) {
            region.setCursor(Cursor.V_RESIZE);
        }
        else {
            region.setCursor(Cursor.DEFAULT);
        }
    }

    protected boolean isInDraggableZone(MouseEvent event) {
        return event.getY() > (region.getHeight() - RESIZE_MARGIN);
    }

    protected void mouseDragged(MouseEvent event) {
        if(!dragging) {
            return;
        }

        double mousey = event.getY();

 //       double newHeight = region.getMinHeight() + (mousey - y);
  //      region.setMinHeight(newHeight);

        double newHeight = region.getPrefHeight() + (mousey - y);
        region.setPrefHeight(newHeight);

        y = mousey;
    }

    protected void mousePressed(MouseEvent event) {

        // ignore clicks outside of the draggable margin
        if(!isInDraggableZone(event)) {
            return;
        }

        dragging = true;

        // make sure that the minimum height is set to the current height once,
        // setting a min height that is smaller than the current height will
        // have no effect
        if (!initMinHeight) {
     //       region.setMinHeight(region.getHeight());
            region.setPrefHeight(region.getHeight());
            initMinHeight = true;
        }

        y = event.getY();
    }
}
