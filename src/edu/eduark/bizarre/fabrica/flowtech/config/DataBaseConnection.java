package edu.eduark.bizarre.fabrica.flowtech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            Credentials.DB_URL, 
            Credentials.DB_USER, 
            Credentials.DB_PASSWORD
        );
    }
}