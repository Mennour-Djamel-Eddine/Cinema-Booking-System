package controller;

import dao.UserDAO;
import model.User;
import model.UserRole;
import java.sql.SQLException;

public class AuthController {
    private final UserDAO userDAO;

    public AuthController() {
        try {
            this.userDAO = new UserDAO();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public User login(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return null;
        }

        try {
            User user = userDAO.getUserByEmail(email.trim());
            if (user != null && password.equals(user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Login error for email: " + email + " - " + e.getMessage());
            return null;
        }
    }

    public boolean register(String name, String email, String password) {
        if (name == null || email == null || password == null ||
                name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return false;
        }

        try {
            if (userDAO.getUserByEmail(email) != null) {
                return false; // Email already exists
            }

            User newUser = new User(0, name.trim(), email.trim(), password, UserRole.User);
            return userDAO.addUser(newUser);
        } catch (Exception e) {
            System.err.println("Registration error for email: " + email + " - " + e.getMessage());
            return false;
        }
    }

    public void close() {
        if (userDAO != null) {
            userDAO.close();
        }
    }
}
