package banking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * Account class that implements core banking operations:
 * deposit, withdraw, and print statement functionality.
 */
public class Account implements BankAccount {

    private record Transaction(Date date, int amount, int balance) {

        @Override
        public String toString() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return String.format("%s || %6d || %7d",
                    dateFormat.format(date), amount, balance);
        }
    }

    private final List<Transaction> transactions;
    private int balance;

    public Account() {
        this.transactions = new ArrayList<>();
        this.balance = 0;
    }

    @Override
    public void deposit(int amount) {
        validatePositiveAmount(amount, "Deposit amount must be positive");

        try {
            this.balance += amount;
            Date currentDate = new Date();
            Transaction transaction = new Transaction(currentDate, amount, balance);
            this.transactions.add(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void withdraw(int amount) {
        validatePositiveAmount(amount, "Withdrawal amount must be positive");

        try {
            if (balance < amount) {
                throw new IllegalStateException(
                        String.format("Insufficient funds. Current balance: %d, Requested withdrawal: %d", balance, amount)
                );
            }

            balance -= amount;
            Date currentDate = new Date();
            Transaction transaction = new Transaction(currentDate, -amount, balance);
            transactions.add(transaction);
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String printStatement() {
        try {
            if (this.transactions.isEmpty()) {
                return "No transactions to display";
            }

            // Create a sorted list of transactions (most recent first)
            List<Transaction> sortedTransactions = new ArrayList<>(this.transactions);
            sortedTransactions.sort(Comparator.comparing(Transaction::date).reversed());

            StringBuilder statement = new StringBuilder();
            statement.append("Date       || Amount || Balance \n");
            for (Transaction transaction : sortedTransactions) {
                statement.append(transaction.toString()).append("\n");
            }

            // Remove the last newline character
            if (!statement.isEmpty()) {
                statement.setLength(statement.length() - 1);
            }

            return statement.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Additional utility methods (not part of the required interface)
    public int getBalance() {
        return this.balance;
    }

    private void validatePositiveAmount(int amount, String message) {
        if (amount <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}