import banking.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

}
