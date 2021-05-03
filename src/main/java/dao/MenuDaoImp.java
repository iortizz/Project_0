package dao;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class MenuDaoImp {
    private static DecimalFormat df2 = new DecimalFormat("#.00");
    private static final Scanner in = new Scanner(System.in);
    private static final Logger log = Logger.getLogger(MenuDaoImp.class);
    private static int choice;

    public static void runProgram(){
        boolean running = true;
        MenuDaoImp.runMenu();
        while (running) {
            MenuDaoImp.UserMenu();
            int x = MenuDaoImp.getUserInput();
            switch (x) {
                case 1:
                    AccountDaoImp.deposit();
                    break;
                case 2:
                    AccountDaoImp.withdraw();
                    break;
                case 3:
                    log.info("Your balance is: $" + df2.format(AccountDaoImp.getCurrentBalance()));
                    break;
                case 0:
                    log.info("Goodbye, " + AccountDaoImp.getFirstName() +"!");
                    running = false;
                    break;
                default:
                    log.info("Unknown");
            }
        }
    }

    public static void runMenu() {
        printMenu();
        getUserInput();
        doStuff(choice);
    }

    public static void printMenu() {
        log.info("Welcome to Isaiah's Bank App");
        log.info("Select an option below");
        log.info("1: Create An Account");
        log.info("2: Log In");
        log.info("0: exit");
    }

    public static void UserMenu() {
        log.info("Select an option below");
        log.info("1: Make a Deposit");
        log.info("2: Make a Withdrawal");
        log.info("3: Get Balance");
        log.info("0: Exit");
    }

    public static int getUserInput() {
        do {
            log.info("Enter a Choice: ");
            try {
                choice = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                log.warn("Please Enter a Number");
            }
            if (choice < 0 || choice > 3) {
                log.error("Please Enter a valid option ");
            }
        } while ((choice < 0 || choice > 3));
        return choice;
    }


    public static void doStuff(int choice) {
        log.debug("doStuff() called");
        try {
            switch (choice) {
                case 1:
                    AccountDaoImp.createUser();
                    break;
                case 2:
                    AccountDaoImp.logIn();
                    break;
                case 0:
                    log.info("Goodbye!");
                    break;
                default:
                    log.error("Input not found");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
