package ch08.caching;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exercise: Build a caching proxy
 * <p>
 * Goal:
 * This kind of proxy would allow us to cache the results of specific methods
 * whose results don't change throughout the lifetime of the process. We want
 * to extend this caching functionality to any object of any interface in our
 * application, so we will use a Dynamic Proxy for the implementation.
 * <pre>
 * Given an interface, a result from any method annotated with the @Cacheable annotation should be:
 * - Cached if it is not in the cache already
 * - Read from the cache instead of invoking the method
 *
 * As a result, every cacheable method should be invoked only once.
 *
 * Example of an interface:
 *
 *      interface DatabaseReader {
 *          &#064;Cacheable
 *          String readCustomerIdByName(String name) throws IOException;
 *          // ...
 *      }
 *
 * Cache details:
 * - Every method has its own cache.
 * - The key to our cache is a list of arguments passed into the method.
 * (since different method arguments may produce a different result)
 * - The value of the cache is the result returned from the method
 * - The arguments and the result may be of any type
 *
 * Note: The dynamic proxy should throw the original target exceptions in case they are thrown.
 * </pre>
 */
public class CachingInvocationHandler implements InvocationHandler {

    /**
     * Map that maps from a method name to a method cache.
     * Each cache is a map from a list of arguments to a method result.
     */
    private final Map<String, Map<List<Object>, Object>> cache = new HashMap<>();

    private final Object realObject;

    public CachingInvocationHandler(Object realObject) {
        this.realObject = realObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (!isMethodCacheable(method)) {
                return method.invoke(realObject, args);
            }

            if (isInCache(method, args)) {
                return getFromCache(method, args);
            }

            Object result = method.invoke(realObject, args);
            putInCache(method, args, result);
            return result;
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }

    }

    boolean isMethodCacheable(Method method) {
        return method.isAnnotationPresent(Cacheable.class);
    }

    /******************************* Helper Methods **************************/

    private boolean isInCache(Method method, Object[] args) {
        if (!cache.containsKey(method.getName())) {
            return false;
        }

        List<Object> argumentsList = Arrays.asList(args);
        Map<List<Object>, Object> argumentsToReturnValue = cache.get(method.getName());

        return argumentsToReturnValue.containsKey(argumentsList);
    }

    private void putInCache(Method method, Object[] args, Object result) {
        if (!cache.containsKey(method.getName())) {
            cache.put(method.getName(), new HashMap<>());
        }
        List<Object> argumentsList = Arrays.asList(args);

        Map<List<Object>, Object> argumentsToReturnValue = cache.get(method.getName());

        argumentsToReturnValue.put(argumentsList, result);
    }

    private Object getFromCache(Method method, Object[] args) {
        if (!cache.containsKey(method.getName())) {
            throw new IllegalStateException(String.format("Result of method: %s is not in the cache", method.getName()));
        }

        List<Object> argumentsList = Arrays.asList(args);

        Map<List<Object>, Object> argumentsToReturnValue = cache.get(method.getName());

        if (!argumentsToReturnValue.containsKey(argumentsList)) {
            throw new IllegalStateException(String.format("Result of method: %s and arguments: %s is not in the cache",
                    method.getName(),
                    argumentsList));
        }

        return argumentsToReturnValue.get(argumentsList);
    }
}