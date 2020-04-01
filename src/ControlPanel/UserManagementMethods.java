package ControlPanel;

import Shared.User;

import java.util.ArrayList;

public class UserManagementMethods {
    public static void createUser(String username, String password, ArrayList<Integer> perms) {
        User newuser = new User(username, password, perms);
        System.out.println("Username: " + newuser.getUserName());
        System.out.println("Password: " + newuser.getUserPassword());
        System.out.println("Create Billboard Permission: "+ newuser.getPermission().get(0));
        System.out.println("Edit All Billboards Permission: " + newuser.getPermission().get(1));
        System.out.println("Schedule Billboards Permission: " + newuser.getPermission().get(2));
        System.out.println("Edit Users Permission: "+ newuser.getPermission().get(3));
    }
    public static void showUsers(ArrayList<User> users) {
        for (User user : users){
            System.out.println(user.getUserName()+ " " + user.getUserPassword() + " " + user.getPermission());
        }
    }
}
