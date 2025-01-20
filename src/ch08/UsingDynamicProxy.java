package ch08;

import ch08.external.DatabaseReader;
import ch08.external.HttpClient;
import ch08.external.impl.DatabaseReaderImpl;
import ch08.external.impl.HttpClientImpl;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsingDynamicProxy {

    public static void main(String[] args) throws InterruptedException {
        DatabaseReader databaseReader = createProxy(new DatabaseReaderImpl());
        useDatabaseReader(databaseReader);

        HttpClient httpClient = createProxy(new HttpClientImpl());
        useHttpClient(httpClient);

        List<String> listOfGreetings = createProxy(new ArrayList<>());
        listOfGreetings.add("hello");
        listOfGreetings.add("hello-world");
        listOfGreetings.remove("hello");
    }

    public static void useHttpClient(HttpClient httpClient) {
        httpClient.initialize();
        String response = httpClient.sendRequest("some-request-data");
        System.out.println("HTTP Response is: " + response);
    }

    public static void useDatabaseReader(DatabaseReader databaseReader) throws InterruptedException {
        int rowsInTable = 0;
        try {
            rowsInTable = databaseReader.countRowsInTable("products");
        } catch (IOException ex) {
            System.err.println("An error occurred: " + ex.getMessage());
        }

        System.out.println("There are " + rowsInTable + " rows in table products");
        String[] data = databaseReader.readRow("SELECT * FROM products");
        System.out.println("Received: " + Arrays.toString(data));
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Object originalObject) {
        Class<?>[] interfaces = originalObject.getClass().getInterfaces();
        TimeMeasuringProxyHandler timeMeasuringProxyHandler = new TimeMeasuringProxyHandler(originalObject);

        return (T) Proxy.newProxyInstance(
                originalObject.getClass().getClassLoader(),
                interfaces, timeMeasuringProxyHandler);
    }

    static class TimeMeasuringProxyHandler implements InvocationHandler {

        private final Object target;

        public TimeMeasuringProxyHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long start = System.nanoTime();
            System.out.println("> Measuring Proxy - Before executing method '" + method.getName() + "()'");

            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            } finally {
                long end = System.nanoTime() - start;
                System.out.printf("> Measuring Proxy - Execution of '%s()' took %d ns\n", method.getName(), end);
            }
        }
    }

}
