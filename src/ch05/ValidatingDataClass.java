package ch05;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClothingProduct extends Product {

    private String color;

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}

class Product {

    private Long id;
    private String name;
    private String description;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}

class ValidatingDataClass {

    public static void main(String[] args) {
        validateGetters(ClothingProduct.class);
        validateSetters(ClothingProduct.class);
    }

    public static void validateGetters(Class<?> clazz) {
        Map<String, Method> methodMap = getMethodMap(clazz);
        for (Field field : getAllFields(clazz)) {
            String getterName = "get" + capitalizeFirstLetter(field.getName());
            if (!methodMap.containsKey(getterName)) {
                throw new IllegalStateException(String.format("Field <%s> doesn't have a getter method", field.getName()));
            }

            Method getterMethod = methodMap.get(getterName);
            if (!getterMethod.getReturnType().equals(field.getType())) {
                throw new IllegalStateException(String.format(
                        "Invalid return type for method %s() found: <%s>, expected: <%s>",
                        getterMethod.getName(),
                        getterMethod.getReturnType().getTypeName(),
                        field.getType().getTypeName()
                ));
            }

            if (getterMethod.getParameterCount() != 0) {
                throw new IllegalStateException("Getter method cannot take any parameter(s)");
            }
        }
    }

    public static void validateSetters(Class<?> clazz) {
        for (Field field : getAllFields(clazz)) {
            String setterName = "set" + capitalizeFirstLetter(field.getName());
            Method setterMethod;
            try {
                setterMethod = clazz.getDeclaredMethod(setterName, field.getType());
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(String.format(
                        "Setter %s(..) not found for the class %s",
                        setterName,
                        clazz.getSimpleName()
                ));
            }

            if (!setterMethod.getReturnType().equals(void.class)) {
                throw new IllegalStateException(String.format("Setter method %s(..) has to return void", setterName));
            }
        }
    }

    private static Map<String, Method> getMethodMap(Class<?> targetClass) {
        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : targetClass.getDeclaredMethods()) {
            methodMap.put(method.getName(), method);
        }
        return methodMap;
    }

    private static List<Field> getAllFields(Class<?> targetClass) {
        if (targetClass == null || targetClass.equals(Object.class)) {
            return Collections.emptyList();
        }

        List<Field> currentClassFields = Arrays.asList(targetClass.getDeclaredFields());
        List<Field> inheritedFields = getAllFields(targetClass.getSuperclass());
        List<Field> allFields = new ArrayList<>(currentClassFields);
        allFields.addAll(inheritedFields);

        return allFields;
    }

    private static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase().concat(str.substring(1));
    }

}
