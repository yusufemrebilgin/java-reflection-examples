package ch05.polymorphism.udp;

public class UdpClient {
    public void sendAndForget(String data) {
        System.out.printf("Request '%s' was sent through UDP\n", data);
    }
}
