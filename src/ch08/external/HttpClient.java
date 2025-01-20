package ch08.external;

public interface HttpClient {

    void initialize();

    String sendRequest(String request);

}
