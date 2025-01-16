package ch04;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercise: Implement a method that performs "smart concatenation" of elements.
 * <p>
 * Input:
 *   1. The type T represents the type of elements the method should return.
 *   2. A variable number of arguments.
 * The arguments can be of:
 *   - Some type T
 *   - An array of type T
 *   - A combination of arrays of type T and elements of type T.
 * <pre>
 * Example 1:
 *   - Integer[] result = concat(Integer.class, 1, 2, 3, 4, 5);
 *   - The result will be an array of 5 integers containing the following elements: [1, 2, 3, 4, 5]
 * Example 2:
 *   - int [] result = contact(int.class, 1, 2, 3, new int[] {4, 5, 6}, 7);
 *   - The result will be an array of 7 integers containing the elements: [1, 2, 3, 4, 5, 6, 7]
 * Example 3:
 *   - String [] result = contact(String.class, new String[] {"a", "b"}, "c", new String[] {"d", "e"});
 *   - The result will be an array of 5 Strings containing the elements : ["a", "b", "c", "d", "e"]
 * </pre>
 */
public class ArrayFlattening {

    @SuppressWarnings("unchecked")
    public <T> T concat(Class<?> type, Object... arguments) {

        if (arguments.length == 0) {
            return null;
        }

        List<Object> list = new ArrayList<>();
        for (Object arg : arguments) {
            if (!arg.getClass().isArray()) {
                list.add(arg);
                continue;
            }
            // If argument is an array
            for (int j = 0, len = Array.getLength(arg); j < len; j++) {
                Object arrayElement = Array.get(arg, j);
                list.add(arrayElement);
            }
        }

        Object flattenedArray = Array.newInstance(type, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(flattenedArray, i, list.get(i));
        }

        return (T) flattenedArray;
    }

}
