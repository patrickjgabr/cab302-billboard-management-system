package Server.Database;

import Server.Database.Database;
import Server.Server;
import Shared.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main_Database {

    public static void main(String[] args) {

        try {
            Properties properties = new Properties();

            //Database db = new Database(properties);
            //db.startConnection();
            //db.checkConfiguration();
            //db.closeConnection();

            /*
            Billboard billboard = new Billboard("WhoDunnett", "TEST", "test", "test", "test", "test", "test", "test");
            BillboardDatabase billboardDB = new BillboardDatabase(properties);
            billboardDB.addToDatabase(billboard);
            Billboard newBillboard = billboardDB.getBillboard(100001);
            System.out.println(newBillboard.getBillboardID());
            System.out.println(newBillboard.getName());
             */

            /*
            ArrayList<Integer> perms = new ArrayList<>();
            perms.add(0,1);
            perms.add(1,1);
            perms.add(2,0);
            perms.add(3,0);
            User  user = new User("user3", "user1pass",perms, 0, "test");

            UserDatabase userDB = new UserDatabase(properties);
            userDB.addToDatabase(user);

            User userselect = userDB.getUser("user2");
            System.out.println(userselect.getUserID());

             */

            BillboardDatabase billboardDB = new BillboardDatabase(properties);
            Billboard billboard = new Billboard("WhoDunnett", "Test", "TEST", "test", "test", "test", "test", "test");

            billboardDB.updateDatabase(billboard);

            //billboardDB.addToDatabase(billboard);
            //Billboard pleaseDontWork = billboardDB.getBillboard(100006);
            //System.out.println(pleaseDontWork.getBillboardID());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
