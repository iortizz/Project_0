package dbutil;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostGresConnection {
    private static final Logger log = Logger.getLogger(PostGresConnection.class);

    public static Connection connection;

    public static Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
//            String url = "jdbc:postgresql://localhost:5432/postgres";
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "");
            if (connection != null) {
                log.debug("Connected!");
            } else {
                log.error("Connection Failed, Try Again.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            log.error(e);
        }
        return connection;
    }
}
