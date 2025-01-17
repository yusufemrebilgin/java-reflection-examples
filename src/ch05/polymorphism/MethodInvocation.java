package ch05.polymorphism;

import ch05.polymorphism.database.DatabaseClient;
import ch05.polymorphism.http.HttpClient;
import ch05.polymorphism.logging.FileLogger;
import ch05.polymorphism.udp.UdpClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodInvocation {
    /*
     * Polymorphism with Reflection
     * - Imagine two completely unrelated classes that do not share a common
     *   interface or parent class, and their method names are also different.
     * - In such a scenario, achieving polymorphism in the traditional sense
     *   (via interfaces or inheritance) is not possible.
     * - However, with reflection, we can mimic polymorphism by dynamically
     *   invoking methods at runtime, even if the classes are not directly
     *   connected or the method names differ.
     * - This approach provides flexibility without requiring structural changes
     *   like implementing new interfaces or extending base classes.
     */
    public static void main(String[] args) throws Throwable {
        DatabaseClient databaseClient = new DatabaseClient();
        HttpClient httpClientA = new HttpClient("123.45.67.0");
        HttpClient httpClientB = new HttpClient("111.22.33.44");
        FileLogger fileLogger = new FileLogger();
        UdpClient udpClient = new UdpClient();

        List<Object> executors = Arrays.asList(databaseClient, httpClientA, httpClientB, fileLogger, udpClient);
        Map<Object, Method> requestExecutors = groupExecutors(executors, String.class);
        executeAll(requestExecutors, "sample-request-data");
    }

    public static void executeAll(Map<Object, Method> requestExecutors, String requestBody) throws Throwable {
        for (Map.Entry<Object, Method> requestExecutorEntry : requestExecutors.entrySet()) {
            Object requestExecutor = requestExecutorEntry.getKey();
            Method methodToBeExecuted = requestExecutorEntry.getValue();

            Boolean result;
            try {
                result = (Boolean) methodToBeExecuted.invoke(requestExecutor, requestBody);
            } catch (InvocationTargetException ex) {
                // InvocationTargetException is a checked exception that wraps
                // an exception thrown by an invoked method or constructor.
                throw ex.getTargetException();
            }

            if (result != null && result.equals(Boolean.FALSE)) {
                System.out.println("Received negative result. Aborting...");
                return;
            }
        }
    }

    public static Map<Object, Method> groupExecutors(List<Object> requestExecutors, Class<?>... methodParameterTypes) {
        Map<Object, Method> instanceToMethod = new HashMap<>();
        for (Object requestExecutor : requestExecutors) {
            Method[] allMethods = requestExecutor.getClass().getDeclaredMethods();
            for (Method method : allMethods) {
                if (Arrays.equals(methodParameterTypes, method.getParameterTypes())) {
                    instanceToMethod.put(requestExecutor, method);
                }
            }
        }
        return instanceToMethod;
    }

}
