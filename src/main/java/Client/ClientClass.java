package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientClass {

    protected final String host;
    protected final int port;
    static Scanner scanner = new Scanner(System.in);

    public ClientClass(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void startClient() throws IOException {

        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

// выбор имени пользователя
            String introduce = in.readLine();
            System.out.println(introduce);
            while (true) {
                System.out.println("введите имя");
                String userName = scanner.nextLine();
                out.println(userName);
                String simpleAnswer = in.readLine();
                if (simpleAnswer.equals("true")) {
                    System.out.println("Добро пожаловать на сервер, " + userName + "!");
                    break;
                } else {
                    System.out.println("Данное имя уже есть на сервере, выберите другое");
                }
            }

// история последних сообщений
            System.out.println("ПОСЛЕДНИЕ СООБЩЕНИЯ НА СЕРВЕРЕ: ");
            String history = in.readLine();
            if (history.equals("false")) {
                System.out.println("Нет истории переписки");
            } else {
                int h = Integer.parseInt(history);
                int j = 0;
                while (j < h) {
                    String m = in.readLine();
                    System.out.println(m);
                    j++;
                }
            }
            System.out.println();
            System.out.println("Теперь можете отправлять сообщения!");

// ждем новых сообщений
            new Thread(() -> {
                try {
                    while (true) {
                        String messageIn = in.readLine();
                        if (messageIn != null) {
                            System.out.println(messageIn);
                        }
                    }
                } catch (IOException ignored) {
                }
            }).start();

            while (true) {
                String message = scanner.nextLine();
                out.println(message);
                if ("/exit".equals(message)) {
                    System.out.println("Вы покинули чат (клиент)"); // контрольное сообщение
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
