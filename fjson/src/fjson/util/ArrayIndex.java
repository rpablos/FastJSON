//  Author: Ronald Pablos
//  Year: 2013

package fjson.util;

import fjson.FjsonConstants;
import fjson.FjsonException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rpablos
 */
public class ArrayIndex<T> implements Cloneable {
    public Object[] array;
    int size = 0;

    public ArrayIndex(int initialCapacity) {
        array = new Object[Math.max(1,initialCapacity)];
    }
    public ArrayIndex() {
        this(100);
    }
    
    public int add(T t) {
        if (size >= FjsonConstants.MAXIMUM_TABLE_ENTRIES)
            return HashMapObjectInt.NO_INDEX;
        if (size == array.length)
            resize(array.length * 2);
        array[size] = t;
        return size++;
    }
    
    public void addAll(List<T> list) {
        for (T t: list) {
            add(t);
        }
    }
    
    public T get(int index) throws FjsonException {
        try {
            return (T) array[index];
        } catch(java.lang.ArrayIndexOutOfBoundsException ex) {
            throw new FjsonException(ex);
        }
    }


    public int getSize() {
        return size;
    }

    public void truncate(int size) {
        //array = Arrays.copyOf(array, size);
        this.size = size;
    }
    private void resize(int size) {
        array = Arrays.copyOf(array, size);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {  
        ArrayIndex<T> result =   (ArrayIndex<T>) super.clone();
        result.array = Arrays.copyOf(array, array.length);
        return result;
    }
    
    public void ensureCapacity(int capacity) {
        if (capacity <= array.length)
            return;
        resize(capacity);
    }
}
