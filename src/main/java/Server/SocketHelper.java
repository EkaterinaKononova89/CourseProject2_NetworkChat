package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketHelper {
    protected Socket socket;
    protected PrintWriter out;
    protected BufferedReader in;
    protected String userName;

    // вспомогательный класс клиентского сокета, который помнит свои входящие и исходящие потоки
    public SocketHelper(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getUserName() {
        return this.userName;
    }

    // методы, необходимые только для тестов
    public BufferedReader getIn() {
        return this.in;
    }

    public PrintWriter getOut() {
        return this.out;
    }
}
