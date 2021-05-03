package appTests;

import java.util.regex.Pattern;

public class Validation {


    public boolean isValidOption(int choice) {
        boolean b = false;
        if (choice > 3 || choice < 0) return b = true;

        return b;
    }

    public boolean isValidContactNumber(String phone) {
        boolean b = false;
        if (phone != null && phone.matches("\\+1-[0-9]{10}")) {
            b = true;
        }
        return b;
    }

    public boolean isValidEmail(String email) {
        if ((Pattern.matches("[A-Za-z0-9+_.-]+@(.+)$", email) && email.length() > 3)) {
            return true;
        } else return false;
    }

    public boolean isNotValidEmail(String email) {
        if (!(Pattern.matches("[A-Za-z0-9+_.-]+@(.+)$", email) && email.length() > 3)) {
            return false;
        } else return false;
    }

    public boolean isValidPassword(String password) {
        if ((Pattern.matches("[a-zA-Z0-9]+", password))) {
            return true;
        }
        return false;
    }

    public boolean isNotValidPassword(String password) {
        if (!(Pattern.matches("[a-zA-Z0-9]+", password))) {
            return true;
        }
        return false;
    }

    public boolean isValidDeposit(double deposit) {
        if (deposit <= 0) {
            return false;
        } else if (deposit < 100) {
            return false;
        } else if (deposit >= 100) {
            return true;
        } else return false;
    }

    public boolean isValidAccount(String accountType) {
        if (accountType.equalsIgnoreCase("Checking") || accountType.equalsIgnoreCase("Saving") || accountType.equalsIgnoreCase("Savings")) {
            return true;
        } else return false;
    }
}

