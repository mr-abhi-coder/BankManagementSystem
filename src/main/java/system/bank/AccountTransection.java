package system.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountTransection {

    public void AccountManager(long accountNumber, Scanner sc) {
        while (true) {
            String welcome2 = """

                (D)eposit -> Add balance
                (W)ithdraw -> Withdraw amount
                (T)ransfer -> Send money
                (C)heck -> Check balance
                (A)ny -> Exit""";
            System.out.println(welcome2);

            String key2 = sc.nextLine();
            char c2 = key2.trim().toUpperCase().charAt(0);
            switch (c2) {
                case 'D':
                    deposit(accountNumber, sc);
                    break;
                case 'W':
                    withdraw(accountNumber, sc);
                    break;
                case 'T':
                    System.out.println("coming soon");
                    //transfer(accountNumber, sc);
                    break;
                case 'C':
                    checkBalance(accountNumber);
                    break;
                default:
                    System.out.println("Logging out");
                    return; // Exit the method
            }
        }
    }

    private boolean transactionValid(long accountNumber, int pin) {
        String query = "SELECT COUNT(*) FROM account WHERE account_number = ? AND pin = ?";
        try (Connection connection = Driver.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setInt(2, pin);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while validating transaction: " + e.getMessage(), e);
        }
        return false;
    }

    public void deposit(long accountNumber, Scanner sc) {
        System.out.print("Enter amount to deposit: ");
        int amount = 0;

        while (true) {
            if (sc.hasNextInt()) {
                amount = sc.nextInt();
                sc.nextLine(); // Consume the remaining newline
                if (amount > 0) break;
                else System.out.println("Please enter a positive amount.");
            } else {
                System.out.println("Invalid input! Please enter a numeric value.");
                sc.next(); // Consume invalid input
            }
        }

        int pin = getValidPin(sc);

        String depositQuery = "UPDATE account SET balance = balance + ? WHERE account_number = ?";
        if (transactionValid(accountNumber, pin)) {
            try (Connection connection = Driver.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(depositQuery)) {
                preparedStatement.setInt(1, amount);
                preparedStatement.setLong(2, accountNumber);

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    System.out.println("Amount credited successfully!");
                } else {
                    System.out.println("Transaction failed. Please try again.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error while processing deposit: " + e.getMessage(), e);
            }
        } else {
            System.out.println("Invalid credentials. Transaction aborted.");
        }
    }

    public void withdraw(long accountNumber, Scanner sc) {
        System.out.print("Enter amount to withdraw: ");
        int amount = 0;

        while (true) {
            if (sc.hasNextInt()) {
                amount = sc.nextInt();
                sc.nextLine(); // Consume the remaining newline
                if (amount > 0) break;
                else System.out.println("Please enter a positive amount.");
            } else {
                System.out.println("Invalid input! Please enter a numeric value.");
                sc.next(); // Consume invalid input
            }
        }

        int pin = getValidPin(sc);

        String withdrawQuery = "UPDATE account SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
        if (transactionValid(accountNumber, pin)) {
            try (Connection connection = Driver.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(withdrawQuery)) {
                preparedStatement.setInt(1, amount);
                preparedStatement.setLong(2, accountNumber);
                preparedStatement.setInt(3, amount);

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    System.out.println("Amount withdrawn successfully!");
                } else {
                    System.out.println("Insufficient balance or transaction failed.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error while processing withdrawal: " + e.getMessage(), e);
            }
        } else {
            System.out.println("Invalid credentials. Transaction aborted.");
        }
    }

//    public void transfer(long accountNumber, Scanner sc) {
//        System.out.print("Enter recipient's account number: ");
//        long recipientAccount = sc.nextLong();
//        sc.nextLine(); // Consume the newline
//
//        System.out.print("Enter amount to transfer: ");
//        int amount = 0;
//
//        while (true) {
//            if (sc.hasNextInt()) {
//                amount = sc.nextInt();
//                sc.nextLine(); // Consume the remaining newline
//                if (amount > 0) break;
//                else System.out.println("Please enter a positive amount.");
//            } else {
//                System.out.println("Invalid input! Please enter a numeric value.");
//                sc.next(); // Consume invalid input
//            }
//        }
//
//        int pin = getValidPin(sc);
//
//        String transferQuery = "UPDATE account SET balance = balance - ? WHERE account_number =?;"
//                + "UPDATE account SET balance = balance + ? WHERE account_number = ?";
//        if (transactionValid(accountNumber, pin)) {
//            try (Connection connection = Driver.getConnection();
//                 PreparedStatement preparedStatement = connection.prepareStatement(transferQuery)) {
//                connection.setAutoCommit(false);
//
//                preparedStatement.setInt(1, amount);
//                preparedStatement.setLong(2, accountNumber);
//                //preparedStatement.setInt(3, amount);
//                preparedStatement.addBatch();
//
//                preparedStatement.setInt(1, amount);
//                preparedStatement.setLong(2, recipientAccount);
//                preparedStatement.addBatch();
//
//                int[] result = preparedStatement.executeBatch();
//                connection.commit();
//
//                if (result.length == 2 && result[0] > 0 && result[1] > 0) {
//                    System.out.println("Amount transferred successfully!");
//                } else {
//                    System.out.println("Transaction failed.");
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException("Error while processing transfer: " + e.getMessage(), e);
//            }
//        } else {
//            System.out.println("Invalid credentials. Transaction aborted.");
//        }
//    }

    public void checkBalance(long accountNumber) {
        String checkQuery = "SELECT balance FROM account WHERE account_number = ?";
        try (Connection connection = Driver.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(checkQuery)) {
            preparedStatement.setLong(1, accountNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Your current balance is: " + resultSet.getDouble("balance"));
                } else {
                    System.out.println("Account not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while checking balance: " + e.getMessage(), e);
        }
    }

    private int getValidPin(Scanner sc) {
        int pin = -1;
        boolean validPin = false;
        while (!validPin) {
            System.out.print("Enter your pin: ");
            if (sc.hasNextInt()) {
                pin = sc.nextInt();
                sc.nextLine(); // Consume the remaining newline
                validPin = true;
            } else {
                System.out.println("Invalid pin! Please enter a numeric pin.");
                sc.next();
            }
        }
        return pin;
    }
}
