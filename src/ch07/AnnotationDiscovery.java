package ch07;

import ch07.annotation.InitializerClass;
import ch07.annotation.InitializerMethod;
import ch07.annotation.RetryOperation;
import ch07.annotation.ScanPackages;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ScanPackages({"ch07.app", "ch07.app.config", "ch07.app.database", "ch07.app.http"})
public class AnnotationDiscovery {

    public static void main(String[] args) {
        initialize();
    }

    public static void initialize() {
        if (!AnnotationDiscovery.class.isAnnotationPresent(ScanPackages.class))
            throw new IllegalStateException();

        ScanPackages packages = AnnotationDiscovery.class.getAnnotation(ScanPackages.class);
        String[] packageNames = packages.value();

        try {
            List<Class<?>> allClasses = getAllClasses(packageNames);
            for (Class<?> clazz : allClasses) {
                if (!clazz.isAnnotationPresent(InitializerClass.class)) {
                    continue;
                }

                Object initializerClassInstance = createInstance(clazz);
                for (Method method : getAllInitializerMethods(clazz)) {
                    callInitializerMethod(initializerClassInstance, method);
                }
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void callInitializerMethod(Object instance, Method initializerMethod) throws Throwable {
        RetryOperation retryOperation = initializerMethod.getAnnotation(RetryOperation.class);
        if (retryOperation == null) {
            initializerMethod.invoke(instance);
            return;
        }

        int numberOfRetries = retryOperation.numberOfRetries();
        long retryDelayInMs = retryOperation.durationBetweenRetriesInMs();
        String failureMessage = retryOperation.failureMessage();
        Set<?> retryableExceptions = Set.of(retryOperation.retryExceptions());

        while (true) {
            try {
                initializerMethod.invoke(instance);
                break;
            } catch (InvocationTargetException ex) {
                Throwable targetException = ex.getTargetException();
                if (numberOfRetries <= 0 || !retryableExceptions.contains(targetException.getClass())) {
                    throw new Exception(failureMessage, targetException);
                }

                numberOfRetries--;
                System.out.println("> Retrying..");
                Thread.sleep(retryDelayInMs);
            }
        }
    }

    private static List<Method> getAllInitializerMethods(Class<?> initializerClass) {
        List<Method> initializerMethods = new ArrayList<>();
        for (Method method : initializerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(InitializerMethod.class)) {
                initializerMethods.add(method);
            }
        }
        return initializerMethods;
    }

    private static List<Class<?>> getAllClasses(String... packageNames) throws URISyntaxException, IOException {
        List<Class<?>> allClasses = new ArrayList<>();
        for (String packageName : packageNames) {
            String relativePackagePath = packageName.replace('.', '/');
            URI packageUri = Objects.requireNonNull(AnnotationDiscovery.class.getResource("/" + relativePackagePath)).toURI();
            if (packageUri.getScheme().equals("file")) {
                Path fullPackagePath = Paths.get(packageUri);
                allClasses.addAll(getAllPackageClasses(fullPackagePath, packageName));
            } else if (packageUri.getScheme().equals("jar")) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(packageUri, Collections.emptyMap())) {
                    Path fullPackagePathInJar = fileSystem.getPath(relativePackagePath);
                    allClasses.addAll(getAllPackageClasses(fullPackagePathInJar, packageName));
                }
            }
        }
        return allClasses;
    }

    private static List<Class<?>> getAllPackageClasses(Path packagePath, String packageName) throws IOException {
        if (!Files.exists(packagePath) || !Files.isDirectory(packagePath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> fileStream = Files.list(packagePath)) {
            return fileStream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(filename -> filename.endsWith(".class"))
                    .map(filename -> filename.replaceFirst("\\.class$", ""))
                    .map(className -> packageName.isBlank() ? className : packageName + "." + className)
                    .map(classFullName -> {
                        try {
                            return Class.forName(classFullName);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("Failed to load class: " + classFullName, e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    private static Object createInstance(Class<?> targetClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> defaultConstructor = targetClass.getDeclaredConstructor();
        return defaultConstructor.newInstance();
    }

}
