package ch04;

import java.lang.reflect.Field;
import java.util.Random;

class Book {
    // Results can be unexpected due to Java optimization.
    // Because when compiler sees a final field with a constant assigned to it,
    // it tries to optimize to code by replacing the variable with that constant
    private final String name = "The Lord of The Rings";
    private final int pages;

    public Book() {
        // The value of this field is determined at runtime.
        // Since the compiler cannot predict its value, no such optimization is applied.
        pages = new Random().nextInt();
    }

    @Override
    public String toString() {
        return "Book{" + "name='" + name + '\'' + ", pages=" + pages + '}';
    }
}

class SettingFinalField {

    public static void main(String[] args) throws Exception {
        // Book initialized -> Book{name='The Lord of The Rings', pages=<random-value>}
        Book book = new Book();

        setField(book, "name", "");     // Has no effect
        setField(book, "pages", 1216);  // Updates the value
        System.out.println(book);
    }

    private static void setField(Object instance, String fieldName, Object newFieldVal) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = instance.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, newFieldVal);
    }

}
