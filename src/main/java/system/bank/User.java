package system.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User {
    private String username;
    private String email;
    private String password;

    //check for if user already exits
    private boolean isUser(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE user_email = ?";
        try (Connection connection = Driver.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email); // Set the email parameter

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while checking if user exists: " + e.getMessage(), e);
        }
        return false;
    }

    //registration
    public void registration(String username,String email, String password){
        if(isUser(email)){
            System.out.println("This email already exits");
            return;
        }
        String query = "INSERT INTO user (username, user_email, user_password)Values(?,?,?)";
        try (Connection connection = Driver.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Account created successfully");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while checking if user exists: " + e.getMessage(), e);
        }
    }
    //check if user provide correct credentials
    private boolean isLogin(String email, String password){
        String query = "SELECT COUNT(*) FROM user WHERE user_email = ? and user_password = ?";
        try (Connection connection = Driver.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while checking if user exists: " + e.getMessage(), e);
        }
        return false;
    }

    public void login(String email, String password){
        if(isLogin(email,password)){
            Account.AccountManager(email);
        }
        System.out.println("Wrong credential");
    }
}
