import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

public class clientServerTest {

    private static Server server;
    private Client client;
    private static Thread serverThread;
    private Thread clientThread;

    @BeforeAll
    public static void startServer() {

        server = new Server();
        serverThread = new Thread(() -> server.run());
    }

    @AfterAll
    public static void closeServer() {
        Thread testThread = new Thread(() -> {
            server.runServer = false;
        });
    }

    @Test
    public void serverStartUp() {
        Thread testThread = new Thread(() -> {
            assertEquals(true, true);
        });
    }

    @Test
    public void clientEchoTest() {
        client = new Client("127.0.0.1", 8080);
        clientThread = new Thread(() -> {
            Message sentMessage = new Message("Test");
            Message returnMessage = client.sendMessgae(sentMessage);
            assertEquals(sentMessage.getData(), returnMessage.getData());
        });
    }
}
