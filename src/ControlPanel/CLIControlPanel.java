package ControlPanel;

import Shared.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Scanner;

public class CLIControlPanel {
    public static  void main (String[] args) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("Hello World from Control Panel!");
        Scanner keyboard = new Scanner(System.in);
        String[] LoginDetails = CLIMethods.login(keyboard);
        while (true) {
            int selection = CLIMethods.menu(keyboard);
            if (selection == 1) {
                int option = CLIMethods.billboards(keyboard);
                if (option == 1) {
                    CLIMethods.createBillboard(keyboard);
                }
                else if (option == 2) {
                    CLIMethods.importBillboard(keyboard);
                }
                else if (option == 3) {
                    CLIMethods.showBillboard(keyboard);
                }
                else if (option == 4) {
                  continue;
                }
            }
            else if (selection == 2) {
                int option = CLIMethods.schedule(keyboard);
                if (option == 1) {
                    continue;
                }
            }
            else if (selection == 3) {
                int option = CLIMethods.userManagement(keyboard);
                if (option == 1) {
                    CLIMethods.createUser(keyboard);
                }
                else if (option == 2) {
                    CLIMethods.showUsers(keyboard);
                }
                else if (option == 3) {
                    continue;
                }
            }
            if (selection == 4) {
                System.exit(0);
            }
        }


    }
}
