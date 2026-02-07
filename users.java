import java.sql.*;

 import java.util.Scanner;

public class users {
    private final Connection con;
    private final Scanner scan;
    public users(Connection con, Scanner scan){
      this.scan=scan;
      this.con=con;
    }
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
            if (email.isEmpty()) {
                System.out.println("Email cannot be empty. Please enter a valid email.");
            } else {
                break;
            }
        }
            String password;
            while (true) {
                System.out.println("Enter password:");
                password = scan.nextLine();
                if (password.isEmpty()) {
                    System.out.println("Password cannot be empty. Please enter a valid password.");
                } else {
                    break;
                }
            }
        if (users_exist(email)) {
            System.out.println("user already exist");
            return;
        }
        String sql="insert into users values(?,?,?)";
        try {
            PreparedStatement preparedStatement= con.prepareStatement(sql);
            preparedStatement.setString(1,full_name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);
            int row= preparedStatement.executeUpdate();
            if (row>0){
                System.out.println("registration successfully");
            }
            else
                System.out.println("registration failed");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
public String login(){
        scan.nextLine();
        System.out.println("enter email");
        String email=scan.nextLine();
        System.out.println("enter password");
        String password=scan.nextLine();
        String login_query="select * from users where email=? and password=?";
        try {
            PreparedStatement preparedStatement= con.prepareStatement(login_query);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);
            ResultSet resultSet=preparedStatement.executeQuery();
            if (resultSet.next()){
                return email;
            }
            else
                return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
}
    private boolean users_exist(String email) {
        String query="select*from users where email=?";
        try{
            PreparedStatement preparedStatement= con.prepareStatement(query);
            preparedStatement.setString(1,email);
            ResultSet resultSet= preparedStatement.executeQuery();
            if (resultSet.next()){
                return true;}
            else
            {return  false;}
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}


        