package Server;
import Shared.*;

public class MessageHandler {
    
    private Message sentMessage;
    private Message returnMessage;
    private Properties properties;

    public MessageHandler(Message sentMessage, Properties properties) {
        this.sentMessage = sentMessage;
        this.returnMessage = new Message(this.sentMessage.getSession());
        this.properties = properties;
    }
    
    public Message getReturnMessage() {
        System.out.println("Message Handler opened... ");

        if (sentMessage.getCommunicationID() == 10) {
            handleGetUser();
        } else if (sentMessage.getCommunicationID() == 20) {
            handleGetBillboards();
        } else if (sentMessage.getCommunicationID() == 21) {
            handleAddBillboard();
        } else if (sentMessage.getCommunicationID() == 22) {
            handleUpdateBillboard();
        } else if (sentMessage.getCommunicationID() == 30) {
            handleGetUsers();
        } else if (sentMessage.getCommunicationID() == 31) {
            handleCreateUser();
        } else if (sentMessage.getCommunicationID() == 32) {
            handleUpdateUser();
        }

        System.out.println("Message Handler closed...");

        return  returnMessage;
    }

    private void handleUpdateUser() {
        try {
            UserDatabase userDB = new UserDatabase(properties);
            userDB.updateDatabase((User)sentMessage.getData());
            returnMessage.setData(200);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleCreateUser() {
        try {
            UserDatabase userDB = new UserDatabase(properties);
            userDB.addToDatabase((User)sentMessage.getData());
            returnMessage.setData(200);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleGetUsers() {
        try {
            UserDatabase userDB = new UserDatabase(properties);
            User[] requestedUsers = userDB.getUsers();
            returnMessage.setData(requestedUsers);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleUpdateBillboard() {
        try {
            BillboardDatabase billboardDB = new BillboardDatabase(properties);
            billboardDB.updateDatabase((Billboard)sentMessage.getData());
            returnMessage.setData(200);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleAddBillboard() {
        try {
            BillboardDatabase billboardDB = new BillboardDatabase(properties);
            billboardDB.addToDatabase((Billboard)sentMessage.getData());
            returnMessage.setData(200);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleGetUser() {
        try {
            UserDatabase userDB = new UserDatabase(properties);
            User requestedUser = userDB.getUser(false, (String) sentMessage.getData());
            returnMessage.setData(requestedUser);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleGetBillboards() {
        try {
            BillboardDatabase billboardDB = new BillboardDatabase(properties);
            Billboard[] requestedBillboards = billboardDB.getBillboards();
            returnMessage.setData(requestedBillboards);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }
}
