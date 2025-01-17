package ch05.testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Exercise: Implement a simple, general-purpose testing framework.
 * <p>
 * The input to our framework is a class containing test cases, where each method is an isolated test.
 * An example of an inputs test class: {@link  PaymentServiceTest}
 * <pre>
 * Input to our framework is a class object
 * We need to:
 *
 *      1. If a method called beforeClass() is present, it needs to be called once,
 *      at the beginning of the test suite.
 *
 *      2. If a method with the name setupTest() is present it needs to be called
 *      before every test.
 *
 *      3. Every method that starts with the name test is considered a test case.
 *      We need to run each of those tests one after another. A separate object of the
 *      test class should be created for each test invocation. The order does not matter.
 *
 *      4. If a method called afterClass() is present, it needs to be run at the end of
 *      the test suite, only once.
 *
 *      5. Any other methods are considered helper methods and should be ignored.
 *
 * Note: A proper beforeClass(), afterClass(), setupTest() and test*() method has to be:
 *      - public
 *      - take zero parameters
 *      - return void
 *
 * If either of those conditions are not met, the method should be ignored.
 * Assumptions: The test class is guaranteed to have a single declared empty constructor.
 * </pre>
 */
public class SimpleTestingFramework {
    public void runTestSuite(Class<?> testClass) throws Throwable {
        Constructor<?> defaultConstructor = testClass.getDeclaredConstructor();
        Object testInstance = defaultConstructor.newInstance();

        Method[] allMethods = testClass.getDeclaredMethods();
        List<Method> allTestMethods = findMethodsByPrefix(allMethods, "test");

        Method beforeClassMethod = findMethodByName(allMethods, "beforeClass");
        if (beforeClassMethod != null) {
            beforeClassMethod.invoke(null);
        }

        Method setupMethod = findMethodByName(allMethods, "setupTest");
        for (Method method : allTestMethods) {
            if (setupMethod != null) {
                setupMethod.invoke(testInstance);
            }
            method.invoke(testInstance);
        }

        Method afterClassMethod = findMethodByName(allMethods, "afterClass");
        if (afterClassMethod != null) {
            afterClassMethod.invoke(null);
        }
    }

    /**
     * Helper method to find a method by name
     * Returns null if a method with the given name does not exist
     */
    private Method findMethodByName(Method[] methods, String name) {
        return Arrays.stream(methods)
                .filter(m -> m.getName().equals(name) && m.getParameterCount() == 0 && m.getReturnType().equals(void.class))
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper method to find all the methods that start with the given prefix
     */
    private List<Method> findMethodsByPrefix(Method[] methods, String prefix) {
        List<Method> methodList = new ArrayList<>();
        for (Method method : methods) {
            String currentMethodName = method.getName();
            if (currentMethodName.startsWith(prefix)) {
                methodList.add(method);
            }
        }
        return methodList;
    }
}
