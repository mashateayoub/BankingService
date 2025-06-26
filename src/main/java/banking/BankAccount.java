package banking;

public interface BankAccount {

    void deposit(int amount);
    void withdraw(int amount);
    String printStatement();

}