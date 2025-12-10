package com.khorshed.mybank.mybankapp.controllers;

import com.khorshed.mybank.mybankapp.Main;
import com.khorshed.mybank.mybankapp.database.DatabaseHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Deposit Controller
 * Handles deposit functionality
 */
public class DepositController {

    @FXML
    private TextField accountNumberField;

    @FXML
    private TextField depositAmountField;

    @FXML
    private Label messageLabel;

    private DatabaseHelper dbHelper;

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize() {
        dbHelper = new DatabaseHelper();
    }

    /**
     * Deposits money into account
     */
    @FXML
    private void depositMoney() {
        // Clear previous message
        messageLabel.setText("");
        messageLabel.setStyle("-fx-text-fill: black;");

        try {
            // Validate inputs
            if (accountNumberField.getText().trim().isEmpty()) {
                showError("Please enter account number!");
                return;
            }

            if (depositAmountField.getText().trim().isEmpty()) {
                showError("Please enter deposit amount!");
                return;
            }

            // Parse values
            int accountNumber = Integer.parseInt(accountNumberField.getText().trim());
            double amount = Double.parseDouble(depositAmountField.getText().trim());

            // Validate values
            if (accountNumber <= 0) {
                showError("Account number must be positive!");
                return;
            }

            if (amount <= 0) {
                showError("Deposit amount must be greater than zero!");
                return;
            }

            // Check if account exists
            if (!dbHelper.accountExists(accountNumber)) {
                showError("Account not found!");
                return;
            }

            // Get current balance
            double oldBalance = dbHelper.getBalance(accountNumber);

            // Perform deposit
            boolean success = dbHelper.deposit(accountNumber, amount);

            if (success) {
                double newBalance = dbHelper.getBalance(accountNumber);
                showSuccess("Deposit successful!\nAmount Deposited: $" + String.format("%.2f", amount) +
                        "\nNew Balance: $" + String.format("%.2f", newBalance));
                clearFields();
            } else {
                showError("Deposit failed. Please try again.");
            }

        } catch (NumberFormatException e) {
            showError("Invalid input! Please enter valid numbers.");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns to dashboard
     */
    @FXML
    private void backToDashboard() {
        try {
            Main.changeScene("Dashboard.fxml");
        } catch (Exception e) {
            System.err.println("Error returning to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays error message
     */
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
    }

    /**
     * Displays success message
     */
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
    }

    /**
     * Clears all input fields
     */
    private void clearFields() {
        accountNumberField.clear();
        depositAmountField.clear();
    }
}
