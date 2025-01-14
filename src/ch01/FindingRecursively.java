package ch01;

import java.util.HashSet;
import java.util.Set;

/**
 * Exercise: Finding all implemented interfaces of a class
 */
public class FindingRecursively {

    public static void main(String[] args) {
        Set<Class<?>> allImplementedInterfaces = findAllImplementedInterfaces(A.class);
        allImplementedInterfaces.stream().map(Class::getSimpleName).forEach(c -> System.out.print(c + " "));
    }

    /**
     * Returns all the interfaces that the current input class implements.
     * <p>
     * Note: If the input is an interface, the method returns all the interfaces the
     * input interfaces extends.
     */
    public static Set<Class<?>> findAllImplementedInterfaces(Class<?> inputClass) {
        Set<Class<?>> allImplementedInterfaces = new HashSet<>();

        Class<?>[] interfaces = inputClass.getInterfaces();
        for (Class<?> currentInterface : interfaces) {
            allImplementedInterfaces.add(currentInterface);
            allImplementedInterfaces.addAll(findAllImplementedInterfaces(currentInterface));
        }

        return allImplementedInterfaces;
    }

    private static class A implements B, C, D {}

    private interface B extends E {}
    private interface C extends F {}
    private interface D extends F, G {}
    private interface E {}
    private interface F {}
    private interface G {}

}
