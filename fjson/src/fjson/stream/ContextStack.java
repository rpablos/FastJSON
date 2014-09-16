//  Author: Ronald Pablos
//  Year: 2013

package fjson.stream;

import java.util.Arrays;

/**
 *
 * @author rpablos
 */
class ContextStack {
    boolean[] context_array = new boolean[16];
    int length = 0;

    void push(boolean isObject) {
        if (length >= context_array.length)
            resize(context_array.length*2);
        context_array[length++] = isObject;
    }
    boolean pop() {
        return context_array[--length];
    }

    boolean currentContextIsObject() {
        return (length == 0?false:context_array[length-1]);
    }
    boolean currentContextIsArray() {
        return (length == 0?false:!context_array[length-1]);
    }
    boolean noContext() {
        return length == 0;
    }
    void resize(int newsize) {
        context_array = Arrays.copyOf(context_array, newsize);
    }
}
