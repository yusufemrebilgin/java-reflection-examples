package ch03;

import java.lang.reflect.Array;

/**
 * Exercise: Implement a method to read an array from both the beginning
 * and the end. Note: Index can be both positive, negative, and zero.
 * <p>
 * If the index is non-negative, the method returns the element at the given index,
 * counting from the beginning of the array.
 * <p>
 * If the index is negative, the method will return the element at the given index
 * from the end of the array.
 */
public class ReadingArrays {

    public Object getArrayElement(Object array, int index) {
        if (array == null || !array.getClass().isArray()) {
            throw new IllegalArgumentException();
        }

        if (index >= 0) {
            return Array.get(array, index);
        }

        int length = Array.getLength(array);
        return Array.get(array, index + length);
    }

}
