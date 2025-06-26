package banking;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("==================================");
        System.out.println("    BANKING SERVICE SIMULATION    ");
        System.out.println("==================================");

        Account account = new Account();
        try {
            // Simulate the test scenario
            System.out.println("Depositing 1000...");
            account.deposit(1000);
            Thread.sleep(1000);

            System.out.println("Depositing 2000...");
            account.deposit(2000);
            Thread.sleep(1000);

            System.out.println("Withdrawing 500...");
            account.withdraw(500);

            System.out.println("\nAccount Statement:");
            System.out.println(account.printStatement());

            // Cast to access additional methods not in interface
            System.out.printf("\nFinal Balance: %d%n", account.getBalance());

        } catch (Exception e) {
            System.err.println("Error during demo: " + e.getMessage());
        }




    }
}