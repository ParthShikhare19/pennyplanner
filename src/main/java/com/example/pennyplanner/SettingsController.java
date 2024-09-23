package com.example.pennyplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SettingsController {

    @FXML
    private Label nameField;

    @FXML
    private Label usernameField;

    @FXML
    private Label emailField;

    @FXML
    private Label passwordField;

    @FXML
    private RadioButton adminRadio;

    @FXML
    private RadioButton memberRadio;

    private Connection connection;
    private static String username=LoginController.getUserName();
    // Database connection
    public void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/pennyplannerdb";
            String user = "root";
            String password = "oracle";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetch user data from the database and display it
    @FXML
    private void loadUserData() {
        connectToDatabase();
        String query = "SELECT name, User_Name, User_Email, User_Pass FROM user_info WHERE User_ID = ?";
        try {
            int userId = UserSession.getUserId();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,userId); // Replace with dynamic user ID logic
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                nameField.setText(resultSet.getString("name"));
                usernameField.setText(resultSet.getString("User_Name"));
                emailField.setText(resultSet.getString("User_Email"));
                passwordField.setText(resultSet.getString("User_Pass"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Initialize method to load user data on screen load
    @FXML
    public void initialize() {
        loadUserData();
    }

    // Button action for switching to the Dashboard
    @FXML
    private void SettingsToDash(ActionEvent event) {
        // Code to switch to dashboard scene
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

    // Button action for switching to Add Expenses
    @FXML
    private void SettingsToAddexpenses(ActionEvent event) {
        System.out.println("Navigating to Add Expenses");
        // Code to switch to Add Expenses scene
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Addexpense1.fxml"));
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

    // Button action for switching to Analysis
    @FXML
    private void SettingsToAnalysis(ActionEvent event) {
        System.out.println("Navigating to Analysis");
        // Code to switch to Analysis scene
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

    // Button action for switching to Goals
    @FXML
    private void SettingsToGoals(ActionEvent event) {
        System.out.println("Navigating to Goals");
        // Code to switch to Goals scene
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

    // Button action for editing the Name field
    @FXML
    private void editName(ActionEvent event) {
        System.out.println("Editing Name");
        // Add logic to handle name editing
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings 2.fxml"));
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

    // Button action for editing the Username field
    @FXML
    private void editUsername(ActionEvent event) {
        System.out.println("Editing Username");
        // Add logic to handle username editing
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings3.fxml"));
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

    // Button action for editing the Password field
    @FXML
    private void editPassword(ActionEvent event) {
        System.out.println("Editing Password");
        // Add logic to handle password editing
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings4.fxml"));
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

    // Button action for editing the Email field
    @FXML
    private void editEmail(ActionEvent event) {
        System.out.println("Editing Email");
        // Add logic to handle email editing
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings5.fxml"));
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

    // Button action for logging out
    @FXML
    private void LogoutBtn(ActionEvent event) {
        System.out.println("Logging out");
        // Add logic to handle logout
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

    // Button action for confirming account deletion
  @FXML
  private void confirmDeleteAccount() {
      String username = usernameField.getText();
      String url = "jdbc:mysql://localhost:3306/pennyplannerdb";
      String user = "root";
      String password = "oracle";

      if (username.isEmpty()) {
          showErrorMessage("Error", "Username field is empty.");
          return;
      }

      String query = "DELETE FROM user_info WHERE User_ID = ?";

      try  {
          int userId = UserSession.getUserId();
          PreparedStatement preparedStatement = connection.prepareStatement(query);
          preparedStatement.setInt(1,userId); // Replace with dynamic user ID logic
          ResultSet resultSet = preparedStatement.executeQuery();
          preparedStatement.setString(1, String.valueOf(userId));

          int rowsAffected = preparedStatement.executeUpdate();
          if (rowsAffected > 0) {
              showSuccessMessage("Success", "Your account has been deleted.");
              // Optionally, log the user out or redirect to the login page after deletion
          } else {
              showErrorMessage("Error", "Account not found or could not be deleted.");
          }

      } catch (SQLException e) {
          showErrorMessage("Database Error", "An error occurred while deleting your account: " + e.getMessage());
      }
  }

    // Handle role change between Admin and Member
    @FXML
    private void handleRoleChange(ActionEvent event) {
        if (adminRadio.isSelected()) {
            System.out.println("Role changed to Admin");
        } else if (memberRadio.isSelected()) {
            System.out.println("Role changed to Member");
        }
        // Add logic to update role in the database
    }
    public class UserSession {
        private static int userId;

        public static void setUserId(int id) {
            userId = id;
        }

        public static int getUserId() {

            int userId = -1; // Default value if user is not found
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                // Establish database connection
                String url = "jdbc:mysql://localhost:3306/pennyplannerdb";
                String user = "root";
                String password = "oracle";
                connection = DriverManager.getConnection(url, user, password);

                // Prepare the SQL query
                String query = "SELECT User_ID FROM user_info WHERE User_Name = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username); // Set the username parameter

                // Execute the query
                resultSet = preparedStatement.executeQuery();

                // Check if a result was returned
                if (resultSet.next()) {
                    userId = resultSet.getInt("User_ID"); // Fetch the User_ID
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close resources
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return userId; // Return the user ID (or -1 if not found)

        }
    }
    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showSuccessMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
