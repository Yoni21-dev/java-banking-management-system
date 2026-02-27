import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt for hashing

public class accounts {
    private final Connection con;
    private final Scanner scan;

    public accounts(Connection con, Scanner scan) {
        this.scan = scan;
        this.con = con;
    }

    // Open a new account
    public long open_account_no(String email) {
        if (!account_exist(email)) {
            scan.nextLine(); // consume leftover newline
            System.out.println("Enter full name:");
            String full_name = scan.nextLine();

            double balance;
            while (true) {
                System.out.println("Enter initial amount (>=50 birr):");
                balance = scan.nextDouble();
                scan.nextLine(); // consume newline
                if (balance >= 50) break;
                System.out.println("Amount must be >=50 birr.");
            }

            String security_pin;
            while (true) {
                System.out.println("Enter security PIN (4 digits):");
                security_pin = scan.next().trim();
                if (security_pin.matches("\\d{4}")) break; // validate 4 digits
                System.out.println("Invalid PIN! Must be exactly 4 digits.");
            }

            // Hash the security PIN before storing
            String hashedPin = BCrypt.hashpw(security_pin, BCrypt.gensalt(12));

            long account_no = generate_account_no();

            String query = "INSERT INTO accounts (account_no, full_name, email, balance, security_pin) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setLong(1, account_no);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, hashedPin); // store hashed PIN

                int row = preparedStatement.executeUpdate();
                if (row > 0) return account_no;
                else throw new RuntimeException("Account creation failed");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Account already exists");
    }

    // Generate unique account number
    private long generate_account_no() {
        Random rand = new Random();
        long accountNo;

        try (Statement stmt = con.createStatement()) {
            while (true) {
                StringBuilder sb = new StringBuilder("1000");
                for (int i = 0; i < 9; i++) sb.append(rand.nextInt(10));
                accountNo = Long.parseLong(sb.toString());

                String query = "SELECT 1 FROM accounts WHERE account_no = " + accountNo + " LIMIT 1";
                try (ResultSet rs = stmt.executeQuery(query)) {
                    if (!rs.next()) return accountNo; // unique number found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating account number: " + e.getMessage(), e);
        }
    }

    // Get account number by email
    public long get_account_no(String email) {
        String query = "SELECT account_no FROM accounts WHERE email=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("account_no");
            } else {
                throw new RuntimeException("Account number doesn't exist for the given email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Check if account exists by email
    public boolean account_exist(String email) {
        String query = "SELECT 1 FROM accounts WHERE email=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Verify security PIN for a given account number
    public boolean verifyPin(long accountNo, String inputPin) {
        String query = "SELECT security_pin FROM accounts WHERE account_no=?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setLong(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("security_pin");
                return BCrypt.checkpw(inputPin, storedHash); // returns true if PIN matches
            } else {
                return false; // account not found
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}