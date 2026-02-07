
import java.sql.*;
import java.util.Scanner;

public class account_manager {
    private final Connection con;
    private final Scanner scan;
    public account_manager(Connection con, Scanner scan){
        this.scan=scan;
        this.con=con;
    }
    public void deposit(long account_no){
        scan.nextLine();
        System.out.println("enter amount number to deposit");
        double amount=scan.nextDouble();
        System.out.println("enter security pin");
        int security_pin=scan.nextInt();
        try {
            con.setAutoCommit(false);
            if (account_no!=0){
                PreparedStatement preparedStatement= con.prepareStatement("select* from accounts where account_no=? and security_pin=?");
                preparedStatement.setLong(1,account_no);
                preparedStatement.setInt(2,security_pin);
                ResultSet resultSet= preparedStatement.executeQuery();
                if (resultSet.next()){
                    String deposit_query="update accounts set balance=balance+? where account_no=?";
                    PreparedStatement preparedStatement1=con.prepareStatement(deposit_query);
                    preparedStatement1.setDouble(1,amount);
                    preparedStatement1.setLong(2,account_no);
                    int row=preparedStatement1.executeUpdate();
                    if (row>0){
                        System.out.println(amount+"is success fully deposited");  
                        con.commit();
                        con.setAutoCommit(true);
                        return;
                    }
                    else {
                        System.out.println("transaction failed");
                        con.rollback();
                        con.setAutoCommit(true);
                    }
                }
                else{
                    System.out.println("invalid pin");
                }
            }else {
                System.out.println("incorrect account_no");
            }
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void withdrawal(long account_no){
        scan.nextLine();
        System.out.println("enter amount to withdraw from your account");
        double amount=scan.nextDouble();
        System.out.println("enter your security pin");
        int security_pin=scan.nextInt();
        try {
            con.setAutoCommit(false);
            if (account_no!=0){
                PreparedStatement preparedStatement= con.prepareStatement("select*from accounts where account_no=? and security_pin=?");
                preparedStatement.setLong(1,account_no);
                preparedStatement.setInt(2,security_pin);
                ResultSet resultSet= preparedStatement.executeQuery();
                if (resultSet.next()){
                    double current_balance=resultSet.getDouble("balance");
                    if (amount<=current_balance) {
                        String withdrawal = "update accounts set balance=balance-? where account_no=?";
                        PreparedStatement preparedStatement1 = con.prepareStatement(withdrawal);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_no);
                        int row = preparedStatement1.executeUpdate();
                        if (row > 0) {
                            System.out.println(amount + "successfully withdraw from your account");
                            con.commit();
                            con.setAutoCommit(true);
                        } else {
                            System.out.println("transaction failed");
                            con.rollback();
                            con.setAutoCommit(true);
                        }
                    }
                    else
                    {System.out.println("insufficient balance ");}
                }
                else
                {System.out.println("incorrect pin");}
            }else {
                System.out.println("incorrect account_no");
            }
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void transfer_amount(long sender_account){
scan.nextLine();
     System.out.println("enter receiving account number");
     long receiving_account_no=scan.nextLong();
     System.out.println("enter amount");
     double amount=scan.nextDouble();
     System.out.println("enter security pin");
     int security_pin=scan.nextInt();
     try {
         con.setAutoCommit(false);
         if (sender_account!=0 && receiving_account_no!=0){
             PreparedStatement preparedStatement= con.prepareStatement("select*from accounts where account_no=? and security_pin=?");
             preparedStatement.setLong(1,sender_account);
             preparedStatement.setInt(2,security_pin);
             ResultSet resultSet= preparedStatement.executeQuery();
             if (resultSet.next()){
                 double current_balance=resultSet.getDouble("balance");
                 if (amount<=current_balance){
                     String withdraw_query="update accounts set balance=balance-? where account_no=?";
                     String deposit_query="update accounts set balance=balance+? where account_no=?";
                     PreparedStatement preparedStatement1= con.prepareStatement(withdraw_query);
                     preparedStatement1.setDouble(1,amount);
                     preparedStatement1.setLong(2,sender_account);
                     PreparedStatement preparedStatement2= con.prepareStatement(deposit_query);
                     preparedStatement2.setDouble(1,amount);
                     preparedStatement2.setLong(2,receiving_account_no);
                     int row1=preparedStatement1.executeUpdate();
                     int row2=preparedStatement2.executeUpdate();
                     if (row1>0 && row2>0){
                         System.out.println("transaction successfully");
                         con.commit();
                         con.setAutoCommit(true);
                     }else {
                         System.out.println("transaction failed");
                         con.rollback();
                         con.setAutoCommit(true);
                     }
                 }
                 else {
                     System.out.println("insufficient balance");
                 }
             }else {
             System.out.println("invalid pin");}
         }else
             System.out.println("incorrect account_no");
         con.setAutoCommit(true);
     } catch (SQLException e) {
         throw new RuntimeException(e);
     }
    }
    public void check_balance(long account_no){
        scan.nextLine();
        System.out.println("enter security pin");
        int security_pin=scan.nextInt();
        try {
            PreparedStatement preparedStatement= con.prepareStatement("select balance from accounts where account_no=? and security_pin=?");
            preparedStatement.setLong(1,account_no);
            preparedStatement.setInt(2,security_pin);
            ResultSet resultSet=preparedStatement.executeQuery();
            if (resultSet.next()){
                double balance=resultSet.getDouble("balance");
                System.out.println("your balance is:"+ balance);
            }else {
                System.out.println("invalid pin");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}