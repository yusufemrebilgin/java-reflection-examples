package ch08.external.impl;

import ch08.external.HttpClient;

public class HttpClientImpl implements HttpClient {

    @Override
    public void initialize() {
        System.out.println("Initializing HTTP client");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public String sendRequest(String request) {
        System.out.println("Sending request: " + request);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println("Received response");

        return "some-response-data";
    }

}
