package Server;

import Shared.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main_Database {

    public static void main(String[] args) {

        try {
            Properties properties = new Properties();
            Database db = new Database(properties);
            db.checkDatabase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
