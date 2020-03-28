import java.util.TreeMap;

public class Main_Client {

    public static void main(String args[]) {
        Client client = new Client("127.0.0.1", 5000);

        TreeMap<String, String> testBillboardMap = new TreeMap<>();
        testBillboardMap.put("Name", "TEST");
        testBillboardMap.put("CreatorID", "12345");
        testBillboardMap.put("ImageUrl", "TEST");
        testBillboardMap.put("MessageText", "TEST");
        testBillboardMap.put("MessageTextColour", "TEST");
        testBillboardMap.put("BackgroundColour", "TEST");
        testBillboardMap.put("InformationText", "TEST");
        testBillboardMap.put("InformationTextColour", "TEST");

        Billboard testBillboard = new Billboard(testBillboardMap);
        System.out.println(testBillboard.generateXML());
        CommunicationPacket message = new CommunicationPacket(testBillboard.getBillboardExport(), 100);
        client.sendMessage(message);
    }
}
