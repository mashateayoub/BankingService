import banking.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {


    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
    }

    // ========== Basic Functionality Tests ==========

    @Test
    void testBasicDeposit() {
        account.deposit(100);
        assertEquals(100, account.getBalance());
    }

    @Test
    void testBasicWithdraw() {
        account.deposit(100);
        account.withdraw(50);
        assertEquals(50, account.getBalance());
    }

    @Test
    void testBasicPrintStatement() {
        account.deposit(100);
        account.withdraw(30);

        String statement = account.printStatement();
        assertNotNull(statement);
        assertTrue(statement.contains("Date       || Amount || Balance"));
        assertTrue(statement.contains("100"));
        assertTrue(statement.contains("-30"));
    }

    // ========== Concurrent  Tests ==========
    // They Fail when using non-concurrent/thread-Safe Account Class (juste simulating high traffic/demand)
    @Test
    void testConcurrentDeposits() throws InterruptedException {
        int depositsPerThread = 20;
        int amountPerDeposit = 100;

        int numberOfThreads = 12;
//        lunching a pool of 12 threads
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
//        ensuring the 12 threads are al done despite failed withdrawals
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < depositsPerThread; j++) {
                        account.deposit(amountPerDeposit);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        int expectedBalance = numberOfThreads * depositsPerThread * amountPerDeposit;
        assertEquals(expectedBalance, account.getBalance());
    }

    @Test
    void testConcurrentWithdrawals() throws InterruptedException {
        // Initial deposit
        int initialAmount = 10000;
        account.deposit(initialAmount);

        int withdrawalsPerThread = 50;
        int amountPerWithdrawal = 10;

        int numberOfThreads = 12;
//        lunching a pool of 12 threads
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
//        ensuring the 12 threads are al done despite failed withdrawals
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successfulWithdrawals = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < withdrawalsPerThread; j++) {
                        try {
                            account.withdraw(amountPerWithdrawal);
                            successfulWithdrawals.incrementAndGet();
                        } catch (IllegalStateException e) {
                            // Expected when insufficient funds
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify balance consistency
        int expectedRemainingBalance = initialAmount - (successfulWithdrawals.get() * amountPerWithdrawal);
        assertEquals(expectedRemainingBalance, account.getBalance());
        assertTrue(account.getBalance() >= 0);
    }

    @Test
    void testConcurrentInsufficientFunds() throws InterruptedException {
        account.deposit(100); // Small initial balance

        int withdrawalsPerThread = 10;
        int amountPerWithdrawal = 20;

        int numberOfThreads = 12;
//        lunching a pool of 12 threads
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
//        ensuring the 12 threads are al done despite failed withdrawals
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        AtomicInteger successfulWithdrawals = new AtomicInteger(0);
        AtomicInteger failedWithdrawals = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < withdrawalsPerThread; j++) {
                        try {
                            account.withdraw(amountPerWithdrawal);
                            successfulWithdrawals.incrementAndGet();
                        } catch (IllegalStateException e) {
                            failedWithdrawals.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Should have some successful withdrawals until balance is insufficient
        assertTrue(successfulWithdrawals.get() > 0);
        assertTrue(failedWithdrawals.get() > 0);
        assertTrue(account.getBalance() >= 0);
        assertEquals(100 - (successfulWithdrawals.get() * amountPerWithdrawal), account.getBalance());
    }

//  cd write more tests...

}
