package sample.core;

import java.sql.*;
import java.util.Properties;
/**
 * Created by oleh on 09.01.16.
 */
public class DB {

    private static final String dbClassName = "com.mysql.jdbc.Driver";

    private static String ipConnect;
    private static String portConnect;
    private static String db;

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public static Connection connect(String host, String port, String dbname,  String user, String password)throws
            ClassNotFoundException,SQLException
    {
        // Class.forName(xxx) loads the jdbc classes and
        // creates a drivermanager class factory
        Class.forName(dbClassName);

        // Properties for user and password. Here the user and password are both 'paulr'
        Properties p = new Properties();
        p.put("user",user);
        p.put("password",password);

        // Now try to connect
        Connection c = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbname,p);
        return c;
    }
}
