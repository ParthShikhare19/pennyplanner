package com.example.pennyplanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javafx.geometry.Insets;


public class Goal1Controller {

    public class Goal {
        private int id;
        private String description;
        private double amount;
        private String fromDate;
        private String toDate;

        public Goal(String description, double amount, String fromDate, String toDate) {
            this.description = description;
            this.amount = amount;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.id = id;
        }



        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }
    }

    @FXML
    private TableView<Goal> goalTable;

    @FXML
    private TableColumn<Goal, String> descriptionColumn;

    @FXML
    private TableColumn<Goal, Double> amountColumn;

    @FXML
    private TableColumn<Goal, String> fromDateColumn;

    @FXML
    private TableColumn<Goal, String> toDateColumn;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    private ObservableList<Goal> goalList = FXCollections.observableArrayList();
    private Goal selectedGoal;
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

    @FXML
    public void initialize() {
        showUnameAndUmail();
        // Hide update and delete buttons initially
        updateButton.setVisible(false);
        deleteButton.setVisible(false);

        // Set up columns in the table
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        fromDateColumn.setCellValueFactory(new PropertyValueFactory<>("fromDate"));
        toDateColumn.setCellValueFactory(new PropertyValueFactory<>("toDate"));

        // Load data from database
        loadGoalsFromDatabase();

        // Add listener to detect table row selection
        goalTable.setOnMouseClicked((MouseEvent event) -> {
            if (goalTable.getSelectionModel().getSelectedItem() != null) {
                selectedGoal = goalTable.getSelectionModel().getSelectedItem();
                updateButton.setVisible(true);
                deleteButton.setVisible(true);
            }
        });
    }

    private void loadGoalsFromDatabase() {
        String dbURL = "jdbc:mysql://localhost:3306/pennyplannerdb";
        String username = "root";
        String password = "oracle";

        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            String query = "SELECT Goal_desc , Goal_amt ,Date_from , Date_to  FROM goal_info WHERE User_ID = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            int userId = SettingsController.UserSession.getUserId();
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String description = resultSet.getString("Goal_desc");
                double amount = resultSet.getDouble("Goal_amt");
                String fromDate = resultSet.getString("Date_from");
                String toDate = resultSet.getString("Date_to");

                 goalList.add(new Goal(description, amount, fromDate, toDate));
            }

            goalTable.setItems(goalList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) throws SQLException {

        String dbURL = "jdbc:mysql://localhost:3306/pennyplannerdb";
        String username = "root";
        String password = "oracle";
        Connection conn = DriverManager.getConnection(dbURL, username, password);

        // Get the selected goals
        List<Goal> selectedGoals = goalTable.getSelectionModel().getSelectedItems();

        if (selectedGoals.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Goal Selected");
            alert.setContentText("Please select a goal to delete.");
            alert.showAndWait();
            return; // Exit if no goal is selected
        }

        // Loop through selected goals and delete each one
        String deleteQuery = "DELETE FROM goal_info WHERE Goal_ID = ?"; // Adjust to your actual column name

        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            for (Goal selectedGoal : selectedGoals) {
                pstmt.setInt(1, selectedGoal.getId()); // Assuming goal_id is the unique identifier
                pstmt.executeUpdate(); // Execute delete for each selected goal
                goalList.remove(selectedGoal); // Remove from observable list
            }
            goalTable.refresh(); // Refresh the TableView
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error Deleting Goal");
            alert.setContentText("An error occurred while trying to delete the goal(s).");
            alert.showAndWait();
        }
    }




    @FXML
    private void handleUpdate() {
        // Get the selected goal
        Goal selectedGoal = goalTable.getSelectionModel().getSelectedItem();

        if (selectedGoal == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Goal Selected");
            alert.setContentText("Please select a goal to update.");
            alert.showAndWait();
            return;
        }

        // Create a dialog to update the goal
        Stage dialog = new Stage();
        dialog.setTitle("Update Goal");

        VBox dialogVBox = new VBox();
        dialogVBox.setSpacing(10);
        dialogVBox.setPadding(new Insets(10));

        // TextField for description
        TextField descriptionField = new TextField(selectedGoal.getDescription());

        // TextField for amount
        TextField amountField = new TextField(String.valueOf(selectedGoal.getAmount()));

        // DatePicker for fromDate
        DatePicker fromDatePicker = new DatePicker(LocalDate.parse(selectedGoal.getFromDate()));

        // DatePicker for toDate
        DatePicker toDatePicker = new DatePicker(LocalDate.parse(selectedGoal.getToDate()));

        dialogVBox.getChildren().addAll(
                new Label("Description:"), descriptionField,
                new Label("Amount:"), amountField,
                new Label("From Date:"), fromDatePicker,
                new Label("To Date:"), toDatePicker
        );

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            // Validate the amount field
            String amountText = amountField.getText();
            if (!isValidAmount(amountText)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid Amount");
                alert.setContentText("Please enter a valid positive number for the amount.");
                alert.showAndWait();
                return;
            }

            // Update the goal data
            selectedGoal.setDescription(descriptionField.getText());
            selectedGoal.setAmount(Double.parseDouble(amountText));
            selectedGoal.setFromDate(fromDatePicker.getValue().toString());
            selectedGoal.setToDate(toDatePicker.getValue().toString());

            // Refresh the table view
            goalTable.refresh();

            // Close the dialog
            dialog.close();

            // Update the database with new values
            try {
                updateGoalInDatabase(selectedGoal);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialogVBox.getChildren().add(applyButton);

        Scene dialogScene = new Scene(dialogVBox, 300, 300);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    // Validation method for the amount
    private boolean isValidAmount(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false; // If the input is not a valid number, return false
        }
    }
    private void updateGoalInDatabase(Goal goal) throws SQLException {
        String dbURL = "jdbc:mysql://localhost:3306/pennyplannerdb";
        String username = "root";
        String password = "oracle";
        Connection conn = DriverManager.getConnection(dbURL, username, password);

        String updateQuery = "UPDATE goal_info SET Goal_desc = ?, Goal_amt = ?, Date_from = ?, Date_to = ? WHERE Goal_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            // Set the parameters for the prepared statement
            pstmt.setString(1, goal.getDescription());
            pstmt.setDouble(2, goal.getAmount());
            pstmt.setString(3, goal.getFromDate());
            pstmt.setString(4, goal.getToDate());
            pstmt.setInt(5, goal.getId());

            // Execute the update query
            pstmt.executeUpdate();

            // Show confirmation message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Goal Updated");
            alert.setContentText("The goal has been successfully updated.");
            alert.showAndWait();

        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error Updating Goal");
            alert.setContentText("There was an error updating the goal. Please try again.");
            alert.showAndWait();
        }
    }



    @FXML
    private void GoalToDash(ActionEvent event) {
        navigateTo(event, "Dashboard.fxml");
    }

    @FXML
    private void GoalToAddExpenses(ActionEvent event) {
        navigateTo(event, "Addexpense1.fxml");
    }

    @FXML
    private void GoalToAnalysis(ActionEvent event) {
        navigateTo(event, "Analysis_Slide.fxml");
    }

    @FXML
    private void GoalToSettings(ActionEvent event) {
        navigateTo(event, "settings1.fxml");
    }

    @FXML
    private void AddGoalBtn(ActionEvent event) {
        navigateTo(event, "goal2.fxml");
    }
    @FXML
    private void Goal1toAboutUs(ActionEvent event) {
        navigateTo(event, "AboutUs.fxml");
    }

    private void navigateTo(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}