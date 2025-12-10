package com.khorshed.mybank.mybankapp.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DatabaseHelper - Manages all SQLite database operations
 * Provides methods for account and transaction management
 */
public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:database/mybank.db";
    private Connection connection;

    /**
     * Constructor - Initializes database connection and creates tables
     */
    public DatabaseHelper() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates database tables if they don't exist
     */
    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Create accounts table
            String accountsTable = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "accountNumber INTEGER PRIMARY KEY, " +
                    "ownerName TEXT NOT NULL, " +
                    "balance REAL NOT NULL DEFAULT 0)";
            stmt.execute(accountsTable);

            // Create transactions table
            String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "accountNumber INTEGER NOT NULL, " +
                    "type TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "FOREIGN KEY (accountNumber) REFERENCES accounts(accountNumber))";
            stmt.execute(transactionsTable);

            System.out.println("Tables created successfully!");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a new bank account
     * @param accountNumber The account number
     * @param ownerName The account owner's name
     * @param initialDeposit The initial deposit amount
     * @return true if successful, false otherwise
     */
    public boolean createAccount(int accountNumber, String ownerName, double initialDeposit) {
        String sql = "INSERT INTO accounts (accountNumber, ownerName, balance) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            pstmt.setString(2, ownerName);
            pstmt.setDouble(3, initialDeposit);
            pstmt.executeUpdate();

            // Record initial deposit as a transaction if > 0
            if (initialDeposit > 0) {
                recordTransaction(accountNumber, "Initial Deposit", initialDeposit);
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an account exists
     * @param accountNumber The account number to check
     * @return true if exists, false otherwise
     */
    public boolean accountExists(int accountNumber) {
        String sql = "SELECT accountNumber FROM accounts WHERE accountNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the current balance of an account
     * @param accountNumber The account number
     * @return The balance, or -1 if account doesn't exist
     */
    public double getBalance(int accountNumber) {
        String sql = "SELECT balance FROM accounts WHERE accountNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.err.println("Error getting balance: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Gets the owner name of an account
     * @param accountNumber The account number
     * @return The owner name, or null if not found
     */
    public String getOwnerName(int accountNumber) {
        String sql = "SELECT ownerName FROM accounts WHERE accountNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("ownerName");
            }
        } catch (SQLException e) {
            System.err.println("Error getting owner name: " + e.getMessage());
        }
        return null;
    }

    /**
     * Deposits money into an account
     * @param accountNumber The account number
     * @param amount The amount to deposit
     * @return true if successful, false otherwise
     */
    public boolean deposit(int accountNumber, double amount) {
        if (!accountExists(accountNumber) || amount <= 0) {
            return false;
        }

        String sql = "UPDATE accounts SET balance = balance + ? WHERE accountNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountNumber);
            pstmt.executeUpdate();

            // Record transaction
            recordTransaction(accountNumber, "Deposit", amount);

            return true;
        } catch (SQLException e) {
            System.err.println("Error depositing money: " + e.getMessage());
            return false;
        }
    }

    /**
     * Withdraws money from an account
     * @param accountNumber The account number
     * @param amount The amount to withdraw
     * @return true if successful, false otherwise
     */
    public boolean withdraw(int accountNumber, double amount) {
        if (!accountExists(accountNumber) || amount <= 0) {
            return false;
        }

        double currentBalance = getBalance(accountNumber);
        if (currentBalance < amount) {
            return false; // Insufficient balance
        }

        String sql = "UPDATE accounts SET balance = balance - ? WHERE accountNumber = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountNumber);
            pstmt.executeUpdate();

            // Record transaction
            recordTransaction(accountNumber, "Withdraw", amount);

            return true;
        } catch (SQLException e) {
            System.err.println("Error withdrawing money: " + e.getMessage());
            return false;
        }
    }

    /**
     * Transfers money between two accounts
     * @param fromAccount The sender's account number
     * @param toAccount The receiver's account number
     * @param amount The amount to transfer
     * @return true if successful, false otherwise
     */
    public boolean transfer(int fromAccount, int toAccount, double amount) {
        if (!accountExists(fromAccount) || !accountExists(toAccount) || amount <= 0) {
            return false;
        }

        if (fromAccount == toAccount) {
            return false; // Cannot transfer to same account
        }

        double senderBalance = getBalance(fromAccount);
        if (senderBalance < amount) {
            return false; // Insufficient balance
        }

        try {
            // Start transaction
            connection.setAutoCommit(false);

            // Deduct from sender
            String deductSql = "UPDATE accounts SET balance = balance - ? WHERE accountNumber = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deductSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, fromAccount);
                pstmt.executeUpdate();
            }

            // Add to receiver
            String addSql = "UPDATE accounts SET balance = balance + ? WHERE accountNumber = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(addSql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, toAccount);
                pstmt.executeUpdate();
            }

            // Record transactions
            recordTransaction(fromAccount, "Transfer Out to " + toAccount, amount);
            recordTransaction(toAccount, "Transfer In from " + fromAccount, amount);

            // Commit transaction
            connection.commit();
            connection.setAutoCommit(true);

            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
            System.err.println("Error transferring money: " + e.getMessage());
            return false;
        }
    }

    /**
     * Records a transaction in the database
     * @param accountNumber The account number
     * @param type The transaction type
     * @param amount The transaction amount
     */
    private void recordTransaction(int accountNumber, String type, double amount) {
        String sql = "INSERT INTO transactions (accountNumber, type, amount, date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountNumber);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);

            // Get current date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(4, now.format(formatter));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
        }
    }

    /**
     * Gets transaction history for an account
     * @param accountNumber The account number
     * @return ResultSet containing transaction history
     */
    public ResultSet getTransactionHistory(int accountNumber) {
        String sql = "SELECT * FROM transactions WHERE accountNumber = ? ORDER BY id DESC";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, accountNumber);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting transaction history: " + e.getMessage());
            return null;
        }
    }

    /**
     * Closes the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
