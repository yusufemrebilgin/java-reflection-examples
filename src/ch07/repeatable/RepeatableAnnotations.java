package ch07.repeatable;

import ch07.AnnotationDiscovery;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch07.repeatable.Annotations.*;

@ScanPackages("ch07.repeatable")
public class RepeatableAnnotations {

    public static void main(String[] args) throws URISyntaxException, IOException {
        schedule();
    }

    public static void schedule() throws URISyntaxException, IOException {
        ScanPackages scanPackages = RepeatableAnnotations.class.getAnnotation(ScanPackages.class);
        if (scanPackages == null || scanPackages.value().length == 0) {
            return;
        }

        List<Class<?>> allClasses = getAllClasses(scanPackages.value());
        List<Method> scheduledExecutorMethods = getScheduledExecutorMethods(allClasses);

        for (Method method : scheduledExecutorMethods) {
            scheduleMethodExecution(method);
        }
    }

    private static void scheduleMethodExecution(Method method) {
        ExecuteOnSchedule[] schedules = method.getAnnotationsByType(ExecuteOnSchedule.class);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        for (ExecuteOnSchedule schedule : schedules) {
            executorService.scheduleAtFixedRate(
                    () -> runWhenScheduled(method),
                    schedule.delaySeconds(),
                    schedule.periodSeconds(),
                    TimeUnit.SECONDS
            );
        }
    }

    private static void runWhenScheduled(Method method) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("Executing at " + dateFormat.format(currentDate));
        try {
            method.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    private static List<Method> getScheduledExecutorMethods(List<Class<?>> allClasses) {
        List<Method> scheduledMethods = new ArrayList<>();
        for (Class<?> currentClass : allClasses) {
            if (!currentClass.isAnnotationPresent(ScheduledExecutorClass.class)) {
                continue;
            }

            for (Method method : currentClass.getDeclaredMethods()) {
                if (method.getAnnotationsByType(ExecuteOnSchedule.class).length != 0) {
                    scheduledMethods.add(method);
                }
            }
        }

        return scheduledMethods;
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

}
