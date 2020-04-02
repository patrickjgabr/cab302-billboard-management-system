package ControlPanel;
import Shared.*;
import java.util.ArrayList;

public class TestCase {
    public static ArrayList<User> Users () {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Integer> perms = new ArrayList<>();
        perms.add(0,1);
        perms.add(1,1);
        perms.add(2,1);
        perms.add(3,1);
        users.add(new User("admin", "admin",perms));
        perms = new ArrayList<>();
        perms.add(0,1);
        perms.add(1,1);
        perms.add(2,0);
        perms.add(3,0);
        users.add(new User("user1", "user1pass",perms));
        perms = new ArrayList<>();
        perms.add(0,0);
        perms.add(1,0);
        perms.add(2,0);
        perms.add(3,0);
        users.add(new User("user2", "pass",perms));
        return users;
    }
}
