/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

package slapp.editor.main_window;

import java.util.LinkedList;


public class UndoRedoList<T> extends LinkedList<T> {
    private int maxSize;
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
