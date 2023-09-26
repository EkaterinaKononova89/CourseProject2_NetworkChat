package Server;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerClass {
    protected int i = 0;
    protected List<SocketHelper> socketList = new CopyOnWriteArrayList<>(); // использую CopyOnWriteArrayList<> с учетом,
    // что чтения из него будет больше (необходим при каждом новом сообщении в чате), а внесения изменеий будет немного,
    // т.к. они будут возникать только при новом подключении
    protected MessageHistory messageHistory = new MessageHistory(); // хранит последние несколько сообщений для вновь подключившихся
    protected int port;

    public ServerClass() {
    } // конструктор для тестов с мокито спай

    public ServerClass(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port); // в блоке трай с ресурсами, т.к. порт явл-ся рес-м и м.б. занят др. программой; автоклозбл
             FileWriter log = new FileWriter("File.log", true)) {

            log.write("server started " + dateTime() + "\n");
            log.flush();
            System.out.println("server started");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // ждем подключения;
                i++;
                synchronized ("File.log") { // лог, который просто фиксирует новое подключение
                    log.write("INFO: НОВОЕ ПОДКЛЮЧЕНИЕ №" + i + ", " + dateTime() + "\n");
                    log.flush();
                }

                new Thread(() -> { // каждое новое подключение уходит в отдельный поток, в основном потоке метод старт() практически сразу
                    // завершается и в цикле вайл(тру) продолжается ожидание подключения. Состоявшееся подключение существует в новом потоке
                    SocketHelper instance = null;
                    try {
                        instance = new SocketHelper(clientSocket); // создала объект Клиентского сокета, который помнит свои входящие и исходящие потоки;

                        // проверка уникальности имени на сервере
                        instance.getOut().println("Представьтесь!");
                        String userName = instance.getIn().readLine();

                        chooseUniqueName(instance, userName);

                        socketList.add(instance); // добавила его в список сокетов (подключений) - получение сообщений возможно только после выбора имени
                        synchronized ("File.log") { // еще один лог для нового участника
                            log.write("INFO: УЧАСТНИК №" + i + " выбрал имя " + nameDateTime(instance.getUserName()) + "\n");
                            log.flush();
                        }

                        showChatHistory(instance);

                        chatting(instance, log);

                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            if (instance.in != null) {
                                instance.in.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        instance.out.close();
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String dateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalDate.now() + ", " + LocalTime.now().format(dtf);
    }

    public String nameDateTime(String userName) {
        return userName + ", " + dateTime();
    }

    public void chooseUniqueName(SocketHelper instance, String userName) throws IOException { // проверка уникальности имени на сервере
        while (true) {
            if (!invalidNameChecking(userName)) {
                instance.getOut().println("false");
                userName = instance.getIn().readLine();
            } else {
                instance.setUserName(userName);
                instance.getOut().println("true");
                break;
            }
        }
    }

    public boolean invalidNameChecking(String name) {
        if (!socketList.isEmpty()) {
            for (SocketHelper someSocket : getSocketList()) {
                if (name.equals(someSocket.getUserName())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void chatting(SocketHelper instance, FileWriter log) throws IOException, InterruptedException {
        while (true) {
            String message = instance.getIn().readLine();
            if (message.equals("/exit")) {  // разрываем подключение, если "/exit"
                instance.getOut().println("СООБЩЕНИЕ ОТ СЕРВЕРА: Вы вышли из чата"); // out
                log.write("INFO: Участник под именем " + nameDateTime(instance.getUserName()) + " покинул сервер" + "\n");
                log.flush();
                socketList.remove(instance);
                break;
            }
            if (message.equals("/online")) {  // смотрим, кто онлайн
                onLine(instance);
            } else {
                synchronized ("File.log") {
                    log.write("MSG == " + nameDateTime(instance.getUserName()) + ":\n   " + message + "\n");
                    log.flush();
                }
                for (SocketHelper someSocket : socketList) { // проходимся по списку подключений и отправляем сообщение остальным участникам чата
                    if (!someSocket.equals(instance)) {
                        synchronized (someSocket) { // есть вероятность одномоментной отправки сообщений разными клиентами
                            someSocket.getOut().println(TextColor.BLUE + nameDateTime(instance.getUserName()) + ":\n " +
                                    TextColor.RESET + message);
                        }
                    }
                }
                getMessageHistory().createChatHistory(nameDateTime(instance.getUserName()) + ": " + message);// добавить сообщение в историю
            }
        }
    }

    public void showChatHistory(SocketHelper instance) {
        if (messageHistory.getLastMessages().isEmpty()) {
            instance.getOut().println("false");
        } else {
            instance.getOut().println(messageHistory.getLastMessages().size());
            int j = 0;
            while (j < messageHistory.getLastMessages().size()) {
                for (String st : messageHistory.getLastMessages()) {
                    instance.getOut().println(st);
                    j++;
                }
            }
        }
    }

    public void onLine(SocketHelper instance) {
        instance.getOut().println(TextColor.PURPLE + "В сети: ");
        for (SocketHelper cs : socketList) {
            instance.getOut().println(TextColor.PURPLE + cs.getUserName() + TextColor.RESET);
        }
    }

    // методы, необходимые только для тестов
    public List<SocketHelper> getSocketList() {
        return socketList;
    }

    public void setSocketList(SocketHelper instance) {
        socketList.add(instance);
    }

    public MessageHistory getMessageHistory() {
        return this.messageHistory;
    }
}
