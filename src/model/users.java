import java.sql.*;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt; // import BCrypt for password hashing

public class users {
    private final Connection con;
    private final Scanner scan;

    public users(Connection con, Scanner scan){
        this.scan = scan;
        this.con = con;
    }

    // Register a new user
    public void register() {
        scan.nextLine();
        String full_name;
        while (true) {
            System.out.println("Enter full_name:");
            full_name = scan.nextLine();
            if (full_name.isEmpty()) {
                System.out.println("full_name cannot be empty. Please enter your full_name.");
            } else {
                break;
            }
        }

        String email;
        while (true) {
            System.out.println("Enter email:");
            email = scan.nextLine();

            if (email == null || email.trim().isEmpty()) {
                System.out.println("Email cannot be empty. Please enter a valid email.");
            } else if (!email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
                System.out.println("Invalid Gmail address! Only example@gmail.com format is allowed.");
            } else {
                break; // Email is valid
            }
        }

        String password;
        while (true) {
            System.out.println("Enter password:");
            password = scan.nextLine();

            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please enter a valid password.");
                continue;
            }

            boolean isStrong = password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");
            if (!isStrong) {
                System.out.println("⚠️ Your password is weak or does not meet recommended criteria.");
                System.out.println("Do you still want to use it? (yes/no):");
                String choice = scan.nextLine().trim().toLowerCase();
                if (choice.equals("yes") || choice.equals("y")) {
                    break; // allow weak password
                } else {
                    System.out.println("Please enter a stronger password.");
                    continue;
                }
            } else {
                break; // strong password
            }
        }

        if (users_exist(email)) {
            System.out.println("user already exist");
            return;
        }

        // Hash the password before storing
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        String sql = "INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, full_name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashedPassword); // store hashed password
            int row = preparedStatement.executeUpdate();
            if (row > 0){
                System.out.println("registration successfully");
            } else {
                System.out.println("registration failed");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Login method using hashed password
   public String login() {
    scan.nextLine();

    String email;
    String password;

    while (true) {
        System.out.println("enter email");
        email = scan.nextLine().trim();

        System.out.println("enter password");
        password = scan.nextLine().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill your email or password.\n");
        } else {
            break; // both are filled
        }
    }

    String login_query = "SELECT password FROM users WHERE email=?";
    try {
        PreparedStatement preparedStatement = con.prepareStatement(login_query);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String storedHash = resultSet.getString("password");
            if (BCrypt.checkpw(password, storedHash)) {
                return email; // login success
            } else {
                System.out.println("Invalid password.");
                return null;
            }
        } else {
            System.out.println("User not found.");
            return null;
        }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

    private boolean users_exist(String email) {
        String query = "select * from users where email=?";
        try{
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}