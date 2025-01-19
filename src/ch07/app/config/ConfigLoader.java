package ch07.app.config;

import ch07.annotation.InitializerClass;
import ch07.annotation.InitializerMethod;

@InitializerClass
public class ConfigLoader {

    @InitializerMethod
    public void loadAllConfigs() {
        System.out.println("Loading all configuration files");
    }

}
