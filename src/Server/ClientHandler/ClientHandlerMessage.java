package Server.ClientHandler;

import Server.ConsoleMessage.ConsoleMessage;

import java.util.Calendar;
import java.util.Date;

public class ClientHandlerMessage extends ConsoleMessage {

    Calendar calendar = Calendar.getInstance();

    public ClientHandlerMessage() {
        super();
    }

    public void clientHandlerStart(int ID) {
        super.printGeneral("CLIENT HANDLER", "Client handler opened     |   Client ID: ["+ ID +"]     |     Start Time: " + calendar.getTime().toString(), 150);
    }

    public void clientHandlerClose(int ID) {
        super.printGeneral("CLIENT HANDLER", "Client handler closed     |     Client ID: ["+ ID +"]     |     End Time: " + calendar.getTime().toString(), 150);
    }

    public void messageHandlerStart(Integer ID, String token) {
        super.printGeneral("MESSAGE HANDLER", "Message handler started     |     CommunicationID: ["+ID+"]     |     Token: " + token, 125);
    }

    public void messageHandlerClose(Integer returnStatus) {
        super.printGeneral("MESSAGE HANDLER", "Message handler closed     |     Return status: [" + returnStatus +"]", 125);
    }
}
