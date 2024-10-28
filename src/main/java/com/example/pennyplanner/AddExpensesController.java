package com.example.pennyplanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import javafx.geometry.Insets;

public class AddExpensesController {


    //model class start
    public class Transactions {
        private String description;
        private int id;
        private LocalDate date;

        private String category;
        private Double amount;
        private String type;
        private String paymentMethod;

        public Transactions(int id, LocalDate date, String description, String category, Double amount, String type, String paymentMethod) {
            this.id = id;
            this.date = date;
            this.description = description;
            this.category = category;
            this.amount = amount;
            this.type = type;
            this.paymentMethod = paymentMethod;
        }

        // Getters and Setters
        public int getId() {
            return id;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getDescription() {
            return description;
        }

        public String getCategory() {
            return category;
        }

        public Double getAmount() {
            return amount;
        }

        public String getType() {
            return type;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setDate(String date) {
            this.date = LocalDate.parse(date);
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public void setAmount(Double amount) {
            this.amount = amount;
        }
        public void setType(String type) {
            this.type = type;
        }
        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }

        //model class ends
        @FXML
        private TableView<Transactions> transactions;
        @FXML
        private TableColumn<Transactions, String> dateColumn;
        @FXML
        private TableColumn<Transactions,String> descriptionColumn, categoryColumn, amountColumn, typeColumn, paymentMethodColumn;

        @FXML
        private Button deleteButton, updateButton;

        private ObservableList<Transactions> expensesList = FXCollections.observableArrayList();

        private Connection connection;
        private Transactions selectedExpense;
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

        @FXML
        public void initialize() {
            connectToDatabase();
            showUnameAndUmail();

            // Bind table columns to data properties
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

            loadExpensesData();

            transactions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedExpense = newSelection;
                    deleteButton.setVisible(true);
                    updateButton.setVisible(true);
                }
            });

            deleteButton.setVisible(false);
            updateButton.setVisible(false);
        }

        private void connectToDatabase() {
            String url = "jdbc:mysql://localhost:3306/pennyplannerdb";
            String user = "root";
            String password = "oracle";

            try {
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
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

    private void loadExpensesData() {
        expensesList.clear();

        String query = "SELECT Tran_ID, date, Description, category, Tran_amt, Tran_Type, Payment_name FROM transactions WHERE User_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int userId = SettingsController.UserSession.getUserId();
            preparedStatement.setInt(1, userId);

            // Execute the query
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null; // Check for null
                expensesList.add(new Transactions(
                        rs.getInt("Tran_ID"),
                        date,
                        rs.getString("Description"),
                        rs.getString("category"),
                        rs.getDouble("Tran_amt"),
                        rs.getString("Tran_Type"),
                        rs.getString("Payment_name")
                ));
            }
            transactions.setItems(expensesList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
        private void handleDelete(ActionEvent event) {
            List<Transactions> selectedExpenses = transactions.getSelectionModel().getSelectedItems();
            if (selectedExpense != null) {
                String deleteQuery = "DELETE FROM transactions WHERE Tran_ID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                    pstmt.setInt(1, selectedExpense.getId());
                    pstmt.executeUpdate();
                    expensesList.remove(selectedExpense);
                    deleteButton.setVisible(false);
                    updateButton.setVisible(false);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }



        @FXML
        private void ExportExp(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                exportToExcel(file);
            }
        }

        private void exportToExcel(File file) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Expenses");

                // Create headers
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Date");
                header.createCell(1).setCellValue("Description");
                header.createCell(2).setCellValue("Category");
                header.createCell(3).setCellValue("Amount");
                header.createCell(4).setCellValue("Type");
                header.createCell(5).setCellValue("Payment Method");

                // Fill data
                int rowIndex = 1;
                for (Transactions expense : expensesList) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(expense.getDate().toString());
                    row.createCell(1).setCellValue(expense.getDescription());
                    row.createCell(2).setCellValue(expense.getCategory());
                    row.createCell(3).setCellValue(expense.getAmount().toString());
                    row.createCell(4).setCellValue(expense.getType());
                    row.createCell(5).setCellValue(expense.getPaymentMethod());
                }

                // Write to file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // FXML connections

        @FXML
        private void Dashboard(ActionEvent event) {
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
        private void AnalysisFromAddExpense(ActionEvent event) {
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
        private void AddExpensesToGoals(ActionEvent event) {
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
        private void AddexpenseToSettings(ActionEvent event) {
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
        private void ADD(ActionEvent event) {
            try {
                // Load the dashboard FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Addexpense2.fxml"));
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
    public void UpdateButton(ActionEvent event) {

    }
    @FXML
    public void handleUpdate(ActionEvent event) {
        // Get the selected transaction
        Transactions selectedTransaction = transactions.getSelectionModel().getSelectedItem();

        if (selectedTransaction == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Transaction Selected");
            alert.setContentText("Please select a transaction to update.");
            alert.showAndWait();
            return;
        }

        // Create a dialog to update the transaction
        Stage dialog = new Stage();
        dialog.setTitle("Update Transaction");

        VBox dialogVBox = new VBox();
        dialogVBox.setSpacing(10);
        dialogVBox.setPadding(new Insets(10));

        // DatePicker for date
        DatePicker datePicker = new DatePicker(selectedTransaction.getDate());

        // ComboBox for category
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Education", "Food", "Health", "Travelling", "Bills", "Utilities", "Entertainment", "Others");
        categoryBox.setValue(selectedTransaction.getCategory());

        // ComboBox for transaction type
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Income", "Expense");
        typeBox.setValue(selectedTransaction.getType());

        // ComboBox for payment method
        ComboBox<String> paymentMethodBox = new ComboBox<>();
        paymentMethodBox.getItems().addAll("Cash", "UPI", "Cheque");
        paymentMethodBox.setValue(selectedTransaction.getPaymentMethod());

        // TextField for amount
        TextField amountField = new TextField(String.valueOf(selectedTransaction.getAmount()));

        dialogVBox.getChildren().addAll(
                new Label("Date:"), datePicker,
                new Label("Category:"), categoryBox,
                new Label("Transaction Type:"), typeBox,
                new Label("Payment Method:"), paymentMethodBox,
                new Label("Amount:"), amountField
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

            // Update the transaction data
            selectedTransaction.setDate(datePicker.getValue().toString());
            selectedTransaction.setCategory(categoryBox.getValue());
            selectedTransaction.setType(typeBox.getValue());
            selectedTransaction.setPaymentMethod(paymentMethodBox.getValue());
            selectedTransaction.setAmount(Double.parseDouble(amountText));

            // Refresh the table view
            transactions.refresh();

            // Close the dialog
            dialog.close();

            // Update the database with new values
            updateTransactionInDatabase(selectedTransaction);
        });

        dialogVBox.getChildren().add(applyButton);

        Scene dialogScene = new Scene(dialogVBox, 300, 400);
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

    private void updateTransactionInDatabase(Transactions transaction) {
        String updateQuery = "UPDATE transactions SET date = ?, Description = ?, category = ?, Tran_amt = ?, Tran_Type = ?, Payment_name = ? WHERE Tran_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            // Set the parameters for the prepared statement
            pstmt.setDate(1, Date.valueOf(transaction.getDate())); // Convert LocalDate to java.sql.Date
            pstmt.setString(2, transaction.getDescription());
            pstmt.setString(3, transaction.getCategory());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getType());
            pstmt.setString(6, transaction.getPaymentMethod());
            pstmt.setInt(7, transaction.getId());

            // Execute the update query
            pstmt.executeUpdate();

            // Show confirmation message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Transaction Updated");
            alert.setContentText("The transaction has been successfully updated.");
            alert.showAndWait();

        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error Updating Transaction");
            alert.setContentText("There was an error updating the transaction. Please try again.");
            alert.showAndWait();
        }
    }
    @FXML
    private void AddExpense1toAboutUs(ActionEvent event) {
        try {
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutUs.fxmlParthShikhare21Parthshikhare21"));
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
