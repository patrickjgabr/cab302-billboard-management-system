package Server.ClientHandler;

import static org.junit.jupiter.api.Assertions.*;

import Server.Database.Database;
import Server.Database.SessionDatabase;
import Server.Database.UserDatabase;
import Shared.Message;
import Shared.Properties;
import Shared.User;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageHandlerTest {

    private static Properties properties;
    private static Database database;

    private static void insertUsers() {
        User rootUser = new User("root", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(1, 1, 1, 1)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        User noPerms = new User("noPerms", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(0, 0, 0, 0)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");

        UserDatabase userDatabase = new UserDatabase(properties);
        try {
            userDatabase.addToDatabase(rootUser);
            userDatabase.addToDatabase(noPerms);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void setupTestDatabase() {
        try {
            database.startConnection();
            database.runDelete("DELETE FROM sessions");
            database.runDelete("DELETE FROM schedule");
            database.runDelete("DELETE FROM billboards");
            database.runDelete("DELETE FROM users");
            database.closeConnection();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        insertUsers();
    }

    @BeforeAll
    public static void SetupVariables() {
        try {
            properties = new Properties();
            properties.setDatabaseURL("jdbc:mariadb://localhost:3306/testdatabase");
            database = new Database(properties);
            setupTestDatabase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void EmptyMessage() {
        Message message = new Message();
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(501, (Integer)returnMessage.getCommunicationID());
    }

    @Test
    public void MessageInvalidID() {
        Message message = new Message();
        message.setCommunicationID(100);
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(501, returnMessage.getCommunicationID());
    }

    @Test
    public void LoginNoDetails() {
        Message message = new Message().loginUser("", "");
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(502, (Integer)returnMessage.getCommunicationID());
    }

    @Test
    public void LoginNoPassword() {
        Message message = new Message().loginUser("root", "");
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(502, (Integer)returnMessage.getCommunicationID());
    }

    @Test
    public void LoginCorrect() {
        Message message = new Message().loginUser("root", "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, (Integer)returnMessage.getCommunicationID());
    }

    @Test
    public void LogoutNoToken() {
        Message message = new Message().logoutUser("");
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(503, returnMessage.getCommunicationID());
    }

    @Test
    public void LogoutInvalidToken() {
        Message message = new Message().logoutUser("THIS ISN'T A VALID TOKEN");
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(503, returnMessage.getCommunicationID());
    }

    public String setRootToken() {
        SessionDatabase sessionDatabase = new SessionDatabase(properties);
        String token = sessionDatabase.setSession("root");
        return token;
    }

    @Test
    public void LogoutValidToken() {
        Message message = new Message().logoutUser(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void GetBillboardsNoSession() {
        Message message = new Message().requestBillboards();
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(501, returnMessage.getCommunicationID());
    }

    @Test
    public void GetBillboardsValidSession() {
        Message message = new Message().requestBillboards();
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());
    }




}
