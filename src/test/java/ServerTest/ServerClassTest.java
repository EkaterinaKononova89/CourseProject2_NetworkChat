package ServerTest;


import Server.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.*;


public class ServerClassTest {
    ServerClass sut;
    int port = 8091;

    @BeforeEach
    public void beforeEach() {
        sut = new ServerClass(port);
    }

    @AfterEach
    public void afterEach() {
        sut = null;
    }

    @Test
    public void dateTimeTest() {
    } // наверно этот метод не нужно тестить?

    @Test
    public void nameDateTimeTest() {
        // given
        String userName = "Client 1";

        //when
        String result = sut.nameDateTime(userName);

        //then
        Assertions.assertEquals("Client 1, " + sut.dateTime(), result);
    }

    @Test
    public void invalidNameCheckingTest() { // если уже есть ранее подключившиеся клиенты
        // given
        SocketHelper instance1 = Mockito.mock(SocketHelper.class);
        Mockito.when(instance1.getUserName()).thenReturn("User1");

        SocketHelper instance2 = Mockito.mock(SocketHelper.class);
        Mockito.when(instance2.getUserName()).thenReturn("User2");

        sut.setSocketList(instance1);
        sut.setSocketList(instance2);

        //when
        boolean result1 = sut.invalidNameChecking("User1");
        boolean result2 = sut.invalidNameChecking("User2");
        boolean result3 = sut.invalidNameChecking("User3");

        //then
        Assertions.assertFalse(result1);
        Assertions.assertFalse(result2);
        Assertions.assertTrue(result3);
    }

    @Test
    public void invalidNameCheckingTest_emptyList() { // если на сервере еще никого нет
        // given

        //when
        boolean result1 = sut.invalidNameChecking("User1");

        //then
        Assertions.assertTrue(result1);
    }

    @Test
    public void chooseUniqueNameTest_emptyList() throws IOException { // если на сервере еще никого нет
        // given
        SocketHelper instance = Mockito.mock(SocketHelper.class);

        PrintWriter out = Mockito.mock(PrintWriter.class);
        Mockito.when(instance.getOut()).thenReturn(out);

        BufferedReader in = Mockito.mock(BufferedReader.class);
        Mockito.when(instance.getIn()).thenReturn(in);

        //when
        sut.chooseUniqueName(instance, "User1");

        //then
        Mockito.verify(instance, Mockito.times(1)).getOut();
        Mockito.verify(instance, Mockito.times(0)).getIn();
    }

    @Test
    public void chooseUniqueNameTest() throws IOException { // если уже есть ранее подключившиеся клиенты
        // given
        SocketHelper instance1 = Mockito.mock(SocketHelper.class);
        Mockito.when(instance1.getUserName()).thenReturn("User1");


        SocketHelper instance2 = Mockito.mock(SocketHelper.class);
        PrintWriter out = Mockito.mock(PrintWriter.class);
        Mockito.when(instance2.getOut()).thenReturn(out);

        BufferedReader in = Mockito.mock(BufferedReader.class);
        Mockito.when(instance2.getIn()).thenReturn(in);

        //when
        sut.setSocketList(instance1);
        try {  // так вообще можно делать - ловить ошибки в тесте?
            sut.chooseUniqueName(instance2, "User1"); // при неверном имени тест попадает в цикл вайл-тру и выкидывает ошибку;
        } catch (NullPointerException ignored) {
        }

        //then
        Mockito.verify(instance2, Mockito.times(1)).getOut();
        Mockito.verify(instance2, Mockito.times(1)).getIn();
    }

    @Test
    public void showChatHistoryTest_emptyHistory() {
        // given
        SocketHelper instance = Mockito.mock(SocketHelper.class);
        PrintWriter out = Mockito.mock(PrintWriter.class);
        Mockito.when(instance.getOut()).thenReturn(out);

        //when
        sut.showChatHistory(instance);

        //then
        Mockito.verify(instance, Mockito.times(1)).getOut();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(instance.getOut()).println(argumentCaptor.capture());
        Assertions.assertEquals("false", argumentCaptor.getValue());
    }

    @Test
    public void showChatHistoryTest() {
        // given
        SocketHelper instance = Mockito.mock(SocketHelper.class);
        PrintWriter printWriterOut = Mockito.mock(PrintWriter.class);
        Mockito.when(instance.getOut()).thenReturn(printWriterOut);

        sut.getMessageHistory().addLastMessages("Hi!");

        //when
        sut.showChatHistory(instance);

        //then
        Mockito.verify(instance, Mockito.times(2)).getOut();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(instance.getOut()).println(argumentCaptor.capture());
        Assertions.assertEquals("Hi!", argumentCaptor.getValue());
    }

    @Test
    public void chattingTest_exit() throws IOException, InterruptedException {
        //given

        SocketHelper instance = Mockito.mock(SocketHelper.class);
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);

        Mockito.when(instance.getIn()).thenReturn(bufferedReader);
        Mockito.when(instance.getOut()).thenReturn(printWriter);
        Mockito.when(bufferedReader.readLine()).thenReturn("/exit");

        FileWriter log = Mockito.mock(FileWriter.class);
        Mockito.doNothing().when(log).write(Mockito.anyString());
        Mockito.doNothing().when(log).flush();

        //when
        sut.chatting(instance, log);

        //then
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(instance.getOut()).println(argumentCaptor.capture());
        Assertions.assertEquals("СООБЩЕНИЕ ОТ СЕРВЕРА: Вы вышли из чата", argumentCaptor.getValue());
    }

    @Test   // Mockito.spy
    public void chattingTest_onLine() throws IOException, InterruptedException {
        //given
        ServerClass serverClassSpy = Mockito.spy(ServerClass.class);

        SocketHelper instance = Mockito.mock(SocketHelper.class);
        PrintWriter printWriterOut = Mockito.mock(PrintWriter.class);
        BufferedReader bufferedReaderIn = Mockito.mock(BufferedReader.class);

        Mockito.when(instance.getIn()).thenReturn(bufferedReaderIn);
        Mockito.when(instance.getOut()).thenReturn(printWriterOut);
        Mockito.when(bufferedReaderIn.readLine()).thenReturn("/online", "/exit");

        FileWriter log = Mockito.mock(FileWriter.class);
        Mockito.doNothing().when(log).write(Mockito.anyString());
        Mockito.doNothing().when(log).flush();

        //when
        serverClassSpy.chatting(instance, log);

        //then
        Mockito.verify(serverClassSpy, Mockito.times(1)).onLine(instance);
    }

    @Test
    public void chattingTest_messages() throws IOException, InterruptedException { // не изолированный??? nameDateTime()
        //given
        SocketHelper instance = Mockito.mock(SocketHelper.class);
        PrintWriter printWriterOut = Mockito.mock(PrintWriter.class);
        BufferedReader bufferedReaderIn = Mockito.mock(BufferedReader.class);

        SocketHelper instance2 = Mockito.mock(SocketHelper.class);
        PrintWriter printWriterOut2 = Mockito.mock(PrintWriter.class);

        Mockito.when(instance.getIn()).thenReturn(bufferedReaderIn);
        Mockito.when(instance.getOut()).thenReturn(printWriterOut);
        Mockito.when(bufferedReaderIn.readLine()).thenReturn("Hello world!", "/exit");
        Mockito.when(instance.getUserName()).thenReturn("User1");

        Mockito.when(instance2.getOut()).thenReturn(printWriterOut2);

        FileWriter log = Mockito.mock(FileWriter.class);
        Mockito.doNothing().when(log).write(Mockito.anyString());
        Mockito.doNothing().when(log).flush();

        sut.getSocketList().add(instance2);
        sut.getSocketList().add(instance);

        //when
        sut.chatting(instance, log);

        //then
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(instance2.getOut()).println(argumentCaptor.capture());
        Assertions.assertEquals(TextColor.BLUE + sut.nameDateTime("User1") + ":\n " +
                TextColor.RESET + "Hello world!", argumentCaptor.getValue());
    }

    @Test   // Mockito.spy
    public void chattingTest_createHistory() throws IOException, InterruptedException {
        //given
        ServerClass serverClassSpy = Mockito.spy(ServerClass.class);

        SocketHelper instance = Mockito.mock(SocketHelper.class);
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);

        Mockito.when(instance.getIn()).thenReturn(bufferedReader);
        Mockito.when(instance.getOut()).thenReturn(printWriter);
        Mockito.when(bufferedReader.readLine()).thenReturn("Hello world!", "/exit");
        Mockito.when(instance.getUserName()).thenReturn("User1");

        FileWriter log = Mockito.mock(FileWriter.class);
        Mockito.doNothing().when(log).write(Mockito.anyString());
        Mockito.doNothing().when(log).flush();

        MessageHistory messageHistory = Mockito.mock(MessageHistory.class);
        Mockito.when(serverClassSpy.getMessageHistory()).thenReturn(messageHistory);

        //when
        serverClassSpy.chatting(instance, log);

        //then
        Mockito.verify(serverClassSpy.getMessageHistory(), Mockito.times(1))
                .createChatHistory(serverClassSpy.nameDateTime("User1") + ": " + "Hello world!");
    }

    @Test
    public void onLineTest() {
        //given
        SocketHelper instance = Mockito.mock(SocketHelper.class);
        PrintWriter printWriterOut = Mockito.mock(PrintWriter.class);
        Mockito.when(instance.getOut()).thenReturn(printWriterOut);

        Mockito.when(instance.getUserName()).thenReturn("User1");

        sut.getSocketList().add(instance);

        //when
        sut.onLine(instance);

        //then
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(instance.getOut(), Mockito.times(2)).println(argumentCaptor.capture());
        Assertions.assertEquals(TextColor.PURPLE + "User1" + TextColor.RESET, argumentCaptor.getAllValues().get(1));
    }
}


