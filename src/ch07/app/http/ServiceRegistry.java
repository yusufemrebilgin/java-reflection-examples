package ch07.app.http;

import ch07.annotation.InitializerClass;
import ch07.annotation.InitializerMethod;

@InitializerClass
public class ServiceRegistry {

    @InitializerMethod
    public void registerService() {
        System.out.println("Service successfully registered");
    }

}
