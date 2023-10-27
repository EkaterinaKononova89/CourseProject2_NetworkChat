package Client;

import java.io.IOException;

public class MainClient {
    public static void main(String[] args) throws IOException {
        String host = "netology.homework";//"127.0.0.1";
        int port = 8091;

        ClientClass client = new ClientClass(host, port);
        client.startClient();
    }
}

