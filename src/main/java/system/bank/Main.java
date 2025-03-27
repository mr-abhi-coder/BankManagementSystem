package system.bank;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        while(true){
            User user = new User();
            Scanner sc = Driver.getScanner();
            String welcome = """
                    
                    WELCOME TO OUR BANK

                    (R)eigster -> SIGN UP          (L)ogin -> LOGIN                   (A)dmin -> ADMIN         (A)ny -> Exit
                    """;
            System.out.println(welcome);
            //credential to login or register
            String email;
            String password;
            String key = sc.nextLine();
            char c = key.trim().toUpperCase().charAt(0);
            switch (c){
                case 'R':
                    System.out.print("_".repeat(35));
                    System.out.print("Registering");
                    System.out.println("_".repeat(35));
                    System.out.print("Enter your Username : ");
                    String username = sc.nextLine();
                    System.out.print("Enter your email : " );
                    email = sc.nextLine();
                    System.out.print("Enter your Password : " );
                    password = sc.nextLine();
                    user.registration(username,email,password);
                    Account.AccountManager(email);
                    break;
                case 'L':
                    System.out.print("_".repeat(35));
                    System.out.print("Login to Website");
                    System.out.println("_".repeat(35));
                    System.out.print("Enter your email : " );
                    email = sc.nextLine();
                    System.out.print("Enter your Password : " );
                    password = sc.nextLine();
                    user.login(email,password);
                    break;
                case 'A':
                    System.out.println("admin");// Admin.login();
                    break;
                default:
                    System.out.println("Thank you for visiting");
                    return;
            }
        }

    }
}