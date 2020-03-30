package NetworkTest;

import java.util.TreeMap;

public class Main_Testing {

    public static void main(String args[]) {
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
    }
}
