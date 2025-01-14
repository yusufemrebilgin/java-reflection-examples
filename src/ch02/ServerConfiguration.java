package ch02;

import java.net.InetSocketAddress;

public class ServerConfiguration {

    private static ServerConfiguration serverConfigurationInstance;

    private final String greetingMessage;
    private final InetSocketAddress serverAddress;

    private ServerConfiguration(int port, String greetingMessage) {
        this.greetingMessage = greetingMessage;
        this.serverAddress = new InetSocketAddress("localhost", port);
        if (serverConfigurationInstance == null) {
            serverConfigurationInstance = this;
        }
    }

    public static ServerConfiguration getInstance() {
        return serverConfigurationInstance;
    }

    public String getGreetingMessage() {
        return greetingMessage;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

}
