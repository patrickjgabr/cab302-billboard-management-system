import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class Database_MAIN {

    public static void main(String[] args) throws SQLException {
        TreeMap<String, String> testBillboardMap = new TreeMap<>();
        testBillboardMap.put("Name", "Test");
        testBillboardMap.put("CreatorID", "100025");
        testBillboardMap.put("ImageUrl", "Test");
        testBillboardMap.put("MessageText", "TEST");
        testBillboardMap.put("MessageTextColour", "TEST");
        testBillboardMap.put("BackgroundColour", "TEST");
        testBillboardMap.put("InformationText", "TEST");
        testBillboardMap.put("InformationTextColour", "TEST");
        testBillboardMap.put("BillboardID", "100001");
        Billboard billboard = new Billboard(testBillboardMap);

        BillboardDatabase database = new BillboardDatabase(billboard);
        database.updateDatabase();
    }
}
