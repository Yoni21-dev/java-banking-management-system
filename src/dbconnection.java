import java.sql.*;
import java.util.Scanner;
public class dbconnection {
    public static void main(String[] args)  {
        String url = "jdbc:mysql://localhost:3306/bankingmanagment";
        String username = "root";
        String password = "1993@21";
        Connection con;
        try {
            con = DriverManager.getConnection(url, username, password);
            Scanner scan = new Scanner(System.in);
            users user = new users(con, scan);
            accounts acc=new accounts(con,scan);
            account_manager acc_m = new account_manager(con, scan);
            String email;
            long account_no;
            while (true) {
                System.out.println("*** welcome to banking system ***");
                System.out.println();
                System.out.println("enter 1/ for registration");
                System.out.println("enter 2/ for login");
                System.out.println("enter 3/ for user_exist");
                int selection1 = scan.nextInt();
                switch (selection1) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                       email=user.login();
                        System.out.println();
                        if (email!=null){
                            System.out.println("logged in successfully");
                            if (!acc.account_exist(email)){
                              System.out.println();
                              System.out.println("enter 1/ to create new account book");
                              System.out.println("enter 2/ to exist");
                              if (scan.nextInt()==1){
                                  account_no=acc.open_account_no(email);
                                  System.out.println("account created successfully");
                                  System.out.println("your account number is="+ account_no);
                              }else {
                                  break;
                              }
                            }
                            account_no=acc.get_account_no(email);
                            while (true){
                                System.out.println();
                                System.out.println("enter your choice ");
                                System.out.println("1/for deposit");
                                System.out.println("2/for withdrawal");
                                System.out.println("3/for money transfer");
                                System.out.println("4/for check your balance");
                                System.out.println("5/for log out");
                                int selection2=scan.nextInt();
                                switch (selection2){
                                    case 1:acc_m.deposit(account_no);
                                    break;
                                    case 2:acc_m.withdrawal(account_no);
                                    break;
                                    case 3:acc_m.transfer_amount(account_no);
                                    case 4:acc_m.check_balance(account_no);
                                    case 5:
                                        System.out.println("logged  out successfully");
                                        break;
                                    default:System.out.println("enter valid choice");
                                }
                                if (selection2 == 5) break;
                            }
                        }else {
                            System.out.println("incorrect email or password");
                        }
                        break;
                    case 3:
                        System.out.println("THANK YOU FOR using BANKING SYSTEM");
                    System.out.println("exiting system");
                    return;
                    default:System.out.println("enter valid choice");
                    break;
                }
            }
        }catch (SQLException e){
            System.out.println();
        }
    }
}