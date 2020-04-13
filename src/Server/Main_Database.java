package Server;

import Shared.*;

public class Main_Database {

    public static void main(String[] args) {
 //       UserDatabase userDatabase = new UserDatabase();
 //       User[] getUserValue = userDatabase.getUsers();

 //       for (User user: getUserValue) {
 //           System.out.println(user.getUserID());
 //       }

        BillboardDatabase billboardDatabase = new BillboardDatabase();

        Billboard getValue = billboardDatabase.getBillboard(true, "100001");
        System.out.println(getValue.generateXML());

        Billboard[] getValues = billboardDatabase.getBillboards();

        for (Billboard billboard: getValues) {
            System.out.println(billboard.generateXML());
        }

    }
}
