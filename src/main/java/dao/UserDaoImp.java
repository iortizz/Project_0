package dao;

import dbutil.PostGresConnection;
import org.apache.log4j.Logger;

import java.sql.*;

public class UserDaoImp {
    private static final Logger log = Logger.getLogger(UserDaoImp.class);


    public static void addUser(String firstName, String lastName, String email, String password, String phone) {
        String addUserSQL = "INSERT INTO bankapp.user (firstname, lastname, email, password, phone) VALUES(?,?,?,?,?);\n";
        try {
            Connection addUser = PostGresConnection.connect();
            log.debug("connect called");
            PreparedStatement preparedStatement = addUser.prepareStatement(addUserSQL, Statement.RETURN_GENERATED_KEYS);
            log.debug("prepare called");
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, phone);

            int c = preparedStatement.executeUpdate();
            log.debug("execute called");
            if (c != 1) {
                log.info("Registration Failed, Please Try Again Later");
            } else {
                try (Statement stmt = addUser.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE)) {
                    ResultSet rs = stmt.executeQuery("SELECT userid FROM bankapp.user where email='" + AccountDaoImp.getEmail() + "'");
                    if (rs.next()) {
                        AccountDaoImp.setAccountId(rs.getInt("userid"));
                        log.debug(AccountDaoImp.getAccountId());
                        rs.close();
                    }
                }
            }
            preparedStatement.close();
            addUser.close();
            log.debug("close");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        log.info("Account Created!");
    }
}
