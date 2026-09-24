package edu.eduark.bizarre.fabrica.flowtech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    
    public static Connection getConnection() throws SQLException {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: No se encontró la librería mysql-connector-j.");
            e.printStackTrace();
        }


        String urlLimpia = Credentials.DB_URL.trim();
        String usuarioLimpio = Credentials.DB_USER.trim();
        String passwordLimpio = Credentials.DB_PASSWORD.trim();

  
        return DriverManager.getConnection(urlLimpia, usuarioLimpio, passwordLimpio);
    }
}