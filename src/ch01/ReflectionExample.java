package ch01;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReflectionExample {

    public static void main(String[] args) throws ClassNotFoundException {

        // with .class suffix
        Class<String> stringClass = String.class;

        // with instance method
        Map<String, String> map = new HashMap<>();
        Class<?> mapClass = map.getClass();

        // with static method
        Class<?> squareClass = Class.forName("ch01.ReflectionExample$Square");
        printClassInfo(stringClass, mapClass, squareClass);

        var circle = new Drawable() {
            @Override
            public int getNumberOfCorners() {
                return 0;
            }
        };

        printClassInfo(Collection.class, Color.class, int.class, int[][].class, circle.getClass());
    }

    private static void printClassInfo(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            System.out.printf("Class info details for class \"%s\"\n", clazz.getSimpleName());
            System.out.println("- Package name: " + clazz.getPackageName());

            Class<?>[] implementedInterfaces = clazz.getInterfaces();
            System.out.println("- Implemented Interfaces:");

            if (implementedInterfaces.length == 0)
                System.out.println("\tNo interfaces implemented");

            for (Class<?> implementedInterface : implementedInterfaces) {
                System.out.println("\t" + implementedInterface.getSimpleName());
            }

            System.out.println("- Is array: " + clazz.isArray());
            System.out.println("- Is primitive: " + clazz.isPrimitive());
            System.out.println("- Is enum: " + clazz.isEnum());
            System.out.println("- Is interface: " + clazz.isInterface());
            System.out.println("- Is anonymous: " + clazz.isAnonymousClass());
            System.out.println();
        }
    }

    private static class Square implements Drawable {
        @Override
        public int getNumberOfCorners() {
            return 4;
        }
    }

    private interface Drawable {
        int getNumberOfCorners();
    }

    private enum Color {
        RED,
        GREEN,
        BLUE
    }

}
