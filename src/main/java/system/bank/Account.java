package system.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.Scanner;

public class Account {

    public static void AccountManager(String email) {
        Account acount = new Account();
        Scanner sc = Driver.getScanner();
        while (true) {
            String welcome2 = """
                
                (C)reate -> Create new Account          (L)ogin -> Login to old Account                        (A)ny -> Exit
                """;
            System.out.println(welcome2);
            // Credential to login or register
            String key2 = sc.nextLine();
            char c2 = key2.trim().toUpperCase().charAt(0);
            switch (c2) {
                case 'C':
                    System.out.print("_".repeat(35));
                    System.out.print("Creating New Account");
                    System.out.println("_".repeat(35));
                    acount.createAccount(email, sc);
                    break;
                case 'L':
                    System.out.print("_".repeat(35));
                    System.out.print("Login in Account");
                    System.out.println("_".repeat(35));
                    long accountNumber = acount.loginAccount(sc);
                    if(accountNumber != -1){
                        AccountTransection accountTransection = new AccountTransection();
                        accountTransection.AccountManager(accountNumber, sc);
                    }
                    break;
                default:
                    System.out.println("Logging out");
                    return; // Exit the method
            }
        }
    }

    private void createAccount(String email, Scanner sc) {
        String accountQuery = "INSERT INTO account(account_fname, account_number, pin, account_email) VALUES(?, ?, ?, ?)";
        long accountNumber;

        // Ensure the account number is unique
        do {
            Random random = new Random();
            accountNumber = 1000 + random.nextLong(9999);
        } while (!isUniqueAccountNumber(accountNumber)); // Inverted the check here

        try (Connection connection = Driver.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(accountQuery)) {

            System.out.print("Enter your Full Name: ");
            String fullName = sc.nextLine();

            int pin = 0;
            boolean validPin = false;
            while (!validPin) {
                System.out.print("Enter your pin: ");
                if (sc.hasNextInt()) {
                    pin = sc.nextInt();
                    sc.nextLine(); // Consume the leftover newline character
                    validPin = true;
                } else {
                    System.out.println("Invalid pin! Please enter a numeric pin.");
                    sc.next(); // Consume the invalid input
                }
            }

            preparedStatement.setString(1, fullName);
            preparedStatement.setLong(2, accountNumber);
            preparedStatement.setInt(3, pin);
            preparedStatement.setString(4, email);

            int rowAffected = preparedStatement.executeUpdate();
            if (rowAffected > 0) {
                System.out.println("Account created successfully!");
                System.out.println("Your account number: " + accountNumber);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while creating the account: " + e.getMessage(), e);
        }
    }

    // To check if account number is unique
    private boolean isUniqueAccountNumber(long accountNumber) {
        String findAccount = "SELECT COUNT(*) FROM account WHERE account_number = ?";
        try (Connection connection = Driver.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findAccount)) {

            preparedStatement.setLong(1, accountNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // If the count is 0, the account number is unique
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while checking if account number is unique: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean isLoginAccount(long accountNumber, int pin){
        String isTrue = "SELECT COUNT(*) FROM account WHERE account_number = ? and pin = ?";
        try (Connection connection = Driver.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(isTrue)) {

            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setInt(2, pin);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next() && resultSet.getInt(1) > 0){
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while checking if account number is unique: " + e.getMessage(), e);
        }
        return false;
    }

    private long loginAccount(Scanner sc) {
        long accountNumber = -1;
        int pin = -1;
        String confirm = "y";

        while (!isLoginAccount(accountNumber, pin)) {
            System.out.print("ENTER \"Y\" to login and \"N\" to exit : ");
            confirm = sc.nextLine().trim();
            if (confirm.equalsIgnoreCase("n")) {
                break;
            }

            System.out.print("Enter your account number: ");
            if (sc.hasNextLong()) {
                accountNumber = sc.nextLong();
            } else {
                System.out.println("Invalid input for account number. Please enter a valid number.");
                sc.nextLine();
                continue;
            }

            System.out.print("Enter your pin: ");
            if (sc.hasNextInt()) {
                pin = sc.nextInt();
            } else {
                System.out.println("Invalid input for pin. Please enter a numeric pin.");
                continue;
            }

            sc.nextLine();

            if (!isLoginAccount(accountNumber, pin)) {
                System.out.println("Wrong credentials, try again or enter 'n' to exit.");
            }
        }
        if(isLoginAccount(accountNumber, pin)){
            System.out.println("Login successful!");
            return accountNumber;
        }
        System.out.println("Login Unsuccessful");
        return -1;
    }

}
