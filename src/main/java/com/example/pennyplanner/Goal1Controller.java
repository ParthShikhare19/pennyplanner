package com.example.pennyplanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Goal1Controller {


    //model class start

    public class Goal {
        private String description;
        private double amount;
        private String fromDate;
        private String toDate;

        public Goal(String description, double amount, String fromDate, String toDate) {
            this.description = description;
            this.amount = amount;
            this.fromDate = fromDate;
            this.toDate = toDate;
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

        //model class ends


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
        public void initialize() {
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
                String query = "SELECT Goal_desc , Goal_amt , Date_from , Date_to FROM goals";
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String description = resultSet.getString("description");
                    double amount = resultSet.getDouble("amount");
                    String fromDate = resultSet.getString("from_date");
                    String toDate = resultSet.getString("to_date");

                    goalList.add(new Goal(description, amount, fromDate, toDate));
                }

                goalTable.setItems(goalList);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void handleDelete() {
            if (selectedGoal != null) {
                deleteGoalFromDatabase(selectedGoal);
                goalList.remove(selectedGoal);
                updateButton.setVisible(false);
                deleteButton.setVisible(false);
            }
        }

        private void deleteGoalFromDatabase(Goal goal) {
            String dbURL = "jdbc:mysql://localhost:3306/pennyplannerdb";
            String username = "root";
            String password = "oracle";

            try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
                String query = "DELETE FROM goals WHERE Goal_ID = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, goal.getDescription());
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void handleUpdate() {
            if (selectedGoal != null) {
                // Logic to go to update page and pass selectedGoal
                // For example: pass this goal to a new update page controller
            }
        }
    }
    @FXML
    private void GoalToDash(ActionEvent event) {
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
    private void GoalToAddExpenses(ActionEvent event) {
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
    private void GoalToAnalysis(ActionEvent event) {
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
    private void GoalToSettings(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings1.fxml"));
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
    private void AddGoalBtn(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("goal2.fxml"));
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
