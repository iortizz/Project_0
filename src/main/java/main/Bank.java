package main;

import dao.MenuDaoImp;
import org.apache.log4j.Logger;

public class Bank {
    private static final Logger log = Logger.getLogger(Bank.class);

    public static void main(String[] args) {
        MenuDaoImp.runProgram();
    }
}
