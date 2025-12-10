package com.khorshed.mybank.mybankapp.controllers;

import com.khorshed.mybank.mybankapp.Main;
import com.khorshed.mybank.mybankapp.database.DatabaseHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Create Account Controller
 * Handles account creation functionality
 */
public class CreateAccountController {

    @FXML
    private TextField accountNumberField;

    @FXML
    private TextField ownerNameField;

    @FXML
    private TextField initialDepositField;

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
     * Creates a new account
     */
    @FXML
    private void createAccount() {
        // Clear previous message
        messageLabel.setText("");
        messageLabel.setStyle("-fx-text-fill: black;");

        try {
            // Validate inputs
            if (accountNumberField.getText().trim().isEmpty()) {
                showError("Please enter account number!");
                return;
            }

            if (ownerNameField.getText().trim().isEmpty()) {
                showError("Please enter owner name!");
                return;
            }

            if (initialDepositField.getText().trim().isEmpty()) {
                showError("Please enter initial deposit amount!");
                return;
            }

            // Parse values
            int accountNumber = Integer.parseInt(accountNumberField.getText().trim());
            String ownerName = ownerNameField.getText().trim();
            double initialDeposit = Double.parseDouble(initialDepositField.getText().trim());

            // Validate values
            if (accountNumber <= 0) {
                showError("Account number must be positive!");
                return;
            }

            if (initialDeposit < 0) {
                showError("Initial deposit cannot be negative!");
                return;
            }

            // Check if account already exists
            if (dbHelper.accountExists(accountNumber)) {
                showError("Account number already exists!");
                return;
            }

            // Create account
            boolean success = dbHelper.createAccount(accountNumber, ownerName, initialDeposit);

            if (success) {
                showSuccess("Account created successfully!\nAccount Number: " + accountNumber +
                        "\nOwner: " + ownerName +
                        "\nBalance: $" + String.format("%.2f", initialDeposit));
                clearFields();
            } else {
                showError("Failed to create account. Please try again.");
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
        ownerNameField.clear();
        initialDepositField.clear();
    }
}
