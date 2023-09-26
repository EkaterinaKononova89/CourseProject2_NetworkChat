package Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8091;

        ServerClass server = new ServerClass(port);
        server.start();
    }
}
