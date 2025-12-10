package com.khorshed.mybank.mybankapp.controllers;

import com.khorshed.mybank.mybankapp.Main;
import javafx.fxml.FXML;
import javafx.application.Platform;

/**
 * Dashboard Controller
 * Handles navigation from the main dashboard
 */
public class DashboardController {

    /**
     * Opens Create Account page
     */
    @FXML
    private void openCreateAccount() {
        try {
            Main.changeScene("CreateAccount.fxml");
        } catch (Exception e) {
            System.err.println("Error opening Create Account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens Deposit page
     */
    @FXML
    private void openDeposit() {
        try {
            Main.changeScene("Deposit.fxml");
        } catch (Exception e) {
            System.err.println("Error opening Deposit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens Withdraw page
     */
    @FXML
    private void openWithdraw() {
        try {
            Main.changeScene("Withdraw.fxml");
        } catch (Exception e) {
            System.err.println("Error opening Withdraw: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens Transfer page
     */
    @FXML
    private void openTransfer() {
        try {
            Main.changeScene("Transfer.fxml");
        } catch (Exception e) {
            System.err.println("Error opening Transfer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens Check Balance page
     */
    @FXML
    private void openCheckBalance() {
        try {
            Main.changeScene("CheckBalance.fxml");
        } catch (Exception e) {
            System.err.println("Error opening Check Balance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens Transaction History page
     */
    @FXML
    private void openTransactionHistory() {
        try {
            Main.changeScene("TransactionHistory.fxml");
        } catch (Exception e) {
            System.err.println("Error opening Transaction History: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exits the application
     */
    @FXML
    private void exitApplication() {
        Platform.exit();
        System.exit(0);
    }
}
