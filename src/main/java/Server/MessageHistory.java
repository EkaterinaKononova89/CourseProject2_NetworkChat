package Server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MessageHistory {
    protected BlockingQueue<String> lastMessages = new LinkedBlockingQueue<>();
    protected final static int HISTORY_MAX_SIZE = 10; // или может другое число

    public void createChatHistory(String message) throws InterruptedException { // наполняем очередь 10 последними сообщениями
        if (lastMessages.size() < HISTORY_MAX_SIZE) {
            lastMessages.put(message);
        } else {
            lastMessages.take();
            lastMessages.put(message);
        }
    }

    // методы только для тестов
    public BlockingQueue<String> getLastMessages() {
        return this.lastMessages;
    }

    public void addLastMessages(String msg) { // создала только для теста, поэтому метод add, а не put
        lastMessages.add(msg);
    }

    public void setLastMessages(BlockingQueue<String> lastMessages1) {
        lastMessages = lastMessages1;
    }
}
