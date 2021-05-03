package dao;

import dbutil.PostGresConnection;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AccountDaoImp {
    private static DecimalFormat df2 = new DecimalFormat("#.00");
    private static Scanner in = new Scanner(System.in);
    private static Logger log = Logger.getLogger(AccountDaoImp.class);
    private static String accountType;
    private static int userId;
    private static String firstName;
    private static String lastName;
    private static String email;
    private static String phone;
    private static String password;
    private static double currentBalance = 0;
    private static boolean isEmployee = false;


    public static void createUser() throws SQLException {

        log.info("Would You Like To Open a Checking or Savings Account?");
        setAccountType(in.nextLine());
        while (!(accountType.equalsIgnoreCase("Checking") || accountType.equalsIgnoreCase("Saving") || accountType.equalsIgnoreCase("Savings"))) {
            log.warn("Please Enter Valid Account Type ");
            setAccountType(in.nextLine());
        }
        log.info("Please Enter First Name: ");
        setFirstName(in.nextLine());
        while (!(Pattern.matches("[a-zA-Z]+", firstName) && firstName.length() > 2)) {
            log.warn("Please Enter Valid First Name: ");
            setFirstName(in.nextLine());
        }
        log.info("Please Enter Last Name: ");
        setLastName(in.nextLine());
        while (!(Pattern.matches("[a-zA-Z]+", lastName) && lastName.length() > 2)) {
            log.warn("Please Enter Valid Last Name: ");
            setLastName(in.nextLine());
        }
        log.info("Please Enter Email: ");
        setEmail(in.nextLine());
        while (!(Pattern.matches("[A-Za-z0-9+_.-]+@(.+)$", email) && email.length() > 3)) {
            log.warn("Please Enter a Valid Email: ");
            setEmail(in.nextLine());
        }
        log.info("Enter a Password: ");
        setPassword(in.nextLine());
        while (!(Pattern.matches("[A-Z]{1}[a-zA-Z0-9]{6,20}+", password) && password.length() >= 6)) {
            log.warn("Password Must Contain 1st Letter Uppercase, 1 Or More Digits, And Be 6+ Characters Long: ");
            setPassword(in.nextLine());
        }
        log.info("Please Enter Phone Number: ");
        setPhone(in.nextLine());
        while (!(Pattern.matches("[0-9]{10}+", phone))) {
            log.warn("Please Enter Valid 10 Digit Phone Number: ");
            setPhone(in.nextLine());
        }

        UserDaoImp.addUser(firstName, lastName, email, password, phone);
        log.info("Your New Login is: " + getEmail());
        log.info("Your New Password is: " + getPassword());
        insertBalance();
    }

    public static void insertBalance() throws SQLException {
        log.debug("add balance called");
        log.debug(getAccountId() + getAccountType());
        // set all the preparedstatement parameters

        log.info("Please Enter Deposit Amount: $");
        double firstDeposit = in.nextDouble();
        while (firstDeposit <= 0) {    //check if deposit is valid
            log.warn("You Cannot Deposit Negative Values or 0");
            firstDeposit = in.nextDouble();
        }
        while (firstDeposit < 100) {
            log.warn("A Deposit of $100 or More Is Required To Open a New " + accountType + " Account");
            firstDeposit = in.nextDouble();
        }
        while (firstDeposit >= 100) {   //add deposit to set initial account balance
            log.debug(currentBalance = currentBalance + firstDeposit);
            String sql = "INSERT INTO bankapp.account (accountid, accounttype, balance) VALUES(?, ?, ?);\n";

            try (Connection conn = PostGresConnection.connect();
                 PreparedStatement prep = conn.prepareStatement(sql,
                         Statement.RETURN_GENERATED_KEYS)) {

                prep.setInt(1, getAccountId());
                prep.setString(2, getAccountType());
                prep.setFloat(3, (float) currentBalance);

                int x = prep.executeUpdate();
                if (x > 0) {
                    try (ResultSet rs = prep.getGeneratedKeys()) {
                        if (rs.next()) {
                            log.info("Deposit of $"+df2.format(firstDeposit)+" Was Successful!");
                            break;
                        }
                    } catch (SQLException ex) {
                        log.error(ex.getMessage());
                    }
                }
            } catch (SQLException ex) {
                log.error(ex.getMessage());
            }
        }
    }

    public static void logIn() {
        while (true) {
            log.info("Enter Your Email: ");
            String checkEmail = in.nextLine();
            log.info("Enter Your Password: ");
            String checkPassword = in.nextLine();
            String sql = "SELECT email, password FROM bankapp.user Where (email='" + checkEmail + "' AND password='" + checkPassword + "')";

            try (Connection connection = PostGresConnection.connect()) { //open connection
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) { //check if any accounts are in DB
                    if (checkEmail.equalsIgnoreCase(rs.getString("email")) && checkPassword.equals(rs.getString("password"))) { //check if user contains valid email/password
                        try {
                            String sqlValid = "SELECT * FROM bankapp.user as u join bankapp.account as a on u.userid = a.accountid Where (u.email='" + checkEmail + "' AND u.password='" + checkPassword + "')";
                            preparedStatement = connection.prepareStatement(sqlValid); //access users info
                            rs = preparedStatement.executeQuery();
                            while (rs.next()) { //assign info locally from db
                                setEmail(rs.getString("email"));
                                setPassword(rs.getString("password"));
                                setLastName(rs.getString("lastname"));
                                setFirstName(rs.getString("firstname"));
                                setPhone(rs.getString("phone"));
                                setIsEmployee(rs.getBoolean("isemployee"));
                                setAccountType(rs.getString("accounttype"));
                                setAccountId(rs.getInt("userid"));
                                setBalance(rs.getDouble("balance"));
                                log.info("Welcome, " + getFirstName() + "!");
                            }
                            rs.close(); //close connection
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        rs.close();
                        connection.close();
                    } else {
                        log.warn("Wrong Email or Password!");
                        rs.close();
                        connection.close();
                        logIn();
                    }
                } else {
                    log.warn("User Not Found");
                    rs.close();
                    connection.close();
                    MenuDaoImp.runMenu();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            break;
        }
    }

    public static void deposit() {
        log.info("How much would you like to deposit?");
        double deposit = in.nextDouble();
        while ((deposit <= 0)) {
            log.info("Please Enter Valid Deposit Greater Than Zero");
            deposit = in.nextDouble();
        }

        try {
            Connection connection = PostGresConnection.connect();
            log.debug("connection executed");
            Statement stmt = connection.createStatement();
            log.debug("createstmt executed");
            String sql = "UPDATE bankapp.account SET balance=balance+" + deposit + " WHERE accountid=" + getAccountId() + ";";
            stmt.executeUpdate(sql);
            log.debug("sql executed");
            String sql2 = "SELECT balance FROM bankapp.account where accountid=" + getAccountId() + ";";
            ResultSet rs = stmt.executeQuery(sql2);
            log.debug("rs executed");
            while (rs.next()) {
                //Retrieve by column name
                setBalance(rs.getDouble("balance"));
                log.info("Deposit of $"+df2.format(deposit)+" Was Successful!");
                log.info(getAccountType() + " Has a Balance of $" + df2.format(getCurrentBalance()));
            }
            rs.close();

        } catch (SQLException e) {

            log.error(e);
        }


    }

    public static void withdraw() {
        log.info("How much would you like to withdraw?");
        double withdraw = in.nextDouble();
        while ((withdraw > getCurrentBalance())) {
            log.warn("You Cannot Withdraw More Than Your Balance of $" + getCurrentBalance());
            log.warn("Please Enter Valid Withdraw");
            withdraw = in.nextDouble();
        }
        while ((withdraw <= 0)) {
            log.warn("You Cannot Withdraw a Negative Amount or Zero");
            log.warn("Please Enter Valid Withdraw");
            withdraw = in.nextDouble();
        }

        try {
            Connection connection = PostGresConnection.connect();
            log.debug("connection executed");
            Statement stmt = connection.createStatement();
            log.debug("createstmt executed");
            String sql = "UPDATE bankapp.account SET balance=balance-" + withdraw + " WHERE accountid=" + getAccountId() + ";";
            stmt.executeUpdate(sql);
            log.debug("sql executed");
            String sql2 = "SELECT balance FROM bankapp.account where accountid=" + getAccountId() + ";";
            ResultSet rs = stmt.executeQuery(sql2);
            log.debug("rs executed");
            while (rs.next()) {
                //Retrieve by column name
                setBalance(rs.getDouble("balance"));
                log.info("Withdrawal of $"+df2.format(withdraw)+" Was Successful!");
                log.info(getAccountType() + " Has a Balance of $" + df2.format(getCurrentBalance()));
            }
            rs.close();

        } catch (SQLException e) {

            log.error(e);
        }
    }

    private static void setIsEmployee(boolean isemployee) {
        isEmployee = isemployee;
    }

    private static void addMoney(double firstDeposit) {
        currentBalance = currentBalance + firstDeposit;
    }

    private static void setBalance(double balance) {
        currentBalance = balance;
    }


    public static String getAccountType() {
        return accountType;
    }

    public static void setAccountType(String accountType) {
        AccountDaoImp.accountType = accountType;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        AccountDaoImp.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        AccountDaoImp.lastName = lastName;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        AccountDaoImp.email = email;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        AccountDaoImp.phone = phone;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        AccountDaoImp.password = password;
    }

    public static double getCurrentBalance() {
        return currentBalance;
    }

    public static int getAccountId() {
        return userId;
    }

    public static void setAccountId(int userId) {
        AccountDaoImp.userId = userId;
    }
}
