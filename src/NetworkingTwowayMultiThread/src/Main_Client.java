public class Main_Client {

    public static void main(String args[]) {
        Client client = new Client("127.0.0.1", 8080);

        Message sentMessage = new Message("Test");
        Message returnMessage = client.sendMessgae(sentMessage);

        System.out.println(returnMessage.getData().toString());


    }
}
