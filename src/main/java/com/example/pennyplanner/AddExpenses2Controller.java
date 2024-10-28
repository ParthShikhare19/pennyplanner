package com.example.pennyplanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class AddExpenses2Controller {

    @FXML
    private TextField descriptionField;
    @FXML
    private TextField amountField;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private ChoiceBox<String> TranTypeChoiceBox;
    @FXML
    private ChoiceBox<String> PayMethChoiceBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label Uemail;

    @FXML
    private Label Uname;
    public void showUnameAndUmail()
    {
        connectToDatabase();
        try {
            // Assuming userId is passed when the user logs in
            int userId = SettingsController.UserSession.getUserId();
            String query = "SELECT User_Name,User_Email FROM user_info WHERE User_ID = ?";
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pennyplannerdb", "root", "oracle");
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Uname.setText(resultSet.getString("User_Name"));
                Uemail.setText(resultSet.getString("User_Email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void connectToDatabase() {
        try {
            // Replace with your own database connection details
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pennyplannerdb", "root", "oracle");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // MySQL Database URL and credentials
    private final String url = "jdbc:mysql://localhost:3306/pennyplannerdb";
    private final String user = "root";
    private final String password = "oracle";
    private String time;

    @FXML
    private void AddButton() {

        String description = descriptionField.getText();
        String amount = amountField.getText();
        String category = choiceBox.getValue();
        String transactionType = TranTypeChoiceBox.getValue();
        String paymentMethod = PayMethChoiceBox.getValue();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : null;


        if (description.isEmpty() || amount.isEmpty() || (category == null) || transactionType.isEmpty() ||
                paymentMethod.isEmpty() || (date == null)) {
            // Show error if any field is empty
            showAlert("Error", "All fields must be filled out.", Alert.AlertType.ERROR);
        }
        if (!amount.matches("\\d+(\\.\\d{1,2})?")) {
            showAlert("Invalid Amount", "Please enter a valid number for the amount.", Alert.AlertType.ERROR);
            return;
        }
        else {
            // Insert into the database
            double amountValue = Double.parseDouble(amount);
            insertExpense(description, Double.parseDouble(amount), category, transactionType, paymentMethod, date,null);
        }
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
                preparedStatement.setString(1, LoginController.getUserName()); // Set the username parameter

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

    private void insertExpense(String description, double amount, String category, String transactionType, String paymentMethod, String date, String time) {
        String query = "INSERT INTO transactions (description, Tran_amt, category, Tran_type, payment_name, date,User_ID,Home_Id ) VALUES (?, ?, ?, ?, ?, ?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int userId = SettingsController.UserSession.getUserId();
            int homeId=getHomeId();

            preparedStatement.setString(1, description);
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, category);
            preparedStatement.setString(4, transactionType);
            preparedStatement.setString(5, paymentMethod);
            preparedStatement.setString(6, date);
            preparedStatement.setInt(7,userId);
            preparedStatement.setInt(8,homeId);


            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Add expense", "Added successfully !");
                clearFields();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add expense. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        descriptionField.clear();
        amountField.clear();
        TranTypeChoiceBox.setValue(null);
        PayMethChoiceBox.setValue(null);
        datePicker.setValue(null);
        choiceBox.setValue(null);
    }


    @FXML
    private void BackDash() {
        // Implement the logic to go back to the dashboard
    }


    @FXML
    private void BackDash(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Addexpense1.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene to the dashboard
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // List of options for the ChoiceBox
    private String[] choices = { "Education", "Food", "Health", "Travelling","Bills","Utilities","Entertainment","Others" };
    private String[] choice1 = {"Income" , "Expense"};
    private String[] choice2 = {"Cash" , "UPI" , "cheque","Card" };

    @FXML
    public void initialize() {
        showUnameAndUmail();
        // Add items to the ChoiceBox
        choiceBox.getItems().addAll(choices);
        TranTypeChoiceBox.getItems().addAll(choice1);
        PayMethChoiceBox.getItems().addAll(choice2);

        // Set default value
        choiceBox.setValue(choices[7]);
        TranTypeChoiceBox.setValue(null);
        PayMethChoiceBox.setValue(null);
    }

    // Event handler for the submit button
    @FXML
    private void handleSubmit() {
        String selectedChoice = choiceBox.getValue();
        showAlert(Alert.AlertType.INFORMATION, "Selected Choice", "You selected: " + selectedChoice);
        String SelectedChoice =  TranTypeChoiceBox.getValue();
        showAlert(Alert.AlertType.INFORMATION, "Selected Choice", "You selected: " + SelectedChoice);
        String Selectedchoice =  PayMethChoiceBox.getValue();
        showAlert(Alert.AlertType.INFORMATION, "Selected Choice", "You selected: " + Selectedchoice);
    }

    // Method to show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private int getHomeId() {
        int homeId = -1; // Default value if not found
        String query = "SELECT home_ID FROM user_info WHERE User_Name = ?"; // Adjust table name and column name as necessary

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            String username = LoginController.getUserName(); // Get the username from the LoginController

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                homeId = resultSet.getInt("home_ID"); // Retrieve the home_ID
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return homeId; // Return the retrieved homeId or -1 if not found
    }
    @FXML
    private void AddExpense2toAboutUs(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutUs.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene to the dashboard
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

