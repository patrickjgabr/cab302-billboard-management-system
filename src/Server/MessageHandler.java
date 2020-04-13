package Server;
import Shared.*;

public class MessageHandler {
    
    private Message sentMessage;
    private Message returnMessage;

    public MessageHandler(Message sentMessage) {
        this.sentMessage = sentMessage;
        this.returnMessage = new Message(this.sentMessage.getSession());
    }
    
    public Message getReturnMessage() {

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

        return  returnMessage;
    }

    private void handleUpdateUser() {
        try {
            UserDatabase userDB = new UserDatabase((User)returnMessage.getData());
            userDB.updateDatabase();
            returnMessage.setData(200);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleCreateUser() {
        try {
            UserDatabase userDB = new UserDatabase((User)returnMessage.getData());
            userDB.addToDatabase();
            returnMessage.setData(200);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleGetUsers() {
        try {
            UserDatabase userDB = new UserDatabase();
            User[] requestedUsers = userDB.getUsers();
            returnMessage.setData(requestedUsers);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleUpdateBillboard() {
        try {
            BillboardDatabase billboardDB = new BillboardDatabase((Billboard)sentMessage.getData());
            billboardDB.updateDatabase();
            returnMessage.setData(200);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleAddBillboard() {
        try {
            BillboardDatabase billboardDB = new BillboardDatabase((Billboard)sentMessage.getData());
            billboardDB.addToDatabase();
            returnMessage.setData(200);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleGetUser() {
        try {
            UserDatabase userDB = new UserDatabase();
            User requestedUser = userDB.getUser(false, (String) sentMessage.getData());
            returnMessage.setData(requestedUser);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }

    private void handleGetBillboards() {
        try {
            BillboardDatabase billboardDB = new BillboardDatabase();
            Billboard[] requestedBillboards = billboardDB.getBillboards();
            returnMessage.setData(requestedBillboards);
        } catch (Exception e) {
            returnMessage.setData(500);
        }
    }
}
