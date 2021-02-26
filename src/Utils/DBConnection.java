package Utils;

import java.sql.*;


/**
 * This class sets up the DB connection*/

public class DBConnection {
    public static final String protocol = "jdbc";
    public static final String vendorName = ":mysql:";
    public static final String ipAddress = "//wgudb.ucertify.com/WJ081MC";
    public static final String jdbcURL = protocol + vendorName + ipAddress;

    public static final String mySQLJdbcDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection connection = null;

    //Password and Username
    public static final String userName = "U081MC";
    public static final String password = "53689194638";

    /***
     * startConnection method sets up the connection with the database
     * @return the connection
     */
    public static Connection startConnection(){
        try {
            Class.forName(mySQLJdbcDriver);
            connection = DriverManager.getConnection(jdbcURL, userName, password);
            System.out.println("Connection successful.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public static Connection getConnection(){
        return connection;
    }

    /***
     * closeConnection method closes the connection with the database.
     */
    public static void closeConnection(){
        try {
            connection.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

}
