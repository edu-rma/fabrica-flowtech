
package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuarioRepository {
    
public boolean registrar(int idRol, String nombre, String apellido, String email, String passwordHash) {
        String sql = "INSERT INTO usuarios (id_rol, nombre, apellido, email, password_hash, activo) VALUES (?, ?, ?, ?, ?, TRUE)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idRol);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellido);
            pstmt.setString(4, email);
            pstmt.setString(5, passwordHash);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario en la base de datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
