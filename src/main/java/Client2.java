import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String host = "netology.homework";//"127.0.0.1";
        int port = 8091;

        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

// выбор имени пользователя
            String introduce = in.readLine();
            System.out.println(introduce);

            chooseUserName(out, in);

// история последних сообщений
            System.out.println("ПОСЛЕДНИЕ СООБЩЕНИЯ НА СЕРВЕРЕ: ");

            showLastMessages(in);

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

    // выбор имени пользователя
    public static void chooseUserName(PrintWriter out, BufferedReader in) throws IOException {
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
    }

    // история последних сообщений
    public static void showLastMessages(BufferedReader in) throws IOException {
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
    }
}
