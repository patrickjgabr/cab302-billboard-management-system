public class Main_Server {


    public static void main(String args[]) {
        Server server = new Server(5000);
        server.startServer();

        CommunicationPacket message = server.receiveMessages();
        server.closeServer();
        Billboard billboard = new Billboard(message.getInformation());
        System.out.println(billboard.generateXML());
    }
}
