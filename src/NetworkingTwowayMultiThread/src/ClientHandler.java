import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ClientHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        Message receivedMessage;

        System.out.println("Client handler socket open...");

        try {
            receivedMessage = (Message)inputStream.readObject();

            if (receivedMessage.getCommunicationID() == 60) {
                handleEchoMessage(receivedMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Client handler socket closed...");
    }

    private void handleEchoMessage(Message received ) {
        try {
            outputStream.writeObject(received);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
