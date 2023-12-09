package slapp.editor.main_window;

import java.util.LinkedList;


public class UndoRedoList<T> extends LinkedList<T> {
    private int maxSize = 10;
    private int currentIndex = 0;

    public UndoRedoList(int size) {
        super();
        this.maxSize = size;
    }

    @Override
    public void push(T element) {
        for (int i = 0; i < currentIndex; i++) {

            this.removeFirst();

        }
        while (this.size() >= maxSize) {
            this.removeLast();
        }

        currentIndex = 0;
        this.addFirst(element);
  //      System.out.println("push: " + this.toString());

    }

    public T getUndoElement() {
        T element = null;
        if (currentIndex + 1 < this.size()) {
            currentIndex++;
            element = this.get(currentIndex);
        }

        return element;
    }

    public T getRedoElement() {
        T element = null;
        if (currentIndex > 0) {
            currentIndex--;
            element = this.get(currentIndex);
        }

        return element;
    }


    public boolean canRedo() {
        boolean canRedo = true;
        if (currentIndex <= 0) canRedo = false;
        return canRedo;
    }

    public boolean canUndo() {
        boolean canUndo = false;
        if (currentIndex + 1 < this.size()) canUndo = true;
        return canUndo;
    }


}
