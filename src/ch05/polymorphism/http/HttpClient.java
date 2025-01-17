package ch05.polymorphism.http;

public class HttpClient {

    private final String serverAddress;

    public HttpClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public boolean sendRequest(String data) {
        System.out.printf("Request with body '%s' was successfully sent to server with addr %s\n", data, serverAddress);
        return true;
    }

}
