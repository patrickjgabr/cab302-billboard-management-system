package ControlPanel;

import java.util.Scanner;

public class CLIControlPanel {
    public static  void main (String[] args) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Hello World from Control Panel!");
        System.out.print("Username: ");
        String username = keyboard.nextLine();
        System.out.print("Password: ");
        String password = keyboard.nextLine();
        System.out.println("CLI Menu (Administrator):");
        System.out.println("    1. Billboards");
        System.out.println("    2. Scheduler");
        System.out.println("    3. User Management");
        System.out.print("Option: ");
        int selection = keyboard.nextInt();
        if (selection == 1) {
            System.out.println("Billboards");
            System.out.println("    1. Create Billboard");
            System.out.println("    2. Import Billboard");
            System.out.println("    2. Show Billboards");
        }
        else if (selection == 2) {
            System.out.println("Scheduler");
        }
        else if (selection == 3) {
            System.out.println("User Management");
            System.out.println("    1. Create User");
            System.out.println("    2. Show Users");
        }

    }
}
