package ch06;

import ch06.auction.Auction;
import ch06.auction.Bid;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ModifiersExample {
    /*
     * Constructor Modifiers:
     *      - public, protected, private and default (package-private)
     * Class Level Non-Access Modifiers:
     *      - abstract:         Makes the class abstract (non instantiable)
     *      - final:            Finalizes the implementation of the class
     *      - static:           Makes inner classes instantiable without outer class
     *      - interface:        Marks the class as an interface
     * Method Level Non-Access Modifiers:
     *      - abstract:         Makes the method abstract
     *      - final:            Finalizes the implementation of the method
     *      - static:           Makes the method a class level method
     *      - synchronized:     Method can be accessed by one thread at a time
     *      - native:           Implemented in another programming language (typically C)
     * Field Level Non-Access Modifiers:
     *      - final:            Finalizes the field
     *      - static:           Makes the field a class level field
     *      - transient:        Marks field not be serialized
     *      - volatile:         Makes reads/writes to long/double atomic, and prevent data races
     *
     * Bitmap Encoding of Modifiers
     *      The modifiers are encoded as a bitmap.
     *      Each modifier represents a single bit.
     *      Example:
     *          public = 1              binary representation: .... 0000 0001
     *          static = 8              binary representation: .... 0001 0000
     *          public | static = 9     binary representation: .... 0001 0001
     *
     *      To help us work with those bitmaps, Reflection comes with the Modifier class that contains
     *      bit masks for all modifiers.
     *      Example:
     *          int modifiers = Product.class.getModifiers();
     *          if ((modifiers & Modifier.ABSTRACT) != 0)
     *              System.out.println("Product is an abstract class");
     */

    public static void main(String[] args) throws ClassNotFoundException {
        printClassModifiers(Auction.class);
        printClassModifiers(Bid.class);
        printClassModifiers(Bid.Builder.class);
        printClassModifiers(Class.forName("ch06.auction.Bid$Builder$BidImpl"));
        printClassModifiers(Serializable.class);
        System.out.println("*".repeat(50) + "\n");

        printMethodModifiers(Auction.class.getDeclaredMethods());
        System.out.println("*".repeat(50) + "\n");

        printFieldModifiers(Auction.class.getDeclaredFields());
    }

    public static void printClassModifiers(Class<?> clazz) {
        int modifier = clazz.getModifiers();
        System.out.printf("Class %s's access modifier is: %s\n", clazz.getSimpleName(), getAccessModifierName(modifier));

        if (Modifier.isAbstract(modifier))
            System.out.println("- The class is an abstract class");
        if (Modifier.isInterface(modifier))
            System.out.println("- The class is an interface");
        if (Modifier.isStatic(modifier))
            System.out.println("- The clas is static");
        System.out.println();
    }

    public static void printMethodModifiers(Method[] methods) {
        for (Method method : methods) {
            int modifier = method.getModifiers();
            System.out.printf("%s() access modifier is: %s\n", method.getName(), getAccessModifierName(modifier));

            if (Modifier.isSynchronized(modifier))
                System.out.println("- The method is synchronized");
            else
                System.out.println("- The method is not synchronized");
            System.out.println();
        }
    }

    public static void printFieldModifiers(Field[] fields) {
        for (Field field : fields) {
            int modifier = field.getModifiers();
            System.out.printf("Field %s's access modifier is: %s\n", field.getName(), getAccessModifierName(modifier));

            if (Modifier.isVolatile(modifier))
                System.out.println("- The field is volatile");
            if (Modifier.isTransient(modifier))
                System.out.println("- The field is transient");
            if (Modifier.isFinal(modifier))
                System.out.println("- The field is final");
            System.out.println();
        }
    }

    private static String getAccessModifierName(int modifierBits) {
        if (Modifier.isPublic(modifierBits)) {
            return "public";
        } else if (Modifier.isProtected(modifierBits)) {
            return "protected";
        } else if (Modifier.isPrivate(modifierBits)) {
            return "private";
        } else {
            return "package-private";
        }
    }

    public static void runAuction() {
        Auction auction = new Auction();
        auction.startAuction();

        Bid b1 = Bid.builder().bidderName("company-1").price(10).build();
        auction.addBid(b1);
        Bid b2 = Bid.builder().bidderName("company-2").price(12).build();
        auction.addBid(b2);

        auction.stopAuction();

        System.out.println(auction.getAllBids());
        System.out.println("Highest bid: " + auction.getHighestBid().get());
    }

}
