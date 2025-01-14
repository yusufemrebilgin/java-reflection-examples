package ch02.web;

import ch02.ServerConfiguration;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebServer {

    public void start() throws IOException {
        HttpServer httpServer = HttpServer.create(ServerConfiguration.getInstance().getServerAddress(), 0);
        httpServer.createContext("/greeting").setHandler(exchange -> {
            String responseMessage = ServerConfiguration.getInstance().getGreetingMessage();
            exchange.sendResponseHeaders(200, responseMessage.length());

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(responseMessage.getBytes());
            responseBody.flush();
            responseBody.close();
        });

        InetSocketAddress serverAddress = ServerConfiguration.getInstance().getServerAddress();
        System.out.printf("> Starting server ond address %s:%d\n", serverAddress.getHostName(), serverAddress.getPort());
        httpServer.start();
    }

}
