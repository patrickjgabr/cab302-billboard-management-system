package Server.ClientHandler;

import Server.ConsoleMessage.ConsoleMessage;

import java.util.Calendar;
import java.util.Date;

/**
 * ClientHandlerMessage class extends ConsoleMessage and uses its public methods to provide pre formatted database error messages for development.
 */
public class ClientHandlerMessage extends ConsoleMessage {

    Calendar calendar = Calendar.getInstance();

    /**
     * Default constructor used to instantiate a ClientHandlerMessage Object
     */
    public ClientHandlerMessage() {
        super();
    }

    /**
     * Formats and prints a message indicating that the a client has been started
     * @param ID Client ID
     */
    public void clientHandlerStart(int ID) {
        super.printGeneral("CLIENT HANDLER", "Client handler opened     |   Client ID: ["+ ID +"]     |     Start Time: " + calendar.getTime().toString(), 150);
    }

    /**
     * Formats and prints a message indicating that the a client has been closed
     * @param ID Client ID
     */
    public void clientHandlerClose(int ID) {
        super.printGeneral("CLIENT HANDLER", "Client handler closed     |     Client ID: ["+ ID +"]     |     End Time: " + calendar.getTime().toString(), 150);
    }

    /**
     * Formats and prints a message indicating that a message handler has been started
     * @param ID Client ID
     * @param token Token
     */
    public void messageHandlerStart(Integer ID, String token) {
        super.printGeneral("MESSAGE HANDLER", "Message handler started     |     CommunicationID: ["+ID+"]                                                   Token: " + token, 125);
    }

    /**
     * Formats and prints a message indicating that a message handler has been closed
     * @param returnStatus Communication ID of return message
     */
    public void messageHandlerClose(Integer returnStatus) {
        super.printGeneral("MESSAGE HANDLER", "Message handler closed     |     Return status: [" + returnStatus +"]", 125);
    }
}
