package com.example.pennyplanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ChoiceBox;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalysisController {
    @FXML
    private ChoiceBox<String> AnalysisChoiceBox;

    @FXML
    private PieChart pieChart;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label Uemail;

    @FXML
    private Label Uname;


    private Connection connection;
    private int userId = SettingsController.UserSession.getUserId();
    public void showUnameAndUmail()
    {
        connectToDatabase();
        try {
            // Assuming userId is passed when the user logs in
            userId = SettingsController.UserSession.getUserId();
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
    public void connectToDatabase() {
        try {
            // Replace with your own database connection details
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pennyplannerdb", "root", "oracle");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() throws SQLException {
        // Initialize database connection
        initDatabaseConnection();
        showUnameAndUmail();

        // Populate the ChoiceBox with options
        AnalysisChoiceBox.getItems().addAll("Today", "Yesterday","This Week", "This Month", "This Year");
        AnalysisChoiceBox.setValue("Today"); // Default selection

        // Listener to detect changes in the ChoiceBox
        AnalysisChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            try {
                updateCharts(newVal);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Initial load of charts with default option "Today"
        updateCharts("Today");
    }

    /**
     * Initializes the database connection
     */
    private void initDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/pennyplannerdb"; // Update with your database details
        String user = "root"; // Update with your database username
        String password = "oracle"; // Update with your database password

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates charts based on the selected time period.
     *
     * @param timePeriod The selected time period (e.g., "Today", "Yesterday", "This Month", "This Year").
     */
    private void updateCharts(String timePeriod) throws SQLException {
        // Clear existing data in the charts
        pieChart.getData().clear();
        barChart.getData().clear();

        // Fetch the relevant data based on the selected time period
        switch (timePeriod) {
            case "Today":
                loadTodayData();
                break;
            case "Yesterday":
                loadYesterdayData();
                break;
            case "This Month":
                loadThisMonthData();
                break;
            case "This Year":
                loadThisYearData();
                break;
            case "This Week":
                loadThisWeekData();
                break;
        }
    }

    /**
     * Loads data for "Today" and updates the charts.
     */
    private void loadTodayData() throws SQLException {
        String pieChartQuery = "SELECT category, SUM(Tran_amt) as total FROM transactions WHERE date = CURDATE() AND User_ID = ? GROUP BY category";
        String barChartQuery = "SELECT date, SUM(Tran_amt) as total FROM transactions WHERE date = CURDATE() AND User_ID = ? GROUP BY date";

        try (PreparedStatement pieStmt = connection.prepareStatement(pieChartQuery);
             PreparedStatement barStmt = connection.prepareStatement(barChartQuery)) {
            pieStmt.setInt(1, userId);
            barStmt.setInt(1, userId);
            loadData(pieStmt, barStmt);
        }
    }

    /**
     * Loads data for "Yesterday" and updates the charts.
     */
    private void loadYesterdayData() throws SQLException {
        String pieChartQuery = "SELECT category, SUM(Tran_amt) as total FROM transactions WHERE date = CURDATE() - INTERVAL 1 DAY AND User_ID = ? GROUP BY category";
        String barChartQuery = "SELECT date, SUM(Tran_amt) as total FROM transactions WHERE date = CURDATE() - INTERVAL 1 DAY AND User_ID = ? GROUP BY date";

        try (PreparedStatement pieStmt = connection.prepareStatement(pieChartQuery);
             PreparedStatement barStmt = connection.prepareStatement(barChartQuery)) {
            pieStmt.setInt(1, userId);
            barStmt.setInt(1, userId);
            loadData(pieStmt, barStmt);
        }
    }

    /**
     * Loads data for "This Week" and updates the charts.
     */
    private void loadThisWeekData() throws SQLException {
        String pieChartQuery = "SELECT category, SUM(Tran_amt) AS total " +
                "FROM transactions " +
                "WHERE WEEK(date) = WEEK(CURDATE()) AND YEAR(date) = YEAR(CURDATE()) AND User_ID = ? " +
                "GROUP BY category";

        String barChartQuery = "SELECT DATE(date) AS date, SUM(Tran_amt) AS total " +
                "FROM transactions " +
                "WHERE WEEK(date) = WEEK(CURDATE()) AND YEAR(date) = YEAR(CURDATE()) AND User_ID = ? " +
                "GROUP BY DATE(date)";

        try (PreparedStatement pieStmt = connection.prepareStatement(pieChartQuery);
             PreparedStatement barStmt = connection.prepareStatement(barChartQuery)) {
            pieStmt.setInt(1, userId);
            barStmt.setInt(1, userId);
            loadData(pieStmt, barStmt);
        }
    }

    /**
     * Loads data for "This Month" and updates the charts.
     */
    private void loadThisMonthData() throws SQLException {
        String pieChartQuery = "SELECT category, SUM(Tran_amt) as total FROM transactions WHERE MONTH(date) = MONTH(CURDATE()) AND YEAR(date) = YEAR(CURDATE()) AND User_ID = ? GROUP BY category";
        String barChartQuery = "SELECT date, SUM(Tran_amt) as total FROM transactions WHERE MONTH(date) = MONTH(CURDATE()) AND YEAR(date) = YEAR(CURDATE()) AND User_ID = ? GROUP BY date";

        try (PreparedStatement pieStmt = connection.prepareStatement(pieChartQuery);
             PreparedStatement barStmt = connection.prepareStatement(barChartQuery)) {
            pieStmt.setInt(1, userId);
            barStmt.setInt(1, userId);
            loadData(pieStmt, barStmt);
        }
    }

    /**
     * Loads data for "This Year" and updates the charts.
     */
    private void loadThisYearData() throws SQLException {
        String pieChartQuery = "SELECT category, SUM(Tran_amt) AS total " +
                "FROM transactions " +
                "WHERE YEAR(date) = YEAR(CURDATE()) AND User_ID = ? " +
                "GROUP BY category";

        // Updated barChartQuery to get all months
        String barChartQuery = "SELECT m.month, IFNULL(SUM(t.Tran_amt), 0) AS total " +
                "FROM (SELECT 1 AS month UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 " +
                "UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 " +
                "UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12) AS m " +
                "LEFT JOIN transactions t ON m.month = MONTH(t.date) " +
                "AND YEAR(t.date) = YEAR(CURDATE()) AND t.User_ID = ? " +
                "GROUP BY m.month " +
                "ORDER BY m.month";

        try (PreparedStatement pieStmt = connection.prepareStatement(pieChartQuery);
             PreparedStatement barStmt = connection.prepareStatement(barChartQuery)) {
            pieStmt.setInt(1, userId);
            barStmt.setInt(1, userId);
            loadDataForYear(pieStmt, barStmt);
        }
    }

    private void loadDataForYear(PreparedStatement pieStmt, PreparedStatement barStmt) throws SQLException {
        // Clear previous data from charts
        pieChart.getData().clear();
        barChart.getData().clear();

        // Load data for PieChart
        try (ResultSet pieRs = pieStmt.executeQuery()) {
            while (pieRs.next()) {
                String category = pieRs.getString("category");
                double total = pieRs.getDouble("total");
                pieChart.getData().add(new PieChart.Data(category, total));
            }
        }

        // Load data for BarChart
        try (ResultSet barRs = barStmt.executeQuery()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            // Create an array of month names
            String[] monthNames = {"January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};

            // Initialize an array to store totals for each month
            double[] monthlyTotals = new double[12];

            // Populate the monthlyTotals array with results from the ResultSet
            while (barRs.next()) {
                int month = barRs.getInt("month");  // Get the month as an integer (1-12)
                double total = barRs.getDouble("total");
                monthlyTotals[month - 1] = total;  // Store the total for the corresponding month (0-based index)
            }

            // Add each month name and corresponding total to the series
            for (int i = 0; i < monthNames.length; i++) {
                series.getData().add(new XYChart.Data<>(monthNames[i], monthlyTotals[i])); // Add month name and total
            }

            barChart.getData().add(series);
        }
    }
    /**
     * Executes the given queries to load data for PieChart and BarChart.
     *
     * @param pieStmt Prepared statement for loading data into the PieChart.
     * @param barStmt Prepared statement for loading data into the BarChart.
     */
    private void loadData(PreparedStatement pieStmt, PreparedStatement barStmt) throws SQLException {
        // Load data for PieChart
        try (ResultSet pieRs = pieStmt.executeQuery()) {
            while (pieRs.next()) {
                String category = pieRs.getString("category");
                double total = pieRs.getDouble("total");
                pieChart.getData().add(new PieChart.Data(category, total));
            }
        }

        // Load data for BarChart
        try (ResultSet barRs = barStmt.executeQuery()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (barRs.next()) {
                String date = barRs.getString("date");
                double total = barRs.getDouble("total");
                series.getData().add(new XYChart.Data<>(date, total));
            }
            barChart.getData().add(series);
        }
    }

    @FXML
    private void AnalysisToDash(ActionEvent event) {
        switchScene(event, "Dashboard.fxml");
    }

    @FXML
    private void AddExpenseFromAnalysis(ActionEvent event) {
        switchScene(event, "Addexpense1.fxml");
    }

    @FXML
    private void AnalysisToGoal(ActionEvent event) {
        switchScene(event, "goal 1.fxml");
    }

    @FXML
    private void AnalysisToSettings(ActionEvent event) {
        switchScene(event, "settings1.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlFile) {
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
    @FXML
    private void AnalysistoAboutUs(ActionEvent event) {
        switchScene(event, "AboutUs.fxml");
    }
}
