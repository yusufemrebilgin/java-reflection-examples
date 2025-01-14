package ch02;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class DiscoveringConstructors {

    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        printConstructorInfo(Person.class);
        System.out.println();

        Address address = createInstanceWithArguments(Address.class, "Street 123", "Neighbourhood 123");
        Person person = createInstanceWithArguments(Person.class, "Mike", 44, address);
        System.out.println(person);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstanceWithArguments(Class<T> clazz, Object... args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == args.length) {
                return (T) constructor.newInstance(args);
            }
        }

        System.out.println("An appropriate constructor was not found");
        return null;
    }

    public static void printConstructorInfo(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        System.out.printf("Class \"%s\" has %d declared constructors\n", clazz.getSimpleName(), constructors.length);

        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            List<String> parameterTypeNames = Arrays.stream(parameterTypes)
                    .map(Class::getSimpleName)
                    .toList();
            System.out.println(parameterTypeNames);
        }
    }

    static class Person {
        private final String name;
        private final int age;
        private final Address address;

        public Person() {
            this("anonymous", 0, null);
        }

        public Person(String name) {
            this(name, 0, null);
        }

        public Person(String name, int age) {
            this(name, age, null);
        }

        public Person(String name, int age, Address address) {
            this.name = name;
            this.age = age;
            this.address = address;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", address=" + address +
                    '}';
        }
    }

    static class Address {
        private String street;
        private String neighbourhood;

        public Address(String street, String neighbourhood) {
            this.street = street;
            this.neighbourhood = neighbourhood;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "street='" + street + '\'' +
                    ", neighbourhood='" + neighbourhood + '\'' +
                    '}';
        }
    }

}
