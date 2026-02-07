import java.sql.*;
import java.util.Scanner;

public class accounts {
    private final Connection con;
    private final Scanner scan;

    public accounts(Connection con, Scanner scan) {
        this.scan = scan;
        this.con = con;
    }

    public long open_account_no(String email) {
        if (!account_exist(email)) {
            scan.nextLine();
            System.out.println("enter full name");
            String full_name = scan.nextLine();
            double balance;
            while (true) {
                System.out.println("Enter initial amount (must be greater than or equal to 50 birr):");
                balance = scan.nextDouble();
                if (balance >= 50) {
                    break;
                } else {
                    System.out.println("Initial amount must be greater than or equal to 50 birr. Please try again.");
                }
            }

            System.out.println("enter security pin");
            int security_pin = scan.nextInt();
            long account_no = generate_account_no();
            String query = "insert into accounts values(?,?,?,?,?)";
            try {
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setLong(1, account_no);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setInt(5, security_pin);
                int row = preparedStatement.executeUpdate();
                if (row > 0) {
                    return account_no ;
                } else {
                    System.out.println("creation account is failed");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
            throw new RuntimeException("Account already exists");
    }

    private long generate_account_no() {
        String query = "SELECT MAX(account_no) AS max_account_no FROM accounts";
        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
    
            if (resultSet.next()) {
                long last_account_no = resultSet.getLong("max_account_no");
    
                // If there are no accounts yet start from 1000100
                if (resultSet.wasNull()) {
                    return 1000100;
                } else {
                    return last_account_no + 1; // Increment from last account number
                }
            } else {
                return 1000100; // Default starting account number
            }
    
        } catch (SQLException e) {
            throw new RuntimeException("Error generating account number: " + e.getMessage(), e);
        }
    }
    
    

    public long get_account_no(String email) {
        String query = "select *from accounts where email=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("account_no");
            }
            else {
                throw new RuntimeException("Account number doesn't exist for the given email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

public boolean account_exist(String email){
    String query="select*from accounts where email=?";
    try{
        PreparedStatement preparedStatement= con.prepareStatement(query);
        preparedStatement.setString(1,email);
        ResultSet resultSet=preparedStatement.executeQuery();
        if (resultSet.next()){
            return true;
        }
        else
            return false;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}
}