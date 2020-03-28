public class Main_Server {


    public static void main(String args[]) {
        Server server = new Server(5000);
        server.startServer();

        CommunicationPacket message = server.receiveMessages();
        System.out.println(message.getData());
        server.closeServer();
    }
}
