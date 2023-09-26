package ServerTest;

import Server.MessageHistory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHistoryTest {

    @Test
    public void createChatHistoryTest() throws InterruptedException {
        // given
        MessageHistory sut = new MessageHistory();

        BlockingQueue<String> lastMessages = Mockito.mock(LinkedBlockingQueue.class);
        Mockito.when(lastMessages.size()).thenReturn(10);
        sut.setLastMessages(lastMessages);

        //when
        sut.createChatHistory("11");

        // then
        Mockito.verify(lastMessages, Mockito.times(1)).take();
        Mockito.verify(lastMessages, Mockito.times(1)).put("11");
    }

    @Test
    public void createChatHistoryTest2() throws InterruptedException {
        // given
        MessageHistory sut = new MessageHistory();

        BlockingQueue<String> lastMessages = Mockito.mock(LinkedBlockingQueue.class);
        Mockito.when(lastMessages.size()).thenReturn(9);
        sut.setLastMessages(lastMessages);

        //when
        sut.createChatHistory("10");

        // then
        Mockito.verify(lastMessages, Mockito.times(0)).take();
        Mockito.verify(lastMessages, Mockito.times(1)).put("10");
    }
}
