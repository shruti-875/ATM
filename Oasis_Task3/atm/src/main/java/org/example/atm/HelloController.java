package org.example.atm;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class HelloController {
    @FXML
    private TextField userIdField;

    @FXML
    private PasswordField pinField;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab mainMenuTab;

    @FXML
    private Tab transactionHistoryTab;

    @FXML
    private Tab withdrawTab;

    @FXML
    private Tab depositTab;

    @FXML
    private Tab transferTab;

    @FXML
    private TextField withdrawAmountField;

    @FXML
    private TextField depositAmountField;

    @FXML
    private TextField transferAmountField;

    @FXML
    private TextField recipientUserIdField;

    @FXML
    private Label transactionHistoryLabel;

    private ATM atm;

    public HelloController() {
        atm = new ATM();
    }

    @FXML
    private void handleLogin() {
        String userId = userIdField.getText();
        String pin = pinField.getText();

        if (atm.login(userId, pin)) {
            tabPane.setVisible(true);
            mainMenuTab.setDisable(false);
            transactionHistoryTab.setDisable(false);
            withdrawTab.setDisable(false);
            depositTab.setDisable(false);
            transferTab.setDisable(false);
            tabPane.getSelectionModel().select(mainMenuTab);
        } else {
            System.out.println("Invalid login");
        }
    }

    @FXML
    private void showMainMenu() {
        tabPane.getSelectionModel().select(mainMenuTab);
    }

    @FXML
    private void showTransactionHistory() {
        transactionHistoryLabel.setText(atm.getTransactionHistory());
        tabPane.getSelectionModel().select(transactionHistoryTab);
    }

    @FXML
    private void showWithdraw() {
        tabPane.getSelectionModel().select(withdrawTab);
    }

    @FXML
    private void handleWithdraw() {
        double amount = Double.parseDouble(withdrawAmountField.getText());
        atm.withdraw(amount);
        showMainMenu();
    }

    @FXML
    private void showDeposit() {
        tabPane.getSelectionModel().select(depositTab);
    }

    @FXML
    private void handleDeposit() {
        double amount = Double.parseDouble(depositAmountField.getText());
        atm.deposit(amount);
        showMainMenu();
    }

    @FXML
    private void showTransfer() {
        tabPane.getSelectionModel().select(transferTab);
    }

    @FXML
    private void handleTransfer() {
        double amount = Double.parseDouble(transferAmountField.getText());
        String recipientUserId = recipientUserIdField.getText();
        atm.transfer(amount, recipientUserId);
        showMainMenu();
    }

    @FXML
    private void handleQuit() {
        try {
            atm.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}