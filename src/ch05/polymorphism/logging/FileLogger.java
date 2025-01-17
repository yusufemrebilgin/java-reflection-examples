package ch05.polymorphism.logging;

public class FileLogger {
    public void sendRequest(String data) {
        System.out.printf("Data '%s' was logged to the file system\n", data);
    }
}
