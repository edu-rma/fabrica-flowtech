package edu.eduark.bizarre.fabrica.flowtech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    
    public static Connection getConnection() throws SQLException {
        try {
            // 1. Obligamos a Java a cargar el Driver de MySQL en memoria
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: No se encontró la librería mysql-connector-j.");
            e.printStackTrace();
        }

        // 2. Limpiamos cualquier espacio invisible que se haya copiado por accidente
        String urlLimpia = Credentials.DB_URL.trim();
        String usuarioLimpio = Credentials.DB_USER.trim();
        String passwordLimpio = Credentials.DB_PASSWORD.trim();

        // 3. Ejecutamos la conexión con los datos limpios
        return DriverManager.getConnection(urlLimpia, usuarioLimpio, passwordLimpio);
    }
}