package ch04;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Scanner;

public class ConfigLoader {

    private static final String BASE_PATH;

    static {
        String packagePath = ConfigLoader.class.getPackageName().replace(".", File.separator);
        String workingUserDir = System.getProperty("user.dir");
        BASE_PATH = String.join(File.separator, workingUserDir, "src", packagePath);
    }

    public static void main(String[] args) {
        var config = createConfigObject(UserInterfaceConfig.class, "user-interface.cfg");
        System.out.println(config);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createConfigObject(Class<T> targetClass, String configFilename) {
        File configFile = new File(String.join(File.separator, BASE_PATH, configFilename));
        try (Scanner scanner = new Scanner(configFile)) {
            Constructor<?> defaultConstructor = targetClass.getDeclaredConstructor();
            T configInstance = (T) defaultConstructor.newInstance();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] keyValuePairs = line.split("=");
                String propertyKey = keyValuePairs[0];
                String propertyVal = keyValuePairs[1];

                Field field;
                try {
                    field = targetClass.getDeclaredField(propertyKey);
                } catch (NoSuchFieldException ex) {
                    System.err.println("Property key is not supported: " + propertyKey);
                    continue;
                }

                Object parsedFieldValue;
                Class<?> fieldType = field.getType();
                if (fieldType.isArray()) {
                    parsedFieldValue = parseArray(fieldType.getComponentType(), propertyVal);
                } else {
                    parsedFieldValue = parseValue(fieldType, propertyVal);
                }

                field.setAccessible(true);
                field.set(configInstance, parsedFieldValue);
            }

            return configInstance;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object parseArray(Class<?> arrayElementType, String elements) {
        String[] elementValues = elements.split(",");
        Object arrayInstance  = Array.newInstance(arrayElementType, elementValues.length);

        for (int i = 0; i < elementValues.length; i++) {
            Array.set(arrayInstance, i, parseValue(arrayElementType, elementValues[i]));
        }
        return arrayInstance;
    }

    private static Object parseValue(Class<?> fieldType, String fieldValue) {
        if (fieldType.equals(boolean.class)) {
            return Boolean.parseBoolean(fieldValue);
        } else if (fieldType.equals(byte.class)) {
            return Byte.parseByte(fieldValue);
        } else if (fieldType.equals(short.class)) {
            return Short.parseShort(fieldValue);
        } else if (fieldType.equals(int.class)) {
            return Integer.parseInt(fieldValue);
        } else if (fieldType.equals(long.class)) {
            return Long.parseLong(fieldValue);
        } else if (fieldType.equals(float.class)) {
            return Float.parseFloat(fieldValue);
        } else if (fieldType.equals(double.class)) {
            return Double.parseDouble(fieldValue);
        } else if (fieldType.equals(String.class)) {
            return fieldValue;
        }

        throw new RuntimeException("Type is not supported: " + fieldType.getTypeName());
    }

}
