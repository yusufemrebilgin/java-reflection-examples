package ch03;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Basic JSON Serializer Implementation
 */
public class JsonSerializer {

    private static final String START_OBJECT = "{";
    private static final String END_OBJECT = "}";
    private static final String START_ARRAY = "[";
    private static final String END_ARRAY = "]";

    public static void main(String[] args) throws IllegalAccessException {
        // Book model for testing serializer
        record Book(String name, int pages, String[] genres) {}
        Book book = new Book("The Lord of The Rings", 1216, new String[] {"Fantasy", "Classic", "Adventure"});

        JsonSerializer jsonSerializer = new JsonSerializer();
        String json = jsonSerializer.objectToJson(book, 0);
        System.out.println(json);
    }

    public String objectToJson(Object object, int indentSize) throws IllegalAccessException {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        StringBuilder json = new StringBuilder();
        json.append(START_OBJECT).append("\n");

        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            if (field.isSynthetic()) {
                continue;
            }

            json.append(indent(indentSize + 1));
            json.append(formatStringValue(field.getName())).append(":");

            Class<?> fieldType = field.getType();
            Object fieldInstance = field.get(object);

            if (fieldType == String.class) {
                json.append(formatStringValue(fieldInstance.toString()));
            } else if (fieldType.isPrimitive()) {
                json.append(formatPrimitiveValue(fieldInstance, fieldType));
            } else if (fieldType.isArray()) {
                json.append(formatArrayValue(fieldInstance, indentSize + 1));
            } else {
                json.append(objectToJson(fieldInstance, indentSize + 1));
            }

            if (i != fields.length - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        return json.append(indent(indentSize)).append(END_OBJECT).toString();
    }

    /***** Helper Methods *****/

    private String indent(int indentSize) {
        return "\t".repeat(Math.max(0, indentSize));
    }

    private String formatStringValue(String fieldName) {
        return String.format("\"%s\"", fieldName);
    }

    private String formatPrimitiveValue(Object fieldInstance, Class<?> fieldType) {
        if (fieldType == float.class || fieldType == double.class) {
            return String.format("%.02f", (double) fieldInstance);
        } else {
            return fieldInstance.toString();
        }
    }

    private String formatArrayValue(Object arrayInstance, int indentSize) throws IllegalAccessException {
        int lengthOfArray = Array.getLength(arrayInstance);
        Class<?> arrayClass = arrayInstance.getClass();
        Class<?> componentTypeOfArray = arrayClass.getComponentType();

        StringBuilder arrayJson = new StringBuilder().append(START_ARRAY).append("\n");
        for (int i = 0; i < lengthOfArray; i++) {
            Object arrayElement = Array.get(arrayInstance, i);
            arrayJson.append(indent(indentSize + 2));
            if (componentTypeOfArray == String.class) {
                arrayJson.append(formatStringValue(arrayElement.toString()));
            } else if (componentTypeOfArray.isPrimitive()) {
                arrayJson.append(formatPrimitiveValue(arrayElement, componentTypeOfArray));
            } else {
                arrayJson.append(objectToJson(arrayElement, indentSize));
            }

            if (i != lengthOfArray - 1) {
                arrayJson.append(",");
            }
            arrayJson.append("\n");
        }

        return arrayJson.append(indent(indentSize)).append(END_ARRAY).toString();
    }

}
