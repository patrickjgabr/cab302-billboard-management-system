package ControlPanel;
import Shared.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CLIMethods {
    public static String[] login(Scanner keyboard){
        String[] LoginDetails = new String[2];
        System.out.print("Username: ");
        LoginDetails[0] = keyboard.nextLine();
        System.out.print("Password: ");
        LoginDetails[0] = keyboard.nextLine();
        return LoginDetails;
    }

    public static int menu(Scanner keyboard){
        System.out.println("CLI Menu (Administrator):");
        System.out.println("    1. Billboards");
        System.out.println("    2. Scheduler");
        System.out.println("    3. User Management");
        System.out.println("    4. Exit");
        System.out.print("Option: ");
        int select = keyboard.nextInt();
        keyboard.nextLine();
        return select;

    }

    public static int billboards(Scanner keyboard){
        System.out.println("Billboards");
        System.out.println("    1. Create Billboard");
        System.out.println("    2. Import Billboard");
        System.out.println("    3. Show Billboards");
        System.out.println("    4. Back");
        System.out.print("Option: ");
        int select = keyboard.nextInt();
        keyboard.nextLine();
        return select;
    }

    public static int schedule(Scanner keyboard){
        System.out.println("Scheduler");
        System.out.println("    1. Back");
        System.out.print("Option: ");
        int select = keyboard.nextInt();
        keyboard.nextLine();
        return select;

    }

    public static int userManagement(Scanner keyboard){
        System.out.println("User Management");
        System.out.println("    1. Create User");
        System.out.println("    2. Show Users");
        System.out.println("    3. Back");
        System.out.print("Option: ");
        int select = keyboard.nextInt();
        keyboard.nextLine();
        return select;
    }

    public static void createBillboard(Scanner keyboard) {

    }
    public static void importBillboard(Scanner keyboard) throws IOException, SAXException, ParserConfigurationException {
        File file =new File("example.xml");
        BillboardMethods.importBillboard(file);
        System.out.print("press enter to continue.");
        keyboard.nextLine();
    }
    public static void showBillboard(Scanner keyboard) {

    }
    public static void createUser(Scanner keyboard) {
        System.out.println("Create new user: ");
        System.out.print("Username: ");
        String username = keyboard.nextLine();
        System.out.print("Password: ");
        String password = keyboard.nextLine();
        ArrayList<Integer> perms = new ArrayList<Integer>();
        System.out.print("Create Billboard Permission (0 = False,1 = True): ");
        perms.add(keyboard.nextInt());
        keyboard.nextLine();
        System.out.print("Edit All Billboards Permission (0 = False,1 = True): ");
        perms.add(keyboard.nextInt());
        keyboard.nextLine();
        System.out.print("Schedule Billboards Permission (0 = False,1 = True): ");
        perms.add(keyboard.nextInt());
        keyboard.nextLine();
        System.out.print("Edit Users Permission (0 = False,1 = True): ");
        perms.add(keyboard.nextInt());
        keyboard.nextLine();
        UserManagementMethods.createUser(username,password,perms);
        System.out.print("press enter to continue.");
        keyboard.nextLine();
    }
    public static void showUsers(Scanner keyboard) {

    }

}
