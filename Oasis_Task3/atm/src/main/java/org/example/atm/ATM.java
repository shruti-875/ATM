package org.example.atm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ATM {
    private Connection connection;
    private String userId;

    public ATM() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_db", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String userId, String pin) {
        try {
            String query = "SELECT * FROM users WHERE user_id = ? AND pin = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.setString(2, pin);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.userId = userId;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void withdraw(double amount) {
        try {
            connection.setAutoCommit(false);

            String query = "SELECT balance FROM users WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getDouble("balance") >= amount) {
                double newBalance = resultSet.getDouble("balance") - amount;

                String updateQuery = "UPDATE users SET balance = ? WHERE user_id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setDouble(1, newBalance);
                updateStatement.setString(2, userId);
                updateStatement.executeUpdate();

                String transactionQuery = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, 'withdraw', ?)";
                PreparedStatement transactionStatement = connection.prepareStatement(transactionQuery);
                transactionStatement.setString(1, userId);
                transactionStatement.setDouble(2, amount);
                transactionStatement.executeUpdate();

                connection.commit();
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deposit(double amount) {
        try {
            connection.setAutoCommit(false);

            String query = "SELECT balance FROM users WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double newBalance = resultSet.getDouble("balance") + amount;

                String updateQuery = "UPDATE users SET balance = ? WHERE user_id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setDouble(1, newBalance);
                updateStatement.setString(2, userId);
                updateStatement.executeUpdate();

                String transactionQuery = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, 'deposit', ?)";
                PreparedStatement transactionStatement = connection.prepareStatement(transactionQuery);
                transactionStatement.setString(1, userId);
                transactionStatement.setDouble(2, amount);
                transactionStatement.executeUpdate();

                connection.commit();
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void transfer(double amount, String recipientUserId) {
        try {
            connection.setAutoCommit(false);

            String query = "SELECT balance FROM users WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getDouble("balance") >= amount) {
                double newBalance = resultSet.getDouble("balance") - amount;

                String updateQuery = "UPDATE users SET balance = ? WHERE user_id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setDouble(1, newBalance);
                updateStatement.setString(2, userId);
                updateStatement.executeUpdate();

                query = "SELECT balance FROM users WHERE user_id = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, recipientUserId);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    double recipientNewBalance = resultSet.getDouble("balance") + amount;

                    updateQuery = "UPDATE users SET balance = ? WHERE user_id = ?";
                    updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setDouble(1, recipientNewBalance);
                    updateStatement.setString(2, recipientUserId);
                    updateStatement.executeUpdate();

                    String transactionQuery = "INSERT INTO transactions (user_id, transaction_type, amount, recipient_user_id) VALUES (?, 'transfer', ?, ?)";
                    PreparedStatement transactionStatement = connection.prepareStatement(transactionQuery);
                    transactionStatement.setString(1, userId);
                    transactionStatement.setDouble(2, amount);
                    transactionStatement.setString(3, recipientUserId);
                    transactionStatement.executeUpdate();

                    connection.commit();
                } else {
                    connection.rollback();
                }
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTransactionHistory() {
        StringBuilder history = new StringBuilder();
        try {
            String query = "SELECT * FROM transactions WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                history.append("Type: ").append(resultSet.getString("transaction_type"))
                        .append(", Amount: ").append(resultSet.getDouble("amount"))
                        .append(", Recipient: ").append(resultSet.getString("recipient_user_id"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history.toString();
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}