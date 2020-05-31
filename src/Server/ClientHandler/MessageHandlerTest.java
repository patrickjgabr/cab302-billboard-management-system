package Server.ClientHandler;

import static org.junit.jupiter.api.Assertions.*;

import Server.Database.*;
import Shared.*;
import org.junit.jupiter.api.*;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageHandlerTest {

    /*
    IMPORTANT: RUNNING THIS TEST WILL RESET THE DATABASE
     */

    private static Properties properties;
    private static Database database;

    private static void setExpiredToken() {
        try {
            UserDatabase userDatabase = new UserDatabase(properties);
            User user = userDatabase.getUser("expiredToken", true);

            Connection connection = DriverManager.getConnection(properties.getDatabaseURL() + '/' + properties.getDatabaseName(), properties.getDatabaseUser(), properties.getDatabasePassword());
            PreparedStatement statement = connection.prepareStatement("INSERT INTO sessions VALUES (?,?,?,?)");
            statement.clearParameters();
            statement.setString(1 ,"THISTOKENISEXPIRED");
            statement.setInt(2 ,user.getUserID());
            statement.setString(3 ,"2010-05-18");
            statement.setString(4 ,"10:00:00");
            statement.executeUpdate();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private static void insertUsers() {
        User rootUser = new User("root", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(1, 1, 1, 1)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        User noPerms = new User("noPerms", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(0, 0, 0, 0)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        User expiredToken = new User("expiredToken", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(0, 0, 0, 0)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");

        UserDatabase userDatabase = new UserDatabase(properties);
        try {
            userDatabase.addToDatabase(rootUser);
            userDatabase.addToDatabase(noPerms);
            userDatabase.addToDatabase(expiredToken);
            setExpiredToken();
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
            database = new Database(properties);
            setupTestDatabase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void EmptyMessage() {
        Message message = new Message();
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(503, (Integer)returnMessage.getCommunicationID());
    }

    @Test
    public void MessageInvalidID() {
        Message message = new Message();
        message.setCommunicationID(100);
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(503, returnMessage.getCommunicationID());
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
        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void LogoutInvalidToken() {
        Message message = new Message().logoutUser("THIS ISN'T A VALID TOKEN");
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());
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
    public void UserExpiredToken() {
        Message message = new Message();
        message.setCommunicationID(20);
        message.setSession("THISTOKENISEXPIRED");
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(503, returnMessage.getCommunicationID());
    }

    @Test
    public void GetBillboardsNoSession() {
        Message message = new Message().requestBillboards();
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(503, returnMessage.getCommunicationID());
    }

    @Test
    public void GetBillboardsValidSession() {
        Message message = new Message().requestBillboards();
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void AddBillboardNoData() {
        Message message = new Message();
        message.setSession(setRootToken());
        message.setCommunicationID(21);
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void AddBillboardWrongData() {
        Message message = new Message();
        message.setSession(setRootToken());
        message.setCommunicationID(21);
        message.setData(new User("root", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(1, 1, 1, 1)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n"));
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    public String setNoPermsToken() {
        SessionDatabase sessionDatabase = new SessionDatabase(properties);
        String token = sessionDatabase.setSession("noPerms");
        return token;
    }

    @Test
    public void AddBillboardNoPerms() {
        Message message = new Message().createBillboard(new Billboard("creatorName", "name", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        message.setSession(setNoPermsToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(504, returnMessage.getCommunicationID());
    }

    @Test
    public void AddBillboardWrongCreator() {
        Message message = new Message().createBillboard(new Billboard("creatorName", "name", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(505, returnMessage.getCommunicationID());
    }

    @Test
    public void AddBillboardCorrect() {
        Message message = new Message().createBillboard(new Billboard("root", "name", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void AddDuplicate() {
        Message message = new Message().createBillboard(new Billboard("root", "SAMENAME", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        message.setSession(setRootToken());

        MessageHandler messageHandler1 = new MessageHandler(message, properties);
        Message returnMessage1 = messageHandler1.getReturnMessage();

        MessageHandler messageHandler2 = new MessageHandler(message, properties);
        Message returnMessage2 = messageHandler1.getReturnMessage();
        assertEquals(506, returnMessage2.getCommunicationID());
    }

    @Test
    public void UpdateBillboardNoData() {
        Message message = new Message();
        message.setSession(setRootToken());
        message.setCommunicationID(22);

        MessageHandler messageHandler = new MessageHandler(message, properties);
        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void UpdateBillboardWrongData() {
        Message message = new Message();
        message.setSession(setRootToken());
        message.setCommunicationID(22);
        message.setData(1);

        MessageHandler messageHandler = new MessageHandler(message, properties);
        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void UpdateBillboardNoPerms() {
        try {
            BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
            Billboard billboard = billboardDatabase.getBillboard("name", false);

            Message message = new Message().updateBillboard(billboard);
            message.setSession(setNoPermsToken());

            MessageHandler messageHandler = new MessageHandler(message, properties);
            Message returnMessage = messageHandler.getReturnMessage();
            assertEquals(200, returnMessage.getCommunicationID());
        } catch (Throwable throwable) {
            assertEquals(false, true);
        }
    }

    @Test
    public void UpdateBillboardNotExist() {
        try {
            BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
            Billboard billboard = billboardDatabase.getBillboard("I DONT EXIST", false);

            Message message = new Message().updateBillboard(billboard);
            message.setSession(setNoPermsToken());

            MessageHandler messageHandler = new MessageHandler(message, properties);
            Message returnMessage = messageHandler.getReturnMessage();
            assertEquals(500, returnMessage.getCommunicationID());
        } catch (Throwable throwable) {
            assertEquals(false, true);
        }
    }

    @Test
    public void UpdateBillboardValid() {
        try {
            BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
            Billboard newBillboard = new Billboard("root", "new", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE");

            User root = new UserDatabase(properties).getUser("root", true);
            billboardDatabase.addToDatabase(newBillboard, root.getUserID());
            Billboard billboard = billboardDatabase.getBillboard("new", false);
            billboard.setMessageText("NEW TEXT");

            Message message = new Message().updateBillboard(billboard);
            message.setSession(setRootToken());

            MessageHandler messageHandler = new MessageHandler(message, properties);
            Message returnMessage = messageHandler.getReturnMessage();
            assertEquals(200, returnMessage.getCommunicationID());

            Billboard billboard1 = billboardDatabase.getBillboard(billboard.getName(), false);
            assertEquals("NEW TEXT", billboard1.getMessageText());
            assertEquals(0, billboard1.getScheduled());

        } catch (Throwable throwable) {
            assertEquals(false, true);
        }
    }

    @Test
    public void RemoveBillboardDoesntExist() {
        Billboard updateBillboard = new Billboard("root", "doesntExist", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE");
        Message message = new Message().deleteBillboard(updateBillboard);
        message.setSession(setRootToken());

        MessageHandler messageHandler = new MessageHandler(message, properties);
        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void RemoveBillboardExists() {
        Billboard billboard = new Billboard("root", "name", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE", "NEWVALUE");
        Message message = new Message().createBillboard(billboard);
        message.setSession(setRootToken());

        MessageHandler messageHandler = new MessageHandler(message, properties);
        messageHandler.getReturnMessage();

        BillboardDatabase billboardDatabase = new BillboardDatabase(properties);
        try {
            billboard = billboardDatabase.getBillboard("name", false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        message = new Message().deleteBillboard(billboard);
        message.setSession(setRootToken());
        messageHandler = new MessageHandler(message, properties);
        Message returnMessage = messageHandler.getReturnMessage();

        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void GetUsersNoSession() {
        Message message = new Message().requestUsers();
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(503, returnMessage.getCommunicationID());
    }

    @Test
    public void GetUsersValidSession() {
        Message message = new Message().requestUsers();
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateUserNoData() {
        Message message = new Message();
        message.setCommunicationID(31);
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateUserInvalidData() {
        Message message = new Message();
        message.setCommunicationID(31);
        message.setSession(setRootToken());
        message.setData(1);
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateUserNoPerms() {
        User newUser = new User("newUser", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(0, 0, 0, 0)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        Message message = new Message().createUsers(newUser);
        message.setSession(setNoPermsToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(504, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateUserCorrect() {
        User newUser = new User("newUser", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(0, 0, 0, 0)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        Message message = new Message().createUsers(newUser);
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateUserDuplicate() {
        User sameUser = new User("SAMEUSER", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(0, 0, 0, 0)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        Message message = new Message().createUsers(sameUser);
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        Message returnMessage2 = messageHandler.getReturnMessage();

        assertEquals(519, returnMessage2.getCommunicationID());
    }

    @Test
    public void UpdateUserNoData() {
        Message message = new Message();
        message.setCommunicationID(32);
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void UpdateUserInvalidData() {
        Message message = new Message();
        message.setCommunicationID(32);
        message.setSession(setRootToken());
        message.setData(1);
        MessageHandler messageHandler = new MessageHandler(message, properties);

        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void UpdateUserNoPerms() {
        User newUser = new User("newUser", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(0, 0, 0, 0)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        Message message = new Message().updateUser(newUser);
        message.setSession(setNoPermsToken());

        MessageHandler messageHandler = new MessageHandler(message, properties);
        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(504, returnMessage.getCommunicationID());
    }

    @Test
    public void UpdateUserCorrect() {
        User newUser = new User("correctUpdate", "089542505d659cecbb988bb5ccff5bccf85be2dfa8c221359079aee2531298bb", new ArrayList<>(Arrays.asList(1, 1, 1, 1)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        UserDatabase userDatabase = new UserDatabase(properties);
        try {
            userDatabase.addToDatabase(newUser);
        } catch (Throwable throwable) {}

        Message message = new Message().updateUser(newUser);
        message.setSession(setRootToken());

        MessageHandler messageHandler = new MessageHandler(message, properties);
        Message returnMessage = messageHandler.getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());

        try {
            User user = userDatabase.getUser("correctUpdate", true);
            assertEquals(new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1)), user.getPermission());
            assertEquals("76f29a4a6613bc1efd89ae4df268f9161b13a2bcd094b1536bca89c3d678be7c", user.getUserPassword());
        } catch (Throwable throwable) {
            assertEquals(false, true);
        }
    }

    @Test
    public void RemoveUserNonExist() {
        User doesntExist = new User("doesn'tExist", "089542505d659cecbb988bb5ccff5bccf85be2dfa8c221359079aee2531298bb", new ArrayList<>(Arrays.asList(1, 1, 1, 1)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");

        Message message = new Message().deleteUser(doesntExist);
        MessageHandler messageHandler = new MessageHandler(message, properties);
        Message returnMessage = messageHandler.getReturnMessage();

        assertEquals(503, returnMessage.getCommunicationID());
    }

    @Test
    public void RemoveUserExists() {
        User exist = new User("userExists", "089542505d659cecbb988bb5ccff5bccf85be2dfa8c221359079aee2531298bb", new ArrayList<>(Arrays.asList(1, 1, 1, 1)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");
        UserDatabase userDatabase = new UserDatabase(properties);
        try {
            userDatabase.addToDatabase(exist);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        User user = null;
        try {
            user = new UserDatabase(properties).getUser("userExists", true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Message message = new Message().deleteUser(user);
        message.setSession(setRootToken());
        Message returnMessage = new MessageHandler(message, properties).getReturnMessage();

        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void GetScheduleNoSession() {
        Message message = new Message();
        Message returnMessage = new MessageHandler(message, properties).getReturnMessage();
        assertEquals(503, returnMessage.getCommunicationID());
    }

    @Test
    public void GetScheduleValidSession() {
        Message message = new Message().requestSchedule();
        message.setSession(setRootToken());

        Message returnMessage = new MessageHandler(message, properties).getReturnMessage();

        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateScheduleNoData() {
        Message message = new Message();
        message.setCommunicationID(41);
        message.setSession(setRootToken());
        Message returnMessage = new MessageHandler(message, properties).getReturnMessage();

        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateScheduleInvalidData() {
        Message message = new Message();
        message.setCommunicationID(41);
        message.setData(1);
        message.setSession(setRootToken());
        Message returnMessage = new MessageHandler(message, properties).getReturnMessage();

        assertEquals(500, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateScheduleNoPerms() {
        Message message = new Message();
        message.setCommunicationID(41);
        message.setSession(setNoPermsToken());

        Message returnMessage = new MessageHandler(message, properties).getReturnMessage();
        assertEquals(504, returnMessage.getCommunicationID());
    }

    @Test
    public void CreateScheduleCorrect() {
        Message message = new Message().createBillboard(new Billboard("root", "name", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);
        messageHandler.getReturnMessage();

        Billboard billboard = null;
        try {
            billboard = new BillboardDatabase(properties).getBillboard("name", false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        User user = null;
        try {
            user = new UserDatabase(properties).getUser("root", true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Scheduled newschedule = new Scheduled(user.getUserID(),"root",billboard.getBillboardID(),"Test",ScheduleHelper.CalculateStart(1,0,0,1),60, new int[]{0,0,0});
        message = new Message().scheduleBillboard(newschedule);
        message.setSession(setRootToken());

        Message returnMessage = new MessageHandler(message, properties).getReturnMessage();
        assertEquals(200, returnMessage.getCommunicationID());
    }

    @Test
    public void UpdateScheduleNoData() {
        Message message = new Message();
        message.setCommunicationID(42);
        message.setSession(setRootToken());

        MessageHandler messageHandler = new MessageHandler(message, properties);
        message = messageHandler.getReturnMessage();

        assertEquals(500, message.getCommunicationID());
    }

    @Test
    public void UpdateScheduleInvalidData() {
        Message message = new Message();
        message.setData(1);
        message.setCommunicationID(42);
        message.setSession(setRootToken());

        MessageHandler messageHandler = new MessageHandler(message, properties);
        message = messageHandler.getReturnMessage();

        assertEquals(500, message.getCommunicationID());
    }

    @Test
    public void UpdateScheduleNoPerms() {

        Message message = new Message().createBillboard(new Billboard("root", "name", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);
        messageHandler.getReturnMessage();

        Billboard billboard = null;
        try {
            billboard = new BillboardDatabase(properties).getBillboard("name", false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        User user = null;
        try {
            user = new UserDatabase(properties).getUser("root", true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Scheduled newschedule = new Scheduled(user.getUserID(),"root",billboard.getBillboardID(),"Test",ScheduleHelper.CalculateStart(1,0,0,1),60, new int[]{0,0,0});
        message = new Message().scheduleBillboard(newschedule);
        message.setSession(setRootToken());

        Message returnMessage = new MessageHandler(message, properties).getReturnMessage();

        ArrayList<Scheduled> scheduled = null;

        try {
            scheduled = new ScheduleDatabase(properties).getSchedule();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Scheduled schedule = scheduled.get(0);
        message = new Message().updateSchedule(schedule);
        message.setSession(setNoPermsToken());
        message = new MessageHandler(message, properties).getReturnMessage();
        assertEquals(517, message.getCommunicationID());
    }

    @Test
    public void UpdateScheduleCorrect() {
        Message message = new Message().createBillboard(new Billboard("root", "name1", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);
        messageHandler.getReturnMessage();

        Billboard billboard = null;
        try {
            billboard = new BillboardDatabase(properties).getBillboard("name1", false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        User user = null;
        try {
            user = new UserDatabase(properties).getUser("root", true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Scheduled newschedule = new Scheduled(user.getUserID(),"root", billboard.getBillboardID(),"Test",ScheduleHelper.CalculateStart(1,0,0,1),60, new int[]{0,0,0});
        message = new Message().scheduleBillboard(newschedule);
        message.setSession(setRootToken());

        messageHandler = new MessageHandler(message, properties);
        messageHandler.getReturnMessage();

        newschedule.setDay(5);
        message = new Message().scheduleBillboard(newschedule);
        message.setSession(setRootToken());

        message = new MessageHandler(message, properties).getReturnMessage();

        assertEquals(200, message.getCommunicationID());
    }

    @Test
    public void RemoveScheduleNonExist() {
        Scheduled scheduled = new Scheduled();
        scheduled.setID(1);
        Message message = new Message().deleteSchedule(scheduled);
        message.setSession(setRootToken());

        MessageHandler messageHandler = new MessageHandler(message, properties);
        message = messageHandler.getReturnMessage();

        assertEquals(514, message.getCommunicationID());
    }

    @Test
    public void RemoveScheduleExist() {
        Message message = new Message().createBillboard(new Billboard("root", "name3", "imageUrl", "msgText", "", "#000FFF", "infoText", ""));
        message.setSession(setRootToken());
        MessageHandler messageHandler = new MessageHandler(message, properties);
        messageHandler.getReturnMessage();

        Billboard billboard = null;
        try {
            billboard = new BillboardDatabase(properties).getBillboard("name3", false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        User user = null;
        try {
            user = new UserDatabase(properties).getUser("root", true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        Scheduled newschedule = new Scheduled(user.getUserID(),"root",billboard.getBillboardID(),billboard.getName(),ScheduleHelper.CalculateStart(1,0,0,1),60, new int[]{0,0,0});
        message = new Message().scheduleBillboard(newschedule);
        message.setSession(setRootToken());

        message = new MessageHandler(message, properties).getReturnMessage();

        ArrayList<Scheduled> schedule = null;
        try {
            schedule = new ScheduleDatabase(properties).getSchedule();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        message = new Message().deleteSchedule(schedule.get(0));
        message.setSession(setRootToken());
        message = new MessageHandler(message, properties).getReturnMessage();

        assertEquals(200, message.getCommunicationID());
    }

    @AfterAll
    public static void refreshDatabase() {
        Database database = new Database(properties);
        User rootUser = new User("root", "c13866ce9e42e90d3cf50ded2dc9e00194ffc4ad4e15865cd1b368f168944646", new ArrayList<>(Arrays.asList(1, 1, 1, 1)), 100000,"y6WOb24rUAINN6KoUQ7lWNeniyTpsxPaZqzEhvAMzSqE5MrIx2kJS9TaTm0rl96n");

        database.startConnection();
        try {
            database.runDelete("DELETE FROM sessions");
            database.runDelete("DELETE FROM schedule");
            database.runDelete("DELETE FROM billboards");
            database.runDelete("DELETE FROM users");
            database.closeConnection();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        UserDatabase userDatabase = new UserDatabase(properties);
        try {
            userDatabase.addToDatabase(rootUser);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
