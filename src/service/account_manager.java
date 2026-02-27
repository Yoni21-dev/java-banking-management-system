import java.sql.*;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class account_manager {
    private final Connection con;
    private final Scanner scan;

    public account_manager(Connection con, Scanner scan){
        this.scan = scan;
        this.con = con;
    }

    // Deposit method with hashed PIN
    public void deposit(long account_no){
        scan.nextLine();
        System.out.println("Enter amount to deposit:");
        double amount = scan.nextDouble();
        scan.nextLine(); // consume newline
        System.out.println("Enter security PIN:");
        String inputPin = scan.nextLine().trim();

        try {
            con.setAutoCommit(false);
            if (account_no != 0) {
                PreparedStatement ps = con.prepareStatement("SELECT balance, security_pin FROM accounts WHERE account_no=?");
                ps.setLong(1, account_no);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("security_pin");
                    double currentBalance = rs.getDouble("balance");

                    if (BCrypt.checkpw(inputPin, storedHash)) {
                        PreparedStatement depositStmt = con.prepareStatement(
                                "UPDATE accounts SET balance = balance + ? WHERE account_no=?");
                        depositStmt.setDouble(1, amount);
                        depositStmt.setLong(2, account_no);
                        int row = depositStmt.executeUpdate();
                        if (row > 0) {
                            double newBalance = currentBalance + amount;
                            System.out.println("Deposit successful.");
                            System.out.println("Your balance is: " + newBalance);
                            System.out.println("Logged out successfully.\n");
                            con.commit();
                            return; // automatically return to main menu
                        } else {
                            System.out.println("Transaction failed.");
                            con.rollback();
                        }
                    } else {
                        System.out.println("Invalid PIN.");
                    }
                } else {
                    System.out.println("Account not found.");
                }
            } else {
                System.out.println("Incorrect account number.");
            }
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Withdrawal with hashed PIN
    public void withdrawal(long account_no){
        scan.nextLine();
        System.out.println("Enter amount to withdraw:");
        double amount = scan.nextDouble();
        scan.nextLine();
        System.out.println("Enter security PIN:");
        String inputPin = scan.nextLine().trim();

        try {
            con.setAutoCommit(false);
            if (account_no != 0) {
                PreparedStatement ps = con.prepareStatement("SELECT balance, security_pin FROM accounts WHERE account_no=?");
                ps.setLong(1, account_no);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("security_pin");
                    double currentBalance = rs.getDouble("balance");

                    if (BCrypt.checkpw(inputPin, storedHash)) {
                        if (amount <= currentBalance) {
                            PreparedStatement withdrawStmt = con.prepareStatement(
                                    "UPDATE accounts SET balance = balance - ? WHERE account_no=?");
                            withdrawStmt.setDouble(1, amount);
                            withdrawStmt.setLong(2, account_no);
                            int row = withdrawStmt.executeUpdate();
                            if (row > 0) {
                                double newBalance = currentBalance - amount;
                                System.out.println("withdrawal successful.");
                                System.out.println("Your balance is: " + newBalance);
                                System.out.println("Logged out successfully.\n");
                                con.commit();
                                return;
                            } else {
                                System.out.println("Transaction failed.");
                                con.rollback();
                            }
                        } else {
                            System.out.println("Insufficient balance.");
                        }
                    } else {
                        System.out.println("Invalid PIN.");
                    }
                } else {
                    System.out.println("Account not found.");
                }
            } else {
                System.out.println("Incorrect account number.");
            }
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Transfer with hashed PIN and automatic balance display
    public void transfer_amount(long sender_account){
        scan.nextLine();
        System.out.println("Enter receiving account number:");
        long receiving_account_no = scan.nextLong();
        System.out.println("Enter amount:");
        double amount = scan.nextDouble();
        scan.nextLine();
        System.out.println("Enter security PIN:");
        String inputPin = scan.nextLine().trim();

        try {
            con.setAutoCommit(false);
            if (sender_account != 0 && receiving_account_no != 0) {
                PreparedStatement ps = con.prepareStatement("SELECT balance, security_pin FROM accounts WHERE account_no=?");
                ps.setLong(1, sender_account);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("security_pin");
                    double currentBalance = rs.getDouble("balance");

                    if (BCrypt.checkpw(inputPin, storedHash)) {
                        if (amount <= currentBalance) {
                            PreparedStatement withdrawStmt = con.prepareStatement(
                                    "UPDATE accounts SET balance = balance - ? WHERE account_no=?");
                            withdrawStmt.setDouble(1, amount);
                            withdrawStmt.setLong(2, sender_account);

                            PreparedStatement depositStmt = con.prepareStatement(
                                    "UPDATE accounts SET balance = balance + ? WHERE account_no=?");
                            depositStmt.setDouble(1, amount);
                            depositStmt.setLong(2, receiving_account_no);

                            int row1 = withdrawStmt.executeUpdate();
                            int row2 = depositStmt.executeUpdate();

                            if (row1 > 0 && row2 > 0) {
                                con.commit();
                                double newBalance = currentBalance - amount;
                                System.out.println("Transaction successful.");
                                System.out.println("Your balance is: " + newBalance);
                                System.out.println("Logged out successfully.\n");
                                return; // go back to main menu automatically
                            } else {
                                System.out.println("Transaction failed.");
                                con.rollback();
                            }
                        } else {
                            System.out.println("Insufficient balance.");
                        }
                    } else {
                        System.out.println("Invalid PIN.");
                    }
                } else {
                    System.out.println("Sender account not found.");
                }
            } else {
                System.out.println("Incorrect account number.");
            }
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Check balance with PIN
    public void check_balance(long account_no){
        scan.nextLine();
        System.out.println("Enter security PIN:");
        String inputPin = scan.nextLine().trim();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT balance, security_pin FROM accounts WHERE account_no=?");
            ps.setLong(1, account_no);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("security_pin");
                double balance = rs.getDouble("balance");

                if (BCrypt.checkpw(inputPin, storedHash)) {
                    System.out.println("Your balance is: " + balance);
                } else {
                    System.out.println("Invalid PIN.");
                }
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}