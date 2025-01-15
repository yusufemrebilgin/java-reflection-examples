package ch03;

import java.lang.reflect.Field;

public class DiscoveringFields {

    public static void main(String[] args) throws IllegalAccessException {
        printDeclaredFieldInfo(Product.class);
        printDeclaredFieldInfo(Movie.class);
        printDeclaredFieldInfo(Movie.MovieStats.class);

        var movie = new Movie("Gladiator", 2000, 11.99, true, Category.ACTION);
        printDeclaredFieldValues(movie.getClass(), movie);
    }

    public static void printDeclaredFieldInfo(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("Field Name: " + field.getName());
            System.out.println("Field Type: " + field.getType().getName());
            System.out.println("Is Synthetic Field: " + field.isSynthetic());
            System.out.println();
        }
    }

    public static <T> void printDeclaredFieldValues(Class<? extends T> clazz, T instance) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            System.out.printf("[name=%s, value=%s]\n", field.getName(), field.get(instance));
        }
    }

    public static class Movie extends Product {

        public static final double MINIMUM_PRICE = 10.99;

        private boolean isReleased;
        private Category category;

        public Movie(String name, int year, double price, boolean isReleased, Category category) {
            super(name, year, Math.max(price, MINIMUM_PRICE));
            this.isReleased = isReleased;
            this.category = category;
        }

        // Nested Class for Synthetic Field Check
        public class MovieStats {
            private double timesWatched;

            public MovieStats(double timesWatched) {
                this.timesWatched = timesWatched;
            }

            public double getRevenue() {
                return timesWatched * actualPrice;
            }
        }
    }

    // Superclass for Movie
    public static class Product {
        protected String name;
        protected int year;
        protected double actualPrice;

        public Product(String name, int year, double actualPrice) {
            this.name = name;
            this.year = year;
            this.actualPrice = actualPrice;
        }
    }

    public enum Category {
        ADVENTURE,
        ACTION,
        COMEDY
    }

}
