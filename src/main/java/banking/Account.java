package banking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

// Concurrency
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
//    Making the balance either volatile or atomic because it's a shared variable between threads
    private volatile int balance;
//    A ReadWriteLock maintains a pair of associated locks, one for read-only operations and one for writing.
//    The read lock may be held simultaneously by multiple reader threads, so long as there are no writers.
//    The write lock is exclusive.
    private final ReadWriteLock lock;

    public Account() {
        this.transactions = new ArrayList<>();
        this.balance = 0;
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void deposit(int amount) {
        validatePositiveAmount(amount, "Deposit amount must be positive");

//        Acquires the lock.
//        If the lock is not available then the current thread becomes disabled
//        for thread scheduling purposes and lies dormant until the lock has been acquired
        lock.writeLock().lock();
        try {
            this.balance += amount;
            Date currentDate = new Date();
            Transaction transaction = new Transaction(currentDate, amount, balance);
            this.transactions.add(transaction);
        } finally {
//            Releases the lock.
            lock.writeLock().unlock();
        }
    }

    @Override
    public void withdraw(int amount) {
        validatePositiveAmount(amount, "Withdrawal amount must be positive");

        lock.writeLock().lock();
        try {
            if (balance < amount) {
                throw new IllegalStateException(
                        String.format("Insufficient funds. Requested withdrawal: %d", amount)
                );
            }

            balance -= amount;
            Date currentDate = new Date();
            Transaction transaction = new Transaction(currentDate, -amount, balance);
            transactions.add(transaction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String printStatement() {
        lock.readLock().lock();
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
        } finally {
            lock.readLock().unlock();
        }
    }

    // Additional utility methods (not part of the required interface)
    public int getBalance() {
        lock.readLock().lock();
        try {
            return this.balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void validatePositiveAmount(int amount, String message) {
        if (amount <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}