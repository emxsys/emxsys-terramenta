package com.terramenta.time.options;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

public class SortableComboBoxModel<E extends Comparable<? super E>> extends AbstractListModel<E> implements MutableComboBoxModel<E>, Serializable {

    private final List<E> objects;
    private Object selectedObject;
    private boolean autoSort = true;

    /**
     * Constructs an empty DefaultComboBoxModel object.
     */
    public SortableComboBoxModel() {
        objects = new ArrayList<>();
    }

    /**
     * Constructs a DefaultComboBoxModel object initialized with
     * an array of objects.
     *
     * @param items an array of Object objects
     */
    public SortableComboBoxModel(final E items[]) {
        objects = new ArrayList<>(items.length);

        int i, c;
        for (i = 0, c = items.length; i < c; i++) {
            objects.add(items[i]);
        }

        if (isAutoSort()) {
            Collections.sort(objects);
        }

        if (getSize() > 0) {
            selectedObject = getElementAt(0);
        }
    }

    /**
     * Constructs a DefaultComboBoxModel object initialized with
     * a vector.
     *
     * @param v a Vector object ...
     */
    public SortableComboBoxModel(List<E> v) {
        objects = v;

        if (isAutoSort()) {
            Collections.sort(objects);
        }

        if (getSize() > 0) {
            selectedObject = getElementAt(0);
        }
    }

    // implements javax.swing.ComboBoxModel
    /**
     * Set the value of the selected item. The selected item may be null.
     * <p>
     * @param anObject The combo box value or null for no selection.
     */
    @Override
    public void setSelectedItem(Object anObject) {
        if ((selectedObject != null && !selectedObject.equals(anObject))
                || selectedObject == null && anObject != null) {
            selectedObject = anObject;
            fireContentsChanged(this, -1, -1);
        }
    }

    // implements javax.swing.ComboBoxModel
    @Override
    public Object getSelectedItem() {
        return selectedObject;
    }

    // implements javax.swing.ListModel
    @Override
    public int getSize() {
        return objects.size();
    }

    // implements javax.swing.ListModel
    @Override
    public E getElementAt(int index) {
        if (index >= 0 && index < objects.size()) {
            return objects.get(index);
        } else {
            return null;
        }
    }

    /**
     * Returns the index-position of the specified object in the list.
     *
     * @param anObject
     * @return an int representing the index position, where 0 is
     *         the first position
     */
    public int getIndexOf(Object anObject) {
        return objects.indexOf(anObject);
    }

    // implements javax.swing.MutableComboBoxModel
    @Override
    public void addElement(E anObject) {
        objects.add(anObject);

        if (isAutoSort()) {
            Collections.sort(objects);
        }

        fireIntervalAdded(this, objects.size() - 1, objects.size() - 1);
        if (objects.size() == 1 && selectedObject == null && anObject != null) {
            setSelectedItem(anObject);
        }
    }

    // implements javax.swing.MutableComboBoxModel
    @Override
    public void insertElementAt(E anObject, int index) {
        objects.add(index, anObject);

        if (isAutoSort()) {
            Collections.sort(objects);
        }

        fireIntervalAdded(this, index, index);
    }

    // implements javax.swing.MutableComboBoxModel
    @Override
    public void removeElementAt(int index) {
        if (getElementAt(index) == selectedObject) {
            if (index == 0) {
                setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
            } else {
                setSelectedItem(getElementAt(index - 1));
            }
        }

        objects.remove(index);

        if (isAutoSort()) {
            Collections.sort(objects);
        }

        fireIntervalRemoved(this, index, index);
    }

    // implements javax.swing.MutableComboBoxModel
    @Override
    public void removeElement(Object anObject) {
        int index = objects.indexOf(anObject);
        if (index != -1) {
            removeElementAt(index);
        }
    }

    /**
     * Empties the list.
     */
    public void removeAllElements() {
        if (objects.size() > 0) {
            int firstIndex = 0;
            int lastIndex = objects.size() - 1;
            objects.clear();
            selectedObject = null;
            fireIntervalRemoved(this, firstIndex, lastIndex);
        } else {
            selectedObject = null;
        }
    }

    public boolean isAutoSort() {
        return autoSort;
    }

    public void setAutoSort(boolean autoSort) {
        this.autoSort = autoSort;
    }

}
