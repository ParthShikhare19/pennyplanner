package com.example.pennyplanner;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;

public class ChoiceBox1Controller {

    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private ChoiceBox<String> PayMethChoiceBox;

    @FXML
    private ChoiceBox<String> TranTypeChoiceBox;

    // List of options for the ChoiceBox
    private String[] choices = { "Education", "Food", "Health", "Travelling","Bills","Utilities","Entertainment","Others" };
    private String[] choice1 = {"Income" , "Expense"};
    private String[] choice2 = {"Cash" , "UPI" , "cheque" };

    @FXML
    public void initialize() {
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
}
