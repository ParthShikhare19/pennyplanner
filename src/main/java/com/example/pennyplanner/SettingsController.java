package com.example.pennyplanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class SettingsController {

    @FXML
    private Label nameField, usernameField, passwordField, emailField, homeIDField;

    @FXML
    private RadioButton adminRadio, memberRadio;




    // Load user data from the database
    public void initialize() {

        Database();
    }
    public class Database {
        // Database connection details
        private static final String URL = "jdbc:mysql://localhost:3306/pennyplannerdb"; // Update with your database URL
        private static final String USER = "root"; // Update with your database username
        private static final String PASSWORD = "oracle"; // Update with your database password

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
            loadUserData();
        }

    private void loadUserData() {
        // Database connection and loading logic (mocked for example)
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT name, User_Name, User_Pass, User_Email FROM user_info WHERE User_ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, UserSession.getCurrentUserId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                nameField.setText(resultSet.getString("name"));
                usernameField.setText(resultSet.getString("username"));
                passwordField.setText(resultSet.getString("password"));
                emailField.setText(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editName() {
        navigateToEditPage("settings 2.fxml");
    }

    @FXML
    private void editUsername() {
        navigateToEditPage("settings3.fxml");
    }

    @FXML
    private void editPassword() {
        navigateToEditPage("settings4.fxml");
    }

    @FXML
    private void editEmail() {
        navigateToEditPage("settings5.fxml");
    }

    @FXML
    private void confirmDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete your account?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAccount();
        }
    }

    private void deleteAccount() {
        // Database deletion logic
        try (Connection connection = Database.getConnection()) {
            String query = "DELETE FROM user_info WHERE User_ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, UserSession.getCurrentUserId());
            statement.executeUpdate();
            // Handle successful deletion (e.g., redirect to login screen)
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRoleChange() {
        if (adminRadio.isSelected()) {
            int homeID = (int) (Math.random() * 1000000); // Random 6-digit number
            homeIDField.setText(String.valueOf(homeID));
            saveHomeIDToDatabase(homeID);
        } else if (memberRadio.isSelected()) {
            String homeID = homeIDField.getText();
            if (!homeID.isEmpty()) {
                saveHomeIDToDatabase(homeID);
            }
        }
    }

    private void saveHomeIDToDatabase(int homeID) {
        // Save home ID logic
        try (Connection connection = Database.getConnection()) {
            String query = "UPDATE user_info SET home_ID = ? WHERE User_ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, homeID);
            statement.setInt(2, UserSession.getCurrentUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveHomeIDToDatabase(String homeID) {
        // Save home ID logic
        try (Connection connection = Database.getConnection()) {
            String query = "UPDATE user_info SET home_ID = ? WHERE User_ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, homeID);
            statement.setInt(2, UserSession.getCurrentUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateToEditPage(String fxmlFile) {
        // Logic to switch to the specified FXML page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class UserSession {
        // Static method to simulate getting the current user's ID
        public static int getCurrentUserId() {
            // Replace with actual logic to retrieve the current user's ID
            return 1; // Example user ID
        }
    }

    @FXML
    private void SettingsToDash(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the scene to the dashboard
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void SettingsToAddexpenses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddExpenses.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void SettingsToAnalysis(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Analysis_Slide.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the scene to the dashboard
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void SettingsToGoals(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("goal 1.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the scene to the dashboard
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void LogoutBtn(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the scene to the dashboard
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    }
}