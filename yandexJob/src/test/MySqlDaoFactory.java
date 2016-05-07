package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDaoFactory {
    private String user = "root";
    private String password = "qweqwe";
    private String url = "jdbc:mysql://localhost:3306/db1";
    private String driver = "com.mysql.jdbc.Driver";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public MySqlDaoFactory() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}