package ch07.app;

import ch07.annotation.InitializerClass;
import ch07.annotation.InitializerMethod;

@InitializerClass
public class AutoSaver {

    @InitializerMethod
    public void startAutoSavingThreads() {
        System.out.println("Start automatic data saving to disk");
    }

}
