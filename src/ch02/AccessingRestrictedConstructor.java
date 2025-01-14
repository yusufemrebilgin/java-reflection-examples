package ch02;

import ch02.web.WebServer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AccessingRestrictedConstructor {

    public static void main(String[] args) throws IOException {
        initConfiguration();
        WebServer server = new WebServer();
        server.start();
    }

    public static void initConfiguration() {
        try {
            Constructor<ServerConfiguration> constructor = ServerConfiguration.class.getDeclaredConstructor(int.class, String.class);
            constructor.setAccessible(true);
            constructor.newInstance(8080, "Welcome!");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
