package edu.eduark.bizarre.fabrica.flowtech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase con patrón de diseño Singleton para la conexión a MySQL.
 * 
 * @author informatica
 */
public class DataBaseConnection {

    private static Connection connection;

    /*
     * El constructor es privado para evitar
     * la instanciación directa de la clase.
     */
    private DataBaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Registrar explícitamente el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                String url = Credentials.getUrlDb();
                String user = Credentials.getUserDb();
                String pass = Credentials.getPassDb();

                connection = DriverManager.getConnection(url, user, pass);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Error: No se encontró el controlador (Driver) de MySQL.", e);
            }
        }
        return connection;
    }
}