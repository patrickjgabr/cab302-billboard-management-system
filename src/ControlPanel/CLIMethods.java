package ControlPanel;

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
        return keyboard.nextInt();
    }

    public static int billboards(Scanner keyboard){
        System.out.println("Billboards");
        System.out.println("    1. Create Billboard");
        System.out.println("    2. Import Billboard");
        System.out.println("    3. Show Billboards");
        System.out.println("    4. Back");
        System.out.print("Option: ");
        return keyboard.nextInt();
    }

    public static int schedule(Scanner keyboard){
        System.out.println("Scheduler");
        System.out.println("    1. Back");
        System.out.print("Option: ");
        return keyboard.nextInt();

    }

    public static int userManagement(Scanner keyboard){
        System.out.println("User Management");
        System.out.println("    1. Create User");
        System.out.println("    2. Show Users");
        System.out.println("    3. Back");
        System.out.print("Option: ");
        return keyboard.nextInt();
    }
}
