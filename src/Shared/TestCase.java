package Shared;
import Shared.*;
import java.util.ArrayList;

public class TestCase {
    public static ArrayList<User> users () {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Integer> perms = new ArrayList<>();
        perms.add(0,1);
        perms.add(1,1);
        perms.add(2,1);
        perms.add(3,1);
        users.add(new User("admin", "admin",perms, 0));
        perms = new ArrayList<>();
        perms.add(0,1);
        perms.add(1,1);
        perms.add(2,0);
        perms.add(3,0);
        users.add(new User("user1", "user1pass",perms, 0));
        perms = new ArrayList<>();
        perms.add(0,0);
        perms.add(1,0);
        perms.add(2,0);
        perms.add(3,0);
        users.add(new User("user2", "pass",perms, 0));
        return users;
    }

    public static ArrayList<Billboard> billboards () {
        ArrayList<Billboard> billboards = new ArrayList<>();
        billboards.add(new Billboard("creatorName", "name", "imageUrl", "msgText", "msgColour", "bgColour", "infoText", "infoColour"));
        return billboards;
    }
}
