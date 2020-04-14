package Server;

import Shared.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Main_Database {

    public static void main(String[] args) {

        Billboard newBilboard = new Billboard("test", "test", "test", "test", "test", "test", "test", "test");

        BillboardDatabase billboardDB = new BillboardDatabase();
        billboardDB.addToDatabase(newBilboard);

    }
}
