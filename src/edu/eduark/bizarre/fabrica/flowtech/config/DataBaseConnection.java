
package edu.eduark.bizarre.fabrica.flowtech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    
    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                Credentials.URL, 
                Credentials.USER, 
                Credentials.PASSWORD
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC de MySQL no encontrado.", e);
        }
    }
}
